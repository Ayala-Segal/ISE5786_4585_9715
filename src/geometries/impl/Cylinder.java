package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.Objects;

/**
 * Represents a finite cylinder in three-dimensional space.
 * <p>
 * A cylinder is defined by a central axis (a ray), a radius, and a height.
 * Unlike {@link Tube}, the cylinder has a limited height along its axis.
 * </p>
 * <p>
 * This class inherits from {@link Tube} and can be used in geometric computations
 * such as finding normals, intersections, and distances.
 * </p>
 *
 */

public class Cylinder extends Tube {
    /**
     * The height of the cylinder along its axis.
     */
    private final double _height;

    /**
     * Constructs a cylinder with the specified axis, radius, and height.
     *
     * @param axis   the central axis of the cylinder
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder along the axis
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        this._height = height;
    }

    @Override
    public final Vector getNormal(Point point) {
        return null;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Cylinder: height=" + _height;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Cylinder cylinder = (Cylinder) o;
        return Double.compare(_height, cylinder._height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _height);
    }
}
