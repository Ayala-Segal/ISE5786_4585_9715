package geometries.impl;

import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * Represents a triangle in a 3D Cartesian coordinate system.
 * <p>
 * A triangle is a specific type of polygon defined by exactly three vertices.
 * This class extends {@link Polygon} and inherits all properties and methods
 * of a polygon, while enforcing that there are exactly three points.
 * </p>
 * <p>
 * The triangle is always convex by definition.
 * </p>
 *
 */
public class Triangle extends Polygon {

    /**
     * Constructs a triangle from three given points.
     * <p>
     * The points define the vertices of the triangle in order. They are
     * passed to the constructor of the superclass {@link Polygon} which
     * handles the creation of the supporting plane and validation.
     * </p>
     *
     * @param p1 first vertex of the triangle
     * @param p2 second vertex of the triangle
     * @param p3 third vertex of the triangle
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    public String toString() {
        return "Triangle: " + super.toString();
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        return null;
    }
}
