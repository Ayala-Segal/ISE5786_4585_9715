package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Point}.
 * The tests verify:
 * <ul>
 * <li>{@link Point#subtract(Point)}</li>
 * <li>{@link Point#add(Vector)}</li>
 * <li>{@link Point#distanceSquared(Point)}</li>
 * <li>{@link Point#distance(Point)}</li>
 * </ul>
 * Tests follow Equivalence Partitions (TC) and Boundary Values (BVA).
 */
class PointTests {

    /** Delta for double comparison */
    private static final double DELTA = 1e-6;

    /** Error message */
    private static final String ERROR_SUBTRACT = "ERROR: subtract() result is incorrect";
    private static final String ERROR_ADD = "ERROR: add() result is incorrect";
    private static final String ERROR_DISTANCE_SQUARED = "ERROR: distanceSquared() result is incorrect";
    private static final String ERROR_DISTANCE = "ERROR: distance() result is incorrect";
    private static final String ERROR_EXCEPTION = "ERROR: expected exception was not thrown";
    PointTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Test subtract method
     */
    @Test
    void testSubtract() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: regular subtraction
        Point p1 = new Point(2, 3, 4);
        Point p2 = new Point(1, 1, 1);
        assertDoesNotThrow(() -> p1.subtract(p2), ERROR_SUBTRACT);
        assertEquals(new Vector(1, 2, 3), p1.subtract(p2), ERROR_SUBTRACT);

        // TC02: negative result
        Point p11 = new Point(1,1,1);
        Point p21 = new Point(2,2,2);
        assertEquals(new Vector(-1,-1,-1), p11.subtract(p21), ERROR_SUBTRACT);

        // =============== Boundary Values Tests ==================

        // TC11: subtract same point → zero vector → exception
        Point p3 = new Point(1, 2, 3);
        assertThrows(IllegalArgumentException.class, () -> p3.subtract(p3), ERROR_SUBTRACT);
    }

    /**
     * Test add method
     */
    @Test
    void testAdd() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: regular addition
        Point p = new Point(1, 2, 3);
        Vector v = new Vector(-1, -2, -3);
        assertEquals(Point.ZERO, p.add(v), ERROR_ADD);

        // =============== Boundary Values Tests ==================

    }

    /**
     * Test distanceSquared method
     */
    @Test
    void testDistanceSquared() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: general case
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 3, 4);
        assertEquals(3, p1.distanceSquared(p2), DELTA, ERROR_DISTANCE_SQUARED);

        // =============== Boundary Values Tests ==================

        // TC11: same point → distance = 0
        assertEquals(0, p1.distanceSquared(p1), DELTA, ERROR_DISTANCE_SQUARED);
        //Symmetry test
        assertEquals(p1.distanceSquared(p2), p2.distanceSquared(p1), DELTA, ERROR_DISTANCE_SQUARED);
    }

    /**
     * Test distance method
     */
    @Test
    void testDistance() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: general case
        Point p1 = new Point(0, 0, 0);
        Point p2 = new Point(0, 3, 4);
        assertEquals(5, p1.distance(p2), DELTA, ERROR_DISTANCE);
        //Symmetry test
        assertEquals( p1.distance(p2), p2.distance(p1), DELTA, ERROR_DISTANCE);

        // =============== Boundary Values Tests ==================

        // TC11: same point → distance = 0
        assertEquals(0, p1.distance(p1), DELTA, ERROR_DISTANCE);
    }
}