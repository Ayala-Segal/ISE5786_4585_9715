package renderer;

import geometries.api.Intersectable;
import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

import java.util.List;

/**
 * Simple ray tracer implementation.
 * <p>
 * Computes color based on closest intersection point.
 * </p>
 */
class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructs a simple ray tracer.
     *
     * @param scene the scene to render
     */
    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces a ray and computes its color.
     *
     * @param ray the ray to trace
     * @return resulting color
     */
    @Override
    Color traceRay(Ray ray) {

        List<Point> intersections = scene.geometries.findIntersections(ray);

        if (intersections == null) {
            return scene.background;
        }

        Point closest = ray.findClosestPoint(intersections);

        return calcColor(closest);
    }

    /**
     * Calculates color at intersection point.
     *
     * @param point intersection point
     * @return resulting color
     */
    private Color calcColor(Point point) {

        if (point == null) {
            return scene.background;
        }

        return scene.ambientLight.getIntensity();
    }
}