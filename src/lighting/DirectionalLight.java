package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents directional light source.
 */
public class DirectionalLight extends Light implements LightSource {

    /**
     * Direction of the light.
     */
    private final Vector direction;

    /**
     * Constructs directional light.
     *
     * @param intensity light intensity
     * @param direction light direction
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this.direction = direction.normalize();
    }

    @Override
    public Vector getL(Point p) {
        return direction;
    }

    @Override
    public Color getIntensity(Point p) {
        return intensity;
    }
}