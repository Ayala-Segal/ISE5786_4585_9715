package geometries.impl;
import java.util.ArrayList;
import java.util.List;
import geometries.api.Intersectable;
import primitives.Point;
import primitives.Ray;

 /* *
 * A composite class representing a collection of geometric objects.
 * <p>
 * This class implements the Composite design pattern and allows treating
 * multiple {@link Intersectable} objects as a single entity.
 * </p>
 * <p>
 * It stores a list of geometries and enables adding new geometries
 * and performing operations (such as intersection calculations)
 * on all of them.
 * </p>
 */
public class Geometries extends Intersectable {

    /**
     * The list of geometries in the composite.
     */
    private final List<Intersectable> geometries = new ArrayList<>();

    /**
     * Constructs a Geometries object with the given geometries.
     * <p>
     * The geometries are added using the {@link #add(Intersectable...)} method.
     * </p>
     *
     * @param geometries geometries to be added to the collection
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds one or more geometries to the collection.
     * <p>
     * Uses existing Java methods to avoid redundant logic (DRY principle).
     * </p>
     *
     * @param geometries geometries to add
     */
    public void add(Intersectable... geometries) {
        if (geometries != null) {
            this.geometries.addAll(List.of(geometries));
        }
    }

    @Override
    public List<Point> findIntersections(Ray ray) {

        List<Point> result = null;

        for (Intersectable g : geometries) {
            List<Point> temp = g.findIntersections(ray);

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