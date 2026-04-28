package renderer;

import geometries.api.Intersectable;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration tests for validating intersections between camera rays and geometries.
 */
class CameraIntersectionIntegration {

    private static final int COLS = 3;
    private static final int ROWS = 3;

    private static final double SIZE = 3;
    private static final double DIST = 1;

    private static final Camera BASE_CAMERA = Camera.getBuilder()
            .setLocation(Point.ZERO)
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpSize(SIZE, SIZE)
            .setVpDistance(DIST)
            .setResolution(COLS, ROWS)
            .build();

    private static final Camera OFFSET_CAMERA = Camera.getBuilder()
            .setLocation(new Point(0, 0, 0.5))
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpSize(SIZE, SIZE)
            .setVpDistance(DIST)
            .setResolution(COLS, ROWS)
            .build();

    @Test
    void testCameraRaySphereIntegration() {
        assertIntersectionsCount(BASE_CAMERA, new Sphere(new Point(0, 0, -3), 1), 2, "Sphere TC01");
        assertIntersectionsCount(OFFSET_CAMERA, new Sphere(new Point(0, 0, -2.5), 2.5), 18, "Sphere TC02");
        assertIntersectionsCount(OFFSET_CAMERA, new Sphere(new Point(0, 0, -2), 2), 10, "Sphere TC03");
        assertIntersectionsCount(OFFSET_CAMERA, new Sphere(new Point(0, 0, -1), 4), 9, "Sphere TC04");
        assertIntersectionsCount(BASE_CAMERA, new Sphere(new Point(0, 0, 1), 0.5), 0, "Sphere TC05");
    }

    @Test
    void testCameraRayPlaneIntegration() {
        assertIntersectionsCount(BASE_CAMERA, new Plane(new Point(0, 0, -5), new Vector(0, 0, 1)), 9, "Plane TC01");
        assertIntersectionsCount(BASE_CAMERA, new Plane(new Point(0, 0, -5), new Vector(0, 1, 2)), 9, "Plane TC02");
        assertIntersectionsCount(BASE_CAMERA, new Plane(new Point(0, 0, -5), new Vector(0, 1, 1)), 6, "Plane TC03");
    }

    @Test
    void testCameraRayTriangleIntegration() {
        assertIntersectionsCount(BASE_CAMERA,
                new Triangle(
                        new Point(0, 1, -2),
                        new Point(1, -1, -2),
                        new Point(-1, -1, -2)),
                1, "Triangle TC01");

        assertIntersectionsCount(BASE_CAMERA,
                new Triangle(
                        new Point(0, 20, -2),
                        new Point(1, -1, -2),
                        new Point(-1, -1, -2)),
                2, "Triangle TC02");
    }

    /**
     * Helper method required by assignment:
     * counts intersections and asserts the result.
     */
    private void assertIntersectionsCount(Camera cam, Intersectable geo,
                                          int expected, String name) {
        int result = countAllIntersections(cam, geo);
        assertEquals(expected, result, name);
    }

    /**
     * Counts all intersection points for all pixels.
     */
    private int countAllIntersections(Camera cam, Intersectable geo) {
        int sum = 0;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                sum += intersectionsPerPixel(cam, geo, col, row);
            }
        }

        return sum;
    }

    /**
     * Counts intersections for a single pixel.
     */
    private int intersectionsPerPixel(Camera cam, Intersectable geo, int x, int y) {
        List<Point> points = geo.findIntersections(cam.constructRay(x, y));
        return (points == null) ? 0 : points.size();
    }
}