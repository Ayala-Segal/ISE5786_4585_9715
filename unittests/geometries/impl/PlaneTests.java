package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Plane}.
 * The tests verify:
 * <ul>
 * <li>Constructors</li>
 * <li>{@link Plane#getNormal(Point)}</li>
 * </ul>
 * Tests follow Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class PlaneTests {

    private static final double DELTA = 1e-6;
    private static final String ERROR_NORMAL = "ERROR: normal is incorrect";
    private static final String ERROR_EXCEPTION = "ERROR: expected exception was not thrown";

    PlaneTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Test constructor (Point, Vector)
     */
    @Test
    void testConstructorPointVector() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: normal is normalized
        Vector n = new Vector(0, 3, 4);

        assertDoesNotThrow(() -> new Plane(new Point(0,0,0), n), ERROR_EXCEPTION);

        Plane plane = new Plane(new Point(0, 0, 0), n);

        assertEquals(1, plane.getNormal(new Point(1,1,1)).length(), DELTA, ERROR_NORMAL);

        // ensure normal is parallel to original vector
        Vector n1 = plane.getNormal(new Point(1,1,1));
        Vector n2 = plane.getNormal(new Point(5,5,5));

        assertEquals(0, n1.crossProduct(n2).length(), DELTA);

        // EP02: normal is constant for any point
        assertEquals(plane.getNormal(new Point(1,1,1)),
                plane.getNormal(new Point(5,5,5)),
                ERROR_NORMAL);
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
                ERROR_EXCEPTION);

        // =============== Boundary Values Tests ==================

        // BV01: two identical points
        assertThrows(IllegalArgumentException.class, () ->
                        new Plane(new Point(0, 0, 0),
                                new Point(0, 0, 0),
                                new Point(1, 0, 0)),
                ERROR_EXCEPTION);

        // BV02: all points identical
        assertThrows(IllegalArgumentException.class, () ->
                        new Plane(new Point(0, 0, 0),
                                new Point(0, 0, 0),
                                new Point(0, 0, 0)),
                ERROR_EXCEPTION);

        // BV03: points on same line
        assertThrows(IllegalArgumentException.class, () ->
                        new Plane(new Point(0, 0, 0),
                                new Point(1, 1, 1),
                                new Point(2, 2, 2)),
                ERROR_EXCEPTION);
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
        assertEquals(1, n.length(), DELTA, ERROR_NORMAL);

        // orthogonal to edges
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);

        assertEquals(0, n.dotProduct(v1), DELTA, ERROR_NORMAL);
        assertEquals(0, n.dotProduct(v2), DELTA, ERROR_NORMAL);

        // EP02: normal is constant
        assertEquals(plane.getNormal(p1), plane.getNormal(p2), ERROR_NORMAL);

        // =============== Boundary Values Tests ==================

        // BV01: reference point
        Vector n2 = plane.getNormal(p2);

        assertEquals(1, n2.length(), DELTA, ERROR_NORMAL);
    }
}