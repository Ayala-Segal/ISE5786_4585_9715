package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class Tube
 */
class TubeTests {

    private static final double DELTA = 1e-6;
    private static final String ERROR = "ERROR: wrong result";

    /**
     * Test getNormal(Point)
     */
    @Test
    void testGetNormal() {

        Ray axis = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Tube tube = new Tube(1, axis);

        // ============ Equivalence Partitions Tests ==============

        // EP01: point on tube (side, general case)
        Point p1 = new Point(1, 0, 5);
        Vector n1 = tube.getNormal(p1);

        assertEquals(1, n1.length(), DELTA, ERROR);
        assertEquals(new Vector(1, 0, 0), n1, ERROR);

        // EP02: another side point
        Point p2 = new Point(0, 1, -3);
        Vector n2 = tube.getNormal(p2);

        assertEquals(1, n2.length(), DELTA, ERROR);
        assertEquals(new Vector(0, 1, 0), n2, ERROR);

        // =============== Boundary Values Tests ==================

        // BV01: point מול ראש הציר (t = 0)
        Point p3 = new Point(1, 0, 0);
        Vector n3 = tube.getNormal(p3);

        assertEquals(1, n3.length(), DELTA, ERROR);
        assertEquals(new Vector(1, 0, 0), n3, ERROR);
    }
}