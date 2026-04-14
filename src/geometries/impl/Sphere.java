package geometries.impl;

import java.util.List;
import java.util.Objects;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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
        return null;
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

    @Override
    public List<Point> findIntersections(Ray ray) {
        return null;
    }
}
