package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Vector}
 */
class VectorTests {

    /** Delta for double comparison */
    private static final double DELTA = 1e-6;

    /** Error message */
    private static final String ERROR = "ERROR: wrong result";

    /**
     * Test constructor of {@link Vector}
     */
    @Test
    void testConstructor() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: create valid vector
        assertDoesNotThrow(() -> new Vector(1, 2, 3), ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: zero vector should throw exception
        assertThrows(IllegalArgumentException.class,
                () -> new Vector(0, 0, 0),
                ERROR);
    }

    /**
     * Test {@link Vector#add(Vector)} method
     */
    @Test
    void testAdd() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: regular addition
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(2, 3, 4);
        assertEquals(new Vector(3, 5, 7), v1.add(v2), ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: addition that results zero vector
        Vector v3 = new Vector(1, 2, 3);
        Vector v4 = new Vector(-1, -2, -3);
        assertThrows(IllegalArgumentException.class,
                () -> v3.add(v4),
                ERROR);
    }

    /**
     * Test {@link Vector#subtract(Vector)} method
     */
    @Test
    void testSubtract() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: regular subtraction
        Vector v1 = new Vector(3, 5, 7);
        Vector v2 = new Vector(1, 2, 3);
        assertEquals(new Vector(2, 3, 4), v1.subtract(v2), ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: subtraction that results zero vector
        Vector v3 = new Vector(1, 2, 3);
        Vector v4 = new Vector(1, 2, 3);
        assertThrows(IllegalArgumentException.class,
                () -> v3.subtract(v4),
                ERROR);
    }

    /**
     * Test {@link Vector#scale(double)} method
     */
    @Test
    void testScale() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: scaling by non-zero scalar
        Vector v = new Vector(1, 2, 3);
        assertEquals(new Vector(2, 4, 6), v.scale(2), ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: scaling by zero should throw exception
        assertThrows(IllegalArgumentException.class,
                () -> v.scale(0),
                ERROR);
    }

    /**
     * Test {@link Vector#dotProduct(Vector)} method
     */
    @Test
    void testDotProduct() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: general dot product
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(0, 3, -2);
        assertEquals(0, v1.dotProduct(v2), DELTA, ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: orthogonal vectors
        Vector v3 = new Vector(1, 0, 0);
        Vector v4 = new Vector(0, 1, 0);
        assertEquals(0, v3.dotProduct(v4), DELTA, ERROR);
    }

    /**
     * Test {@link Vector#crossProduct(Vector)} method
     */
    @Test
    void testCrossProduct() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: regular cross product
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(0, 3, -2);
        Vector vr = v1.crossProduct(v2);

        // check length
        assertEquals(v1.length() * v2.length(), vr.length(), DELTA, ERROR);

        // check orthogonality
        assertEquals(0, vr.dotProduct(v1), DELTA, ERROR);
        assertEquals(0, vr.dotProduct(v2), DELTA, ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: parallel vectors → zero vector → exception
        Vector v3 = new Vector(1, 2, 3);
        Vector v4 = new Vector(2, 4, 6);
        assertThrows(IllegalArgumentException.class,
                () -> v3.crossProduct(v4),
                ERROR);
    }

    /**
     * Test {@link Vector#lengthSquared()} method
     */
    @Test
    void testLengthSquared() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: normal vector
        Vector v = new Vector(1, 2, 2);
        assertEquals(9, v.lengthSquared(), DELTA, ERROR);
    }

    /**
     * Test {@link Vector#length()} method
     */
    @Test
    void testLength() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: normal vector
        Vector v = new Vector(0, 3, 4);
        assertEquals(5, v.length(), DELTA, ERROR);
    }

    /**
     * Test {@link Vector#normalize()} method
     */
    @Test
    void testNormalize() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: normalize vector
        Vector v = new Vector(1, 2, 3);
        Vector n = v.normalize();

        // check length = 1
        assertEquals(1, n.length(), DELTA, ERROR);

        // check parallel
        assertThrows(IllegalArgumentException.class,
                () -> v.crossProduct(n),
                ERROR);
    }
}