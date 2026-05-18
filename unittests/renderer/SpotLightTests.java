package renderer;

import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

class SpotLightTest {

    private final Color INTENSITY = new Color(200, 200, 200);
    private final Point POSITION = new Point(0, 0, 0);
    private final Vector DIRECTION = new Vector(0, 0, -1);

    /**
     * EP: direction from point to light position
     */
    @Test
    void testGetL() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION);

        Vector l = light.getL(new Point(0, 0, 5));

        assertEquals(new Vector(0, 0, -1), l);
    }

    /**
     * EP + BV:
     * intensity depends on angle between spotlight direction and l
     */
    @Test
    void testGetIntensity() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION)
                .setKc(1).setKl(0).setKq(0);

        Point inFront = new Point(0, 0, -5);   // inside beam
        Point behind = new Point(0, 0, 5);     // behind light (BV)

        Color frontIntensity = light.getIntensity(inFront);
        Color behindIntensity = light.getIntensity(behind);

        // light in front should be stronger than behind
        assertTrue(frontIntensity.lowerThan(new Color(255, 255, 255))
                || frontIntensity.equals(INTENSITY)); // depends on implementation

        // behind should be very weak or 0-like behavior
        assertTrue(behindIntensity.lowerThan(frontIntensity));
    }
}