package renderer;

import lighting.DirectionalLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

class DirectionalLightTest {

    private final Color INTENSITY = new Color(100, 150, 200);
    private final Vector DIRECTION = new Vector(-1, -1, -1).normalize();

    /**
     * TC01 - intensity is constant for all points (Directional light has no attenuation)
     */
    @Test
    void testGetIntensity() {
        DirectionalLight light = new DirectionalLight(INTENSITY, DIRECTION);

        Point p1 = new Point(0, 0, 0);
        Point p2 = new Point(10, -5, 2);
        Point p3 = new Point(1, 2, 3);

        assertEquals(INTENSITY, light.getIntensity(p1));
        assertEquals(INTENSITY, light.getIntensity(p2));
        assertEquals(INTENSITY, light.getIntensity(p3));
    }

    /**
     * TC02 - direction is constant and normalized
     */
    @Test
    void testGetL() {
        DirectionalLight light = new DirectionalLight(INTENSITY, DIRECTION);

        Vector l1 = light.getL(new Point(1, 1, 1));
        Vector l2 = light.getL(new Point(-5, 2, 0));

        // normalized
        assertEquals(1.0, l1.length(), 1e-10);

        // same direction for all points
        assertEquals(l1, l2);

        // correct direction
        assertEquals(DIRECTION, l1);
    }
}