package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a tube (cylindrical surface) in three-dimensional space.
 * <p>
 * A tube is defined by an axis (a ray representing the center line) and a radius.
 * The tube extends infinitely along its axis.
 * </p>
 * <p>
 * This class inherits from {@link Geometry} and can be used in geometric computations
 * such as finding normals, intersections, and distance calculations.
 * </p>
 */

public class Tube extends Geometry {
    /**
     * The axis of the tube, represented as a ray.
     */
    protected Ray axis;

    /**
     * The radius of the tube.
     */
    protected double radius;

    /**
     * Constructs a tube with the specified axis and radius.
     *
     * @param axis   the central axis of the tube
     * @param radius the radius of the tube
     */

    public Tube(double radius, Ray axis) {
        this.axis = axis;
        this.radius = radius;
    }

    @Override
    public Vector getNormal(Point point) {
        return null;
    }

    @Override
    public String toString() {
        return "axis: " + axis.toString() + ", radius: " + radius;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tube other = (Tube) obj;
        return axis.equals(other.axis) && Double.compare(radius, other.radius) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * axis.hashCode() + Double.hashCode(radius);
    }

}
