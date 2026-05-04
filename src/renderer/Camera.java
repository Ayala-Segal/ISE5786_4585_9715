package renderer;

import java.util.MissingResourceException;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a camera in a 3D rendering system.
 * The camera generates rays through pixels on a view plane.
 */
public class Camera implements Cloneable {

    private Point _p0;
    private Vector _vTo, _vUp, _vRight;
    private double _width, _height, _distance;
    private int _nX = 1, _nY = 1;
    private Point _vpCenter;
    private double _pixelWidth, _pixelHeight;

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
         * Builds and returns a Camera instance after validation.
         *
         * @return a Camera object
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();
            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        /**
         * Validates resolution values and updates the camera.
         */
        private void checkResolution() {
            if (_nX <= 0 || _nY <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");
            }
            _camera._nX = _nX;
            _camera._nY = _nY;
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