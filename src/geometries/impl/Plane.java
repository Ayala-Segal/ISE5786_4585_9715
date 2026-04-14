package geometries.impl;

import java.util.List;
import java.util.Objects;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
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
    private final Point _point;
    /**
     * The normal vector of the plane.
     */
    private final Vector _normal;

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
        this._point = p1;

        // Compute and normalize the plane normal
        this._normal = p2.subtract(p1)
                .crossProduct(p3.subtract(p1))
                .normalize();
    }

    /**
     * Constructs a plane from a point and a normal vector.
     *
     * @param point  a point on the plane
     * @param normal the normal vector of the plane
     */
    public Plane(Point point, Vector normal) {
        this._point = point;
        this._normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Plane plane = (Plane) o;
        return Objects.equals(_point, plane._point) && Objects.equals(_normal, plane._normal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_point, _normal);
    }

    @Override
    public String toString() {
        return "Plane: + point=" + _point +
                ", normal=" + _normal;
    }


    @Override
    public List<Point> findIntersections(Ray ray) {
        return List.of();
    }
}
