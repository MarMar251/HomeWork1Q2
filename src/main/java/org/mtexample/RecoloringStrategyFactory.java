package org.mtexample;

public class RecoloringStrategyFactory {
    public static ImageRecoloringStrategy getStrategy(String type) {
        switch (type.toLowerCase()) {
            case "horizontal":
                return new HorizontalRecoloringStrategy();
            case "block":
                return new BlockRecoloringStrategy();
            default:
                throw new IllegalArgumentException("Unknown strategy type");
        }
    }
}