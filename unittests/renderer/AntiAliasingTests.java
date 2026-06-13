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
 * Second visual demonstration of the anti-aliasing super-sampling improvement.
 * <p>
 * Same rules as {@code AntiAliasingTests}: exactly 10 geometries and 3 light
 * sources, rendered twice with the identical camera so the only difference
 * between the two output images is the anti-aliasing setting.
 * <ul>
 *   <li>{@code testShowcaseBefore} — single ray per pixel (no anti-aliasing)</li>
 *   <li>{@code testShowcaseAfter}  — 81 samples per pixel (9x9 grid)</li>
 * </ul>
 * <p>
 * The scene is a "glass orbs over a mirror floor" still life: a large
 * transparent sphere flanked by a ring of smaller coloured spheres, all
 * resting on a reflective floor, with two diagonal mountain triangles in
 * the background. Curved silhouettes and slanted edges are placed where the
 * staircase effect is easiest to spot. Render time is printed for both runs.
 */
class AntiAliasingTests{

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
     * Contains exactly 10 geometries and 3 light sources.
     *
     * @return fully configured Scene
     */
    private Scene buildScene() {
        Scene scene = new Scene("AntiAliasingTests");

        scene.geometries.add(
                // 1. Reflective floor plane — mirrors the orbs, slanted reflections show aliasing
                new Plane(new Point(0, -110, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(18, 18, 26))
                        .setMaterial(new Material().setKD(0.5).setKS(0.3).setShininess(120)
                                .setKR(0.25)),

                // 2. Central large glass sphere — transparent, dominant curved silhouette
                new Sphere(new Point(0, -25, -120), 50d)
                        .setEmission(new Color(10, 25, 45))
                        .setMaterial(new Material().setKD(0.2).setKS(0.5).setKT(0.45).setShininess(150)),

                // 3. Left orb — cool blue
                new Sphere(new Point(-78, -58, -100), 22d)
                        .setEmission(new Color(30, 70, 180))
                        .setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(90)),

                // 4. Right orb — warm red
                new Sphere(new Point(72, -52, -95), 25d)
                        .setEmission(new Color(180, 35, 35))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70)),

                // 5. Front-right small orb — emerald
                new Sphere(new Point(42, -78, -70), 15d)
                        .setEmission(new Color(30, 150, 70))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(60)),

                // 6. Front-left small orb — amber
                new Sphere(new Point(-38, -80, -75), 14d)
                        .setEmission(new Color(210, 150, 30))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(60)),

                // 7. Upper-right metallic orb — highly reflective, thin bright edge
                new Sphere(new Point(58, 32, -130), 20d)
                        .setEmission(new Color(60, 60, 60))
                        .setMaterial(new Material().setKD(0.2).setKS(0.7).setShininess(160)
                                .setKR(0.3)),

                // 8. Upper-left small orb — violet, isolated silhouette against background
                new Sphere(new Point(-52, 46, -110), 13d)
                        .setEmission(new Color(120, 40, 160))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(80)),

                // 9. Left background triangle — diagonal "mountain" slope
                new Triangle(
                        new Point(-185, -110, -175),
                        new Point(-35, -110, -175),
                        new Point(-110, 72, -175))
                        .setEmission(new Color(55, 40, 80))
                        .setMaterial(new Material().setKD(0.6).setKS(0.2).setShininess(20)),

                // 10. Right background triangle — opposite slope, aliasing in both directions
                new Triangle(
                        new Point(25, -110, -172),
                        new Point(185, -110, -172),
                        new Point(100, 90, -172))
                        .setEmission(new Color(40, 70, 90))
                        .setMaterial(new Material().setKD(0.6).setKS(0.2).setShininess(20))
        );

        scene.setAmbientLight(new AmbientLight(new Color(16, 16, 20)));

        // Light 1: main spot from upper-left — crisp highlights and edge contrast
        scene.lights.add(
                new SpotLight(
                        new Color(750, 540, 360),
                        new Point(-220, 220, 200),
                        new Vector(1, -1, -2))
                        .setKl(0.00002).setKq(0.000002)
        );

        // Light 2: cool point light from upper-right — fills shadows, adds depth
        scene.lights.add(
                new PointLight(
                        new Color(350, 380, 600),
                        new Point(220, 160, 120))
                        .setKl(0.00003).setKq(0.000005)
        );

        // Light 3: directional rim light from below — picks out bottom edges
        scene.lights.add(
                new DirectionalLight(
                        new Color(90, 90, 170),
                        new Vector(0, 1, -1))
        );
        return scene;
    }

    /**
     * Builds and returns the shared camera builder pointing at the scene.
     * Anti-aliasing is NOT configured here — each test sets it independently
     * so the two renders differ only in that one setting.
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
                .setRayTracer(scene, RayTracerType.SIMPLE) ;
    }

    // ── Tests ────────────────────────────────────────────────────────────────

    /**
     * Renders the showcase scene WITHOUT anti-aliasing (single ray per pixel).
     * <p>
     * Output file: {@code AntiAliasingTests_before.png}
     * <p>
     * Observe the jagged staircase edges on the orbs and the diagonal
     * mountain triangles. Render time is printed to the console.
     */
    @Test
    void testShowcaseBefore() {
        Scene scene = buildScene();

        long start = System.currentTimeMillis();

        buildCameraBuilder(scene)
                .build()
                .renderImage()
                .writeToImage("AntiAliasingTests_before");

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("%-40s %d ms%n", "Render time (no anti-aliasing):", elapsed);
    }

    /**
     * Renders the showcase scene WITH anti-aliasing (81 samples per pixel, 9x9 grid).
     * <p>
     * Output file: {@code AntiAliasingTests_after.png}
     * <p>
     * Compare with {@code AntiAliasingTests_before.png}: the orb
     * silhouettes and the diagonal triangle edges should appear visibly
     * smoother. Render time is printed to illustrate the performance cost.
     */
    @Test
    @SuppressWarnings("java:S109")
    void testShowcaseAfter() {
        Scene scene = buildScene();

        long start = System.currentTimeMillis();

        buildCameraBuilder(scene)
                .setAntiAliasing(NUM_SAMPLES, PIXEL_SIZE)
                .setDebugPrint(1)
                .build()
                .renderImage()
                .writeToImage("AntiAliasingTests_after");

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("%-40s %d ms%n", "Render time (anti-aliasing 81 samples):", elapsed);
    }
}
