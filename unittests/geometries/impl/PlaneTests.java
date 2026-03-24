package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class Plane
 */
class PlaneTests {

    private static final double DELTA = 1e-6;
    private static final String ERROR = "ERROR: wrong result";

    /**
     * Test constructor (Point, Vector)
     */
    @Test
    void testConstructorPointVector() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: normal is normalized
        Vector n = new Vector(0, 3, 4);
        Plane plane = new Plane(new Point(0, 0, 0), n);

        assertEquals(1, plane.getNormal(null).length(), DELTA, ERROR);
    }

    /**
     * Test constructor (3 points)
     */
    @Test
    void testConstructor3Points() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: valid plane
        assertDoesNotThrow(() ->
                        new Plane(new Point(0, 0, 1),
                                new Point(1, 0, 0),
                                new Point(0, 1, 0)),
                ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: two identical points
        assertThrows(IllegalArgumentException.class, () ->
                        new Plane(new Point(0, 0, 0),
                                new Point(0, 0, 0),
                                new Point(1, 0, 0)),
                ERROR);

        // BV02: all points identical
        assertThrows(IllegalArgumentException.class, () ->
                        new Plane(new Point(0, 0, 0),
                                new Point(0, 0, 0),
                                new Point(0, 0, 0)),
                ERROR);

        // BV03: points on same line
        assertThrows(IllegalArgumentException.class, () ->
                        new Plane(new Point(0, 0, 0),
                                new Point(1, 1, 1),
                                new Point(2, 2, 2)),
                ERROR);
    }

    /**
     * Test getNormal(Point)
     */
    @Test
    void testGetNormal() {

        Point p1 = new Point(0, 0, 1);
        Point p2 = new Point(1, 0, 0);
        Point p3 = new Point(0, 1, 0);

        Plane plane = new Plane(p1, p2, p3);

        // ============ Equivalence Partitions Tests ==============

        // EP01: normal at general point
        Vector n = plane.getNormal(p1);

        // length = 1
        assertEquals(1, n.length(), DELTA, ERROR);

        // orthogonal to edges
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);

        assertEquals(0, n.dotProduct(v1), DELTA, ERROR);
        assertEquals(0, n.dotProduct(v2), DELTA, ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: reference point
        Vector n2 = plane.getNormal(p2);

        assertEquals(1, n2.length(), DELTA, ERROR);
    }
}