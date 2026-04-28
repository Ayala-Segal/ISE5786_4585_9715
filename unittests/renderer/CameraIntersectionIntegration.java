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
 * Integration tests for validating the number of intersection points
 * between rays constructed from a camera and different geometries.
 *
 * The tests verify correct ray construction through the camera's view plane
 * and correct intersection logic for Sphere, Plane, and Triangle geometries.
 */
class CameraIntersectionIntegration {

    /** Number of columns in the view plane grid */
    private static final int COLS = 3;

    /** Number of rows in the view plane grid */
    private static final int ROWS = 3;

    /** Size of the view plane */
    private static final double SIZE = 3;

    /** Distance from the camera to the view plane */
    private static final double DIST = 1;

    /**
     * Base camera positioned at origin looking towards negative Z axis.
     */
    private static final Camera BASE_CAMERA = Camera.getBuilder()
            .setLocation(Point.ZERO)
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpSize(SIZE, SIZE)
            .setVpDistance(DIST)
            .setResolution(COLS, ROWS)
            .build();

    /**
     * Slightly offset camera to test additional ray configurations.
     */
    private static final Camera OFFSET_CAMERA = Camera.getBuilder()
            .setLocation(new Point(0, 0, 0.5))
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpSize(SIZE, SIZE)
            .setVpDistance(DIST)
            .setResolution(COLS, ROWS)
            .build();

    /**
     * Tests ray-sphere intersection scenarios from different camera setups.
     */
    @Test
    void testCameraRaySphereIntegration() {
        assertIntersectionsCount(BASE_CAMERA, new Sphere(new Point(0, 0, -3), 1), 2, "Sphere TC01");
        assertIntersectionsCount(OFFSET_CAMERA, new Sphere(new Point(0, 0, -2.5), 2.5), 18, "Sphere TC02");
        assertIntersectionsCount(OFFSET_CAMERA, new Sphere(new Point(0, 0, -2), 2), 10, "Sphere TC03");
        assertIntersectionsCount(OFFSET_CAMERA, new Sphere(new Point(0, 0, -1), 4), 9, "Sphere TC04");
        assertIntersectionsCount(BASE_CAMERA, new Sphere(new Point(0, 0, 1), 0.5), 0, "Sphere TC05");
    }

    /**
     * Tests ray-plane intersection scenarios from the camera.
     */
    @Test
    void testCameraRayPlaneIntegration() {
        assertIntersectionsCount(BASE_CAMERA, new Plane(new Point(0, 0, -5), new Vector(0, 0, 1)), 9, "Plane TC01");
        assertIntersectionsCount(BASE_CAMERA, new Plane(new Point(0, 0, -5), new Vector(0, 1, 2)), 9, "Plane TC02");
        assertIntersectionsCount(BASE_CAMERA, new Plane(new Point(0, 0, -5), new Vector(0, 1, 1)), 6, "Plane TC03");
    }

    /**
     * Tests ray-triangle intersection scenarios from the camera.
     */
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
     * Helper method that asserts the expected number of intersection points
     * for a given geometry and camera configuration.
     *
     * @param cam      the camera used to construct rays
     * @param geo      the geometry to test intersections against
     * @param expected expected number of intersection points
     * @param name     test case identifier
     */
    private void assertIntersectionsCount(Camera cam, Intersectable geo,
                                          int expected, String name) {
        int result = countAllIntersections(cam, geo);
        assertEquals(expected, result, name);
    }

    /**
     * Counts total intersection points between rays from all pixels
     * and the given geometry.
     *
     * @param cam camera used to construct rays
     * @param geo geometry to test
     * @return total number of intersection points
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
     * Computes number of intersection points for a single pixel.
     *
     * @param cam camera used to construct ray
     * @param geo geometry to test
     * @param x   pixel column index
     * @param y   pixel row index
     * @return number of intersection points for that pixel
     */
    private int intersectionsPerPixel(Camera cam, Intersectable geo, int x, int y) {
        List<Point> points = geo.findIntersections(cam.constructRay(x, y));
        return (points == null) ? 0 : points.size();
    }
}