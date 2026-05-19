package renderer;

import geometries.api.Intersectable;
import lighting.LightSource;
import primitives.Color;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static primitives.Util.alignZero;

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

    // preprocessIntersection
    protected boolean preprocessIntersection(Intersectable.Intersection intersection, Vector v) {
        intersection.v = v;
        intersection.n = intersection.geometry.getNormal(intersection.point);
        intersection.vn = alignZero(intersection.v.dotProduct(intersection.n));
        return intersection.vn != 0;
    }

    // preprocessLightSource
    protected boolean setLightSource(Intersectable.Intersection intersection, LightSource light) {
        intersection.light = light;
        intersection.l = light.getL(intersection.point);
        intersection.ln = alignZero(intersection.l.dotProduct(intersection.n));
        return intersection.ln * intersection.vn > 0;
    }
}