package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Tube}.
 * Verifies correctness of {@link Tube#getNormal(Point)}.

 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class TubeTests {

    private static final double DELTA = 1e-6;
    private static final String ERROR = "ERROR: wrong normal result";

    // Tube axis along Z direction
    private static final Ray AXIS =
            new Ray(new Point(0, 0, 0), Vector.AXIS_Z);

    @Test
    void testGetNormal() {

        Tube tube = new Tube(1, AXIS);

        // ================= TC TESTS =================

        // TC01 - TC: point on tube side (forward direction of axis)
        Point p1 = new Point(1, 0, 5);
        Vector n1 = tube.getNormal(p1);

        assertEquals(1, n1.length(), DELTA, ERROR);
        assertEquals(0, n1.dotProduct(Vector.AXIS_Z), DELTA, ERROR);


        // TC02 - TC: point on tube side (backward direction of axis)
        Point p2 = new Point(0, 1, -3);
        Vector n2 = tube.getNormal(p2);

        assertEquals(1, n2.length(), DELTA, ERROR);
        assertEquals(0, n2.dotProduct(Vector.AXIS_Z), DELTA, ERROR);


        // ================= BVA TEST =================

        // TC11 - point aligned with axis projection (t = 0 plane)
        Point p3 = new Point(1, 0, 0);
        Vector n3 = tube.getNormal(p3);

        assertEquals(1, n3.length(), DELTA, ERROR);
        assertEquals(0, n3.dotProduct(Vector.AXIS_Z), DELTA, ERROR);
    }
}