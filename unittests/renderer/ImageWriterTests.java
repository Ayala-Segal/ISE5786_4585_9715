package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

/**
 * Test class for {@link ImageWriter}.
 * <p>
 * This test creates a basic image consisting of a colored background
 * with a grid overlay. The purpose is to verify the correct functionality
 * of pixel writing and image exporting.
 * </p>
 */
public class ImageWriterTests {

    /** Image width in pixels */
    private static final int WIDTH = 800;

    /** Image height in pixels */
    private static final int HEIGHT = 500;

    /** Size of each grid cell in pixels */
    private static final int CELL_SIZE = 50;

    /** Background color of the image */
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 0);

    /** Grid line color */
    private static final Color GRID_COLOR = new Color(255, 0, 0);

    /**
     * Test method for creating a grid image.
     * <p>
     * The method generates an image with a uniform background color
     * and overlays grid lines at fixed intervals using a nested loop.
     * Each pixel is colored according to whether it lies on a grid line
     * or within a cell.
     * </p>
     */
    @Test
    public void testImageWriter() {
        ImageWriter imageWriter = new ImageWriter(WIDTH, HEIGHT);

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {

                boolean isGridLine =
                        (i % CELL_SIZE == 0) || (j % CELL_SIZE == 0);

                Color color = isGridLine ? GRID_COLOR : BACKGROUND_COLOR;

                imageWriter.writePixel(j, i, color);
            }
        }

        imageWriter.writeToImage("gridImage");
    }
}