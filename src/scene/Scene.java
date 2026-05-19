package scene;

import java.util.ArrayList;
import java.util.List;

import geometries.impl.Geometries;
import lighting.LightSource;
import primitives.Color;
import lighting.AmbientLight;


public class Scene {

    public String name;
    public Color background = Color.BLACK;
    public AmbientLight ambientLight = AmbientLight.NONE;
    public Geometries geometries = new Geometries();
    public List<LightSource> lights = new ArrayList<>();
    public Scene(String name) {
        this.name = name;
    }

    // chaining setters

    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}