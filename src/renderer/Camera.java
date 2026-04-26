package renderer;
import java.util.MissingResourceException;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a camera in a 3D rendering system.
 * The camera is responsible for generating rays through pixels on a view plane.
 */
public class Camera implements Cloneable  {

    private Point _p0;
    private Vector _vTo,_vUp,_vRight;
    private double _width, _height, _distance;
    private int _nX = 1, _nY = 1;
    private Point _vpCenter;
    private double _pixelWidth,_pixelHeight;

    private Camera(){}

    /**
     * Returns a new Builder instance for constructing a Camera.
     *
     * @return a Camera Builder
     */
    public static Builder getBuilder() { return new Builder(); }

    /**
     * Constructs a ray passing through a specific pixel on the view plane.
     *
     * @param xIndex the column index of the pixel
     * @param yIndex the row index of the pixel
     * @return a Ray going from the camera through the specified pixel
     */
    public Ray constructRay(int xIndex, int yIndex) { return null; }


    /**
     * Builder class for constructing and configuring a Camera instance.
     */
    public static class Builder {
        private final Camera _camera = new Camera();

        /**
         * Sets the location of the camera in 3D space.
         *
         * @param location the camera position
         * @return the Builder instance for chaining
         */
        public Builder setLocation(Point location){
            _camera._p0 = location;
            return this;
        }

        /**
         * Sets the camera direction using forward (to) and up vectors.
         *
         * @param to forward direction vector
         * @param up upward direction vector
         * @return the Builder instance for chaining
         */
        public Builder setDirection(Vector to, Vector up){
            _camera._vTo = to;
            _camera._vUp = up;
            return this;
        }

        /**
         * Sets the camera direction toward a target point with an up vector.
         *
         * @param target the point the camera looks at
         * @param up upward direction vector
         * @return the Builder instance for chaining
         */
        public Builder setDirection(Point target, Vector up){
            _camera._vUp = up;
            if (_camera._p0 != null && target != null) {
                _camera._vTo = target.subtract(_camera._p0);
            } else {
                _camera._vTo = null;
            }
            return this;
        }

        /**
         * Sets the camera direction toward a target point with default up vector handling.
         *
         * @param target the point the camera looks at
         * @return the Builder instance for chaining
         */
        public Builder setDirection(Point target){
            _camera._vUp = Vector.AXIS_Y; // Y axis
            if (_camera._p0 != null && target != null) {
                _camera._vTo = target.subtract(_camera._p0);
            } else {
                _camera._vTo = null;
            }
            return this;
        }

        /**
         * Sets the size of the view plane.
         *
         * @param width view plane width
         * @param height view plane height
         * @return the Builder instance for chaining
         */
        public Builder setVpSize(double width, double height){
            _camera._width = width;
            _camera._height = height;
            return this;
        }

        /**
         * Sets the distance between the camera and the view plane.
         *
         * @param distance distance from camera to view plane
         * @return the Builder instance for chaining
         */
        public Builder setVpDistance(double distance){
            _camera._distance = distance;
            return this;
        }

        /**
         * Sets the resolution of the view plane.
         *
         * @param nX number of columns (pixels in X axis)
         * @param nY number of rows (pixels in Y axis)
         * @return the Builder instance for chaining
         */
        public Builder setResolution(int nX, int nY){
            _camera._nX = nX;
            _camera._nY = nY;
            return this;
        }

        /**
         * Builds and returns a configured Camera instance after validation.
         *
         * @return a fully constructed Camera object
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();
            try {
                return (Camera)_camera.clone();
            } catch (CloneNotSupportedException _) {
                return null;
            }
        }

        /**
         * Validates resolution parameters of the view plane.
         */
        private void checkResolution() {
            if (_camera._nX <= 0 || _camera._nY <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");
            }
        }

        /**
         * Validates camera location and direction parameters.
         */
        private void checkLocationAndDirection() {

            if (_camera._p0 == null)
                throw new MissingResourceException("Missing camera location", "Camera", "p0");

            if (_camera._vUp == null)
                throw new MissingResourceException("Missing up vector", "Camera", "vUp");

            if (_camera._vTo == null)
                throw new MissingResourceException("Missing direction", "Camera", "vTo");

            // normalize vTo
            _camera._vTo = _camera._vTo.normalize();

            // vRight = vTo x vUp
            Vector vRight;
            try {
                vRight = _camera._vTo.crossProduct(_camera._vUp).normalize();
            } catch (Exception e) {
                throw new IllegalArgumentException("vTo and vUp cannot be parallel");
            }

            _camera._vRight = vRight;

            // vUp = vRight x vTo (orthogonal fix)
            _camera._vUp = _camera._vRight.crossProduct(_camera._vTo).normalize();
        }

        private void checkViewPlane() {

            if (_camera._width <= 0 || _camera._height <= 0)
                throw new IllegalArgumentException("View plane size must be positive");

            if (_camera._distance <= 0)
                throw new IllegalArgumentException("Distance must be positive");

            // center point
            _camera._vpCenter = _camera._p0.add(_camera._vTo.scale(_camera._distance));

            // pixel size
            _camera._pixelWidth = _camera._width / _camera._nX;
            _camera._pixelHeight = _camera._height / _camera._nY;
        }

    }

}