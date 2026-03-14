package geometries.api;

import primitives.Point;
import primitives.Vector;

public abstract class Geometry {

    /**
     * Returns the normal vector to the geometry at a given point.
     *
     * @param point the point on the surface
     * @return the normal vector at the point
     */
    public Vector getNormal(Point point) {
        return null;
    }


}
