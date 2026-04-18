package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeometriesTests {

    private static final String ERROR = "Wrong number of intersection points";

    /**
     * Test method for {@link Geometries#findIntersections(Ray)}
     */
    @Test
    void testFindIntersections() {

        // Shapes
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1);
        Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));
        Triangle triangle = new Triangle(
                new Point(0, 1, 0),
                new Point(1, -1, 0),
                new Point(-1, -1, 0)
        );

        Geometries geometries = new Geometries(sphere, plane, triangle);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Some geometries are intersected
        // Sphere → 2, Plane → 1, Triangle → 1  ==> total 4
        Ray ray1 = new Ray(new Point(0, 0, -2), new Vector(0, 0, 1));
        List<Point> result1 = geometries.findIntersections(ray1);

        assertNotNull(result1, "Expected intersections");
        assertEquals(4, result1.size(), ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: No geometries are intersected
        Ray ray2 = new Ray(new Point(0, 0, -2), new Vector(0, 1, 0));
        assertNull(geometries.findIntersections(ray2),
                "Expected null when no intersections");

        // BV02: Only one geometry is intersected
        // Only plane should intersect → 1 point
        Ray ray3 = new Ray(new Point(2, 2, -2), new Vector(0, 0, 1));
        List<Point> result3 = geometries.findIntersections(ray3);

        assertNotNull(result3, "Expected one intersection");
        assertEquals(1, result3.size(), ERROR);

        // BV03: All geometries are intersected
        // same as EP but זה מכסה גם את המקרה הזה לפי הדרישה
        Ray ray4 = new Ray(new Point(0, 0, -2), new Vector(0, 0, 1));
        List<Point> result4 = geometries.findIntersections(ray4);

        assertNotNull(result4, "Expected intersections");
        assertEquals(4, result4.size(), ERROR);
    }
}