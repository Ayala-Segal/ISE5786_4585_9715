package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;
import java.util.Objects;

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

public class Tube extends RadialGeometry {
    /**
     * The axis of the tube, represented as a ray.
     */
    protected Ray _axis;


    /**
     * Constructs a tube with the specified axis and radius.
     *
     * @param axis   the central axis of the tube
     * @param radius the radius of the tube
     */

    public Tube(double radius, Ray axis) {
        super(radius);
        this._axis = axis;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        Vector p0p = point.subtract(p0);
        double t = v.dotProduct(p0p);

        Point o;

        if (t == 0) {
            o = p0;
        } else {
            o = _axis.getPoint(t);
        }

        return point.subtract(o).normalize();
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "Tube: axis: " + _axis.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tube other = (Tube) obj;
        return _axis.equals(other._axis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _axis.hashCode());
    }


}
