package primitives;

/**
 * Represents a ray (half-line) in three-dimensional space.
 * <p>
 * A ray is defined by an origin point and a direction vector.
 * The direction vector is automatically normalized upon creation.
 * </p>
 * <p>
 * Rays are commonly used in computational geometry and ray tracing algorithms.
 * </p>
 *
 * @author Dan Zilberstein
 */
public class Ray {

    /**
     * The starting point of the ray.
     */
    private Point origin;

    /**
     * The normalized direction vector of the ray.
     */
    private Vector direction;

    /**
     * Constructs a ray with the specified origin and direction.
     * The direction vector will be normalized automatically.
     *
     * @param origin    the starting point of the ray
     * @param direction the direction vector of the ray
     */
    Ray(Point origin, Vector direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }
}

@Override
public String toString() {
    return origin + ", " + direction;
}

@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Ray other = (Ray) obj;
    return origin.equals(other.origin) && direction.equals(other.direction);
}

@Override
public int hashCode() {
    return 31 * origin.hashCode() + direction.hashCode();
}
}
