package renderer;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.*;

import primitives.Blackboard;
import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;
import scene.Scene;

/**
 * Represents a camera in a 3D rendering system.
 * The camera generates rays through pixels on a view plane,
 * traces them via a ray tracer, and writes the resulting colors
 * to an image.
 * <p>
 * Supports anti-aliasing super-sampling via a configurable {@link Blackboard}.
 * When anti-aliasing is enabled, multiple rays are cast through each pixel
 * and their colors are averaged, producing smoother edges.
 */
public class Camera implements Cloneable {

    private Point  _p0;
    private Vector _vTo, _vUp, _vRight;
    private double _width, _height, _distance;
    private int    _nX = 1, _nY = 1;
    private Point  _vpCenter;
    private double _pixelWidth, _pixelHeight;

    private ImageWriter    _imageWriter;
    private RayTracerBase  _rayTracer;

    /**
     * Blackboard used for anti-aliasing super-sampling.
     * Default: single sample (anti-aliasing disabled).
     */
    private Blackboard _antiAliasingBlackboard = new Blackboard();

    /** Amount of threads to use fore rendering image by the camera */
    private int threadsCount = 0;
    /**
     * Amount of threads to spare for Java VM threads:<br>
     * Spare threads if trying to use all the cores
     */
    private static final int SPARE_THREADS = 2;
    /**
     * Debug print interval in seconds (for progress percentage)<br>
     * if it is zero - there is no progress output
     */
    private double printInterval = 0;

    /**
     * Maximum recursion depth for Adaptive Super-Sampling.<br>
     * 0 = disabled (regular super-sampling is used instead).<br>
     * Depth 1 → 2×2 sub-regions, depth 2 → 3×3, depth 3 → 5×5, etc.
     */
    private int _adaptiveMaxDepth = 0;

    /**
     * Color difference threshold for Adaptive Super-Sampling.<br>
     * If the maximum channel difference between corner colors is below
     * this value, the region is considered uniform and recursion stops.
     */
    private double _adaptiveThreshold = 0.0;
    /**
     * Pixel manager for supporting:
     * <ul>
     * <li>multi-threading</li>
     * <li>debug print of progress percentage in Console window/tab</li>
     * </ul>
     */
    private PixelManager pixelManager;

    private Camera() {}

    /**
     * Returns a builder for constructing a Camera instance.
     *
     * @return a new {@link Builder}
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray passing through the center of a given pixel
     * on the view plane.
     *
     * @param xIndex column index of the pixel
     * @param yIndex row index of the pixel
     * @return a Ray from the camera origin through the pixel center
     */
    public Ray constructRay(int xIndex, int yIndex) {
        Point  center = getPixelCenter(xIndex, yIndex);
        Vector dir    = center.subtract(_p0).normalize();
        return new Ray(_p0, dir);
    }

    /** This function renders image's pixel color map from the scene
     * included in the ray tracer object
     * @return the camera object itself
     */
    public Camera renderImage() {
        pixelManager = new PixelManager(_nY, _nX, printInterval);
        return switch (threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    /**
     * Casts a beam of rays through the specified pixel, averages the resulting
     * colors, and writes the final color to the image.
     * <p>
     * If anti-aliasing is disabled (default {@link Blackboard}),
     * exactly one ray is cast through the pixel center — identical
     * to the original behavior.
     *
     * @param xIndex column index of the pixel
     * @param yIndex row index of the pixel
     */
    private void castRay(int xIndex, int yIndex) {
        Color color = castRayBeam(xIndex, yIndex);
        _imageWriter.writePixel(xIndex, yIndex, color);
        pixelManager.pixelDone();
    }

    /**
     * Sends a beam of rays through the pixel and returns the averaged color.
     * <p>
     * When anti-aliasing is disabled ({@code numSamples <= 1} or {@code size = 0}),
     * a single ray through the pixel center is used.
     * Otherwise, sample points are distributed across the pixel area using
     * the configured {@link Blackboard}, and the resulting colors are averaged.
     *
     * @param xIndex column index of the pixel
     * @param yIndex row index of the pixel
     * @return averaged color for this pixel
     */
    private Color castRayBeam(int xIndex, int yIndex) {
        Blackboard bb     = _antiAliasingBlackboard;
        Point      center = getPixelCenter(xIndex, yIndex);

        // anti-aliasing disabled: single ray through pixel center
        if (bb.getNumSamples() <= 1 || Util.isZero(bb.getSize()))
            return _rayTracer.traceRay(new Ray(_p0, center.subtract(_p0)));

        // Adaptive Super-Sampling enabled: recursive adaptive sampling
        if (_adaptiveMaxDepth > 0) {
            double halfSize = bb.getSize() / 2.0;
            return adaptiveSample(center, halfSize, _adaptiveMaxDepth,
                    null, null, null, null);
        }

        // regular super-sampling: generate all sample points and average
        List<Point> samplePoints = bb.generatePoints(center, _vRight, _vUp);

        Color sum = Color.BLACK;
        for (Point p : samplePoints)
            sum = sum.add(_rayTracer.traceRay(new Ray(_p0, p.subtract(_p0))));

        return sum.reduce(samplePoints.size());
    }

    /**
     * Recursively computes the color for a square sub-region using
     * Adaptive Super-Sampling.
     * <p>
     * The four corners of the region are sampled. If all corners are
     * sufficiently similar (below {@link #_adaptiveThreshold}), their
     * average is returned immediately. Otherwise, the region is split
     * into four quadrants and each is sampled recursively.
     * <p>
     * Corner colors from the parent call are passed in to avoid
     * recomputing shared edges between adjacent quadrants.
     *
     * @param center   center point of this region on the view plane
     * @param halfSize half the side length of this region
     * @param depth    remaining recursion depth (stops at 0)
     * @param cTL      pre-computed top-left  corner color, or null to compute
     * @param cTR      pre-computed top-right corner color, or null to compute
     * @param cBL      pre-computed bottom-left  corner color, or null to compute
     * @param cBR      pre-computed bottom-right corner color, or null to compute
     * @return averaged color for this region
     */
    private Color adaptiveSample(Point center, double halfSize, int depth,
                                 Color cTL, Color cTR, Color cBL, Color cBR) {
        // compute corner points
        Point pTL = cornerPoint(center, -halfSize,  halfSize);
        Point pTR = cornerPoint(center,  halfSize,  halfSize);
        Point pBL = cornerPoint(center, -halfSize, -halfSize);
        Point pBR = cornerPoint(center,  halfSize, -halfSize);

        // compute any missing corner colors
        if (cTL == null) cTL = tracePoint(pTL);
        if (cTR == null) cTR = tracePoint(pTR);
        if (cBL == null) cBL = tracePoint(pBL);
        if (cBR == null) cBR = tracePoint(pBR);

        // if uniform enough or max depth reached — return average
        if (depth == 0 || isUniform(cTL, cTR, cBL, cBR))
            return cTL.add(cTR).add(cBL).add(cBR).reduce(4);

        double quarter = halfSize / 2.0;

        // center color (shared by all four quadrants)
        Color cCenter = tracePoint(center);

        // edge midpoints (each shared by two quadrants)
        Color cTop    = tracePoint(cornerPoint(center,       0,  halfSize));
        Color cBottom = tracePoint(cornerPoint(center,       0, -halfSize));
        Color cLeft   = tracePoint(cornerPoint(center, -halfSize,       0));
        Color cRight  = tracePoint(cornerPoint(center,  halfSize,       0));

        // recurse into four quadrants, passing shared corners to avoid recomputation
        Color q1 = adaptiveSample(cornerPoint(center, -quarter,  quarter), quarter, depth - 1,
                cTL, cTop, cLeft, cCenter);
        Color q2 = adaptiveSample(cornerPoint(center,  quarter,  quarter), quarter, depth - 1,
                cTop, cTR, cCenter, cRight);
        Color q3 = adaptiveSample(cornerPoint(center, -quarter, -quarter), quarter, depth - 1,
                cLeft, cCenter, cBL, cBottom);
        Color q4 = adaptiveSample(cornerPoint(center,  quarter, -quarter), quarter, depth - 1,
                cCenter, cRight, cBottom, cBR);

        return q1.add(q2).add(q3).add(q4).reduce(4);
    }

    /**
     * Computes a corner point offset from a center by (dx, dy)
     * along the camera's right and up axes.
     *
     * @param center base point
     * @param dx     offset along _vRight
     * @param dy     offset along _vUp
     * @return the offset point on the view plane
     */
    private Point cornerPoint(Point center, double dx, double dy) {
        Point p = center;
        if (!Util.isZero(dx)) p = p.add(_vRight.scale(dx));
        if (!Util.isZero(dy)) p = p.add(_vUp.scale(dy));
        return p;
    }

    /**
     * Traces a single ray from the camera origin through a point on the
     * view plane and returns the resulting color.
     *
     * @param p target point on the view plane
     * @return color returned by the ray tracer
     */
    private Color tracePoint(Point p) {
        return _rayTracer.traceRay(new Ray(_p0, p.subtract(_p0)));
    }

    /**
     * Checks whether four corner colors are uniform enough to stop recursion.
     * Uniformity is determined by comparing the maximum per-channel difference
     * between any two corners against {@link #_adaptiveThreshold}.
     *
     * @param c1 top-left color
     * @param c2 top-right color
     * @param c3 bottom-left color
     * @param c4 bottom-right color
     * @return true if all corners are within threshold of each other
     */
    private boolean isUniform(Color c1, Color c2, Color c3, Color c4) {
        return maxDiff(c1, c2) < _adaptiveThreshold
                && maxDiff(c1, c3) < _adaptiveThreshold
                && maxDiff(c1, c4) < _adaptiveThreshold
                && maxDiff(c2, c3) < _adaptiveThreshold
                && maxDiff(c2, c4) < _adaptiveThreshold
                && maxDiff(c3, c4) < _adaptiveThreshold;
    }

    /**
     * Returns the maximum absolute difference across all three color channels
     * between two colors.
     *
     * @param a first color
     * @param b second color
     * @return max channel difference in [0, 255]
     */
    private double maxDiff(Color a, Color b) {
        var ca = a.getColor();
        var cb = b.getColor();
        return Math.max(
                Math.abs(ca.getRed()   - cb.getRed()),
                Math.max(
                        Math.abs(ca.getGreen() - cb.getGreen()),
                        Math.abs(ca.getBlue()  - cb.getBlue())
                )
        );
    }

    /**
     * Computes the 3D center point of a pixel on the view plane.
     * <p>
     * Extracted from {@link #constructRay} to allow reuse in beam generation
     * without duplicating the offset calculation.
     *
     * @param xIndex column index of the pixel
     * @param yIndex row index of the pixel
     * @return the 3D center point of the pixel on the view plane
     */
    private Point getPixelCenter(int xIndex, int yIndex) {
        double xOffset = (xIndex - (_nX - 1) / 2.0) * _pixelWidth;
        double yOffset = -((yIndex - (_nY - 1) / 2.0) * _pixelHeight);

        Point p = _vpCenter;
        if (!Util.isZero(xOffset)) p = p.add(_vRight.scale(xOffset));
        if (!Util.isZero(yOffset)) p = p.add(_vUp.scale(yOffset));
        return p;
    }

    /**
     * Draws a grid over the rendered image.
     * Pixels whose row or column index is a multiple of {@code interval}
     * are colored with the given color.
     *
     * @param interval spacing between grid lines (in pixels)
     * @param color    color of the grid lines
     * @return this Camera (for chaining)
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < _nX; i++)
            for (int j = 0; j < _nY; j++)
                if (i % interval == 0 || j % interval == 0)
                    _imageWriter.writePixel(i, j, color);
        return this;
    }

    /**
     * Writes the rendered image to a file.
     *
     * @param imageName name of the output image file
     * @return this Camera (for chaining)
     */
    public Camera writeToImage(String imageName) {
        _imageWriter.writeToImage(imageName);
        return this;
    }

    /**
     *  Render image using multi-threading by parallel streaming
     * @return the camera object itself
     */
    private Camera renderImageStream() {
        IntStream.range(0, _nY).parallel()
                .forEach(i -> IntStream.range(0, _nX).parallel()
                        .forEach(j -> castRay(j, i)));
        return this;
    }
    /**
     * Render image without multi-threading
     * @return the camera object itself
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < _nY; ++i)
            for (int j = 0; j < _nX; ++j)
                castRay(j, i);
        return this;
    }
    /**
     * Render image using multi-threading by creating and running raw threads
     * @return the camera object itself
     */
    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        while (threadsCount-- > 0)
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null)
                    castRay(pixel.col(), pixel.row());
            }));
        for (var thread : threads) thread.start();
        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignored) {}
        return this;
    }

    // ── Builder ──────────────────────────────────────────────────────────────

    /**
     * Builder for constructing a {@link Camera} instance.
     */
    public static class Builder {

        private final Camera _camera = new Camera();

        private Point  _location = null;
        private Vector _vTo      = null;
        private Point  _target   = null;
        private Vector _vUp      = Vector.AXIS_Y;
        private double _width    = 0;
        private double _height   = 0;
        private double _distance = 0;
        private int    _nX       = 1, _nY = 1;

        /**
         * Sets the camera location.
         *
         * @param location camera position in world space
         * @return this Builder (for chaining)
         */
        public Builder setLocation(Point location) {
            _location = location;
            return this;
        }

        /**
         * Sets the camera direction using explicit forward and up vectors.
         *
         * @param to forward direction vector
         * @param up upward direction vector
         * @return this Builder (for chaining)
         */
        public Builder setDirection(Vector to, Vector up) {
            _vTo    = to;
            _target = null;
            _vUp    = up;
            return this;
        }

        /**
         * Sets the camera direction toward a target point with a given up vector.
         *
         * @param target target point to look at
         * @param up     upward direction vector
         * @return this Builder (for chaining)
         */
        public Builder setDirection(Point target, Vector up) {
            _target = target;
            _vTo    = null;
            _vUp    = up;
            return this;
        }

        /**
         * Sets the camera direction toward a target point using the default up vector.
         *
         * @param target target point to look at
         * @return this Builder (for chaining)
         */
        public Builder setDirection(Point target) {
            _target = target;
            _vTo    = null;
            _vUp    = Vector.AXIS_Y;
            return this;
        }

        /**
         * Sets the view plane dimensions.
         *
         * @param width  view plane width
         * @param height view plane height
         * @return this Builder (for chaining)
         */
        public Builder setVpSize(double width, double height) {
            _width  = width;
            _height = height;
            return this;
        }

        /**
         * Sets the distance from the camera origin to the view plane.
         *
         * @param distance distance to the view plane
         * @return this Builder (for chaining)
         */
        public Builder setVpDistance(double distance) {
            _distance = distance;
            return this;
        }

        /**
         * Sets the pixel resolution of the view plane.
         *
         * @param nX number of pixel columns
         * @param nY number of pixel rows
         * @return this Builder (for chaining)
         */
        public Builder setResolution(int nX, int nY) {
            _nX = nX;
            _nY = nY;
            return this;
        }

        /**
         * Sets the ray tracer used to compute pixel colors.
         *
         * @param scene the scene to render
         * @param type  the ray tracer type
         * @return this Builder (for chaining)
         * @throws IllegalArgumentException if the type is not supported
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE)
                _camera._rayTracer = new SimpleRayTracer(scene);
            else
                throw new IllegalArgumentException("Unsupported ray tracer type: " + type);
            return this;
        }

        /**
         * Configures anti-aliasing super-sampling using default shape (SQUARE)
         * and sampling pattern (GRID).
         *
         * @param numSamples number of sample points per pixel (prefer perfect squares: 4, 9, 25…);
         *                   use {@code 1} to disable anti-aliasing
         * @param size       side length of the sampling area; must not exceed the pixel size —
         *                   validated at {@link #build()}; use {@code 0} to disable anti-aliasing
         * @return this Builder (for chaining)
         */
        public Builder setAntiAliasing(int numSamples, double size) {
            return setAntiAliasing(numSamples, size,
                    Blackboard.TargetShape.SQUARE,
                    Blackboard.SamplingPattern.GRID);
        }

        /**
         * Configures anti-aliasing super-sampling with a specified target shape,
         * using the default sampling pattern (GRID).
         *
         * @param numSamples number of sample points per pixel (prefer perfect squares: 4, 9, 25…);
         *                   use {@code 1} to disable anti-aliasing
         * @param size       side length (SQUARE) or radius (CIRCLE) of the sampling area;
         *                   must not exceed the pixel size — validated at {@link #build()};
         *                   use {@code 0} to disable anti-aliasing
         * @param shape      shape of the 2D target area ({@link Blackboard.TargetShape#SQUARE}
         *                   or {@link Blackboard.TargetShape#CIRCLE})
         * @return this Builder (for chaining)
         */
        public Builder setAntiAliasing(int numSamples, double size,
                                       Blackboard.TargetShape shape) {
            return setAntiAliasing(numSamples, size, shape,
                    Blackboard.SamplingPattern.GRID);
        }

        /**
         * Configures anti-aliasing super-sampling with full control over all parameters.
         * <p>
         * When {@code numSamples > 1} and {@code size > 0}, the camera casts multiple rays
         * through each pixel, distributed across the pixel area according to the given
         * shape and pattern, and averages the resulting colors to produce smoother edges.
         * <p>
         * The {@code size} value is validated against the actual pixel dimensions at
         * {@link #build()} — it must not exceed the smaller of pixel width and pixel height.
         *
         * @param numSamples number of sample points per pixel (prefer perfect squares: 4, 9, 25…);
         *                   use {@code 1} to disable anti-aliasing
         * @param size       side length (SQUARE) or radius (CIRCLE) of the sampling area;
         *                   must not exceed the pixel size — validated at {@link #build()};
         *                   use {@code 0} to disable anti-aliasing
         * @param shape      shape of the 2D target area ({@link Blackboard.TargetShape#SQUARE}
         *                   or {@link Blackboard.TargetShape#CIRCLE})
         * @param pattern    sampling pattern ({@link Blackboard.SamplingPattern#GRID},
         *                   {@link Blackboard.SamplingPattern#STOCHASTIC},
         *                   or {@link Blackboard.SamplingPattern#JITTERED})
         * @return this Builder (for chaining)
         * @throws IllegalArgumentException if {@code size} exceeds the pixel size (thrown at {@link #build()})
         */
        public Builder setAntiAliasing(int numSamples, double size,
                                       Blackboard.TargetShape shape,
                                       Blackboard.SamplingPattern pattern) {
            _camera._antiAliasingBlackboard = new Blackboard()
                    .setNumSamples(numSamples)
                    .setSize(size)
                    .setShape(shape)
                    .setSamplingPattern(pattern);
            return this;
        }

        /**
         * Set multi-threading <br>
         * Parameter value meaning:
         * <ul>
         * <li>-2 - number of threads is number of logical processors less 2</li>
         * <li>-1 - stream processing parallelization (implicit multi-threading) is used</li>
         * <li>0 - multi-threading is not activated</li>
         * <li>1 and more - literally number of threads</li>
         * </ul>
         * @param threads number of threads
         * @return builder object itself
         */
        public Builder setMultithreading(int threads) {
            if (threads < -3)
                throw new IllegalArgumentException("Multithreading parameter must be -2 or higher");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                _camera.threadsCount = cores <= 2 ? 1 : cores;
            } else
                _camera.threadsCount = threads;
            return this;
        }
        /**
         * Enables Adaptive Super-Sampling on top of the existing anti-aliasing beam.
         * <p>
         * When enabled, instead of sampling all grid points uniformly, the pixel
         * is recursively subdivided only where color variation exceeds the threshold.
         * <p>
         * Must be combined with {@link #setAntiAliasing} — the beam size from
         * anti-aliasing defines the initial region size for ASS.
         * <p>
         * Recursion depth guide:
         * <ul>
         *   <li>0 — disabled (regular anti-aliasing)</li>
         *   <li>1 — 2×2 sub-regions</li>
         *   <li>2 — 3×3 equivalent</li>
         *   <li>3 — 5×5 equivalent</li>
         *   <li>4 — 9×9 equivalent (high quality)</li>
         * </ul>
         *
         * @param maxDepth        maximum recursion depth (0 = disabled)
         * @param colorThreshold  max per-channel color difference [0–255] to
         *                        consider a region uniform; typical value: 10–30
         * @return this Builder (for chaining)
         */
        public Builder setAdaptiveSuperSampling(int maxDepth, double colorThreshold) {
            if (maxDepth < 0)
                throw new IllegalArgumentException("ASS depth must be non-negative");
            if (colorThreshold < 0)
                throw new IllegalArgumentException("ASS threshold must be non-negative");
            _camera._adaptiveMaxDepth  = maxDepth;
            _camera._adaptiveThreshold = colorThreshold;
            return this;
        }

        /**
         * @param interval printing interval in %
         * @return builder object itself
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0) throw new IllegalArgumentException("interval parameter must be non-negative");
            _camera.printInterval = interval;
            return this;
        }

        /**
         * Validates all parameters and builds the {@link Camera} instance.
         *
         * @return a fully configured Camera
         * @throws MissingResourceException  if a required parameter is missing
         * @throws IllegalArgumentException  if any parameter value is invalid
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            if (_camera._rayTracer == null)
                setRayTracer(new Scene("test"), RayTracerType.SIMPLE);

            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        /**
         * Validates resolution values and initializes the image writer.
         *
         * @throws IllegalArgumentException if resolution values are not positive
         */
        private void checkResolution() {
            if (_nX <= 0 || _nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
            _camera._nX          = _nX;
            _camera._nY          = _nY;
            _camera._imageWriter = new ImageWriter(_nX, _nY);
        }

        /**
         * Validates and computes the camera orientation vectors.
         *
         * @throws MissingResourceException if location or direction is missing
         * @throws IllegalArgumentException if vTo and vUp are parallel
         */
        private void checkLocationAndDirection() {
            if (_location == null)
                throw new MissingResourceException("Missing camera location", "Camera", "p0");
            if (_vUp == null)
                throw new MissingResourceException("Missing up vector", "Camera", "vUp");
            if (_vTo == null && _target == null)
                throw new MissingResourceException("Missing direction", "Camera", "vTo");

            _camera._p0 = _location;

            Vector vTo = (_vTo != null) ? _vTo : _target.subtract(_location);
            _camera._vTo = vTo.normalize();

            try {
                _camera._vRight = _camera._vTo.crossProduct(_vUp).normalize();
            } catch (Exception e) {
                throw new IllegalArgumentException("vTo and vUp cannot be parallel");
            }

            _camera._vUp = _camera._vRight.crossProduct(_camera._vTo).normalize();
        }

        /**
         * Validates view plane parameters and computes pixel dimensions.
         *
         * @throws IllegalArgumentException if size or distance values are not positive
         */
        private void checkViewPlane() {
            if (_width <= 0 || _height <= 0)
                throw new IllegalArgumentException("View plane size must be positive");
            if (_distance <= 0)
                throw new IllegalArgumentException("Distance must be positive");

            _camera._width       = _width;
            _camera._height      = _height;
            _camera._distance    = _distance;
            _camera._vpCenter    = _camera._p0.add(_camera._vTo.scale(_distance));
            _camera._pixelWidth  = _width  / _nX;
            _camera._pixelHeight = _height / _nY;

            // ── Check size of anti-aliasing ──────────────────────────────────────
            double bbSize = _camera._antiAliasingBlackboard.getSize();
            if (!Util.isZero(bbSize)) {
                double minPixel = Math.min(_camera._pixelWidth, _camera._pixelHeight);
                if (bbSize > minPixel)
                    throw new IllegalArgumentException(
                            "Anti-aliasing size (%.4f) must not exceed pixel size (%.4f)"
                                    .formatted(bbSize, minPixel));
            }
        }
    }
}