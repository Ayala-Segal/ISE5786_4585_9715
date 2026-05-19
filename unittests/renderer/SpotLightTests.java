package renderer;

import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

class SpotLightTests {

    private final Color INTENSITY = new Color(200, 200, 200);
    private final Point POSITION  = new Point(0, 0, 0);
    private final Vector DIRECTION = new Vector(0, 0, -1); // pointing toward -Z

    // ============ getL Tests ============

    /**
     * EP01: getL returns normalized direction from position to point
     */
    @Test
    void testGetL_EP01_normalizedDirection() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION);
        Point p = new Point(0, 0, -5);

        Vector l = light.getL(p);

        // Must be normalized
        assertEquals(1.0, l.length(), 1e-10,
                "getL should return a unit vector");

        // Direction from (0,0,0) to (0,0,-5) → (0,0,-1)
        assertEquals(new Vector(0, 0, -1), l,
                "getL direction should point from position toward p");
    }

    /**
     * BV01: getL when point coincides with light position — should throw (zero vector)
     */
    @Test
    void testGetL_BV01_pointAtLightPosition() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION);

        assertThrows(Exception.class,
                () -> light.getL(POSITION),
                "getL should throw when point equals light position (zero vector)");
    }

    // ============ getIntensity Tests ============

    /**
     * EP01: point directly in front of spotlight — cos(angle)=1, attenuation=1
     * Expected intensity = INTENSITY * 1 / 1 = INTENSITY
     */
    @Test
    void testGetIntensity_EP01_pointInFrontOfSpot() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION)
                .setKc(1).setKl(0).setKq(0);

        // Point at (0,0,-1): l=(0,0,-1), direction=(0,0,-1), cos = dot((0,0,-1),(0,0,-1)) = 1
        Color result = light.getIntensity(new Point(0, 0, -1));

        assertEquals(INTENSITY, result,
                "Point directly in front of spotlight should receive full intensity");
    }

    /**
     * EP02: point behind spotlight — cos < 0 → clamped to 0 → intensity = BLACK
     * Point at (0,0,5): l=(0,0,1), direction=(0,0,-1), cos = -1 → max(0,-1) = 0
     */
    @Test
    void testGetIntensity_EP02_pointBehindSpot() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION)
                .setKc(1).setKl(0).setKq(0);

        Color result = light.getIntensity(new Point(0, 0, 5));

        assertEquals(Color.BLACK, result,
                "Point behind spotlight should receive zero intensity");
    }

    /**
     * BV01: point at 90° to spotlight direction — cos = 0 → intensity = BLACK
     * Point at (1,0,0): l=(1,0,0), direction=(0,0,-1), cos = dot = 0
     */
    @Test
    void testGetIntensity_BV01_pointAt90Degrees() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION)
                .setKc(1).setKl(0).setKq(0);

        Color result = light.getIntensity(new Point(1, 0, 0));

        assertEquals(Color.BLACK, result,
                "Point at 90° to spotlight direction should receive zero intensity");
    }

    /**
     * BV02: point coincides with light position — getL throws → getIntensity throws too
     */
    @Test
    void testGetIntensity_BV02_pointAtLightPosition() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION)
                .setKc(1).setKl(0).setKq(0);

        assertThrows(Exception.class,
                () -> light.getIntensity(POSITION),
                "getIntensity at light position should throw (zero vector in getL)");
    }
}