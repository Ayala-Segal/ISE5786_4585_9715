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
     *
     * @param intersection the intersection point with precomputed data
     * @return resulting color from all light contributions
     */
    private Color calcLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission();

        for (LightSource lightSource : scene.lights) {
            if (setLightSource(intersection, lightSource)) {
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