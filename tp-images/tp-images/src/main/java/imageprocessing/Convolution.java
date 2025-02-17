package imageprocessing;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;

import boofcv.alg.color.ColorHsv;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public class Convolution {

    // Applique un filtre moyenneur sur une image en niveaux de gris
    public static void applyMeanFilter(GrayU8 input, GrayU8 output, int size) {
        if (size % 2 == 0) {
            throw new IllegalArgumentException("La taille du filtre doit être un nombre impair.");
        }
        int offset = size / 2;

        for (int i = offset; i < input.height - offset; i++) {
            for (int j = offset; j < input.width - offset; j++) {
                int sum = 0;

                for (int m = -offset; m <= offset; m++) {
                    for (int n = -offset; n <= offset; n++) {
                        sum += input.get(j + n, i + m);
                    }
                }

                int meanValue = sum / (size * size);
                meanValue = Math.min(255, Math.max(0, meanValue));
                output.set(j, i, meanValue);
            }
        }
    }

    // Applique un filtre moyenneur sur une image en couleur
    public static void applyMeanFilterColor(Planar<GrayU8> input, Planar<GrayU8> output, int size) {
        if (size % 2 == 0) {
            throw new IllegalArgumentException("La taille du filtre doit être impair.");
        }

        int radius = size / 2;

        for (int channel = 0; channel < 3; channel++) {
            for (int y = radius; y < input.height - radius; y++) {
                for (int x = radius; x < input.width - radius; x++) {
                    int sum = 0;

                    for (int ky = -radius; ky <= radius; ky++) {
                        for (int kx = -radius; kx <= radius; kx++) {
                            int pixelValue = input.getBand(channel).get(x + kx, y + ky);
                            sum += pixelValue;
                        }
                    }

                    int average = sum / (size * size);
                    output.getBand(channel).set(x, y, average);
                }
            }
        }
    }

    // Applique une convolution sur une image en niveaux de gris
    public static void applyConvolution(GrayU8 input, GrayS16 output, int[][] kernel) {
        int offset = kernel.length / 2;
        for (int i = offset; i < input.height - offset; i++) {
            for (int j = offset; j < input.width - offset; j++) {
                int sum = 0;
                for (int m = 0; m < kernel.length; m++) {
                    for (int n = 0; n < kernel[m].length; n++) {
                        sum += input.get(j + n - offset, i + m - offset) * kernel[m][n];
                    }
                }
                output.set(j, i, sum);
            }
        }
    }

    // Calcule le gradient d'une image en utilisant deux noyaux (X et Y)
    public static void computeGradient(GrayU8 input, GrayU8 output, int[][] kernelX, int[][] kernelY) {
        int offset = kernelX.length / 2;

        for (int i = offset; i < input.height - offset; i++) {
            for (int j = offset; j < input.width - offset; j++) {
                int gradX = 0;
                int gradY = 0;

                for (int m = 0; m < kernelX.length; m++) {
                    for (int n = 0; n < kernelX[m].length; n++) {
                        int pixel = input.get(j + n - offset, i + m - offset);
                        gradX += pixel * kernelX[m][n];
                    }
                }

                for (int m = 0; m < kernelY.length; m++) {
                    for (int n = 0; n < kernelY[m].length; n++) {
                        int pixel = input.get(j + n - offset, i + m - offset);
                        gradY += pixel * kernelY[m][n];
                    }
                }

                int gradNorm = (int) Math.sqrt(gradX * gradX + gradY * gradY);
                gradNorm = Math.min(255, Math.max(0, gradNorm));
                output.set(j, i, gradNorm);
            }
        }
    }

    // Applique le filtre de Sobel pour calculer le gradient
    public static void applySobelFilter(GrayU8 input, GrayU8 output) {
        int[][] kernelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] kernelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
        computeGradient(input, output, kernelX, kernelY);
    }

    // Applique le filtre de Prewitt pour calculer le gradient
    public static void applyPrewittFilter(GrayU8 input, GrayU8 output) {
        int[][] kernelX = {
            {-1, 0, 1},
            {-1, 0, 1},
            {-1, 0, 1}
        };
        int[][] kernelY = {
            {-1, -1, -1},
            {0, 0, 0},
            {1, 1, 1}
        };
        computeGradient(input, output, kernelX, kernelY);
    }

    // Convertit une image couleur en niveaux de gris
    public static void convertToGrayscale(Planar<GrayU8> input, GrayU8 output) {
        for (int y = 0; y < input.height; y++) {
            for (int x = 0; x < input.width; x++) {
                int r = input.getBand(0).get(x, y);
                int g = input.getBand(1).get(x, y);
                int b = input.getBand(2).get(x, y);

                int grayValue = (int) (0.3 * r + 0.59 * g + 0.11 * b);
                grayValue = Math.min(255, Math.max(0, grayValue));
                output.set(x, y, grayValue);
            }
        }
    }

    // Ajuste la luminosité d'une image couleur
    public static void adjustBrightness(Planar<GrayU8> input, Planar<GrayU8> output, int delta) {
        for (int y = 0; y < input.height; y++) {
            for (int x = 0; x < input.width; x++) {
                for (int channel = 0; channel < 3; channel++) {
                    int value = input.getBand(channel).get(x, y);
                    value += delta;
                    value = Math.min(255, Math.max(0, value));
                    output.getBand(channel).set(x, y, value);
                }
            }
        }
    }

    // Applique un filtre de teinte à une image couleur
    public static void applyHueFilter(Planar<GrayU8> input, Planar<GrayU8> output, float newHueDegrees) {
        float newHue = (float) Math.toRadians(newHueDegrees);
        Planar<GrayF32> hsv = new Planar<>(GrayF32.class, input.width, input.height, 3);

        for (int y = 0; y < input.height; y++) {
            for (int x = 0; x < input.width; x++) {
                float r = input.getBand(0).get(x, y) / 255.0f;
                float g = input.getBand(1).get(x, y) / 255.0f;
                float b = input.getBand(2).get(x, y) / 255.0f;

                float[] hsvPixel = new float[3];
                ColorHsv.rgbToHsv(r, g, b, hsvPixel);

                hsvPixel[0] = newHue;

                float[] rgbPixel = new float[3];
                ColorHsv.hsvToRgb(hsvPixel[0], hsvPixel[1], hsvPixel[2], rgbPixel);

                output.getBand(0).set(x, y, (int) (rgbPixel[0] * 255));
                output.getBand(1).set(x, y, (int) (rgbPixel[1] * 255));
                output.getBand(2).set(x, y, (int) (rgbPixel[2] * 255));
            }
        }
    }

    // Calcule l'histogramme des teintes d'une image couleur
    public static int[] computeHueHistogram(Planar<GrayU8> input) {
        int[] histogram = new int[360];

        for (int y = 0; y < input.height; y++) {
            for (int x = 0; x < input.width; x++) {
                int r = input.getBand(0).get(x, y);
                int g = input.getBand(1).get(x, y);
                int b = input.getBand(2).get(x, y);

                float rNorm = r / 255.0f;
                float gNorm = g / 255.0f;
                float bNorm = b / 255.0f;

                float[] hsvPixel = new float[3];
                ColorHsv.rgbToHsv(rNorm, gNorm, bNorm, hsvPixel);

                int hue = (int) Math.toDegrees(hsvPixel[0]) % 360;
                histogram[hue]++;
            }
        }

        return histogram;
    }

    // Crée une image représentant l'histogramme des teintes
    public static BufferedImage createHueHistogramImage(int[] histogram) {
        int width = 360;
        int height = 256;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        int maxCount = 0;
        for (int i = 0; i < histogram.length; i++) {
            if (histogram[i] > maxCount) {
                maxCount = histogram[i];
            }
        }

        g2d.setColor(Color.WHITE);
        for (int i = 0; i < histogram.length; i++) {
            int barHeight = (int) ((double) histogram[i] / maxCount * height);
            g2d.fillRect(i, height - barHeight, 1, barHeight);
        }

        g2d.dispose();
        return image;
    }

    public static void main(final String[] args) {
        if (args.length < 2) {
            System.out.println("missing input or output image filename");
            System.exit(-1);
        }
        final String inputPath = args[0];
        BufferedImage input = UtilImageIO.loadImage(inputPath);
        Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);
        Planar<GrayU8> output = image.createSameShape();

        // Exemple d'utilisation des fonctions
        // applyMeanFilterColor(image, output, 11);
        // applySobelFilter(image.getBand(0), output.getBand(0));
        // convertToGrayscale(image, output.getBand(0));
        // adjustBrightness(image, output, -50);
        // applyHueFilter(image, output, 270);

        final String outputPath = args[1];
        UtilImageIO.saveImage(output, outputPath);
        System.out.println("Image saved in: " + outputPath);
    }
}