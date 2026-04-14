package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

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

        // Step 1: Find intersection with the supporting plane
        List<Point> intersections = _plane.findIntersections(ray);
        if (intersections == null) return null;

        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Triangle vertices
        Point p1 = _vertices.get(0);
        Point p2 = _vertices.get(1);
        Point p3 = _vertices.get(2);

        // Vectors from ray origin to triangle vertices
        Vector v1 = p1.subtract(p0);
        Vector v2 = p2.subtract(p0);
        Vector v3 = p3.subtract(p0);

        // Normals to the planes formed by the ray origin and triangle edges
        Vector n1 = v1.crossProduct(v2);
        Vector n2 = v2.crossProduct(v3);
        Vector n3 = v3.crossProduct(v1);

        // Dot products between ray direction and normals
        double s1 = alignZero(v.dotProduct(n1));
        double s2 = alignZero(v.dotProduct(n2));
        double s3 = alignZero(v.dotProduct(n3));

        // If any value is zero → point is on edge or vertex → no intersection
        if (isZero(s1) || isZero(s2) || isZero(s3)) return null;

        // Check if all signs are the same (point is inside triangle)
        boolean positive = (s1 > 0) && (s2 > 0) && (s3 > 0);
        boolean negative = (s1 < 0) && (s2 < 0) && (s3 < 0);

        // Return intersection only if inside the triangle
        return (positive || negative) ? intersections : null;
    }
}
