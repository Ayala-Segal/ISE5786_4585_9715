package geometries.impl;

import java.util.ArrayList;
import java.util.List;

import geometries.api.Intersectable;
import geometries.api.Intersectable.Intersection;
import primitives.Point;
import primitives.Ray;

/**
 * A composite class representing a collection of geometric objects.
 */
public class Geometries extends Intersectable {

    /**
     * List of geometries.
     */
    private final List<Intersectable> geometries = new ArrayList<>();

    /**
     * Constructs a Geometries object.
     *
     * @param geometries geometries to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds geometries to the collection.
     *
     * @param geometries geometries to add
     */
    public void add(Intersectable... geometries) {
        if (geometries != null) {
            this.geometries.addAll(List.of(geometries));
        }
    }

    /**
     * Calculates intersections for composite geometry.
     *
     * @param ray the ray
     * @return list of intersections or null
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {

        List<Intersection> result = null;

        for (Intersectable g : geometries) {
            List<Intersection> temp = g.calcIntersections(ray);

            if (temp != null && !temp.isEmpty()) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.addAll(temp);
            }
        }

        return result;
    }
}