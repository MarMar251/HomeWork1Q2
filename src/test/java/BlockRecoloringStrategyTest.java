
import junit.framework.TestCase;
import org.mtexample.BlockRecoloringStrategy;
import org.mtexample.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BlockRecoloringStrategyTest extends TestCase {

    private static final String SOURCE_FILE = "src/main/resources/many-flowers.jpg";
    private static final String DESTINATION_FILE = "./out/B2.jpg";
    private static final int[] THREAD_COUNTS = {2, 4, 8, 16};

    private BlockRecoloringStrategy strategy;
    private BufferedImage originalImage;

    protected void setUp() throws IOException {
        strategy = new BlockRecoloringStrategy();
        originalImage = ImageIO.read(new File(SOURCE_FILE));
    }

    public void testRecolorActualImage() throws IOException {
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        long startTime = System.currentTimeMillis();
        strategy.recolor(originalImage, resultImage, 4);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Block recoloring with 4 threads took " + duration + " ms");

        File outputFile = new File(DESTINATION_FILE);
        ImageIO.write(resultImage, "jpg", outputFile);

        assertTrue("Output file should exist", outputFile.exists());
        assertTrue("Output file should have content", outputFile.length() > 0);

        BufferedImage savedImage = ImageIO.read(outputFile);

        assertEquals("Width should match", originalImage.getWidth(), savedImage.getWidth());
        assertEquals("Height should match", originalImage.getHeight(), savedImage.getHeight());

        for (int x = 0; x < savedImage.getWidth(); x += 10) {
            for (int y = 0; y < savedImage.getHeight(); y += 10) {
                int originalRGB = originalImage.getRGB(x, y);
                int resultRGB = savedImage.getRGB(x, y);

                if (ImageUtils.isShadeOfGray(ImageUtils.getRed(originalRGB),
                        ImageUtils.getGreen(originalRGB),
                        ImageUtils.getBlue(originalRGB))) {
                    assertFalse("Gray pixels should be modified", originalRGB == resultRGB);
                }
            }
        }
    }

    public void testRecolorWithDifferentThreadCounts() throws IOException {
        for (int threads : THREAD_COUNTS) {
            BufferedImage threadResultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            long startTime = System.currentTimeMillis();
            strategy.recolor(originalImage, threadResultImage, threads);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.println("Block recoloring with " + threads + " threads took " + duration + " ms");

            File outputFile = new File("./out/B1_" + threads + "threads.jpg");
            ImageIO.write(threadResultImage, "jpg", outputFile);

            assertTrue("Output file for " + threads + " threads should exist", outputFile.exists());
            assertTrue("Output file for " + threads + " threads should have content", outputFile.length() > 0);
        }
    }



    protected void tearDown() {
        File outputDir = new File("./out");
        if (outputDir.exists() && outputDir.isDirectory()) {
            File[] files = outputDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith("B1")) {
                        boolean deleted = file.delete();
                        if (!deleted) {
                            System.err.println("Failed to delete file: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }

        strategy = null;
        originalImage = null;

        System.gc();
    }
}