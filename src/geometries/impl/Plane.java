package geometries.impl;

public class Plane {

    private Point point;
    private Vector normal;

    public Plane(Point p1, Point p2, Point p3) {

    }

    public Plane(Point point, Vector normal) {
        this.point = point;
        this.normal = normal;
    }

}
