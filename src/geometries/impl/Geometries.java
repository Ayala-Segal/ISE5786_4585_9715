package geometries.impl;

import java.util.ArrayList;
import java.util.List;

import geometries.api.Intersectable;
import primitives.Point;
import primitives.Ray;

public class Geometries extends Intersectable {

    private final List<Intersectable> geometries = new ArrayList<>();

    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    public void add(Intersectable... geometries) {
        if (geometries != null) {
            for (Intersectable g : geometries) {
                if (g != null) {
                    this.geometries.add(g);
                }
            }
        }
    }

    @Override
    public List<Point> findIntersections(Ray ray) {

        if (ray == null) {
            return null;
        }

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