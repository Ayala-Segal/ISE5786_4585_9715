package renderer;

import static java.awt.Color.BLUE;
import static java.awt.Color.RED;

import org.junit.jupiter.api.Test;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.SpotLight;
import primitives.*;
import scene.Scene;

/**
 * Tests for reflection and transparency functionality, test for partial
 * shadows
 * (with transparency)
 * @author Dan Zilberstein
 */
class TransparencyReflectionTests {
    /** Default constructor to satisfy JavaDoc generator */
    TransparencyReflectionTests() { /* to satisfy JavaDoc generator */ }

    /** Scene for the tests */
    private final Scene          _scene         = new Scene("Test scene");
    /** Camera builder for the tests with triangles */
    private final Camera.Builder _cameraBuilder = Camera.getBuilder()     //
            .setRayTracer(_scene, RayTracerType.SIMPLE);

    /** Produce a picture of a sphere lighted by a spot light */
    @Test
    @SuppressWarnings("java:S109")
    void testTwoSpheres() {
        _scene.geometries.add( //
                new Sphere(new Point(0, 0, -50), 50D).setEmission(new Color(BLUE)) //
                        .setMaterial(new Material().setKD(0.4).setKS(0.3).setShininess(100).setKT(0.3)), //
                new Sphere(new Point(0, 0, -50), 25D).setEmission(new Color(RED)) //
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100))); //
        _scene.lights.add( //
                new SpotLight(new Color(1000, 600, 0), new Point(-100, -100, 500), new Vector(-1, -1, -2)) //
                        .setKl(0.0004).setKq(0.0000006));

        _cameraBuilder
                .setLocation(new Point(0, 0, 1000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(1000).setVpSize(150, 150) //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("refractionTwoSpheres");
    }

    /** Produce a picture of a sphere lighted by a spot light */
    @Test
    @SuppressWarnings("java:S109")
    void testTwoSpheresOnMirrors() {
        _scene.geometries.add( //
                new Sphere(new Point(-950, -900, -1000), 400D).setEmission(new Color(0, 50, 100)) //
                        .setMaterial(new Material().setKD(0.25).setKS(0.25).setShininess(20) //
                                .setKT(new Double3(0.5, 0, 0))), //
                new Sphere(new Point(-950, -900, -1000), 200D).setEmission(new Color(100, 50, 20)) //
                        .setMaterial(new Material().setKD(0.25).setKS(0.25).setShininess(20)), //
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
                        new Point(670, 670, 3000)) //
                        .setEmission(new Color(20, 20, 20)) //
                        .setMaterial(new Material().setKR(1)), //
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
                        new Point(-1500, -1500, -2000)) //
                        .setEmission(new Color(20, 20, 20)) //
                        .setMaterial(new Material().setKR(new Double3(0.5, 0, 0.4))));
        _scene.setAmbientLight(new AmbientLight(new Color(26, 26, 26)));
        _scene.lights.add(new SpotLight(new Color(1020, 400, 400), new Point(-750, -750, -150), new Vector(-1, -1, -4)) //
                .setKl(0.00001).setKq(0.000005));

        _cameraBuilder
                .setLocation(new Point(0, 0, 10000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(10000).setVpSize(2500, 2500) //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("reflectionTwoSpheresMirrored");
    }

    /**
     * Produce a picture of a two triangles lighted by a spot light with a
     * partially
     * transparent Sphere producing partial shadow
     */
    @Test
    @SuppressWarnings("java:S109")
    void testTrianglesTransparentSphere() {
        _scene.geometries.add(
                new Triangle(new Point(-150, -150, -115), new Point(150, -150, -135),
                        new Point(75, 75, -150))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60)),
                new Triangle(new Point(-150, -150, -115), new Point(-70, 70, -140), new Point(75, 75, -150))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60)),
                new Sphere(new Point(60, 50, -50), 30D).setEmission(new Color(BLUE))
                        .setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(30).setKT(0.6)));
        _scene.setAmbientLight(new AmbientLight(new Color(38, 38, 38)));
        _scene.lights.add(
                new SpotLight(new Color(700, 400, 400), new Point(60, 50, 0), new Vector(0, 0, -1))
                        .setKl(4E-5).setKq(2E-7));

        _cameraBuilder
                .setLocation(new Point(0, 0, 1000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(1000).setVpSize(200, 200) //
                .setResolution(600, 600) //
                .build() //
                .renderImage() //
                .writeToImage("refractionShadow");
    }

    /**
     * A custom scene combining transparency, reflection, and shadow effects.
     * Scene contains:
     * - A reflective floor (triangle pair)
     * - A transparent blue sphere casting a partial shadow
     * - A solid red sphere reflected in the floor
     * - A small emissive white sphere acting as a "lamp"
     * Note: no object has both kT and kR simultaneously to avoid ray trapping.
     */
    @Test
    @SuppressWarnings("java:S109")
    void testCustomScene() {
        // Floor: two triangles with CONTRASTING colors so the diagonal edge is visible
        _scene.geometries.add(
                new Triangle(
                        new Point(-400, -100, -500),
                        new Point(400, -100, -500),
                        new Point(400, -100, 300))
                        .setEmission(new Color(120, 80, 40))
                        .setMaterial(new Material().setKD(0.5).setKS(0.3).setShininess(30).setKR(0.2)),
                new Triangle(
                        new Point(-400, -100, -500),
                        new Point(-400, -100, 300),
                        new Point(400, -100, 300))
                        .setEmission(new Color(60, 90, 50))
                        .setMaterial(new Material().setKD(0.5).setKS(0.3).setShininess(30).setKR(0.2)),

                // Back wall - two contrasting triangles to make the diagonal edge visible
                new Triangle(
                        new Point(-400, -100, -500),
                        new Point(400, -100, -500),
                        new Point(400, 250, -500))
                        .setEmission(new Color(30, 40, 110))
                        .setMaterial(new Material().setKD(0.6).setKS(0.2).setShininess(20)),
                new Triangle(
                        new Point(-400, -100, -500),
                        new Point(-400, 250, -500),
                        new Point(400, 250, -500))
                        .setEmission(new Color(90, 30, 90))
                        .setMaterial(new Material().setKD(0.6).setKS(0.2).setShininess(20)),

                // Transparent blue sphere - sits on floor, casts partial shadow
                new Sphere(new Point(-70, -30, -180), 70D)
                        .setEmission(new Color(0, 0, 130))
                        .setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(30).setKT(0.5)),

                // Solid red sphere - reflected in floor
                new Sphere(new Point(80, -40, -250), 60D)
                        .setEmission(new Color(130, 10, 10))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(80)),

                // Small emissive white sphere as lamp
                new Sphere(new Point(0, 80, -100), 12D)
                        .setEmission(new Color(220, 220, 180))
                        .setMaterial(new Material().setKD(0.1).setKS(0.1).setShininess(10))
        );

        _scene.setAmbientLight(new AmbientLight(new Color(30, 30, 30)));

        _scene.lights.add(
                new SpotLight(new Color(800, 500, 300), new Point(-100, 200, 100), new Vector(1, -2, -3))
                        .setKl(0.00003).setKq(0.000003)
        );

        // Camera tilted downward to clearly see the floor
        _cameraBuilder
                .setLocation(new Point(0, 120, 500))
                .setDirection(new Point(0, -50, -200), Vector.AXIS_Y)
                .setVpDistance(400).setVpSize(200, 200)
                .setResolution(600, 600)
                .build()
                .renderImage()
                .writeToImage("customSceneReflectionTransparency");
    }

}