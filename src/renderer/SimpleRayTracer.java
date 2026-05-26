package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;
import geometries.api.Geometry;
import java.util.List;

/**
 * Simple ray tracer implementation.
 */
class SimpleRayTracer extends RayTracerBase {

    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;
    private static final Double3 INITIAL_K = Double3.ONE;
    /**
     * Small offset used to shift the shadow ray origin along the surface normal,
     * preventing self-shadowing due to numerical precision errors.
     * The value is chosen to be small enough to be imperceptible in the rendered image,
     * yet large enough to avoid false self-intersections.
     * Can be adjusted based on the scale of the scene geometry.
     */
    private static final double DELTA = 0.1;

    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    Color traceRay(Ray ray) {
        Intersection closest = findClosestIntersection(ray);

        if (closest == null)
            return scene.background;

        return calcColor(closest, ray);
    }

    /**
     * Calculates the color of a ray by tracing it into the scene.
     *
     * @param ray the ray to trace
     * @return the resulting color
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        return calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K);
    }

    /**
     * Recursive calculation of color at an intersection point.
     * Handles termination conditions based on recursion depth and attenuation factor.
     *
     * @param intersection the intersection point
     * @param level        remaining recursion depth
     * @param k            accumulated attenuation factor
     * @return calculated color
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {

        if (level == 0 || k.isLowerThan(MIN_CALC_COLOR_K))
            return Color.BLACK;

        // local effects (כפי שכתוב בדרישה)
        Color color = calcLocalEffects(intersection);

        // global effects (כפי שכתוב בסעיף ז)
        color = color.add(calcGlobalEffects(intersection, k, level));

        return color;
    }
    /**
     * Calculates the sum of local lighting effects from all external light sources.
     * For each light source, a shadow ray is cast to determine visibility.
     * If the intersection point is in shadow (i.e., the light source is blocked),
     * the contribution of that light source is skipped.
     *
     * @param intersection the intersection point with precomputed data
     * @return resulting color from all light contributions
     */
    private Color calcLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission();

        for (LightSource lightSource : scene.lights) {
            if (setLightSource(intersection, lightSource) && unshaded(intersection)) {
                color = color.add(
                        lightSource.getIntensity(intersection.point)
                                .scale(calcDiffuse(intersection)
                                        .add(calcSpecular(intersection)))
                );
            }
        }
        return color;
    }

    /**
     * Checks whether the given intersection point is unshaded with respect to
     * its associated light source, by casting a shadow ray from the point toward
     * the light source and testing for blocking geometry.
     *
     * The shadow ray origin is slightly shifted along the surface normal toward
     * the light source (by DELTA) to prevent self-shadowing due to numerical precision errors.
     *
     * Only intersections closer to the shadow ray origin than the light source
     * are considered blocking, but only if the blocking object is sufficiently opaque
     * (kT < MIN_CALC_COLOR_K).
     *
     * @param intersection the intersection point with precomputed data
     *                     (including light vector l, normal n, and ln)
     * @return true if no geometry blocks the light, false if point is in shadow
     */
    private boolean unshaded(Intersection intersection) {

        // direction from point to light
        Vector pointToLight = intersection.l.scale(-1);

        // shift origin to avoid self-intersection
        Vector delta = intersection.n.scale(intersection.ln < 0 ? DELTA : -DELTA);
        Ray shadowRay = new Ray(intersection.point.add(delta), pointToLight);

        List<Intersection> shadowIntersections =
                scene.geometries.calcIntersections(shadowRay);

        if (shadowIntersections == null)
            return true;

        double lightDistance = intersection.light.getDistance(intersection.point);

        for (Intersection shadowIntersection : shadowIntersections) {

            // only consider objects between point and light
            if (shadowIntersection.point.distance(intersection.point) < lightDistance) {

                // check transparency threshold
                Double3 kt = shadowIntersection.geometry.getMaterial().kT;

                if (kt.isLowerThan(new Double3(MIN_CALC_COLOR_K))) {
                    return false;
                }
            }
        }

        return true;
    }
    /**
     * Calculates the diffuse component of the Phong reflection model.
     * Formula: kD * |l · n|
     *
     * @param intersection intersection with precomputed l, n, ln
     * @return diffuse coefficient as Double3
     */
    private Double3 calcDiffuse(Intersection intersection) {
        return intersection.material.kD.scale(Math.abs(intersection.ln));
    }

    /**
     * Calculates the specular component of the Phong reflection model.
     * Formula: kS * max(0, -v · r)^nShininess
     * where r = l - n * 2*(l·n)
     *
     * @param intersection intersection with precomputed v, l, n, ln
     * @return specular coefficient as Double3
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector r = intersection.l.subtract(
                intersection.n.scale(2 * intersection.ln)
        );
        double vr = Math.max(0, intersection.v.dotProduct(r) * -1);
        return intersection.material.kS
                .scale(Math.pow(vr, intersection.material.nShininess));
    }

    /**
     * Constructs a reflection ray from the intersection point.
     * The ray direction is the reflection of the incoming vector on the surface normal.
     * The starting point is slightly shifted along the normal (DELTA)
     * to avoid self-intersections due to numerical precision.
     *
     * @param n           surface normal
     * @param p           intersection point
     * @param v           incoming ray direction
     * @param intersection the intersection data (used for normal orientation)
     * @return reflection ray
     */
    private Ray constructReflectionRay(Vector n, Point p, Vector v, Intersection intersection) {

        Vector r = v.subtract(n.scale(2 * v.dotProduct(n)));

        Vector delta = n.scale(intersection.n.dotProduct(v) > 0 ? DELTA : -DELTA);

        return new Ray(p.add(delta), r);
    }

    /**
     * Constructs a refraction (transparency) ray from the intersection point.
     * The ray continues in the same direction as the incoming ray,
     * but its origin is slightly shifted along the normal (DELTA)
     * to prevent self-intersection due to numerical precision errors.
     *
     * @param p           intersection point
     * @param v           incoming ray direction
     * @param n           surface normal
     * @param intersection the intersection data (used for normal orientation)
     * @return refraction ray
     */
    private Ray constructRefractionRay(Point p, Vector v, Vector n, Intersection intersection) {

        Vector delta = n.scale(intersection.n.dotProduct(v) > 0 ? -DELTA : DELTA);

        return new Ray(p.add(delta), v);
    }

    /**
     * Calculates global lighting effect for reflection or refraction rays.
     *
     * The method:
     * - Finds intersections of the secondary ray with the scene
     * - Selects the closest intersection
     * - Recursively computes its color using calcColor
     * - Returns background color if no intersection exists
     * - Multiplies the resulting color by the material attenuation factor (kx)
     *
     * @param ray   secondary ray (reflection or refraction)
     * @param k     accumulated attenuation factor (not used directly here but kept for interface consistency)
     * @param level recursion depth
     * @param kx    material attenuation factor (reflection or transparency coefficient)
     * @return global illumination color contribution
     */
    private Color calcGlobalEffect(Ray ray, Double3 k, int level, Double3 kx) {

        List<Intersection> intersections = scene.geometries.calcIntersections(ray);

        // no hit → background color
        if (intersections == null)
            return scene.background;

        Intersection closest = ray.findClosestIntersection(intersections);

        if (closest == null)
            return scene.background;

        // recursive color calculation
        Color color = calcColor(closest, level - 1, k);

        // apply material attenuation factor (NOT accumulated k)
        return color.scale(kx);
    }

    /**
     * Calculates global lighting effects from reflection and refraction rays.
     *
     * The method:
     * - Traces a reflection ray and computes its contribution
     * - Traces a refraction ray and computes its contribution
     * - Sums both contributions into a single color
     *
     * @param intersection the intersection point
     * @param k            accumulated attenuation factor
     * @param level        recursion depth level
     * @return sum of global effects (reflection + refraction)
     */
    private Color calcGlobalEffects(Intersection intersection, Double3 k, int level) {

        // reflection contribution
        Color reflectionColor = calcGlobalEffect(
                constructReflectionRay(intersection.n, intersection.point, intersection.v, intersection),
                k,
                level,
                intersection.material.kR
        );

        // refraction contribution
        Color refractionColor = calcGlobalEffect(
                constructRefractionRay(intersection.point, intersection.v, intersection.n, intersection),
                k,
                level,
                intersection.material.kT
        );

        // sum of both effects
        return reflectionColor.add(refractionColor);
    }
    /**
     * Finds the closest intersection point of a ray with the scene geometries.
     *
     * The method:
     * - Computes all intersections of the ray with the scene
     * - Selects the intersection closest to the ray origin
     *
     * @param ray the ray to test
     * @return the closest intersection, or null if no intersections exist
     */
    private Intersection findClosestIntersection(Ray ray) {

        List<Intersection> intersections = scene.geometries.calcIntersections(ray);

        if (intersections == null || intersections.isEmpty())
            return null;

        return ray.findClosestIntersection(intersections);
    }
}