package renderer;

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
    private Vector _vpCenter;
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
        public Builder setLocation(Point location){return  this;}

        /**
         * Sets the camera direction using forward (to) and up vectors.
         *
         * @param to forward direction vector
         * @param up upward direction vector
         * @return the Builder instance for chaining
         */
        public  Builder setDirection(Vector to, Vector up){return  this;}

        /**
         * Sets the camera direction toward a target point with an up vector.
         *
         * @param target the point the camera looks at
         * @param up upward direction vector
         * @return the Builder instance for chaining
         */
        public  Builder setDirection(Point target, Vector up) {return  this;}

        /**
         * Sets the camera direction toward a target point with default up vector handling.
         *
         * @param target the point the camera looks at
         * @return the Builder instance for chaining
         */
        public  Builder setDirection(Point target) {return  this;}

        /**
         * Sets the size of the view plane.
         *
         * @param width view plane width
         * @param height view plane height
         * @return the Builder instance for chaining
         */
        public  Builder setVpSize(double width, double height) {return  this;}

        /**
         * Sets the distance between the camera and the view plane.
         *
         * @param distance distance from camera to view plane
         * @return the Builder instance for chaining
         */
        public  Builder setVpDistance(double distance) {return  this;}

        /**
         * Sets the resolution of the view plane.
         *
         * @param nX number of columns (pixels in X axis)
         * @param nY number of rows (pixels in Y axis)
         * @return the Builder instance for chaining
         */
        public  Builder setResolution(int nX, int nY)  {return  this;}

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
         * Validates view plane parameters before building the camera.
         */
        private void checkViewPlane() {
        }

        /**
         * Validates camera location and direction parameters.
         */
        private void checkLocationAndDirection() {
        }

        /**
         * Validates resolution parameters of the view plane.
         */
        private void checkResolution() {
        }
    }

}