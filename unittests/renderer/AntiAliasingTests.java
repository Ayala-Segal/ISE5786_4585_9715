package renderer;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import static java.awt.Color.*;

/**
 * Visual tests for anti-aliasing super-sampling improvement.
 * <p>
 * Each test renders the same scene twice:
 * once without anti-aliasing and once with anti-aliasing.
 * Compare the two output images to see the improvement —
 * jagged edges should appear smoother in the anti-aliased version.
 */
class AntiAliasingTests {

    /** Default constructor to satisfy JavaDoc generator */
    AntiAliasingTests() { /* to satisfy JavaDoc generator */ }

    /** Scene shared across tests */
    private final Scene _scene = new Scene("AntiAliasingScene");

    /** Base camera builder shared across tests */
    private final Camera.Builder _cameraBuilder = Camera.getBuilder()
            .setRayTracer(_scene, RayTracerType.SIMPLE);

    /**
     * Renders a sphere on a dark background — with and without anti-aliasing.
     * <p>
     * Without anti-aliasing: the edges of the sphere appear jagged (staircase effect).
     * With anti-aliasing: the edges appear smooth and natural.
     * <p>
     * Output files:
     * <ul>
     *   <li>{@code antiAliasing_sphere_before.png} — no anti-aliasing</li>
     *   <li>{@code antiAliasing_sphere_after.png}  — with anti-aliasing (81 samples)</li>
     * </ul>
     */
    @Test
    @SuppressWarnings("java:S109")
    void testSphereAntiAliasing() {
        _scene.geometries.add(
                // large central sphere — its curved edge shows aliasing clearly
                new Sphere(new Point(0, 0, -100), 50D)
                        .setEmission(new Color(BLUE))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(80)),

                // small sphere to the side — thin silhouette reveals jagged edges
                new Sphere(new Point(80, 30, -120), 20D)
                        .setEmission(new Color(RED))
                        .setMaterial(new Material().setKD(0.4).setKS(0.3).setShininess(40)),

                // diagonal triangle — slanted edge is where aliasing is most visible
                new Triangle(
                        new Point(-200, -80, -150),
                        new Point(200, -80, -150),
                        new Point(0, 120, -150))
                        .setEmission(new Color(20, 100, 20))
                        .setMaterial(new Material().setKD(0.6).setKS(0.2).setShininess(20))
        );

        _scene.setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

        _scene.lights.add(
                new SpotLight(new Color(800, 600, 400),
                        new Point(-150, 150, 200),
                        new Vector(1, -1, -2))
                        .setKl(0.00002).setKq(0.000002)
        );

        // ── without anti-aliasing ──────────────────────────────────────────
        _cameraBuilder
                .setLocation(new Point(0, 0, 300))
                .setDirection(Point.ZERO, Vector.AXIS_Y)
                .setVpDistance(300)
                .setVpSize(200, 200)
                .setResolution(500, 500)
                .build()
                .renderImage()
                .writeToImage("antiAliasing_sphere_before");

        // ── with anti-aliasing (9x9 = 81 samples per pixel) ───────────────
        _cameraBuilder
                .setLocation(new Point(0, 0, 300))
                .setDirection(Point.ZERO, Vector.AXIS_Y)
                .setVpDistance(300)
                .setVpSize(200, 200)
                .setResolution(500, 500)
                .setAntiAliasing(
                        new Blackboard()
                                .setSize(200d / 500)   // pixel size = vpSize / resolution
                                .setNumSamples(289)     // 17x17 grid
                )
                .build()
                .renderImage()
                .writeToImage("antiAliasing_sphere_after");
    }
}
