package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Triangle}.
 * The tests verify correctness of {@link Triangle#getNormal(Point)}.
 *
 * Tests follow the methodology of:
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class TriangleTests {

    /** Default constructor to satisfy JavaDoc generator */
    TriangleTests() { /* to satisfy JavaDoc generator */ }

    /** Delta for comparing double values */
    private static final double DELTA = 1e-6;

    /** Error message for assertions */
    private static final String ERROR = "ERROR: wrong normal result";

    // ============ Test Points ============

    /** First triangle vertex */
    private static final Point P1 = new Point(0, 0, 1);

    /** Second triangle vertex */
    private static final Point P2 = new Point(1, 0, 0);

    /** Third triangle vertex */
    private static final Point P3 = new Point(0, 1, 0);

    /**
     * Test method for {@link Triangle#getNormal(Point)}.
     * Verifies correct normal vector computation.
     */
    @Test
    void testGetNormal() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: Valid triangle - check normal is unit vector and orthogonal
        Triangle triangle = new Triangle(P1, P2, P3);

        Point testPoint = P1;

        Vector result = triangle.getNormal(testPoint);

        // Act + Assert: method does not throw exception
        assertDoesNotThrow(() -> triangle.getNormal(testPoint),
                "getNormal() threw unexpected exception");

        // Assert: normal is unit vector
        assertEquals(1, result.length(), DELTA, ERROR);

        // Assert: orthogonal to two edges
        Vector edge1 = P2.subtract(P1);
        Vector edge2 = P3.subtract(P1);

        assertEquals(0, result.dotProduct(edge1), DELTA, ERROR);
        assertEquals(0, result.dotProduct(edge2), DELTA, ERROR);
    }
}