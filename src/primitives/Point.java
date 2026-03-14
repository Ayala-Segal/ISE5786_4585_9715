/**
 *
 */
package primitives;

/**
 * Represents a point in a three-dimensional Cartesian coordinate system.
 * <p>
 * A point is defined by three coordinates (x, y, z) that are internally
 * stored in a {@link Double3} object.
 * </p>
 *
 * <p>
 * The class provides basic geometric operations commonly required in
 * computational geometry and ray tracing calculations, such as:
 * <ul>
 * <li>Subtracting two points to obtain a {@link Vector}</li>
 * <li>Adding a {@link Vector} to a point to obtain a translated point</li>
 * <li>Computing the squared distance between two points</li>
 * <li>Computing the Euclidean distance between two points</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class is designed to be a lightweight geometric primitive used
 * throughout the geometry package. Most operations create new immutable
 * objects rather than modifying existing instances.
 * </p>
 *
 * @author Dan Zilberstein
 */
public class Point {

    /**
     * Internal representation of the point coordinates.
     */
    private Double3 _xyz;

    /**
     * Constant representing the origin point (0,0,0).
     */
    private static final Point ZERO = new Point(Double3.ZERO);

    /**
     * Constructs a point using explicit coordinate values.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     */
    public Point(double x, double y, double z) {
        this._xyz = new Double3(x, y, z);
    }

    /**
     * Constructs a point from an existing {@link Double3} object.
     *
     * @param xyz the coordinate triad representing the point
     */
    public Point(Double3 xyz) {
        this._xyz = xyz;
    }

    /**
     * Subtracts another point from this point.
     * <p>
     * The result represents the vector from the given point to this point.
     * </p>
     *
     * @param other the point to subtract
     * @return a new {@link Vector} representing the difference
     */
    public Vector subtract(Point other) {
        return new Vector(this._xyz.subtract(other._xyz));
    }

    /**
     * Adds a vector to this point.
     * <p>
     * The operation translates the point by the given vector and returns
     * a new point representing the resulting location.
     * </p>
     *
     * @param vector the vector to add
     * @return a new {@code Point} translated by the vector
     */
    public Point add(Vector vector) {
        return new Point(vector._xyz.add(this._xyz));
    }

    /**
     * Calculates the squared distance between this point and another point.
     * <p>
     * This method avoids computing a square root and is therefore more
     * efficient when only relative distances are required.
     * </p>
     *
     * @param other the point to measure distance from
     * @return the squared distance between the two points
     */
    public double distanceSquared(Point other) {

        Double3 diff = this._xyz.subtract(other._xyz);

        Double3 squaredDiff = diff.product(diff);

        return squaredDiff._d1() + squaredDiff._d2() + squaredDiff._d3();
    }

    /**
     * Calculates the Euclidean distance between this point and another point.
     *
     * @param other the point to measure distance from
     * @return the distance between the two points
     */
    public double distance(Point other) {
        return Math.sqrt(this.distanceSquared(other));
    }

}
