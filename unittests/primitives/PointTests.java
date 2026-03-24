package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class Point
 */
class PointTests {

    /** Delta for double comparison */
    private static final double DELTA = 1e-6;

    /** Error message */
    private static final String ERROR = "ERROR: wrong result";

    /**
     * Test subtract method
     */
    @Test
    void testSubtract() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: regular subtraction
        Point p1 = new Point(2, 3, 4);
        Point p2 = new Point(1, 1, 1);
        assertEquals(new Vector(1, 2, 3), p1.subtract(p2), ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: subtract same point → zero vector → exception
        Point p3 = new Point(1, 2, 3);
        assertThrows(IllegalArgumentException.class,
                () -> p3.subtract(p3),
                ERROR);
    }

    /**
     * Test add method
     */
    @Test
    void testAdd() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: regular addition
        Point p = new Point(1, 2, 3);
        Vector v = new Vector(-1, -2, -3);
        assertEquals(Point.ZERO, p.add(v), ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: adding zero vector is not possible (should throw)
        assertThrows(IllegalArgumentException.class,
                () -> p.add(new Vector(0, 0, 0)),
                ERROR);
    }

    /**
     * Test distanceSquared method
     */
    @Test
    void testDistanceSquared() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: general case
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 3, 4);
        assertEquals(3, p1.distanceSquared(p2), DELTA, ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: same point → distance = 0
        assertEquals(0, p1.distanceSquared(p1), DELTA, ERROR);
    }

    /**
     * Test distance method
     */
    @Test
    void testDistance() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: general case
        Point p1 = new Point(0, 0, 0);
        Point p2 = new Point(0, 3, 4);
        assertEquals(5, p1.distance(p2), DELTA, ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: same point → distance = 0
        assertEquals(0, p1.distance(p1), DELTA, ERROR);
    }
}