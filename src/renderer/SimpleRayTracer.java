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
     * Non-recursive entry point for color calculation.
     * Adds ambient light once (not repeated in recursion),
     * preprocesses the intersection, then delegates to the recursive overload.
     *
     * @param intersection the closest intersection
     * @param ray          the incoming ray
     * @return resulting color including ambient light and all effects
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.direction()))
            return Color.BLACK;

        return scene.ambientLight.getIntensity()
                .scale(intersection.material.kA)
                .add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K));
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

        // local effects
        Color color = calcLocalEffects(intersection, k);

        // global effects
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
    private Color calcLocalEffects(Intersection intersection, Double3 k) {

        Color color = intersection.geometry.getEmission();

        for (LightSource lightSource : scene.lights) {

            if (setLightSource(intersection, lightSource)) {

                Double3 ktr = transparency(intersection);

                if (ktr.product(k).isGreaterThan(MIN_CALC_COLOR_K)) {
                    color = color.add(
                            lightSource.getIntensity(intersection.point)
                                    .scale(ktr)
                                    .scale(
                                            calcDiffuse(intersection)
                                                    .add(calcSpecular(intersection))
                                    )
                    );
                }
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

        Vector pointToLight = intersection.l.scale(-1);

        Ray shadowRay = new Ray(intersection.point, pointToLight, intersection.n);

        List<Intersection> shadowIntersections =
                scene.geometries.calcIntersections(shadowRay);

        if (shadowIntersections == null)
            return true;

        double lightDistance = intersection.light.getDistance(intersection.point);

        for (Intersection shadowIntersection : shadowIntersections) {

            if (shadowIntersection.point.distance(intersection.point) < lightDistance) {

                Double3 kt = shadowIntersection.geometry.getMaterial().kT;

                if (kt.isLowerThan(new Double3(MIN_CALC_COLOR_K))) {
                    return false;
                }
            }
        }

        return true;
    }

    private Double3 transparency(Intersection intersection) {

        Double3 ktr = Double3.ONE;

        Vector pointToLight = intersection.l.scale(-1);

        Ray shadowRay = new Ray(
                intersection.point,
                pointToLight,
                intersection.n
        );

        List<Intersection> shadowIntersections =
                scene.geometries.calcIntersections(shadowRay);

        if (shadowIntersections == null) {
            return ktr;
        }

        double lightDistance =
                intersection.light.getDistance(intersection.point);

        for (Intersection si : shadowIntersections) {

            if (si.point.distance(intersection.point) < lightDistance) {

                ktr = ktr.product(si.material.kT);

                if (ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                    return Double3.ZERO;
                }
            }
        }

        return ktr;
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

        return new Ray(p, r, n);
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

        return new Ray(p, v, n);
    }

    /**
     * Calculates the color contribution of a single global effect (reflection or refraction).
     *
     * The method:
     * - Computes the accumulated attenuation kkx = k * kx; if too small, returns BLACK (recursion stop)
     * - Finds the closest intersection of the secondary ray
     * - Preprocesses the new intersection with the secondary ray direction
     * - Recursively computes color with the accumulated attenuation kkx
     * - Scales the result by kx (material attenuation factor)
     * - Returns background (scaled by kx) if no intersection is found
     *
     * @param ray   secondary ray (reflection or refraction)
     * @param k     accumulated attenuation factor from previous recursion levels
     * @param level remaining recursion depth
     * @param kx    material attenuation factor (kR for reflection, kT for transparency)
     * @return color contribution of this global effect
     */
    private Color calcGlobalEffect(Ray ray, Double3 k, int level, Double3 kx) {

        // accumulated attenuation: if too small, no visible contribution
        Double3 kkx = k.product(kx);
        if (kkx.isLowerThan(MIN_CALC_COLOR_K))
            return Color.BLACK;

        Intersection closest = findClosestIntersection(ray);

        // no hit → background scaled by material attenuation
        if (closest == null)
            return scene.background.scale(kx);

        // preprocess the new intersection with the secondary ray direction
        if (!preprocessIntersection(closest, ray.direction()))
            return Color.BLACK;

        // recursive color calculation with accumulated k
        return calcColor(closest, level - 1, kkx).scale(kx);
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