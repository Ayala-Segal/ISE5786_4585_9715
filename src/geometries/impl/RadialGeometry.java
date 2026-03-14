package geometries.impl;

import geometries.api.Geometry;

/**
 * Base class for geometries that have a radius.
 * <p>
 * RadialGeometry serves as a common parent for all shapes defined by a central point and a radius,
 * such as spheres and cylinders. It precomputes the squared radius for efficiency.
 * </p>
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *   <li>{@code radius} – the radius of the geometry</li>
 *   <li>{@code radiusSquared} – the squared radius (precomputed)</li>
 * </ul>
 *
 * <p><b>Constructor:</b></p>
 * <ul>
 *   <li>{@code RadialGeometry(double radius)} – initializes both {@code radius} and {@code radiusSquared}</li>
 * </ul>
 *
 * <p>This class is abstract and extends {@link Geometry}.</p>
 *
 */
public abstract class RadialGeometry extends Geometry {

    /**
     * The radius of the geometry
     */
    private double radius;

    /**
     * The squared radius of the geometry (precomputed)
     */
    private double radiusSquared;

    /**
     * Constructs a radial geometry with the specified radius.
     *
     * @param radius the radius of the geometry
     */
    public RadialGeometry(double radius) {
        this.radius = radius;
        this.radiusSquared = radius * radius;
    }

}
