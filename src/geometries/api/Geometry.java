package geometries.api;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Abstract base class for all geometries in the scene.
 * Provides common properties such as emission color.
 */
public abstract class Geometry extends Intersectable {

    /**
     * Emission color of the geometry.
     * Default value is black (no emission).
     */
    private Color emission = Color.BLACK;

    public Color getEmission() {
        return emission;
    }

    public Geometry setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    /**
     * Returns the normal vector to the geometry at a given point.
     *
     * @param point the point on the surface
     * @return the normal vector at the point
     */
    public Vector getNormal(Point point) {
        return null;
    }
}