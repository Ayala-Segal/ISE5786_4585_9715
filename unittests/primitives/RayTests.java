package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class Ray
 */
class RayTests {

    /** Delta for double comparison */
    private static final double DELTA = 1e-6;

    /** Error message */
    private static final String ERROR = "ERROR: wrong result";

    /**
     * Test constructor and getters
     */
    @Test
    void testConstructor() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: regular ray creation
        Point origin = new Point(1, 2, 3);
        Vector dir = new Vector(0, 3, 4);
        Ray ray = new Ray(origin, dir);

        // check origin
        assertEquals(origin, ray.origin(), ERROR);

        // check normalized direction (length = 1)
        assertEquals(1, ray.direction().length(), DELTA, ERROR);

        // check direction is parallel to original
        assertThrows(IllegalArgumentException.class,
                () -> ray.direction().crossProduct(dir),
                ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: direction already normalized
        Vector unit = new Vector(0, 0, 1);
        Ray ray2 = new Ray(origin, unit);

        assertEquals(1, ray2.direction().length(), DELTA, ERROR);

        // BV02: zero vector direction → should throw exception
        assertThrows(IllegalArgumentException.class,
                () -> new Ray(origin, new Vector(0, 0, 0)),
                ERROR);
    }
}