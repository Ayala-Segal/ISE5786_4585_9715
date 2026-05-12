package geometries.api;

import primitives.Color;
import primitives.Material;
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
    private Color _emission = Color.BLACK;

    /**
     * Material properties of the geometry.
     * Initialized with default material.
     */
    private Material _material = new Material();

    public Color getEmission() {
        return _emission;
    }

    public Geometry setEmission(Color emission) {
        this._emission = emission;
        return this;
    }

    /**
     * Returns the material of the geometry.
     *
     * @return the material
     */
    public Material getMaterial() {
        return _material;
    }

    /**
     * Sets the material of the geometry.
     *
     * @param material the material to set
     * @return this Geometry object (for method chaining)
     */
    public Geometry setMaterial(Material material) {
        this._material = material;
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