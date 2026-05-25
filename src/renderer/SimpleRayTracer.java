package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;
import geometries.api.Geometry;
import java.util.List;

/**
 * Simple ray tracer implementation.
 */
class SimpleRayTracer extends RayTracerBase {

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
        List<Intersection> intersections = scene.geometries.calcIntersections(ray);

        if (intersections == null)
            return scene.background;

        Intersection closest = ray.findClosestIntersection(intersections);
        return calcColor(closest, ray.direction());
    }

    /**
     * Calculates color at intersection point.
     *
     * @param intersection closest intersection
     * @param v            ray direction vector
     * @return resulting color
     */
    private Color calcColor(Intersection intersection, Vector v) {
        if (!preprocessIntersection(intersection, v))
            return Color.BLACK;

        return scene.ambientLight.getIntensity()
                .scale(intersection.material.kA)
                .add(calcLocalEffects(intersection));
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
     * <p>
     * The shadow ray origin is slightly shifted along the surface normal toward
     * the light source (by {@code DELTA}) to prevent self-shadowing due to
     * numerical precision errors.
     * <p>
     * Only intersections closer to the shadow ray origin than the light source
     * are considered blocking, preventing false shadowing by objects behind the light.
     * <p>
     * Note: the shadow ray direction is the negation of the light vector {@code l}
     * (which points from the light source to the point), since we shoot from the
     * point toward the light source.
     *
     * @param intersection the intersection point with precomputed data (including
     *                     the light vector {@code l}, normal {@code n}, and {@code ln})
     * @return {@code true} if no geometry blocks the path to the light source,
     *         {@code false} if the point is in shadow
     */
    private boolean unshaded(Intersection intersection) {
        // Shadow ray direction: from point toward light source (opposite of l)
        Vector pointToLight = intersection.l.scale(-1);

        // Shift ray origin slightly along the normal toward the light to avoid self-shadowing
        Vector delta = intersection.n.scale(intersection.ln < 0 ? DELTA : -DELTA);
        Ray shadowRay = new Ray(intersection.point.add(delta), pointToLight);

        List<Intersection> shadowIntersections = scene.geometries.calcIntersections(shadowRay);
        if (shadowIntersections == null) return true;

        double lightDistance = intersection.light.getDistance(intersection.point);

        for (Intersection shadowIntersection : shadowIntersections) {
            if (shadowIntersection.point.distance(intersection.point) < lightDistance)
                return false;
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
}