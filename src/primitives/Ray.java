package primitives;

import geometries.api.Intersectable.Intersection;

import java.util.List;
import java.util.Objects;

/**
 * Represents a ray (half-line) in three-dimensional space.
 */
public class Ray {

    /**
     * Small offset used to shift the shadow ray origin along the surface normal,
     * preventing self-shadowing due to numerical precision errors.
     * The value is chosen to be small enough to be imperceptible in the rendered image,
     * yet large enough to avoid false self-intersections.
     * Can be adjusted based on the scale of the scene geometry.
     */
    private final Point _origin;
    private final Vector _direction;
    private static final double DELTA = 0.1;

    public Ray(Point origin, Vector direction) {
        this._origin = origin;
        this._direction = direction.normalize();
    }

    public Ray(Point origin, Vector direction, Vector normal) {
        this._direction = direction.normalize();

        double nv = normal.dotProduct(this._direction);
        Vector delta = normal.scale(nv > 0 ? DELTA : -DELTA);

        this._origin = origin.add(delta);
    }

    public Point origin() {
        return _origin;
    }

    public Vector direction() {
        return _direction;
    }

    public Point getPoint(double t) {
        try {
            return _origin.add(_direction.scale(t));
        } catch (IllegalArgumentException e) {
            return _origin;
        }
    }

    /**
     * Finds the closest intersection to the ray origin.
     *
     * @param intersections list of intersections (may be null)
     * @return closest Intersection or null
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {

        if (intersections == null || intersections.isEmpty()) {
            return null;
        }

        Intersection closest = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for (Intersection i : intersections) {

            double distance = _origin.distanceSquared(i.point);

            if (distance < minDistance) {
                minDistance = distance;
                closest = i;
            }
        }

        return closest;
    }

    /**
     * Finds closest point from a list of points (backward compatibility).
     */
    public Point findClosestPoint(List<Point> points) {

        return points == null ? null
                : findClosestIntersection(
                points.stream()
                        .map(point -> new Intersection(null, point))
                        .toList()
        ).point;
    }

    @Override
    public String toString() {
        return "Ray: origin: " + _origin + ", direction: " + _direction;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Ray other)) return false;
        return _origin.equals(other._origin) && _direction.equals(other._direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_origin, _direction);
    }
}