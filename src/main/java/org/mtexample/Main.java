package org.mtexample;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static final String SOURCE_FILE = "src/main/resources/many-flowers.jpg";

    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        Scanner scanner = new Scanner(System.in);
        String strategyType = "";
        int numberOfThreads = 0;
        int choice;

        do {
            System.out.println("Choose a recoloring strategy:");
            System.out.println("1. Horizontal");
            System.out.println("2. Block");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    strategyType = "horizontal";
                    break;
                case 2:
                    strategyType = "block";
                    break;
                case 0:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    continue;
            }

            System.out.print("Enter the number of threads: ");
            numberOfThreads = scanner.nextInt();

            System.out.print("Enter the name for the destination file (without extension): ");
            String userInput = scanner.next();
            String destinationFileName = "./out/" + userInput + ".jpg";

            ImageRecoloringStrategy strategy = RecoloringStrategyFactory.getStrategy(strategyType);

            long startTime = System.currentTimeMillis();
            strategy.recolor(originalImage, resultImage, numberOfThreads);
            long endTime = System.currentTimeMillis();

            ImageIO.write(resultImage, "jpg", new File(destinationFileName));
            System.out.println("Duration: " + (endTime - startTime) + " ms");

        } while (choice != 0);

        scanner.close();
    }
}