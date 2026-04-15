package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Sphere}.
 * The tests verify:
 * <ul>
 * <li>{@link Sphere#getNormal(Point)}</li>
 * </ul>
 * Equivalence Partitions (EP).
 */
class SphereTests {

    /**
     * Delta value for comparing double precision values
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for wrong normal calculation
     */
    private static final String ERROR = "ERROR: wrong normal";

    /**
     * Point (0,0,1) used in some tests
     */
    private static final Point P001 = new Point(0, 0, 1);
    /**
     * Point (1,0,0) used in some tests
     */
    private static final Point P100 = new Point(1, 0, 0);
    /**
     * Point (-1,0,0) used in some tests
     */
    private static final Point P01 = new Point(-1, 0, 0);
    /**
     * Vector (0,0,1) used in some tests
     */
    private static final Vector V100 = new Vector(1, 0, 0);
    /**
     * Vector (3,1,0) used in some tests
     */
    private static final Vector V311 = new Vector(3, 1, 0);
    /**
     * Vector (1,1,0) used in some tests
     */
    private static final Vector V110 = new Vector(1, 1, 0);
    /**
     * Point used as intersection in some tests
     */
    private static final Point INTERSECTION1 = new Point(0.0651530771650466, 0.355051025721682, 0);
    /**
     * Point used as intersection in some tests
     */
    private static final Point INTERSECTION2 = new Point(1.53484692283495, 0.844948974278318, 0);
    /**
     * Expected list of intersections in some tests
     */
    private static final List<Point> EXPECTED1 = List.of(INTERSECTION1, INTERSECTION2);
    /**
     * Sphere used in some tests
     */
    private static final Sphere SPHERE = new Sphere(P100, 1d);
    /**
     * Error message for sphere intersection failures
     */
    private static final String ERROR_SPHERE_INTERSECTION = "Wrong sphere intersection result";


    /**
     * Default constructor (for JavaDoc completeness)
     */
    SphereTests() {
    }

    /**
     * Test method for {@link Sphere#getNormal(Point)}.
     * Verifies that the normal vector is unit length and correctly
     * points from the sphere center to the surface point.
     */
    @Test
    void testGetNormal() {

        // ============ Equivalence Partitions Tests ==============

        Sphere sphere = new Sphere(new Point(0, 0, 0), 1);

        // TC01: any point on sphere surface
        Point pointOnSphere = new Point(0, 0, 1);

        Vector result = sphere.getNormal(pointOnSphere);

        // Ensure |n| = 1
        assertEquals(1, result.length(), DELTA, ERROR);

        // Ensure correct direction (from center outward)
        assertEquals(Vector.AXIS_Z, result, ERROR);
    }


    /**
     * Test method for {@link geometries.impl.Sphere#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray's line is outside the sphere (0 points)
        Ray ray1 = new Ray(P01, V110);

        var result1 = SPHERE.findIntersections(ray1);

        assertNull(result1, ERROR_SPHERE_INTERSECTION);

        // EP02: Ray starts before and crosses the sphere (2 points)
        Ray ray2 = new Ray(P01, V311);

        var result2 = SPHERE.findIntersections(ray2);

        assertNotNull(result2, ERROR_SPHERE_INTERSECTION);
        assertEquals(2, result2.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(EXPECTED1, result2, ERROR_SPHERE_INTERSECTION);

        // EP03: Ray starts inside the sphere (1 point)

        Ray ray3 = new Ray(new Point(1, 0.5, 0), V100);

        var result3 = SPHERE.findIntersections(ray3);

        assertNotNull(result3, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result3.size(), ERROR_SPHERE_INTERSECTION);

        // EP04: Ray starts after the sphere (0 points)

        Ray ray4 = new Ray(new Point(3, 0, 0), V100);

        var result4 = SPHERE.findIntersections(ray4);

        assertNull(result4, ERROR_SPHERE_INTERSECTION);


        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray's line crosses the sphere (but not the center)

        // BV11: Ray starts at sphere and goes inside (1 point)
        Ray ray5 = new Ray(new Point(0, 0, 0), V100);

        var result5 = SPHERE.findIntersections(ray5);

        assertNotNull(result5, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result5.size(), ERROR_SPHERE_INTERSECTION);

        // BV12: Ray starts at sphere and goes outside (0 points)
        Ray ray6 = new Ray(new Point(0, 0, 0), new Vector(-1, -1, 0));

        var result6 = SPHERE.findIntersections(ray6);

        assertNull(result6, ERROR_SPHERE_INTERSECTION);


        // **** Group 2: Ray's line goes through the center

        // BV21: Ray starts before the sphere (2 points)
        Ray ray7 = new Ray(new Point(-1, 0, 0), V100);

        var result7 = SPHERE.findIntersections(ray7);

        assertNotNull(result7, ERROR_SPHERE_INTERSECTION);
        assertEquals(2, result7.size(), ERROR_SPHERE_INTERSECTION);

        // BV22: Ray starts at sphere and goes inside (1 point)
        Ray ray8 = new Ray(new Point(0, 0, 0), V100);

        var result8 = SPHERE.findIntersections(ray8);

        assertNotNull(result8, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result8.size(), ERROR_SPHERE_INTERSECTION);

        // BV23: Ray starts inside (1 point)
        Ray ray9 = new Ray(new Point(1, 0.5, 0), V100);

        var result9 = SPHERE.findIntersections(ray9);

        assertNotNull(result9, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result9.size(), ERROR_SPHERE_INTERSECTION);

        // BV24: Ray starts at the center (1 point)
        Ray ray10 = new Ray(P100, V100);

        var result10 = SPHERE.findIntersections(ray10);

        assertNotNull(result10, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result10.size(), ERROR_SPHERE_INTERSECTION);

        // BV25: Ray starts at sphere and goes outside (0 points)
        Ray ray11 = new Ray(new Point(2, 0, 0), V100);

        var result11 = SPHERE.findIntersections(ray11);

        assertNull(result11, ERROR_SPHERE_INTERSECTION);

        // BV26: Ray starts after sphere (0 points)
        Ray ray12 = new Ray(new Point(3, 0, 0), V100);

        var result12 = SPHERE.findIntersections(ray12);

        assertNull(result12, ERROR_SPHERE_INTERSECTION);


        // **** Group 3: Ray's line is tangent to the sphere (1 or 0 points)

        // BV31: Ray starts before the tangent point (1 point)
        var result31 = SPHERE.findIntersections(new Ray(new Point(0, 1, 0), V100));

        assertNotNull(result31, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result31.size(), ERROR_SPHERE_INTERSECTION);


        // BV32: Ray starts at the tangent point (0 points)
        // starts exactly on surface and goes outside → no intersection ahead
        assertNull(SPHERE.findIntersections(new Ray(new Point(1, 1, 0), V100)), ERROR_SPHERE_INTERSECTION);


        // BV33: Ray starts after the tangent point (0 points)
        assertNull(SPHERE.findIntersections(new Ray(new Point(2, 1, 0), V100)), ERROR_SPHERE_INTERSECTION);


        // **** Group 4: Special cases

        // BV41: Ray is outside and orthogonal to center line
        assertNull(SPHERE.findIntersections(new Ray(new Point(0, 2, 0), V100)), ERROR_SPHERE_INTERSECTION);

        // BV42: Ray starts inside and is orthogonal to center line (1 point)
        Ray ray13 = new Ray(new Point(1, 0.5, 0), new Vector(0, 1, 0));

        var result13 = SPHERE.findIntersections(ray13);

        assertNotNull(result13, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result13.size(), ERROR_SPHERE_INTERSECTION);
    }
}