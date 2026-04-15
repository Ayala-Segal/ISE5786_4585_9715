package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;
import java.util.Objects;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents a sphere in a 3D Cartesian coordinate system.
 * <p>
 * A sphere is defined by its center point and radius. It extends {@link RadialGeometry}
 * to inherit common properties of radial geometries.
 * </p>
 */
public class Sphere extends RadialGeometry {

    /**
     * The center point of the sphere.
     */
    private final Point _center;


    /**
     * Constructs a sphere with the given center and radius.
     *
     * @param center the center point of the sphere
     * @param radius the radius of the sphere
     */
    public Sphere(Point center, double radius) {
        super(radius);
        this._center = center;
    }


    @Override
    public final Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    @Override
    public List<Point> findIntersections(Ray ray) {

        // Ray origin and direction
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Ray starts at the sphere center
        if (p0.equals(_center)) {
            return List.of(ray.getPoint(_radius));
        }

        // Vector from sphere center to ray origin
        Vector u = p0.subtract(_center);

        // Quadratic equation coefficients
        double a = v.dotProduct(v);
        double b = 2 * v.dotProduct(u);
        double c = u.dotProduct(u) - _radiusSquared;

        // Compute discriminant
        double discriminant = alignZero(b * b - 4 * a * c);

        // No intersections if discriminant is negative
        if (discriminant < 0) return null;

        // Single intersection (tangent case)
        if (isZero(discriminant)) {
            double t = alignZero(-b / (2 * a));
            return t > 0 ? List.of(ray.getPoint(t)) : null;
        }

        // Two intersection points
        double sqrt = Math.sqrt(discriminant);

        double t1 = alignZero((-b - sqrt) / (2 * a));
        double t2 = alignZero((-b + sqrt) / (2 * a));

        List<Point> result = null;

        // Both intersections are in front of the ray
        if (t1 > 0 && t2 > 0) {
            result = List.of(
                    ray.getPoint(Math.min(t1, t2)),
                    ray.getPoint(Math.max(t1, t2))
            );
        }

        // Only first intersection is valid
        else if (t1 > 0) {
            result = List.of(ray.getPoint(t1));
        }

        // Only second intersection is valid
        else if (t2 > 0) {
            result = List.of(ray.getPoint(t2));
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Sphere sphere = (Sphere) o;
        return Objects.equals(_center, sphere._center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _center);
    }

    public String toString() {
        return super.toString() + "Sphere: center=" + _center;
    }
}
