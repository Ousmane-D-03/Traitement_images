package imageprocessing;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.io.image.ConvertBufferedImage;

import java.awt.image.BufferedImage;
import java.io.File;

public class GrayLevelProcessing {

    // Applique un seuillage à l'image
    public static void applyThreshold(GrayU8 input, int threshold) {
        for (int y = 0; y < input.height; ++y) {
            for (int x = 0; x < input.width; ++x) {
                int grayLevel = input.get(x, y);
                if (grayLevel < threshold) {
                    grayLevel = 0;
                } else {
                    grayLevel = 255;
                }
                input.set(x, y, grayLevel);
            }
        }
    }

    // Ajoute un delta aux niveaux de gris de l'image
    public static void adjustGrayLevel(GrayU8 input, int delta) {
        for (int y = 0; y < input.height; ++y) {
            for (int x = 0; x < input.width; ++x) {
                int grayLevel = input.get(x, y);
                if ((grayLevel + delta) > 255) {
                    grayLevel = 255;
                } else if ((grayLevel + delta) < 0) {
                    grayLevel = 0;
                } else {
                    grayLevel += delta;
                }
                input.set(x, y, grayLevel);
            }
        }
    }

    // Ajuste le contraste de l'image
    public static void adjustContrast(GrayU8 input) {
        int min = 255;
        int max = 0;
        for (int y = 0; y < input.height; ++y) {
            for (int x = 0; x < input.width; ++x) {
                int grayLevel = input.get(x, y);
                if (grayLevel < min) { min = grayLevel; }
                if (grayLevel > max) { max = grayLevel; }
            }
        }
        int[] lut = new int[256];
        for (int t = 0; t < 256; ++t) {
            lut[t] = ((255 * (t - min)) / (max - min));
        }

        for (int y = 0; y < input.height; ++y) {
            for (int x = 0; x < input.width; ++x) {
                int grayLevel = input.get(x, y);
                input.set(x, y, lut[grayLevel]);
            }
        }
    }

    // Égalise l'histogramme de l'image
    public static void equalizeHistogram(GrayU8 input) {
        int[] histogram = new int[256];
        int[] cumulativeHistogram = new int[256];

        // Calcul de l'histogramme
        for (int y = 0; y < input.height; ++y) {
            for (int x = 0; x < input.width; ++x) {
                int grayLevel = input.get(x, y);
                histogram[grayLevel] += 1;
            }
        }

        // Calcul de l'histogramme cumulatif
        for (int k = 0; k < 256; ++k) {
            for (int t = 0; t < k; ++t) {
                cumulativeHistogram[k] += histogram[t];
            }
        }

        // Application de l'égalisation d'histogramme
        for (int y = 0; y < input.height; ++y) {
            for (int x = 0; x < input.width; ++x) {
                int grayLevel = input.get(x, y);
                input.set(x, y, ((cumulativeHistogram[grayLevel] * 255) / (input.height * input.width));
            }
        }
    }

    public static void main(String[] args) {
        // Chargement de l'image
        if (args.length < 2) {
            System.out.println("missing input or output image filename");
            System.exit(-1);
        }
        final String inputPath = args[0];
        GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
        if (input == null) {
            System.err.println("Cannot read input file '" + inputPath);
            System.exit(-1);
        }

        // Traitement de l'image
        // applyThreshold(input, 128);
        // adjustGrayLevel(input, 50);
        adjustContrast(input);
        // equalizeHistogram(input);

        // Sauvegarde de l'image de sortie
        final String outputPath = args[1];
        UtilImageIO.saveImage(input, outputPath);
        System.out.println("Image saved in: " + outputPath);
    }
}