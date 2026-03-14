package geometries.impl;

import java.util.Objects;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a plane in a 3D Cartesian coordinate system.
 * <p>
 * A plane is defined either by three points on the plane or by a point and a normal vector.
 * </p>
 */
public class Plane extends Geometry {

    /**
     * A point on the plane.
     */
    private Point point;
    /**
     * The normal vector of the plane.
     */
    private Vector normal;

    /**
     * Constructs a plane from three points.
     * <p>
     * The first point is stored in the {@code point} field.
     * The normal vector should be computed from the three points (logic not shown here).
     * </p>
     *
     * @param p1 first point on the plane
     * @param p2 second point on the plane
     * @param p3 third point on the plane
     */
    public Plane(Point p1, Point p2, Point p3) {
        this.point = p1;//??
    }

    /**
     * Constructs a plane from a point and a normal vector.
     *
     * @param point  a point on the plane
     * @param normal the normal vector of the plane
     */
    public Plane(Point point, Vector normal) {
        this.point = point;
        this.normal = normal;
    }

    @Override
    public Vector getNormal(Point point) {
        return normal;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Plane plane = (Plane) o;
        return Objects.equals(point, plane.point) && Objects.equals(normal, plane.normal);
    }


    @Override
    public int hashCode() {
        return Objects.hash(point, normal);
    }

    @Override
    public String toString() {
        return "Plane{" +
                "point=" + point +
                ", normal=" + normal +
                '}';
    }
}
