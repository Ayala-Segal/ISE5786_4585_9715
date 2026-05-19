package renderer;

import org.junit.jupiter.api.Test;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import primitives.*;
import scene.Scene;

/**
 * Test rendering images with multiple light sources combined.
 */
class MultipleLightsTests {

    /** Constant for tests resolution */
    private static final int RESOLUTION = 500;

    /** Default constructor to satisfy JavaDoc generator */
    MultipleLightsTests() { /* to satisfy JavaDoc generator */ }

    // ── shared geometry constants (copied from LightsTests) ──────────────────

    private static final int     SHININESS     = 301;
    private static final double  KD            = 0.5;
    private static final double  KS            = 0.5;
    private static final Double3 KD3           = new Double3(0.2, 0.6, 0.4);
    private static final Double3 KS3           = new Double3(0.2, 0.4, 0.3);

    private static final Color   SPHERE_COLOR  = new Color(java.awt.Color.BLUE).reduce(2);
    private static final Point   SPHERE_CENTER = new Point(0, 0, -50);
    private static final double  SPHERE_RADIUS = 50D;

    private static final Point[] VERTICES = {
            new Point(-110, -110, -150),
            new Point(95,   100,  -150),
            new Point(110,  -110, -150),
            new Point(-75,  78,   100)
    };

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Produce a picture of a sphere lighted by directional + point + spot lights.
     */
    @Test
    void testSphereMultipleLights() {
        Scene scene = new Scene("Sphere Multiple Lights");

        scene.geometries.add(
                new Sphere(SPHERE_CENTER, SPHERE_RADIUS)
                        .setEmission(SPHERE_COLOR)
                        .setMaterial(new Material()
                                .setKD(KD).setKS(KS).setShininess(SHININESS))
        );

        // directional — red, from lower-left
        scene.lights.add(new DirectionalLight(
                new Color(700, 100, 100),
                new Vector(1, 1, -0.5)
        ));

        // point — blue, from upper-right, close enough to be visible
        scene.lights.add(new PointLight(
                new Color(100, 100, 700),
                new Point(50, -50, 25)
        ).setKl(0.0005).setKq(0.00005));

        // spotlight — green, front-left aimed at sphere
        scene.lights.add(new SpotLight(
                new Color(100, 700, 100),
                new Point(-60, 20, 60),
                new Vector(1, -0.4, -1.2)
        ).setKl(0.0005).setKq(0.00005));

        Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(0, 0, 1000))
                .setDirection(Point.ZERO, Vector.AXIS_Y)
                .setVpSize(150, 150).setVpDistance(1000)
                .setResolution(RESOLUTION, RESOLUTION)
                .build()
                .renderImage()
                .writeToImage("lightSphereMultiple");
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Produce a picture of two triangles lighted by directional + point + spot lights.
     */
    @Test
    void testTrianglesMultipleLights() {
        Scene scene = new Scene("Triangles Multiple Lights")
                .setAmbientLight(new AmbientLight(new Color(38, 38, 38)));

        Material mat = new Material()
                .setKD(KD3).setKS(KS3).setShininess(SHININESS);

        scene.geometries.add(
                new Triangle(VERTICES[0], VERTICES[1], VERTICES[2]).setMaterial(mat),
                new Triangle(VERTICES[0], VERTICES[1], VERTICES[3]).setMaterial(mat)
        );

        // directional — red, aimed directly at the triangles front face
        scene.lights.add(new DirectionalLight(
                new Color(700, 100, 100),
                new Vector(-0.5, -0.5, -1)
        ));

        // point — blue, positioned in front and to the right
        scene.lights.add(new PointLight(
                new Color(100, 100, 700),
                new Point(60, 20, -50)
        ).setKl(0.0005).setKq(0.00005));

        // spotlight — green, from upper-left aimed diagonally down
        scene.lights.add(new SpotLight(
                new Color(100, 700, 100),
                new Point(-30, 80, -40),
                new Vector(0.5, -1.5, -1)
        ).setKl(0.0005).setKq(0.00005));

        Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(0, 0, 1000))
                .setDirection(Point.ZERO, Vector.AXIS_Y)
                .setVpSize(200, 200).setVpDistance(1000)
                .setResolution(RESOLUTION, RESOLUTION)
                .build()
                .renderImage()
                .writeToImage("lightTrianglesMultiple");
    }
}