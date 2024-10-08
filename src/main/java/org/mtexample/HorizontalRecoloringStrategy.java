package org.mtexample;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class HorizontalRecoloringStrategy implements ImageRecoloringStrategy {
    @Override
    public void recolor(BufferedImage originalImage, BufferedImage resultImage, int numberOfThreads) {
        List<Thread> threads = new ArrayList<>();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int stripHeight = height / numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            final int yOrigin = i * stripHeight;
            final int currentHeight = (i == numberOfThreads - 1) ? (height - yOrigin) : stripHeight;

            Thread thread = new Thread(() -> {
                recolorImage(originalImage, resultImage, 0, yOrigin, width, currentHeight);
            });
            threads.add(thread);
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
