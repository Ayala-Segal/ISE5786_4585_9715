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
     * Finds intersection points between a given {@link Ray}
     * and the geometric object.
     *
     * @param ray the ray to intersect with the geometry
     * @return a list of intersection points, or {@code null}
     *         if there are no intersections
     */
    public abstract List<Point> findIntersections(Ray ray);
}
