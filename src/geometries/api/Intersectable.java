package geometries.api;

import java.util.List;

import primitives.Point;
import primitives.Ray;

/**
 * An abstract base class representing all geometric objects
 * that can be intersected by a {@link Ray}.
 */
public abstract class Intersectable {

    /**
     * Finds intersections as full Intersection objects.
     *
     * @param ray the ray to intersect with the geometry
     * @return list of intersections or null if none
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);

    /**
     * Public template method (NVI pattern).
     * Calls the helper implemented by subclasses.
     *
     * @param ray the ray to intersect with the geometry
     * @return list of intersections or null if none
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }

    /**
     * Finds intersection points between a given {@link Ray}
     * and the geometric object.
     * Implemented using calcIntersections (NVI pattern).
     *
     * @param ray the ray to intersect with the geometry
     * @return a list of intersection points, or {@code null}
     */
    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);

        return intersections == null ? null
                : intersections.stream()
                .map(intersection -> intersection.point)
                .toList();
    }

    /**
     * Represents a single intersection between a ray and a geometry.
     */
    public static final class Intersection {

        public final Intersectable geometry;
        public final Point point;

        public Intersection(Intersectable geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
        }

        @Override
        public String toString() {
            return "Intersection{" +
                    "geometry=" + geometry +
                    ", point=" + point +
                    '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Intersection other)) return false;

            return this.geometry == other.geometry
                    && java.util.Objects.equals(this.point, other.point);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(System.identityHashCode(geometry), point);
        }
    }
}