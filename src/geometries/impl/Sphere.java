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
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {

        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Special case: ray starts exactly at the center
        if (p0.equals(_center)) {
            return List.of(new Intersection(this, ray.getPoint(_radius)));
        }

        Vector u = p0.subtract(_center);

        double a = v.dotProduct(v);
        double b = 2 * v.dotProduct(u);
        double c = u.dotProduct(u) - _radiusSquared;

        double discriminant = alignZero(b * b - 4 * a * c);

        if (discriminant <= 0) {
            if (isZero(discriminant)) {
                double t = alignZero(-b / (2 * a));
                if (t > 0) {
                    return List.of(new Intersection(this, ray.getPoint(t)));
                }
            }
            return null;
        }

        double sqrt = Math.sqrt(discriminant);

        double t1 = alignZero((-b - sqrt) / (2 * a));
        double t2 = alignZero((-b + sqrt) / (2 * a));

        List<Intersection> result = null;

        if (t1 > 0 && t2 > 0) {
            result = List.of(
                    new Intersection(this, ray.getPoint(t1)),
                    new Intersection(this, ray.getPoint(t2))
            );
        } else if (t1 > 0) {
            result = List.of(new Intersection(this, ray.getPoint(t1)));
        } else if (t2 > 0) {
            result = List.of(new Intersection(this, ray.getPoint(t2)));
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
