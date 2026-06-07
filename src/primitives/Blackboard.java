package primitives;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a target area for super-sampling beam generation.
 * <p>
 * Generates 2D sample offsets within a configurable target area
 * and maps them to 3D points in world space.
 * Used by Camera (anti-aliasing, depth of field) and
 * SimpleRayTracer (soft shadows, glossy surfaces).
 * <p>
 * Configurable parameters:
 * <ul>
 *   <li>Size — side length (SQUARE) or radius (CIRCLE) of the target area</li>
 *   <li>Number of samples</li>
 *   <li>Target area shape — {@link TargetShape#SQUARE} or {@link TargetShape#CIRCLE}</li>
 *   <li>Sampling pattern — {@link SamplingPattern#GRID}, {@link SamplingPattern#STOCHASTIC},
 *       or {@link SamplingPattern#JITTERED}</li>
 * </ul>
 * <p>
 * To disable super-sampling: set {@code numSamples=1} or {@code size=0}.
 */
public class Blackboard {

    // ── Enums ────────────────────────────────────────────────────────────────

    /**
     * Shape of the 2D target area.
     */
    public enum TargetShape {
        /** Square target area — simple, uniform, compatible with adaptive super-sampling. */
        SQUARE,
        /** Circular target area — more isotropic, avoids diagonal bias of a square. */
        CIRCLE
    }

    /**
     * Pattern used to distribute sample points within the target area.
     */
    public enum SamplingPattern {
        /**
         * Regular grid — deterministic, uniform coverage, fastest.
         * Currently implemented. Best for reproducibility and adaptive super-sampling.
         */
        GRID,
        /**
         * Random (stochastic) — non-deterministic, avoids grid regularity artifacts.
         * Infrastructure is in place; full implementation is a future extension.
         */
        STOCHASTIC,
        /**
         * Jittered — combines Grid structure with a small random offset per cell.
         * Best overall quality. Infrastructure is in place; full implementation
         * is a future extension.
         */
        JITTERED
    }

    // ── Fields ───────────────────────────────────────────────────────────────

    /** Side length (SQUARE) or radius (CIRCLE) of the target area. */
    private double _size = 0;

    /**
     * Total number of sample points.
     * For GRID, prefer perfect squares: 1, 4, 9, 25, 81, 289...
     */
    private int _numSamples = 1;

    /** Shape of the target area. Default: SQUARE. */
    private TargetShape _shape = TargetShape.SQUARE;

    /** Sampling pattern. Default: GRID. */
    private SamplingPattern _pattern = SamplingPattern.GRID;

    /**
     * Cached list of 2D offsets.
     * Null when any parameter has changed and the cache must be recomputed.
     * Only used for the GRID pattern (deterministic); other patterns
     * are not cached because they involve randomness.
     */
    private List<Double2> _samples = null;

    // ── Setters ──────────────────────────────────────────────────────────────

    /**
     * Sets the size of the target area.
     * For SQUARE: side length. For CIRCLE: radius.
     * Passing {@code 0} disables super-sampling.
     * Invalidates the sample cache.
     *
     * @param size side length or radius; use {@code 0} to disable super-sampling
     * @return this {@code Blackboard} (for chaining)
     */
    public Blackboard setSize(double size) {
        _size = size;
        _samples = null;
        return this;
    }

    /**
     * Sets the total number of sample points.
     * For the GRID pattern, prefer perfect squares: 4, 9, 25, 81, 289...
     * Passing {@code 1} disables super-sampling.
     * Invalidates the sample cache.
     *
     * @param numSamples number of samples; use {@code 1} to disable super-sampling
     * @return this {@code Blackboard} (for chaining)
     */
    public Blackboard setNumSamples(int numSamples) {
        _numSamples = numSamples;
        _samples = null;
        return this;
    }

    /**
     * Sets the shape of the 2D target area.
     * Invalidates the sample cache.
     *
     * @param shape {@link TargetShape#SQUARE} or {@link TargetShape#CIRCLE}
     * @return this {@code Blackboard} (for chaining)
     */
    public Blackboard setShape(TargetShape shape) {
        _shape = shape;
        _samples = null;
        return this;
    }

    /**
     * Sets the sampling pattern used to distribute points within the target area.
     * Only {@link SamplingPattern#GRID} is currently implemented.
     * {@link SamplingPattern#STOCHASTIC} and {@link SamplingPattern#JITTERED}
     * are defined as future extensions and will throw
     * {@link UnsupportedOperationException} if selected.
     * Invalidates the sample cache.
     *
     * @param pattern the desired sampling pattern
     * @return this {@code Blackboard} (for chaining)
     */
    public Blackboard setSamplingPattern(SamplingPattern pattern) {
        _pattern = pattern;
        _samples = null;
        return this;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    /**
     * Returns the size of the target area (side length or radius).
     *
     * @return size of the target area
     */
    public double getSize() { return _size; }

    /**
     * Returns the total number of configured samples.
     *
     * @return number of samples
     */
    public int getNumSamples() { return _numSamples; }

    /**
     * Returns the shape of the target area.
     *
     * @return {@link TargetShape} of this blackboard
     */
    public TargetShape getShape() { return _shape; }

    /**
     * Returns the sampling pattern.
     *
     * @return {@link SamplingPattern} of this blackboard
     */
    public SamplingPattern getSamplingPattern() { return _pattern; }

    // ── Core API ─────────────────────────────────────────────────────────────

    /**
     * Maps the 2D sample offsets to 3D points in world space.
     * <p>
     * Formula: {@code point = center + offset.x * vX + offset.y * vY}
     * <p>
     * If super-sampling is disabled ({@code numSamples <= 1} or {@code size = 0}),
     * returns a single-element list containing {@code center}.
     *
     * @param center the center point of the target area in 3D space
     * @param vX     local X axis of the target area (must be normalized)
     * @param vY     local Y axis of the target area (must be normalized)
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

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Returns the list of 2D sample offsets for the current configuration.
     * <p>
     * For {@link SamplingPattern#GRID}: computes once and caches the result.
     * The cache is invalidated whenever any parameter changes via a setter.
     * <p>
     * {@link SamplingPattern#STOCHASTIC} and {@link SamplingPattern#JITTERED}
     * are not yet implemented and throw {@link UnsupportedOperationException}.
     *
     * @return list of 2D offsets covering the configured target area
     * @throws UnsupportedOperationException if the selected pattern is not yet implemented
     */
    private List<Double2> getSamples() {

        // super-sampling disabled: single center point
        if (_numSamples <= 1 || Util.isZero(_size)) {
            _samples = List.of(new Double2(0, 0));
            return _samples;
        }

        // GRID: return cache if valid
        if (_pattern == SamplingPattern.GRID && _samples != null)
            return _samples;

        _samples = switch (_pattern) {
            case GRID       -> generateGrid();
            case STOCHASTIC -> throw new UnsupportedOperationException(
                    "STOCHASTIC sampling is not yet implemented");
            case JITTERED   -> throw new UnsupportedOperationException(
                    "JITTERED sampling is not yet implemented");
        };

        return _samples;
    }

    /**
     * Generates a regular grid of sample offsets.
     * <p>
     * Divides the target area into {@code gridN x gridN} equal cells
     * and places one sample at the center of each cell, where
     * {@code gridN = round(sqrt(numSamples))}.
     * <p>
     * For {@link TargetShape#CIRCLE}: generates a grid over the enclosing square
     * and filters out points that fall outside the circle.
     * The grid is expanded by a factor of {@code sqrt(4/pi) ~= 1.273}
     * to compensate for the filtered points, as recommended in the project spec.
     *
     * @return list of grid sample offsets
     */
    private List<Double2> generateGrid() {
        List<Double2> samples = new ArrayList<>();
        int gridN = (int) Math.round(Math.sqrt(_numSamples));

        if (_shape == TargetShape.SQUARE) {
            double step  = _size / gridN;
            double start = -_size / 2.0 + step / 2.0;

            for (int row = 0; row < gridN; row++)
                for (int col = 0; col < gridN; col++)
                    samples.add(new Double2(start + col * step, start + row * step));

        } else { // CIRCLE
            // expand grid to compensate for points filtered outside the circle
            // ratio of square area to circle area = 4/pi ≈ 1.273
            int    expandedN = (int) Math.ceil(gridN * Math.sqrt(4.0 / Math.PI));
            double step      = (2.0 * _size) / expandedN;
            double start     = -_size + step / 2.0;

            for (int row = 0; row < expandedN; row++)
                for (int col = 0; col < expandedN; col++) {
                    double x = start + col * step;
                    double y = start + row * step;
                    if (x * x + y * y <= _size * _size)
                        samples.add(new Double2(x, y));
                }
        }

        return samples;
    }
}