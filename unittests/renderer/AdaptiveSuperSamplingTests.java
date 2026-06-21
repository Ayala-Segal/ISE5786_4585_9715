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

import java.util.Random;

/**
 * Performance demonstration of the <b>Adaptive Super-Sampling (ASS)</b>
 * acceleration improvement, combined with the multi-threading feature.
 * <p>
 * The scene is a <b>"Crystal Pyramid"</b> centrepiece — deliberately different
 * in style from {@code AntiAliasingTests} (glass orbs over a mirror floor).
 * A single large four-sided pyramid built from triangles stands at the centre
 * on a polished reflective floor, ringed by a halo of floating glass and
 * metallic spheres, with a row of smaller triangular obelisks behind it. The
 * pyramid's long slanted edges and the curved sphere silhouettes are exactly
 * where adaptive sampling pays off: smooth interior regions collapse to a few
 * samples while busy edges are recursively refined.
 * <p>
 * The same scene, camera, materials, lights and Mini-Project-1 feature
 * (antialiasing at high quality) are used in <b>all four mandatory
 * configurations</b>, so the only variables between runs are the acceleration
 * feature (ASS) and multi-threading:
 * <ol>
 *   <li>{@link #renderPlain_noAcceleration_noMultithreading} — ASS off, MT off</li>
 *   <li>{@link #renderMT_noAcceleration_withMultithreading}  — ASS off, MT on</li>
 *   <li>{@link #renderASS_withAcceleration_noMultithreading} — ASS on,  MT off</li>
 *   <li>{@link #renderASS_MT_withAcceleration_withMultithreading} — ASS on, MT on</li>
 * </ol>
 * Each configuration is a separate test method whose name reflects exactly
 * what is enabled. Every run prints its render time so speedup ratios can be
 * computed: MT contribution and ASS contribution are isolated from each other.
 */
class AdaptiveSuperSamplingTests {

    /** Default constructor to satisfy JavaDoc generator */
    AdaptiveSuperSamplingTests() { /* to satisfy JavaDoc generator */ }

    // ── Configuration constants (no hard-coded values inside system code) ─────

    /** Horizontal/vertical resolution of the rendered image. */
    private static final int RESOLUTION = 600;

    /** View-plane physical size (square). */
    private static final double VP_SIZE = 200d;

    /** Pixel size = vpSize / resolution — used as the antialiasing beam size. */
    private static final double PIXEL_SIZE = VP_SIZE / RESOLUTION;

    /**
     * Number of samples per pixel for the Mini-Project-1 antialiasing feature
     * (9×9 grid). Kept high so the MP1 feature is active "at high quality".
     */
    private static final int NUM_SAMPLES = 81;

    /**
     * Maximum ASS recursion depth. Depth 4 corresponds to a 9×9 effective grid,
     * matching {@link #NUM_SAMPLES} so the two renders target the same quality.
     */
    private static final int ASS_MAX_DEPTH = 4;

    /**
     * Per-channel color difference threshold [0–255] below which a region is
     * considered uniform and recursion stops.
     */
    private static final double ASS_THRESHOLD = 18d;

    /**
     * Number of threads for the multithreaded runs.
     * {@code -2} = automatic (logical cores minus spare threads).
     */
    private static final int MT_THREADS = -2;

    // ── Shared scene (built once, reused for every measurement) ───────────────

    /**
     * Lazily-built shared scene. Held statically so that all runs in this
     * measurement series operate on exactly the same geometry — important
     * because the sphere halo uses randomization.
     */
    private static Scene scene = null;

    /** Fixed seed so the randomized halo is identical across all runs. */
    private static final long SEED = 20260620L;

    /** Floor height (Y) — everything rests on this plane. */
    private static final double FLOOR_Y = -120;

    /** Pyramid depth (Z) — centre of the scene. */
    private static final double CENTRE_Z = -150;

    /**
     * Returns the shared "Crystal Pyramid" scene, building it on first access.
     * <p>
     * Contents:
     * <ul>
     *   <li>Polished reflective floor (Plane)</li>
     *   <li>Back wall (Plane) for reflections and depth</li>
     *   <li>One large central pyramid built from 4 triangular faces</li>
     *   <li>A row of smaller triangular obelisks behind the pyramid</li>
     *   <li>Randomized halo of glass + metallic spheres (transparency,
     *       reflection)</li>
     *   <li>Five light sources of all supported types</li>
     * </ul>
     *
     * @return the shared scene (≈40 geometries)
     */
    private static Scene getScene() {
        if (scene != null) return scene;

        Scene s = new Scene("CrystalPyramid");

        // 1. Polished floor — warm dark tone, mirrors the pyramid and spheres
        s.geometries.add(
                new Plane(new Point(0, FLOOR_Y, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(16, 10, 24))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4)
                                .setShininess(150).setKR(0.35)));

        // 2. Back wall — warm rose-mauve for color contrast with the teal/gold pyramid
        s.geometries.add(
                new Plane(new Point(0, 0, -420), new Vector(0, 0, 1))
                        .setEmission(new Color(38, 24, 34))
                        .setMaterial(new Material().setKD(0.6).setKS(0.2)
                                .setShininess(40).setKR(0.08)));

        // ── Central pyramid: the main object, built from 4 triangle faces ────
        addPyramid(s);

        // ── Background obelisks: a row of slim triangular spires ─────────────
        addObelisks(s);

        // ── Halo: randomized ring of glass + metallic spheres ────────────────
        addHalo(s);

        // ── Lighting: all five supported source types ─────────────────────────
        s.setAmbientLight(new AmbientLight(new Color(10, 10, 14)));

        // Light 1 — warm key spot from upper-left
        s.lights.add(new SpotLight(
                new Color(280, 200, 120),
                new Point(-200, 220, 180),
                new Vector(1, -1, -1.5))
                .setKl(0.0006).setKq(0.000002));

        // Light 2 — cool fill spot from upper-right
        s.lights.add(new SpotLight(
                new Color(120, 160, 280),
                new Point(220, 200, 160),
                new Vector(-1, -1, -1.4))
                .setKl(0.0006).setKq(0.000002));

        // Light 3 — point light low and central, lights the pyramid base
        s.lights.add(new PointLight(
                new Color(160, 140, 110),
                new Point(0, -40, 120))
                .setKl(0.001).setKq(0.00003));

        // Light 4 — point light behind the halo for rim glow
        s.lights.add(new PointLight(
                new Color(100, 120, 150),
                new Point(-60, 150, -60))
                .setKl(0.001).setKq(0.00004));

        // Light 5 — directional sweep from below-front
        s.lights.add(new DirectionalLight(
                new Color(30, 32, 48),
                new Vector(0.3, 1, -1)));

        scene = s;
        return scene;
    }

    /**
     * Builds one large four-sided pyramid centred at {@link #CENTRE_Z},
     * standing on the floor. Four triangular faces share a single apex above
     * a square base, producing four long slanted silhouette edges.
     *
     * @param s scene to add the pyramid faces to
     */
    private static void addPyramid(Scene s) {
        double half   = 60;                 // half the base side length
        double height = 150;                // apex height above the base
        double bz     = CENTRE_Z;           // base centre Z

        // square base corners (on the floor)
        Point a = new Point(-half, FLOOR_Y, bz + half); // front-left
        Point b = new Point( half, FLOOR_Y, bz + half); // front-right
        Point c = new Point( half, FLOOR_Y, bz - half); // back-right
        Point d = new Point(-half, FLOOR_Y, bz - half); // back-left
        Point apex = new Point(0, FLOOR_Y + height, bz);

        Material faceMat = new Material()
                .setKD(0.5).setKS(0.5).setShininess(140).setKR(0.12);

        // four faces — vivid distinct colors so each face reads clearly
        s.geometries.add(new Triangle(a, b, apex)   // front — warm gold
                .setEmission(new Color(90, 60, 10)).setMaterial(faceMat));
        s.geometries.add(new Triangle(b, c, apex)   // right — deep teal
                .setEmission(new Color(10, 70, 90)).setMaterial(faceMat));
        s.geometries.add(new Triangle(c, d, apex)   // back — rich violet
                .setEmission(new Color(60, 20, 90)).setMaterial(faceMat));
        s.geometries.add(new Triangle(d, a, apex)   // left — crimson
                .setEmission(new Color(90, 18, 18)).setMaterial(faceMat));
    }

    /**
     * Adds a row of slim triangular obelisks behind the pyramid, alternating
     * in height, to fill the background with clean diagonal edges.
     *
     * @param s scene to add the obelisks to
     */
    private static void addObelisks(Scene s) {
        Material mat = new Material().setKD(0.55).setKS(0.3).setShininess(60).setKR(0.1);
        int    count  = 6;
        double spread = 420;
        double z      = -330;
        double step   = spread / count;
        double startX = -spread / 2;

        // vivid alternating colors for the obelisks
        int[][] obeliskColors = {
                {80, 30, 10},   // burnt orange
                {10, 60, 90},   // ocean blue
                {70, 10, 70},   // magenta
                {10, 80, 40},   // emerald
                {90, 70, 10},   // amber
                {20, 20, 90}    // cobalt
        };
        for (int i = 0; i < count; i++) {
            double cx     = startX + step * (i + 0.5);
            double halfW  = 16 + (i % 2) * 6;
            double height = 80 + (i % 3) * 55;
            int[]  c      = obeliskColors[i];
            s.geometries.add(new Triangle(
                    new Point(cx - halfW, FLOOR_Y, z),
                    new Point(cx + halfW, FLOOR_Y, z),
                    new Point(cx, FLOOR_Y + height, z))
                    .setEmission(new Color(c[0], c[1], c[2]))
                    .setMaterial(mat));
        }
    }

    /**
     * Adds a randomized halo of spheres arranged loosely in a ring above and
     * around the pyramid's upper half. A mix of transparent (glass) and
     * reflective (metallic) spheres provides transparency and reflection
     * effects. Randomization uses a fixed seed so every run gets the identical
     * halo.
     *
     * @param s scene to add the halo spheres to
     */
    private static void addHalo(Scene s) {
        Random rnd = new Random(SEED);
        int    count   = 20;
        double ringR   = 105;          // ring radius around the apex axis
        double ringR2  = 55;           // inner jitter
        double centreY = 30;           // vertical centre of the ring
        double spreadY = 70;

        for (int i = 0; i < count; i++) {
            double ang = rnd.nextDouble() * Math.PI * 2;
            double rad = ringR2 + rnd.nextDouble() * (ringR - ringR2);
            double x = Math.cos(ang) * rad;
            double y = centreY + (rnd.nextDouble() * 2 - 1) * spreadY;
            double z = CENTRE_Z + Math.sin(ang) * rad * 0.5;
            double r = 8 + rnd.nextDouble() * 10;

            // vivid but controlled emission
            Color emission = new Color(
                    15 + rnd.nextInt(65),
                    15 + rnd.nextInt(65),
                    15 + rnd.nextInt(65));

            Material mat;
            int kind = rnd.nextInt(3);
            if (kind == 0) {
                // glass — transparent
                mat = new Material().setKD(0.25).setKS(0.5)
                        .setKT(0.45).setShininess(150);
            } else if (kind == 1) {
                // metallic — reflective
                mat = new Material().setKD(0.3).setKS(0.6)
                        .setShininess(170).setKR(0.3);
            } else {
                // glossy opaque — solid coloured shapes for contrast
                mat = new Material().setKD(0.6).setKS(0.4).setShininess(90);
            }

            s.geometries.add(new Sphere(new Point(x, y, z), r)
                    .setEmission(emission).setMaterial(mat));
        }

        // one large clear glass sphere floating above the apex as a focal point
        s.geometries.add(new Sphere(new Point(0, 115, CENTRE_Z + 10), 24d)
                .setEmission(new Color(6, 14, 24))
                .setMaterial(new Material().setKD(0.2).setKS(0.5)
                        .setKT(0.5).setShininess(160)));
    }

    // ── Shared camera builder ─────────────────────────────────────────────────

    /**
     * Builds the shared camera builder aimed at the crystal pyramid.
     * Antialiasing (the Mini-Project-1 feature) is always enabled here at high
     * quality; each test then layers ASS and/or multi-threading on top so the
     * runs differ only in those two settings.
     *
     * @return configured camera builder with MP1 antialiasing enabled
     */
    private Camera.Builder baseCamera() {
        return Camera.getBuilder()
                .setLocation(new Point(0, 30, 480))
                .setDirection(new Point(0, 5, -150), Vector.AXIS_Y)
                .setVpDistance(480)
                .setVpSize(VP_SIZE, VP_SIZE)
                .setResolution(RESOLUTION, RESOLUTION)
                .setRayTracer(getScene(), RayTracerType.SIMPLE)
                .setAntiAliasing(NUM_SAMPLES, PIXEL_SIZE); // MP1 feature, high quality
    }

    /**
     * Runs one render configuration, times it, writes the image, and prints
     * the elapsed render time in a uniform format.
     *
     * @param builder  fully configured camera builder
     * @param fileName output image file name
     * @param label    human-readable label for the console line
     */
    private void runAndReport(Camera.Builder builder, String fileName, String label) {
        long start = System.currentTimeMillis();
        builder.build()
                .renderImage()
                .writeToImage(fileName);
        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("%-52s %6d ms%n", label, elapsed);
    }

    // ── The four mandatory measurement configurations ─────────────────────────

    /**
     * Configuration 1 — acceleration OFF, multi-threading OFF.
     * Baseline: full antialiasing beam (81 rays/pixel), single thread.
     * <p>Output: {@code CrystalPyramid_plain.png}
     */
    @Test
    void renderPlain_noAcceleration_noMultithreading() {
        runAndReport(
                baseCamera()
                        .setMultithreading(0),
                "CrystalPyramid_plain",
                "1) ASS off, MT off  (baseline):");
    }

    /**
     * Configuration 2 — acceleration OFF, multi-threading ON.
     * Isolates the multi-threading contribution to the full beam.
     * <p>Output: {@code CrystalPyramid_mt.png}
     */
    @Test
    void renderMT_noAcceleration_withMultithreading() {
        runAndReport(
                baseCamera()
                        .setMultithreading(MT_THREADS),
                "CrystalPyramid_mt",
                "2) ASS off, MT on:");
    }

    /**
     * Configuration 3 — acceleration ON, multi-threading OFF.
     * Isolates the Adaptive Super-Sampling contribution to a single thread.
     * <p>Output: {@code CrystalPyramid_ass.png}
     */
    @Test
    void renderASS_withAcceleration_noMultithreading() {
        runAndReport(
                baseCamera()
                        .setAdaptiveSuperSampling(ASS_MAX_DEPTH, ASS_THRESHOLD)
                        .setMultithreading(0)
                        .setDebugPrint(1),
                "CrystalPyramid_ass",
                "3) ASS on,  MT off:");
    }

    /**
     * Configuration 4 — acceleration ON, multi-threading ON.
     * Both features combined: the fastest configuration.
     * <p>Output: {@code CrystalPyramid_ass_mt.png}
     */
    @Test
    void renderASS_MT_withAcceleration_withMultithreading() {
        runAndReport(
                baseCamera()
                        .setAdaptiveSuperSampling(ASS_MAX_DEPTH, ASS_THRESHOLD)
                        .setMultithreading(MT_THREADS)
                        .setDebugPrint(1),
                "CrystalPyramid_ass_mt1",
                "4) ASS on,  MT on:");
    }
}