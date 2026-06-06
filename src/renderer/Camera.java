package renderer;

import java.util.List;
import java.util.MissingResourceException;

import primitives.*;
import scene.Scene;

/**
 * Represents a camera in a 3D rendering system.
 * The camera generates rays through pixels on a view plane,
 * traces them via a ray tracer, and writes the resulting colors
 * to an image.
 */
public class Camera implements Cloneable {

    private Point _p0;
    private Vector _vTo, _vUp, _vRight;
    private double _width, _height, _distance;
    private int _nX = 1, _nY = 1;
    private Point _vpCenter;
    private double _pixelWidth, _pixelHeight;

    private ImageWriter _imageWriter;
    private RayTracerBase _rayTracer;

    private Blackboard _antiAliasingBlackboard = new Blackboard();

    private Camera() {}

    /**
     * Returns a builder for constructing a Camera instance.
     *
     * @return a Builder object
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray passing through a given pixel on the view plane.
     *
     * @param xIndex column index of the pixel
     * @param yIndex row index of the pixel
     * @return a Ray from the camera through the pixel
     */
    public Ray constructRay(int xIndex, int yIndex) {
        Point pc = _vpCenter;

        double xOffset = (xIndex - (_nX - 1) / 2.0) * _pixelWidth;
        double yOffset = -((yIndex - (_nY - 1) / 2.0) * _pixelHeight);

        Point pij = pc;

        if (xOffset != 0)
            pij = pij.add(_vRight.scale(xOffset));

        if (yOffset != 0)
            pij = pij.add(_vUp.scale(yOffset));

        Vector dir = pij.subtract(_p0).normalize();

        return new Ray(_p0, dir);
    }

    /**
     * Renders the image by casting a ray through every pixel
     * of the view plane and writing the resulting color into
     * the image writer.
     *
     * @return this Camera (for chaining)
     */
    public Camera renderImage() {
        for (int i = 0; i < _nX; i++) {
            for (int j = 0; j < _nY; j++) {
                castRay(i, j);
            }
        }
        return this;
    }

    /**
     * Casts a ray through the specified pixel, traces it,
     * and writes the resulting color to the image.
     *
     * @param xIndex column index of the pixel
     * @param yIndex row index of the pixel
     */
//    private void castRay(int xIndex, int yIndex) {
//        Ray ray = constructRay(xIndex, yIndex);
//        Color color = _rayTracer.traceRay(ray);
//        _imageWriter.writePixel(xIndex, yIndex, color);
//    }

    private void castRay(int xIndex, int yIndex) {
        Color color = castRayBeam(xIndex, yIndex);
        _imageWriter.writePixel(xIndex, yIndex, color);
    }

    private Color castRayBeam(int xIndex, int yIndex) {
        // center point of this pixel on the view plane
        Point pixelCenter = getPixelCenter(xIndex, yIndex);

        Blackboard bb = _antiAliasingBlackboard;

        // no super-sampling
        if (bb.getNumSamples() <= 1 || Util.isZero(bb.getSize())) {
            return _rayTracer.traceRay(new Ray(_p0, pixelCenter.subtract(_p0)));
        }

        // generate sample points inside the pixel
        List<Point> samplePoints = bb.generatePoints(pixelCenter, _vRight, _vUp);

        Color sum = Color.BLACK;
        for (Point p : samplePoints) {
            Ray ray = new Ray(_p0, p.subtract(_p0));
            sum = sum.add(_rayTracer.traceRay(ray));
        }

        return sum.reduce(samplePoints.size());
    }

    private Point getPixelCenter(int xIndex, int yIndex) {
        double xOffset = (xIndex - (_nX - 1) / 2.0) * _pixelWidth;
        double yOffset = -((yIndex - (_nY - 1) / 2.0) * _pixelHeight);

        Point p = _vpCenter;
        if (!Util.isZero(xOffset)) p = p.add(_vRight.scale(xOffset));
        if (!Util.isZero(yOffset)) p = p.add(_vUp.scale(yOffset));
        return p;
    }
    /**
     * Draws a grid over the rendered image with the given interval
     * and color. Pixels whose row or column index is a multiple of
     * the interval are colored.
     *
     * @param interval spacing between grid lines (in pixels)
     * @param color    the color of the grid lines
     * @return this Camera (for chaining)
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < _nX; i++) {
            for (int j = 0; j < _nY; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    _imageWriter.writePixel(i, j, color);
                }
            }
        }
        return this;
    }

    /**
     * Writes the image to a file with the given name using the
     * underlying image writer.
     *
     * @param imageName name of the output image file
     * @return this Camera (for chaining)
     */
    public Camera writeToImage(String imageName) {
        _imageWriter.writeToImage(imageName);
        return this;
    }

    /**
     * Builder class for constructing a Camera instance.
     */
    public static class Builder {

        private final Camera _camera = new Camera();

        private Point _location = null;
        private Vector _vTo = null;
        private Point _target = null;
        private Vector _vUp = Vector.AXIS_Y;
        private double _width = 0;
        private double _height = 0;
        private double _distance = 0;
        private int _nX = 1, _nY = 1;

        /**
         * Sets the camera location.
         *
         * @param location camera position
         * @return the Builder instance
         */
        public Builder setLocation(Point location) {
            _location = location;
            return this;
        }

        /**
         * Sets the camera direction using forward and up vectors.
         *
         * @param to forward direction vector
         * @param up upward direction vector
         * @return the Builder instance
         */
        public Builder setDirection(Vector to, Vector up) {
            _vTo = to;
            _target = null;
            _vUp = up;
            return this;
        }

        /**
         * Sets the camera direction toward a target point with a given up vector.
         *
         * @param target target point
         * @param up upward direction vector
         * @return the Builder instance
         */
        public Builder setDirection(Point target, Vector up) {
            _target = target;
            _vTo = null;
            _vUp = up;
            return this;
        }

        /**
         * Sets the camera direction toward a target point with default up vector.
         *
         * @param target target point
         * @return the Builder instance
         */
        public Builder setDirection(Point target) {
            _target = target;
            _vTo = null;
            _vUp = Vector.AXIS_Y;
            return this;
        }

        /**
         * Sets the view plane size.
         *
         * @param width view plane width
         * @param height view plane height
         * @return the Builder instance
         */
        public Builder setVpSize(double width, double height) {
            _width = width;
            _height = height;
            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         *
         * @param distance distance to view plane
         * @return the Builder instance
         */
        public Builder setVpDistance(double distance) {
            _distance = distance;
            return this;
        }

        /**
         * Sets the resolution of the view plane.
         *
         * @param nX number of columns
         * @param nY number of rows
         * @return the Builder instance
         */
        public Builder setResolution(int nX, int nY) {
            _nX = nX;
            _nY = nY;
            return this;
        }

        /**
         * Sets the ray tracer for the camera based on the requested type.
         *
         * @param scene the scene to be rendered
         * @param type  the ray tracer type
         * @return the Builder instance
         * @throws IllegalArgumentException if the type is not supported
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                _camera._rayTracer = new SimpleRayTracer(scene);
            } else {
                throw new IllegalArgumentException("Unsupported ray tracer type: " + type);
            }
            return this;
        }

        /**
         * Configures the camera's anti-aliasing super-sampling behavior.
         * <p>
         * When a {@link Blackboard} with more than one sample and a non-zero size
         * is provided, the camera will cast multiple rays through each pixel
         * (distributed across the pixel area) and average the resulting colors,
         * producing smoother edges and reducing pixelation artifacts.
         * <p>
         * To disable anti-aliasing, pass a default {@code Blackboard} (1 sample or size 0).
         *
         * @param blackboard the sampling configuration for anti-aliasing;
         *                   must not be {@code null}
         * @return this {@code Builder} instance (for chaining)
         */
        public Builder setAntiAliasing(Blackboard blackboard) {
            _camera._antiAliasingBlackboard = blackboard;
            return this;
        }

        /**
         * Builds and returns a Camera instance after validation.
         *
         * @return a Camera object
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            if (_camera._rayTracer == null) {
                setRayTracer(new Scene("test"), RayTracerType.SIMPLE);
            }

            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        /**
         * Validates resolution values and updates the camera, including
         * the underlying image writer.
         */
        private void checkResolution() {
            if (_nX <= 0 || _nY <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");
            }
            _camera._nX = _nX;
            _camera._nY = _nY;
            _camera._imageWriter = new ImageWriter(_nX, _nY);
        }

        /**
         * Validates location and direction and updates the camera.
         */
        private void checkLocationAndDirection() {

            if (_location == null)
                throw new MissingResourceException("Missing camera location", "Camera", "p0");

            if (_vUp == null)
                throw new MissingResourceException("Missing up vector", "Camera", "vUp");

            if (_vTo == null && _target == null)
                throw new MissingResourceException("Missing direction", "Camera", "vTo");

            _camera._p0 = _location;

            Vector vTo = _vTo;
            if (vTo == null) {
                vTo = _target.subtract(_location);
            }

            _camera._vTo = vTo.normalize();

            try {
                _camera._vRight = _camera._vTo.crossProduct(_vUp).normalize();
            } catch (Exception e) {
                throw new IllegalArgumentException("vTo and vUp cannot be parallel");
            }

            _camera._vUp = _camera._vRight.crossProduct(_camera._vTo).normalize();
        }

        /**
         * Validates view plane parameters and updates the camera.
         */
        private void checkViewPlane() {

            if (_width <= 0 || _height <= 0)
                throw new IllegalArgumentException("View plane size must be positive");

            if (_distance <= 0)
                throw new IllegalArgumentException("Distance must be positive");

            _camera._width = _width;
            _camera._height = _height;
            _camera._distance = _distance;

            _camera._vpCenter = _camera._p0.add(_camera._vTo.scale(_distance));

            _camera._pixelWidth = _width / _nX;
            _camera._pixelHeight = _height / _nY;
        }
    }

}
