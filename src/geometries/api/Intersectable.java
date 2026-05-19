package geometries.api;

import java.util.List;

import lighting.LightSource;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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
     * Also serves as a cache for precomputed lighting data.
     */
    public static final class Intersection {

        public final Geometry geometry;
        public final Point         point;
        public final Material      material;

        // ── cached geometry data (set by preprocessIntersection) ─────
        /** The surface normal at the intersection point. */
        public Vector n;

        /** The direction of the incoming ray (viewer direction). */
        public Vector v;

        /** Dot product of v and normal (v · n). */
        public double vn;

        // ── cached light data (set by preprocessLightSource) ─────────
        /** The current light source being evaluated. */
        public LightSource light;

        /** The direction from the light source to the intersection point. */
        public Vector l;

        /** Dot product of l and normal (l · n). */
        public double ln;

        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;

            this.material = (geometry instanceof Geometry g)
                    ? g.getMaterial()
                    : new Material();
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