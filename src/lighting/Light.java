package lighting;

import primitives.Color;

/**
 * Abstract base class for all light types.
 */
abstract class Light {

    /**
     * The original intensity of the light.
     */
    protected final Color _intensity;

    /**
     * Protected constructor for derived light classes.
     *
     * @param intensity light intensity
     */
    protected Light(Color intensity) {
        this._intensity = intensity;
    }

    /**
     * Returns the original light intensity.
     *
     * @return light intensity
     */
    public Color getIntensity() {
        return _intensity;
    }
}