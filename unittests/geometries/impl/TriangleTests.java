package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class Triangle
 */
class TriangleTests {

    private static final double DELTA = 1e-6;
    private static final String ERROR = "ERROR: wrong result";

    /**
     * Test getNormal(Point)
     */
    @Test
    void testGetNormal() {

        Triangle triangle = new Triangle(
                new Point(0, 0, 1),
                new Point(1, 0, 0),
                new Point(0, 1, 0)
        );

        // ============ Equivalence Partitions Tests ==============

        // EP01: normal inside triangle
        Point p = new Point(0, 0, 1);
        Vector n = triangle.getNormal(p);

        // length = 1
        assertEquals(1, n.length(), DELTA, ERROR);

        // orthogonal to edges
        Vector v1 = new Point(1, 0, 0).subtract(new Point(0, 0, 1));
        Vector v2 = new Point(0, 1, 0).subtract(new Point(0, 0, 1));

        assertEquals(0, n.dotProduct(v1), DELTA, ERROR);
        assertEquals(0, n.dotProduct(v2), DELTA, ERROR);
    }
}