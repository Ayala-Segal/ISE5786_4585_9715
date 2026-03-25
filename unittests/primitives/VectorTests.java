package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Vector}.
 * The tests verify:
 * <ul>
 * <li>Constructor</li>
 * <li>{@link Vector#add(Vector)}</li>
 * <li>{@link Vector#scale(double)}</li>
 * <li>{@link Vector#dotProduct(Vector)}</li>
 * <li>{@link Vector#crossProduct(Vector)}</li>
 * <li>{@link Vector#lengthSquared()}</li>
 * <li>{@link Vector#length()}</li>
 * <li>{@link Vector#normalize()}</li>
 * </ul>
 * Tests follow Equivalence Partitions (TC) and Boundary Values (BVA).
 */
class VectorTests {

    /** Delta for double comparison */
    private static final double DELTA = 1e-6;

    /** Error message */
    private static final String ERROR_ADD = "ERROR: add() result is incorrect";
    private static final String ERROR_SUBTRACT = "ERROR: subtract() result is incorrect";
    private static final String ERROR_SCALE = "ERROR: scale() result is incorrect";
    private static final String ERROR_DOT = "ERROR: dotProduct() result is incorrect";
    private static final String ERROR_CROSS = "ERROR: crossProduct() result is incorrect";
    private static final String ERROR_LENGTH = "ERROR: length calculation is incorrect";
    private static final String ERROR_NORMALIZE = "ERROR: normalize() result is incorrect";
    private static final String ERROR_EXCEPTION = "ERROR: expected exception was not thrown";
    private static final String ERROR_ZERO_VECTOR = "ERROR: zero vector should not be allowed";

    VectorTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Test constructor of {@link Vector}
     */
    @Test
    void testConstructor() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: create valid vector
        assertDoesNotThrow(() -> new Vector(1, 2, 3), ERROR_EXCEPTION);

        // =============== Boundary Values Tests ==================

        // TC11: zero vector should throw exception
        assertThrows(IllegalArgumentException.class,
                () -> new Vector(0, 0, 0),
                ERROR_ZERO_VECTOR);
    }

    /**
     * Test {@link Vector#add(Vector)} method
     */
    @Test
    void testAdd() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: regular addition
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(2, 3, 4);
        assertDoesNotThrow(() -> v1.add(v2), ERROR_EXCEPTION);
        assertEquals(new Vector(3, 5, 7), v1.add(v2), ERROR_ADD);

        // =============== Boundary Values Tests ==================

        //TC11: addition that results zero vector
        Vector v3 = new Vector(1, 2, 3);
        Vector v4 = new Vector(-1, -2, -3);
        assertThrows(IllegalArgumentException.class, () -> v3.add(v4), ERROR_ZERO_VECTOR);
    }

    /**
     * Test {Vector#subtract(Vector)} method
     */
    @Test
    void testSubtract() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: regular subtraction
        Vector v1 = new Vector(3, 5, 7);
        Vector v2 = new Vector(1, 2, 3);
        assertEquals(new Vector(2, 3, 4), v1.subtract(v2), ERROR_SUBTRACT);

        // =============== Boundary Values Tests ==================

        // TC11: subtraction that results zero vector
        Vector v3 = new Vector(1, 2, 3);
        assertThrows(IllegalArgumentException.class, () -> v3.subtract(v3), ERROR_SUBTRACT);
    }

    /**
     * Test {@link Vector#scale(double)} method
     */
    @Test
    void testScale() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: scaling by non-zero scalar
        Vector v = new Vector(1, 2, 3);
        assertDoesNotThrow(() -> v.scale(2), ERROR_EXCEPTION);
        assertEquals(new Vector(2, 4, 6), v.scale(2), ERROR_SCALE);

        // =============== Boundary Values Tests ==================

        // TC11: scaling by zero should throw exception
        assertThrows(IllegalArgumentException.class,
                () -> v.scale(0),
                ERROR_SCALE);
    }

    /**
     * Test {@link Vector#dotProduct(Vector)} method
     */
    @Test
    void testDotProduct() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: general dot product
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(2, 3, 4);
        assertDoesNotThrow(() -> v1.dotProduct(v2), ERROR_DOT);
        assertEquals(20, v1.dotProduct(v2), DELTA, ERROR_DOT);

        // TC02: dotProduct symmetry (v1·v2 = v2·v1)
        assertEquals(v1.dotProduct(v2), v2.dotProduct(v1), DELTA, ERROR_DOT);

        // =============== Boundary Values Tests ==================

        // TC11: orthogonal vectors
        Vector v3 = new Vector(1, 0, 0);
        Vector v4 = new Vector(0, 1, 0);
        assertEquals(0, v3.dotProduct(v4), DELTA, ERROR_DOT);
    }

    /**
     * Test {@link Vector#crossProduct(Vector)} method
     */
    @Test
    void testCrossProduct() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: regular cross product
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(0, 3, -2);
        assertDoesNotThrow(() -> v1.crossProduct(v2), ERROR_EXCEPTION);
        Vector vr = v1.crossProduct(v2);

        // check length
        assertEquals(v1.length() * v2.length(), vr.length(), DELTA, ERROR_CROSS);

        // check orthogonality
        assertEquals(0, vr.dotProduct(v1), DELTA, ERROR_CROSS);
        assertEquals(0, vr.dotProduct(v2), DELTA, ERROR_CROSS);

        // =============== Boundary Values Tests ==================

        //TC11: parallel vectors → zero vector → exception
        Vector v3 = new Vector(1, 2, 3);
        Vector v4 = new Vector(2, 4, 6);
        assertThrows(IllegalArgumentException.class,
                () -> v3.crossProduct(v4),
                ERROR_ZERO_VECTOR);
    }

    /**
     * Test {@link Vector#lengthSquared()} method
     */
    @Test
    void testLengthSquared() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: normal vector
        Vector v = new Vector(1, 2, 2);
        assertEquals(9, v.lengthSquared(), DELTA, ERROR_LENGTH);
    }

    /**
     * Test {@link Vector#length()} method
     */
    @Test
    void testLength() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: normal vector
        Vector v = new Vector(0, 3, 4);
        assertEquals(5, v.length(), DELTA, ERROR_LENGTH);
    }

    /**
     * Test {@link Vector#normalize()} method
     */
    @Test
    void testNormalize() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: normalize vector
        Vector v = new Vector(1, 2, 3);
        assertDoesNotThrow(() -> v.normalize(), ERROR_EXCEPTION);
        Vector n = v.normalize();

        // check length = 1
        assertEquals(1, n.length(), DELTA, ERROR_NORMALIZE);

        // check parallel
        assertThrows(IllegalArgumentException.class,
                () -> v.crossProduct(n),
                ERROR_NORMALIZE);
    }
}