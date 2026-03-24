package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class Sphere
 */
class SphereTests {

    private static final double DELTA = 1e-6;
    private static final String ERROR = "ERROR: wrong result";

    /**
     * Test getNormal(Point)
     */
    @Test
    void testGetNormal() {

        Sphere sphere = new Sphere(new Point(0, 0, 0), 1);

        // ============ Equivalence Partitions Tests ==============

        // EP01: point on sphere
        Point p = new Point(0, 0, 1);
        Vector n = sphere.getNormal(p);

        // length = 1
        assertEquals(1, n.length(), DELTA, ERROR);

        // direction check
        assertEquals(new Vector(0, 0, 1), n, ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: another point on sphere
        Point p2 = new Point(1, 0, 0);
        Vector n2 = sphere.getNormal(p2);

        assertEquals(1, n2.length(), DELTA, ERROR);
        assertEquals(new Vector(1, 0, 0), n2, ERROR);
    }
}