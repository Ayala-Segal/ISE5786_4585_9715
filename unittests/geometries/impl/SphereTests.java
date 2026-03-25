package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

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

    /** Delta value for comparing double precision values */
    private static final double DELTA = 1e-6;

    /** Error message for wrong normal calculation */
    private static final String ERROR = "ERROR: wrong normal";

    /**
     * Default constructor (for JavaDoc completeness)
     */
    SphereTests() { }

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
}