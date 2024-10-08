//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.mtexample;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;

public class Main {
    public static final String SOURCE_FILE = "src/main/resources/many-flowers.jpg";
    public static final String DESTINATION_FILE = "./out/many-flowers.jpg";

    public Main() {
    }

    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File("src/main/resources/many-flowers.jpg"));
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), 1);
        long startTime = System.currentTimeMillis();
        int numberOfThreads = 1;
        recolorMultithreaded(originalImage, resultImage, numberOfThreads);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        File outputFile = new File("./out/many-flowers.jpg");
        ImageIO.write(resultImage, "jpg", outputFile);
        System.out.println(String.valueOf(duration));
    }

    public static void recolorMultithreaded(BufferedImage originalImage, BufferedImage resultImage, int numberOfThreads) {
        List<Thread> threads = new ArrayList();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight() / numberOfThreads;

        for(int i = 0; i < numberOfThreads; ++i) {
            int threadMultiplier = i;
            Thread thread = new Thread(() -> {
                int xOrigin = 0;
                int yOrigin = height * threadMultiplier;
                recolorImage(originalImage, resultImage, xOrigin, yOrigin, width, height);
            });
            threads.add(thread);
        }

        Iterator var10 = threads.iterator();

        Thread thread;
        while(var10.hasNext()) {
            thread = (Thread)var10.next();
            thread.start();
        }

        var10 = threads.iterator();

        while(var10.hasNext()) {
            thread = (Thread)var10.next();

            try {
                thread.join();
            } catch (InterruptedException var9) {
            }
        }

    }

    public static void recolorSingleThreaded(BufferedImage originalImage, BufferedImage resultImage) {
        recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
    }

    public static void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int topCorner, int width, int height) {
        for(int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); ++x) {
            for(int y = topCorner; y < topCorner + height && y < originalImage.getHeight(); ++y) {
                recolorPixel(originalImage, resultImage, x, y);
            }
        }

    }

    public static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);
        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);
        int newRed;
        int newGreen;
        int newBlue;
        if (isShadeOfGray(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }

        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, (Object)null));
    }

    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;
        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;
        rgb |= -16777216;
        return rgb;
    }

    public static int getRed(int rgb) {
        return (rgb & 16711680) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & '\uff00') >> 8;
    }

    public static int getBlue(int rgb) {
        return rgb & 255;
    }
}
