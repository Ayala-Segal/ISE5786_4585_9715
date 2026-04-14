package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Triangle}.
 * The tests verify correctness of {@link Triangle#getNormal(Point)}.
 *
 * Tests follow the methodology of:
 * Equivalence Partitions (EP).
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

    /** Point (0,0,0) used in tests */
    private static final Point P000 = new Point(0, 0, 0);

    /** Point (1,0,0) used in tests */
    private static final Point P100 = new Point(1, 0, 0);

    /** Point (0,1,0) used in tests */
    private static final Point P010 = new Point(0, 1, 0);

    /** Point (0,0,1) used in tests */
    private static final Point P001 = new Point(0, 0, 1);

    /** Triangle in XY plane */
    private static final Triangle TRIANGLE =
            new Triangle(P000, P100, P010);

    /** Error message */
    private static final String ERROR_TRIANGLE_INTERSECTION = "Wrong triangle intersection result";

    /**
     * Test method for {@link geometries.impl.Triangle#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects plane and point is inside triangle (1 point)
        Ray ray1 = new Ray(P001, new Vector(0.2, 0.2, -1));
        var result1 = TRIANGLE.findIntersections(ray1);
        assertNotNull(result1, ERROR_TRIANGLE_INTERSECTION);
        assertEquals(1, result1.size(), ERROR_TRIANGLE_INTERSECTION);

        // EP02: Ray intersects plane but point is outside triangle (against edge) (0 points)
        Ray ray2 = new Ray(P001, new Vector(1, 1, -1));
        var result2 = TRIANGLE.findIntersections(ray2);
        assertNull(result2, ERROR_TRIANGLE_INTERSECTION);

        // EP03: Ray intersects plane but point is outside triangle (against vertex) (0 points)
        Ray ray3 = new Ray(P001, new Vector(-1, -1, -1));
        var result3 = TRIANGLE.findIntersections(ray3);
        assertNull(result3, ERROR_TRIANGLE_INTERSECTION);


        // =============== Boundary Values Tests ==================

        // BV01: Intersection on triangle edge (0 points)
        Ray ray4 = new Ray(P001, new Vector(0.5, 0, -1));
        var result4 = TRIANGLE.findIntersections(ray4);
        assertNull(result4, ERROR_TRIANGLE_INTERSECTION);

        // BV02: Intersection on vertex (0 points)
        Ray ray5 = new Ray(P001, new Vector(0, 0, -1));
        var result5 = TRIANGLE.findIntersections(ray5);
        assertNull(result5, ERROR_TRIANGLE_INTERSECTION);

        // BV03: Intersection on edge continuation (0 points)
        Ray ray6 = new Ray(P001, new Vector(2, 0, -1));
        var result6 = TRIANGLE.findIntersections(ray6);
        assertNull(result6, ERROR_TRIANGLE_INTERSECTION);
    }
}