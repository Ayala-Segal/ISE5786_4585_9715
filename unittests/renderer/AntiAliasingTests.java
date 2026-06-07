package renderer;

import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import static java.awt.Color.*;

/**
 * Visual demonstration tests for the anti-aliasing super-sampling improvement.
 * <p>
 * Each test pair renders the same rich scene twice:
 * <ul>
 *   <li>{@code testAntiAliasingBefore} — without anti-aliasing (single ray per pixel)</li>
 *   <li>{@code testAntiAliasingAfter}  — with anti-aliasing (81 samples per pixel, 9x9 grid)</li>
 * </ul>
 * Compare the two output images to observe the improvement:
 * jagged staircase edges on curved and diagonal surfaces become smooth.
 * <p>
 * Scene contains 10 geometries and 3 light sources positioned to maximize
 * visible aliasing artifacts on curved and diagonal edges.
 * Render time is printed to the console for both runs to illustrate
 * the performance cost of super-sampling.
 */
class AntiAliasingTests {

    /** Default constructor to satisfy JavaDoc generator */
    AntiAliasingTests() { /* to satisfy JavaDoc generator */ }

    // ── Constants ────────────────────────────────────────────────────────────

    /** Pixel size = vpSize / resolution = 200 / 500 */
    private static final double PIXEL_SIZE = 200d / 500;

    /** Number of samples for the anti-aliased render (9x9 grid) */
    private static final int NUM_SAMPLES = 81;

    // ── Scene & camera builders ──────────────────────────────────────────────

    /**
     * Builds and returns the shared scene used in both tests.
     * Contains 10 geometries and 3 light sources.
     *
     * @return fully configured Scene
     */
    private Scene buildScene() {
        Scene scene = new Scene("AntiAliasingDemo");

        scene.geometries.add(
                // 1. Large central sphere — curved edge shows aliasing clearly
                new Sphere(new Point(0, 0, -100), 45d)
                        .setEmission(new Color(20, 60, 150))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setKT(0.2).setShininess(80)),

                // 2. Small sphere top-right — thin silhouette reveals jagged edges
                new Sphere(new Point(70, 50, -120), 18d)
                        .setEmission(new Color(180, 30, 30))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(60)),

                // 3. Small sphere bottom-left
                new Sphere(new Point(-70, -50, -110), 15d)
                        .setEmission(new Color(30, 150, 60))
                        .setMaterial(new Material().setKD(0.4).setKS(0.3).setShininess(40)),

                // 4. Diagonal triangle — slanted edge is where aliasing is most visible
                new Triangle(
                        new Point(-160, -80, -160),
                        new Point(160, -80, -160),
                        new Point(80, 100, -160))
                        .setEmission(new Color(120, 80, 20))
                        .setMaterial(new Material().setKD(0.6).setKS(0.2).setShininess(20)),

                // 5. Second diagonal triangle — opposite slope, aliasing in both directions
                new Triangle(
                        new Point(-160, 80, -170),
                        new Point(160, -90, -170),
                        new Point(-80, -90, -170))
                        .setEmission(new Color(80, 20, 120))
                        .setMaterial(new Material().setKD(0.5).setKS(0.3).setShininess(30)),

                // 6. Floor plane
                new Plane(new Point(0, -110, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(40, 40, 40))
                        .setMaterial(new Material().setKD(0.6).setKS(0.2).setShininess(10)
                                .setKR(0.15)),

                // 7. Medium sphere on the left — partially behind large sphere
                new Sphere(new Point(-90, -20, -130), 25d)
                        .setEmission(new Color(160, 100, 20))
                        .setMaterial(new Material().setKD(0.5).setKS(0.3).setShininess(50)),

                // 8. Small reflective sphere on the right
                new Sphere(new Point(90, -30, -90), 20d)
                        .setEmission(new Color(200, 200, 200))
                        .setMaterial(new Material().setKD(0.2).setKS(0.6).setShininess(100)),

                // 9. Thin tall triangle — near-vertical edge exaggerates staircase effect
                new Triangle(
                        new Point(-120, -100, -140),
                        new Point(-90,  100, -140),
                        new Point(-60, -100, -140))
                        .setEmission(new Color(20, 120, 160))
                        .setMaterial(new Material().setKD(0.5).setKS(0.3).setShininess(40)),

                // 10. Small sphere at the top — isolated silhouette against dark background
                new Sphere(new Point(10, 90, -80), 14d)
                        .setEmission(new Color(220, 180, 30))
                        .setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(70))
        );

        scene.setAmbientLight(new AmbientLight(new Color(15, 15, 15)));

        // Light 1: main spot from upper-left — strong highlights and edges
        scene.lights.add(
                new SpotLight(
                        new Color(700, 500, 300),
                        new Point(-200, 200, 200),
                        new Vector(1, -1, -2))
                        .setKl(0.00002).setKq(0.000002)
        );

        // Light 2: point light from upper-right — fills shadows, adds depth
        scene.lights.add(
                new PointLight(
                        new Color(400, 400, 600),
                        new Point(200, 150, 100))
                        .setKl(0.00003).setKq(0.000005)
        );

        // Light 3: directional light from below — reveals bottom edges
        scene.lights.add(
                new DirectionalLight(
                        new Color(100, 100, 200),
                        new Vector(0, 1, -1))
        );
        return scene;
    }

    /**
     * Builds and returns the shared camera builder pointing at the scene.
     * Anti-aliasing is NOT configured here — each test sets it independently.
     *
     * @param scene the scene to render
     * @return configured camera builder
     */
    private Camera.Builder buildCameraBuilder(Scene scene) {
        return Camera.getBuilder()
                .setLocation(new Point(0, 0, 400))
                .setDirection(Point.ZERO, Vector.AXIS_Y)
                .setVpDistance(400)
                .setVpSize(200, 200)
                .setResolution(500, 500)
                .setRayTracer(scene, RayTracerType.SIMPLE);
    }

    // ── Tests ────────────────────────────────────────────────────────────────

    /**
     * Renders the demo scene WITHOUT anti-aliasing (single ray per pixel).
     * <p>
     * Output file: {@code antiAliasing_before.png}
     * <p>
     * Observe the jagged staircase edges on the spheres and diagonal triangles.
     * Render time is printed to the console.
     */
    @Test
    void testAntiAliasingBefore() {
        Scene scene = buildScene();

        long start = System.currentTimeMillis();

        buildCameraBuilder(scene)
                .build()
                .renderImage()
                .writeToImage("antiAliasing_before");

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("%-35s %d ms%n", "Render time (no anti-aliasing):", elapsed);
    }

    /**
     * Renders the demo scene WITH anti-aliasing (81 samples per pixel, 9x9 Grid).
     * <p>
     * Output file: {@code antiAliasing_after.png}
     * <p>
     * Compare with {@code antiAliasing_before.png}: edges on spheres and
     * diagonal triangles should appear visibly smoother.
     * Render time is printed to the console to illustrate the performance cost.
     */
    @Test
    @SuppressWarnings("java:S109")
    void testAntiAliasingAfter() {
        Scene scene = buildScene();

        long start = System.currentTimeMillis();

        buildCameraBuilder(scene)
                .setAntiAliasing(
                        new Blackboard()
                                .setSize(PIXEL_SIZE)
                                .setNumSamples(NUM_SAMPLES)
                )
                .build()
                .renderImage()
                .writeToImage("antiAliasing_after");

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("%-35s %d ms%n", "Render time (anti-aliasing 81 samples):", elapsed);
    }
}