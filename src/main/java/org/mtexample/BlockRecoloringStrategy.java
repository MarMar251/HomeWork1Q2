package org.mtexample;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BlockRecoloringStrategy implements ImageRecoloringStrategy {
    @Override
    public void recolor(BufferedImage originalImage, BufferedImage resultImage, int numberOfThreads) {
        List<Thread> threads = new ArrayList<>();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int blockWidth = (int) Math.ceil((double) width / Math.sqrt(numberOfThreads));
        int blockHeight = (int) Math.ceil((double) height / Math.sqrt(numberOfThreads));

        for (int i = 0; i < Math.ceil(Math.sqrt(numberOfThreads)); i++) {
            for (int j = 0; j < Math.ceil(Math.sqrt(numberOfThreads)); j++) {
                final int xOrigin = j * blockWidth;
                final int yOrigin = i * blockHeight;
                int currentBlockWidth = Math.min(blockWidth, width - xOrigin);
                int currentBlockHeight = Math.min(blockHeight, height - yOrigin);

                Thread thread = new Thread(() -> {
                    recolorImage(originalImage, resultImage, xOrigin, yOrigin, currentBlockWidth, currentBlockHeight);
                });
                threads.add(thread);
            }
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int topCorner, int width, int height) {
        for (int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); x++) {
            for (int y = topCorner; y < topCorner + height && y < originalImage.getHeight(); y++) {
                recolorPixel(originalImage, resultImage, x, y);
            }
        }
    }

    private void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);
        int red = ImageUtils.getRed(rgb);
        int green = ImageUtils.getGreen(rgb);
        int blue = ImageUtils.getBlue(rgb);
        int newRed;
        int newGreen;
        int newBlue;

        if (ImageUtils.isShadeOfGray(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }

        int newRGB = ImageUtils.createRGBFromColors(newRed, newGreen, newBlue);
        ImageUtils.setRGB(resultImage, x, y, newRGB);
    }
}
