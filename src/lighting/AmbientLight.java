package lighting;

import primitives.Color;

public  class  AmbientLight {

    private final Color _intensity;

    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    public AmbientLight(Color intensity) {
        this._intensity = intensity;
    }

    public Color getIntensity() {
        return _intensity;
    }
}