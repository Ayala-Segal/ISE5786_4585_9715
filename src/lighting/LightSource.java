package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Interface for all light sources in the scene.
 */
public interface LightSource {

    /**
     * Returns the light intensity reaching a point.
     *
     * @param p the illuminated point
     * @return light intensity at the point
     */
    Color getIntensity(Point p);

    /**
     * Returns the normalized direction vector
     * from the light source to the point.
     *
     * @param p the illuminated point
     * @return normalized light direction vector
     */
    Vector getL(Point p);

    /**
     * Returns the distance from the light source to the given point.
     * Used to limit shadow ray intersection checks to the segment
     * between the intersection point and the light source,
     * preventing false shadowing by objects behind the light.
     *
     * @param point the illuminated point
     * @return distance from the light source to the point
     */
    double getDistance(Point point);

}