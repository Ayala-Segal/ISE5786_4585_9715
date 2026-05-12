package renderer;

import geometries.api.Intersectable.Intersection;
import primitives.Color;
import primitives.Ray;
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

        // NVI: use calcIntersections
        List<Intersection> intersections =
                scene.geometries.calcIntersections(ray);

        if (intersections == null) {
            return scene.background;
        }

        // closest intersection (NOT point anymore)
        Intersection closest =
                ray.findClosestIntersection(intersections);

        return calcColor(closest);
    }

    /**
     * Calculates color at intersection point.
     *
     * @param intersection closest intersection
     * @return resulting color
     */
    private Color calcColor(Intersection intersection) {

        if (intersection == null) {
            return scene.background;
        }

        Geometry geometry = (Geometry) intersection.geometry;

        return geometry.getEmission().add(
                scene.ambientLight.getIntensity()
                        .scale(intersection.material.kA)
        );
    }
}