package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents point light source.
 */
public class PointLight extends Light implements LightSource {

    /**
     * Position of the light source.
     */
    protected final Point position;

    protected double kC = 1;
    protected double kL = 0;
    protected double kQ = 0;

    /**
     * Constructs point light.
     *
     * @param intensity light intensity
     * @param position  light position
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    @Override
    public Color getIntensity(Point p) {
        double d = p.distance(position);
        double attenuation = kC + kL * d + kQ * d * d;
        return _intensity.scale(1.0 / attenuation);
    }

    @Override
    public Vector getL(Point p) {
        return p.subtract(position).normalize();
    }

    public PointLight setKl(double kL) {
        this.kL = kL;
        return this;
    }

    public PointLight setKq(double kQ) {
        this.kQ = kQ;
        return this;
    }

    public PointLight setKc(double kC) {
        this.kC = kC;
        return this;
    }


}