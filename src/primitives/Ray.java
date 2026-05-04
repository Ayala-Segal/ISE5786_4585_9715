package primitives;

import java.util.List;
import java.util.Objects;

/**
 * Represents a ray (half-line) in three-dimensional space.
 * <p>
 * A ray is defined by an origin point and a direction vector.
 * The direction vector is automatically normalized upon creation.
 * </p>
 * <p>
 * Rays are commonly used in computational geometry and ray tracing algorithms.
 * </p>
 *
 */
public class Ray {
    /**
     * The starting point of the ray.
     */
    private final Point _origin;

    /**
     * The normalized direction vector of the ray.
     */
    private final Vector _direction;

    /**
     * Constructs a ray with the specified origin and direction.
     * The direction vector will be normalized automatically.
     *
     * @param origin    the starting point of the ray
     * @param direction the direction vector of the ray
     */
    public Ray(Point origin, Vector direction) {
        this._origin = origin;
        this._direction = direction.normalize();
    }

    /**
     * Returns the origin point of the ray.
     *
     * @return the starting point of the ray
     */
    public Point origin() {
        return _origin;
    }

    /**
     * Returns the direction vector of the ray.
     * The vector is normalized.
     *
     * @return the direction vector of the ray
     */
    public Vector direction() {
        return _direction;
    }

    /**
     * Returns a point on the ray at distance t from the origin.
     * The value t can be positive, negative or zero.
     *
     * @param t scalar distance from the ray origin
     * @return point on the ray
     */
    public Point getPoint(double t) {
        try {
            return _origin.add(_direction.scale(t));
        } catch (IllegalArgumentException e) {
            return _origin;
        }
    }

    /**
     * Finds the closest intersection point to the ray origin.
     * <p>
     * The method assumes the list contains intersection points.
     * If the list is null, null is returned.
     * The closest point is determined using squared distance for efficiency.
     * </p>
     *
     * @param points list of intersection points (may be null)
     * @return the closest point to the ray origin, or null if list is null
     */
    Point findClosestPoint(List<Point> points) {

        if (points == null) {
            return null;
        }

        Point closestPoint = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for (Point p : points) {

            double distance = _origin.distanceSquared(p);

            if (distance < minDistance) {
                minDistance = distance;
                closestPoint = p;
            }
        }

        return closestPoint;
    }

    @Override
    public String toString() {
        return "Ray: origin: " + _origin + ", direction: " + _direction;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ray other = (Ray) obj;
        return _origin.equals(other._origin) && _direction.equals(other._direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_origin, _direction);
    }
}
