package org.mtexample;

import java.awt.image.BufferedImage;

public interface ImageRecoloringStrategy {
    void recolor(BufferedImage originalImage, BufferedImage resultImage, int numberOfThreads);
}
