package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Ray}.
 * The tests verify:
 * <ul>
 * <li>Constructor and getters</li>
 * </ul>
 * Tests follow Equivalence Partitions (TC) and Boundary Values (BVA).
 */
class RayTests {

    /** Delta for double comparison */
    private static final double DELTA = 1e-6;

    /** Error message */
    private static final String ERROR_ORIGIN = "ERROR: origin getter returned wrong value";
    private static final String ERROR_DIRECTION = "ERROR: direction is not normalized";
    private static final String ERROR_PARALLEL = "ERROR: direction is not parallel to original vector";
    private static final String ERROR_EXCEPTION = "ERROR: expected exception was not thrown";
    private static final String ERROR_ZERO_VECTOR = "ERROR: zero vector should not be allowed";
    RayTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Test constructor and getters
     */
    @Test
    void testConstructor() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: regular ray creation
        Point origin = new Point(1, 2, 3);
        Vector dir = new Vector(0, 3, 4);

        assertDoesNotThrow(() -> new Ray(origin, dir), ERROR_EXCEPTION);

        Ray ray = new Ray(origin, dir);

        // check origin
        assertEquals(origin, ray.origin(), ERROR_ORIGIN);

        // check normalized direction (length = 1)
        assertEquals(1, ray.direction().length(), DELTA, ERROR_DIRECTION);

        //TC02 direction is parallel to original vector → crossProduct should throw (zero vector)
        assertThrows(IllegalArgumentException.class,
                () -> ray.direction().crossProduct(dir),
                ERROR_PARALLEL);

        // =============== Boundary Values Tests ==================

        //TC11: direction already normalized
        Vector unit = new Vector(0, 0, 1);
        assertDoesNotThrow(() -> new Ray(origin, unit), ERROR_EXCEPTION);
        Ray ray2 = new Ray(origin, unit);

        assertEquals(1, ray2.direction().length(), DELTA, ERROR_DIRECTION);

        // TC12: zero vector direction → should throw exception
        assertThrows(IllegalArgumentException.class,
                () -> new Ray(origin, new Vector(0, 0, 0)),
                ERROR_ZERO_VECTOR);
    }

    @Test
    void testGetPoint() {

        Ray ray = new Ray(new Point(1, 2, 3), new Vector(1, 0, 0));

        // ============ Equivalence Partitions Tests ==============

        // EP01: t > 0
        assertEquals(new Point(3, 2, 3),
                ray.getPoint(2),
                "ERROR: getPoint() wrong result for positive t");

        // EP02: t < 0
        assertEquals(new Point(0, 2, 3),
                ray.getPoint(-1),
                "ERROR: getPoint() wrong result for negative t");

        // =============== Boundary Values Tests ==================

        // BV01: t = 0
        assertEquals(new Point(1, 2, 3),
                ray.getPoint(0),
                "ERROR: getPoint() should return origin when t = 0");
    }
}