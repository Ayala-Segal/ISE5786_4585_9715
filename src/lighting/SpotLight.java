package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents spotlight source.
 */
public class SpotLight extends PointLight {

    /**
     * Direction of the spotlight.
     */
    private final Vector direction;

    /**
     * Constructs spotlight.
     *
     * @param intensity light intensity
     * @param position light position
     * @param direction spotlight direction
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }
    @Override
    public Vector getL(Point p) {
        return super.getL(p);
    }

    @Override
    public Color getIntensity(Point p) {
        Vector l = getL(p);
        double d = position.distance(p);
        double attenuation = kC + kL * d + kQ * d * d;

        double cos = Math.max(0, direction.dotProduct(l));

        return intensity.scale(cos / attenuation);
    }
    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

}