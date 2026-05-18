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
    private final Point P = new Point(1, 2, 3);

    /**
     * EP: intensity is constant everywhere
     */
    @Test
    void testGetIntensity() {
        DirectionalLight light = new DirectionalLight(INTENSITY, DIRECTION);

        assertEquals(INTENSITY, light.getIntensity(P));
        assertEquals(INTENSITY, light.getIntensity(new Point(0, 0, 0)));
        assertEquals(INTENSITY, light.getIntensity(new Point(10, -5, 2)));
    }

    /**
     * EP: direction is constant and normalized
     */
    @Test
    void testGetL() {
        DirectionalLight light = new DirectionalLight(INTENSITY, DIRECTION);

        Vector l = light.getL(P);

        assertEquals(1.0, l.length(), 1e-10);
        assertEquals(DIRECTION, l);
    }
}