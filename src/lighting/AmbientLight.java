package lighting;

import primitives.Color;

/**
 * Represents ambient light in a scene.
 * <p>
 * Ambient light is a constant light that uniformly affects all objects
 * in the scene, regardless of their position, orientation, or relation
 * to other light sources.
 * </p>
 */
public class AmbientLight {

    /**
     * The intensity (color) of the ambient light.
     */
    private final Color _intensity;

    /**
     * A constant representing no ambient light (black color).
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructs an ambient light with the given intensity.
     *
     * @param intensity the color intensity of the ambient light
     */
    public AmbientLight(Color intensity) {
        this._intensity = intensity;
    }

    /**
     * Returns the intensity (color) of the ambient light.
     *
     * @return the ambient light intensity
     */
    public Color getIntensity() {
        return _intensity;
    }
}