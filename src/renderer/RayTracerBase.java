package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Abstract base class for ray tracing.
 */
abstract class RayTracerBase {

    protected final Scene scene;

    /**
     * Constructor that receives a scene.
     * @param scene the scene to trace
     */
    RayTracerBase(Scene scene) {
        this.scene = scene;
    }

    /**
     * Traces a ray and returns the resulting color.
     * @param ray the ray to trace
     * @return the color of the intersection
     */
    abstract Color traceRay(Ray ray);
}