package renderer;

import lighting.PointLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

class PointLightTests {

    private final Color INTENSITY = new Color(100, 150, 200);
    private final Point POSITION  = new Point(0, 0, 0);

    // ============ getL Tests ============

    /**
     * EP01: getL returns a normalized vector pointing from light position to the point
     */
    @Test
    void testGetL_EP01_normalizedDirection() {
        PointLight light = new PointLight(INTENSITY, POSITION);
        Point p = new Point(3, 4, 0); // distance = 5

        Vector l = light.getL(p);

        // Must be normalized
        assertEquals(1.0, l.length(), 1e-10,
                "getL should return a unit vector");

        // Direction from (0,0,0) toward (3,4,0) → normalized (0.6, 0.8, 0)
        assertEquals(new Vector(3, 4, 0).normalize(), l,
                "getL direction should point from position toward p");
    }

    /**
     * BV01: getL when point coincides with light position — should throw (zero vector)
     */
    @Test
    void testGetL_BV01_pointAtLightPosition() {
        PointLight light = new PointLight(INTENSITY, POSITION);

        assertThrows(Exception.class,
                () -> light.getL(POSITION),
                "getL should throw when point equals light position (zero vector)");
    }

    // ============ getIntensity Tests ============

    /**
     * EP01: with kC=1, kL=0, kQ=0 — intensity should equal original (no attenuation)
     */
    @Test
    void testGetIntensity_EP01_noAttenuation() {
        PointLight light = new PointLight(INTENSITY, POSITION)
                .setKc(1).setKl(0).setKq(0);

        Color result = light.getIntensity(new Point(100, 0, 0));

        assertEquals(INTENSITY, result,
                "With kC=1, kL=0, kQ=0 intensity should equal original intensity");
    }

    /**
     * EP02: with kC=1, kL=0, kQ=1 at distance=2 — attenuation = 1 + 4 = 5
     * Expected intensity = (100/5, 150/5, 200/5) = (20, 30, 40)
     */
    @Test
    void testGetIntensity_EP02_quadraticAttenuation() {
        PointLight light = new PointLight(INTENSITY, POSITION)
                .setKc(1).setKl(0).setKq(1);

        Point p = new Point(2, 0, 0); // distance = 2, attenuation = 1 + 4 = 5
        Color result = light.getIntensity(p);

        assertEquals(new Color(20, 30, 40), result,
                "With kQ=1 at distance=2, intensity should be I0/5");
    }

    /**
     * BV01: point coincides with light position — distance=0, attenuation = kC = 1
     * getIntensity should return I0 / 1 = I0
     */
    @Test
    void testGetIntensity_BV01_pointAtLightPosition() {
        PointLight light = new PointLight(INTENSITY, POSITION)
                .setKc(1).setKl(0).setKq(0);

        Color result = light.getIntensity(POSITION);

        assertEquals(INTENSITY, result,
                "At distance=0 with kC=1, intensity should equal original intensity");
    }
}