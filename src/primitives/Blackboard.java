package primitives;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a target area for super-sampling beam generation.
 * Generates 2D sample offsets within a square target area using a Grid pattern.
 * Used by Camera (anti-aliasing, depth of field) and SimpleRayTracer (soft shadows, glossy).
 */
public class Blackboard {

    private double _size = 0;       // side length of the square target area
    private int    _numSamples = 1; // total samples (must be a perfect square for Grid)

    // cached list of 2D offsets — regenerated only when parameters change
    private List<Double2> _samples = null;

    /**
     * Sets the side length of the square target area.
     * Invalidates the cached sample list.
     */
    public Blackboard setSize(double size) {
        _size = size;
        _samples = null;
        return this;
    }

    /**
     * Sets the number of samples (use perfect squares: 1, 9, 25, 81...).
     * Invalidates the cached sample list.
     */
    public Blackboard setNumSamples(int numSamples) {
        _numSamples = numSamples;
        _samples = null;
        return this;
    }

    public double getSize()       { return _size; }
    public int    getNumSamples() { return _numSamples; }

    /**
     * Returns (and caches) the 2D sample offsets.
     * For numSamples=1 or size=0, returns a single (0,0) offset.
     */
    private List<Double2> getSamples() {
        if (_samples != null) return _samples;

        _samples = new ArrayList<>();

        if (_numSamples <= 1 || Util.isZero(_size)) {
            _samples.add(new Double2(0, 0));
            return _samples;
        }

        int gridN = (int) Math.round(Math.sqrt(_numSamples));
        double step = _size / gridN;
        double start = -_size / 2.0 + step / 2.0;

        for (int row = 0; row < gridN; row++) {
            for (int col = 0; col < gridN; col++) {
                double x = start + col * step;
                double y = start + row * step;
                _samples.add(new Double2(x, y));
            }
        }

        return _samples;
    }

    /**
     * Maps the 2D sample offsets to 3D points in space.
     *
     * @param center the center point of the target area
     * @param vX     local X axis of the target area (normalized)
     * @param vY     local Y axis of the target area (normalized)
     * @return list of 3D target points
     */
    public List<Point> generatePoints(Point center, Vector vX, Vector vY) {
        List<Point> points = new ArrayList<>();

        for (Double2 offset : getSamples()) {
            Point p = center;
            if (!Util.isZero(offset.x()))
                p = p.add(vX.scale(offset.x()));
            if (!Util.isZero(offset.y()))
                p = p.add(vY.scale(offset.y()));
            points.add(p);
        }

        return points;
    }
}