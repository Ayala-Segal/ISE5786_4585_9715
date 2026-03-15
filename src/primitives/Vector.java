package primitives;

/**
 * Represents a vector in three-dimensional space.
 * <p>
 * A vector is defined by three coordinates (x, y, z) and is a subclass of {@link Point}.
 * Vectors represent direction and magnitude, and a zero vector is not allowed.
 * </p>
 * <p>
 * Common operations supported:
 * <ul>
 *   <li>Addition of two vectors</li>
 *   <li>Scaling by a scalar</li>
 *   <li>Dot product and cross product</li>
 *   <li>Computing length and squared length</li>
 *   <li>Distance between vectors</li>
 *   <li>Normalization</li>
 * </ul>
 * </p>
 * <p>
 * All calculations use linear algebra formulas; trigonometric functions are not used.
 * </p>
 *
 * @author Dan Zilberstein
 */
public class Vector extends Point {


    /**
     * Constant unit vector along the X axis (1,0,0).
     */
    public static final Vector AXIS_X = new Vector(1, 0, 0);

    /**
     * Constant unit vector along the Y axis (0,1,0).
     */
    public static final Vector AXIS_Y = new Vector(0, 1, 0);

    /**
     * Constant unit vector along the Z axis (0,0,1).
     */
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Constructs a vector with the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @throws IllegalArgumentException if all coordinates are zero
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (Util.isZero(x) && Util.isZero(y) && Util.isZero(z)) {
            throw new IllegalArgumentException("Zero vector is not allowed");
        }
    }

    /**
     * Constructs a vector from an existing {@link Double3} object.
     *
     * @param xyz the coordinate triad representing the vector
     * @throws IllegalArgumentException if the triad equals {@link Double3#ZERO}
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Zero vector is not allowed");
        }
    }

    /**
     * Adds this vector to another vector component-wise.
     *
     * @param other the vector to add
     * @return a new {@code Vector} containing the component-wise sum
     */
    public Vector add(Vector other) {
        return new Vector(this._xyz.add(other._xyz));
    }

    /**
     * Scales this vector by a scalar value.
     *
     * @param scalar the scaling factor
     * @return a new {@code Vector} containing the scaled values
     */
    public Vector scale(double scalar) {
        return new Vector(this._xyz.scale(scalar));
    }

    /**
     * Computes the dot product (scalar product) of this vector with another vector.
     *
     * @param other the other vector
     * @return the dot product as a double
     */
    public double dotProduct(Vector other) {
        return _xyz._d1() * other._xyz._d1()
                + _xyz._d2() * other._xyz._d2()
                + _xyz._d3() * other._xyz._d3();
    }

    /**
     * Computes the cross product (vector product) of this vector with another vector.
     *
     * @param other the other vector
     * @return a new {@code Vector} perpendicular to both vectors
     */
    public Vector crossProduct(Vector other) {
        double x = _xyz._d2() * other._xyz._d3() - _xyz._d3() * other._xyz._d2();
        double y = _xyz._d3() * other._xyz._d1() - _xyz._d1() * other._xyz._d3();
        double z = _xyz._d1() * other._xyz._d2() - _xyz._d2() * other._xyz._d1();

        return new Vector(x, y, z);
    }

    /**
     * Computes the squared length (magnitude) of this vector.
     *
     * @return the squared length as a double
     */
    public double lengthSquared() {
        return this.dotProduct(this);
    }

    /**
     * Computes the length (magnitude) of this vector.
     *
     * @return the length as a double
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes this vector to a unit vector (length of 1).
     *
     * @return a new {@code Vector} in the same direction with length 1
     */
    public Vector normalize() {
        return this.scale(1 / length());
    }

    @Override
    public String toString() {
        return "Vector" + _xyz;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vector other)) return false;
        return _xyz.equals(other._xyz);
    }

    @Override
    public int hashCode() {
        return _xyz.hashCode();
    }
}
