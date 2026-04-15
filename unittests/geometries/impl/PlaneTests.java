package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Plane}.
 * The tests verify:
 * <ul>
 * <li>Constructors</li>
 * <li>{@link Plane#getNormal(Point)}</li>
 * </ul>
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class PlaneTests {

    private static final double DELTA = 1e-6;
    private static final String ERROR_NORMAL = "ERROR: normal is incorrect";
    private static final String ERROR_EXCEPTION = "ERROR: expected exception was not thrown";
    /** Point (0,0,0) used in tests */
    private static final Point P000 = new Point(0, 0, 0);
    /** Point (0,0,1) used in tests */
    private static final Point P001 = new Point(0, 0, 1);
    /** Point (1,0,0) used in tests */
    private static final Point P002 = new Point(0, 0, 2);
    /** Vector (0,0,1) used in tests */
    private static final Vector V001 = new Vector(0, 0, 1);
    /** Vector (1,1,1) used in tests */
    private static final Vector V111 = new Vector(1, 1, 1);
    /** Vector (1,0,0) used in tests */
    private static final Vector V100 = new Vector(1, 0, 0);
    /** Plane z = 1 */
    private static final Plane PLANE = new Plane(P001, V001);
    /** Error message */
    private static final String ERROR_PLANE_INTERSECTION = "Wrong plane intersection result";


    PlaneTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Test constructor (Point, Vector)
     */
    @Test
    void testConstructorPointVector() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: normal is normalized
        Vector n = new Vector(0, 3, 4);

        assertDoesNotThrow(() -> new Plane(new Point(0,0,0), n), ERROR_EXCEPTION);

        Plane plane = new Plane(new Point(0, 0, 0), n);

        // check that the normal is normalized (length = 1)
        Vector n1 = plane.getNormal(new Point(1,1,1));
        assertEquals(1, n1.length(), DELTA, ERROR_NORMAL);

        // TC02: normal is constant for any point
        assertEquals(n1,
                plane.getNormal(new Point(5,5,5)),
                ERROR_NORMAL);
    }

    /**
     * Test constructor (3 points)
     */
    @Test
    void testConstructor3Points() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: valid plane
        assertDoesNotThrow(() ->
                        new Plane(new Point(0, 0, 1),
                                new Point(1, 0, 0),
                                new Point(0, 1, 0)),
                ERROR_EXCEPTION);

        // =============== Boundary Values Tests ==================

        // TC11: two identical points
        assertThrows(IllegalArgumentException.class, () ->
                        new Plane(new Point(0, 0, 0),
                                new Point(0, 0, 0),
                                new Point(1, 0, 0)),
                ERROR_EXCEPTION);

        // TC12: all points identical
        assertThrows(IllegalArgumentException.class, () ->
                        new Plane(new Point(0, 0, 0),
                                new Point(0, 0, 0),
                                new Point(0, 0, 0)),
                ERROR_EXCEPTION);

        // TC13: points on same line
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

        // TC01: normal at general point
        Vector n = plane.getNormal(p1);

        // length = 1
        assertEquals(1, n.length(), DELTA, ERROR_NORMAL);

        // orthogonal to edges
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);

        assertEquals(0, n.dotProduct(v1), DELTA, ERROR_NORMAL);
        assertEquals(0, n.dotProduct(v2), DELTA, ERROR_NORMAL);

        // TC02: normal is constant
        assertEquals(plane.getNormal(p1), plane.getNormal(p2), ERROR_NORMAL);

        // =============== Boundary Values Tests ==================

        // TC11: reference point
        Vector n2 = plane.getNormal(p2);

        assertEquals(1, n2.length(), DELTA, ERROR_NORMAL);
    }

    /**
     * Test method for {@link geometries.impl.Plane#findIntersections(primitives.Ray)}.
     */

    @Test
    void testFindIntersections() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects the plane (1 point)
        Ray ray1 = new Ray(P000, V111);
        var result1 = PLANE.findIntersections(ray1);
        assertNotNull(result1, ERROR_PLANE_INTERSECTION);
        assertEquals(1, result1.size(), ERROR_PLANE_INTERSECTION);

        // EP02: Ray does not intersect the plane (0 points)
        Ray ray2 = new Ray(P002, V111);
        var result2 = PLANE.findIntersections(ray2);
        assertNull(result2, ERROR_PLANE_INTERSECTION);


        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray parallel to the plane

        // BV11: Ray is in the plane (0 points)
        Ray ray3 = new Ray(P001, V100);
        var result3 = PLANE.findIntersections(ray3);
        assertNull(result3, ERROR_PLANE_INTERSECTION);

        // BV12: Ray is parallel and outside the plane (0 points)
        Ray ray4 = new Ray(P000, V100);
        var result4 = PLANE.findIntersections(ray4);
        assertNull(result4, ERROR_PLANE_INTERSECTION);


        // **** Group 2: Ray orthogonal to the plane

        // BV21: Ray starts before the plane (1 point)
        Ray ray5 = new Ray(P000, V001);
        var result5 = PLANE.findIntersections(ray5);
        assertNotNull(result5, ERROR_PLANE_INTERSECTION);
        assertEquals(1, result5.size(), ERROR_PLANE_INTERSECTION);

        // BV22: Ray starts in the plane (0 points)
        Ray ray6 = new Ray(P001, V001);
        var result6 = PLANE.findIntersections(ray6);
        assertNull(result6, ERROR_PLANE_INTERSECTION);

        // BV23: Ray starts after the plane (0 points)
        Ray ray7 = new Ray(P002, V001);
        var result7 = PLANE.findIntersections(ray7);
        assertNull(result7, ERROR_PLANE_INTERSECTION);


        // **** Group 3: Ray not parallel and not orthogonal, starts in plane
        // BV31: Ray starts on the plane (general case)
        Ray ray8 = new Ray(new Point(0.3, 0.3, 1), V111);
        var result8 = PLANE.findIntersections(ray8);
        assertNull(result8, ERROR_PLANE_INTERSECTION);


        // **** Group 4: Ray starts at plane reference point (Q)
        // BV41: Ray starts exactly at Q
        Ray ray9 = new Ray(P001, V111);
        var result9 = PLANE.findIntersections(ray9);
        assertNull(result9, ERROR_PLANE_INTERSECTION);
    }
}