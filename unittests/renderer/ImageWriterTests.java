package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

public class ImageWriterTests {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 500;
    private static final int CELL_SIZE = 50;

    private static final Color BACKGROUND_COLOR = new Color(255, 255, 0);
    private static final Color GRID_COLOR = new Color(255, 0, 0);
    @Test
    public void testImageWriter() {
        ImageWriter imageWriter = new ImageWriter(WIDTH,HEIGHT);

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