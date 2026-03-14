package geometries.impl;

import java.util.Objects;

import primitives.Point;
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
    private Point center;

    /**
     * The radius of the sphere.
     */
    private double radius;

    /**
     * Constructs a sphere with the given center and radius.
     *
     * @param center the center point of the sphere
     * @param radius the radius of the sphere
     */
    public Sphere(Point center, double radius) {

        super(radius);

        this.center = center;
    }


    @Override
    public Vector getNormal(Point point) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Sphere sphere = (Sphere) o;
        return Double.compare(radius, sphere.radius) == 0 && Objects.equals(center, sphere.center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(center, radius);
    }

    public String toString() {
        return "Sphere{" +
                "center=" + center +
                ", radius=" + radius +
                '}';
    }
}
