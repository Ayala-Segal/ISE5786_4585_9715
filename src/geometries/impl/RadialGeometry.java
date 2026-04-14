package geometries.impl;

import java.util.List;
import java.util.Objects;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;

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
    protected double _radius;

    /**
     * The squared radius of the geometry (precomputed)
     */
    protected double _radiusSquared;

    /**
     * Constructs a radial geometry with the specified radius.
     *
     * @param radius the radius of the geometry
     */
    public RadialGeometry(double radius) {
        this._radius = radius;
        this._radiusSquared = radius * radius;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RadialGeometry that = (RadialGeometry) o;
        return Double.compare(_radius, that._radius) == 0 && Double.compare(_radiusSquared, that._radiusSquared) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_radius, _radiusSquared);
    }

    @Override
    public String toString() {
        return "RadialGeometry: radius=" + _radius +
                ", radiusSquared=" + _radiusSquared;
    }

}
