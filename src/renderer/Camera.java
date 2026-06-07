package renderer;

import java.util.List;
import java.util.MissingResourceException;
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

    /**
     * Renders the image by casting rays through every pixel
     * of the view plane and writing the resulting colors to the image.
     *
     * @return this Camera (for chaining)
     */
    public Camera renderImage() {
        for (int i = 0; i < _nX; i++)
            for (int j = 0; j < _nY; j++)
                castRay(i, j);
        return this;
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
        Blackboard bb = _antiAliasingBlackboard;
        Point center = getPixelCenter(xIndex, yIndex);

        // anti-aliasing disabled: single ray through pixel center
        if (bb.getNumSamples() <= 1 || Util.isZero(bb.getSize()))
            return _rayTracer.traceRay(new Ray(_p0, center.subtract(_p0)));

        // anti-aliasing enabled: generate sample points inside the pixel
        List<Point> samplePoints = bb.generatePoints(center, _vRight, _vUp);

        Color sum = Color.BLACK;
        for (Point p : samplePoints)
            sum = sum.add(_rayTracer.traceRay(new Ray(_p0, p.subtract(_p0))));

        // divide by total sample count (not by hits) — physically correct average
        return sum.reduce(samplePoints.size());
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
         * Configures the camera's anti-aliasing super-sampling behavior.
         * <p>
         * When a {@link Blackboard} with more than one sample and a non-zero size
         * is provided, the camera casts multiple rays through each pixel
         * (distributed across the pixel area) and averages the resulting colors,
         * producing smoother edges and reducing pixelation artifacts.
         * <p>
         * To disable anti-aliasing, pass a default {@code new Blackboard()}
         * or set {@code numSamples=1} / {@code size=0}.
         *
         * @param blackboard the sampling configuration; must not be {@code null}
         * @return this Builder (for chaining)
         */
        public Builder setAntiAliasing(Blackboard blackboard) {
            _camera._antiAliasingBlackboard = blackboard;
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
        }
    }
}