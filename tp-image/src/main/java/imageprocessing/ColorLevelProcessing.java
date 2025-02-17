import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import boofcv.struct.image.ColorHsv;
import java.awt.image.BufferedImage;

public class ImageProcessing {

    // 1. Augmenter / diminuer la luminosité
    public void adjustBrightness(Planar<GrayU8> image, int value) {
        for (int i = 0; i < image.width; i++) {
            for (int j = 0; j < image.height; j++) {
                for (int c = 0; c < 3; c++) {
                    int colorValue = image.get(c, i, j).get(i, j) + value;
                    colorValue = Math.min(Math.max(colorValue, 0), 255);
                    image.get(c, i, j).set(i, j, (byte) colorValue);
                }
            }
        }
    }

    // 2. Flouter
    public void blurImage(Planar<GrayU8> image, int radius) {
        int size = radius * 2 + 1;
        for (int i = radius; i < image.width - radius; i++) {
            for (int j = radius; j < image.height - radius; j++) {
                for (int c = 0; c < 3; c++) {
                    int sum = 0;
                    for (int x = -radius; x <= radius; x++) {
                        for (int y = -radius; y <= radius; y++) {
                            sum += image.get(c, i + x, j + y).get(i + x, j + y);
                        }
                    }
                    int avg = sum / (size * size);
                    image.get(c, i, j).set(i, j, (byte) avg);
                }
            }
        }
    }

    // 3. Griser
    public void grayscaleImage(Planar<GrayU8> image) {
        for (int i = 0; i < image.width; i++) {
            for (int j = 0; j < image.height; j++) {
                int r = image.get(0, i, j).get(i, j);
                int g = image.get(1, i, j).get(i, j);
                int b = image.get(2, i, j).get(i, j);
                int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
                image.get(0, i, j).set(i, j, (byte) gray);
                image.get(1, i, j).set(i, j, (byte) gray);
                image.get(2, i, j).set(i, j, (byte) gray);
            }
        }
    }

    // 4. Conversion RGB / HSV
    public void convertRgbToHsv(int r, int g, int b) {
        ColorHsv colorHsv = new ColorHsv();
        float[] hsv = colorHsv.rgbToHsv(r, g, b);
        System.out.println("H: " + hsv[0] + ", S: " + hsv[1] + ", V: " + hsv[2]);
    }

    // 5. Filtre de coloration
    public void colorizeImage(Planar<GrayU8> image, float newHue) {
        for (int i = 0; i < image.width; i++) {
            for (int j = 0; j < image.height; j++) {
                int r = image.get(0, i, j).get(i, j);
                int g = image.get(1, i, j).get(i, j);
                int b = image.get(2, i, j).get(i, j);

                // Conversion RGB -> HSV
                float[] hsv = new float[3];
                ColorHsv.rgbToHsv(r, g, b, hsv);

                // Modifier uniquement la teinte
                hsv[0] = newHue;  // La nouvelle teinte

                // Conversion HSV -> RGB
                ColorHsv.hsvToRgb(hsv[0], hsv[1], hsv[2], hsv);

                image.get(0, i, j).set(i, j, (byte) hsv[0]);
                image.get(1, i, j).set(i, j, (byte) hsv[1]);
                image.get(2, i, j).set(i, j, (byte) hsv[2]);
            }
        }
    }

    // 6. Saturation à zéro
    public void desaturateImage(Planar<GrayU8> image) {
        for (int i = 0; i < image.width; i++) {
            for (int j = 0; j < image.height; j++) {
                int r = image.get(0, i, j).get(i, j);
                int g = image.get(1, i, j).get(i, j);
                int b = image.get(2, i, j).get(i, j);

                // Conversion RGB -> HSV
                float[] hsv = new float[3];
                ColorHsv.rgbToHsv(r, g, b, hsv);

                // Fixer la saturation à 0
                hsv[1] = 0;

                // Conversion HSV -> RGB
                ColorHsv.hsvToRgb(hsv[0], hsv[1], hsv[2], hsv);

                image.get(0, i, j).set(i, j, (byte) hsv[0]);
                image.get(1, i, j).set(i, j, (byte) hsv[1]);
                image.get(2, i, j).set(i, j, (byte) hsv[2]);
            }
        }
    }

    // 7. Histogramme des teintes
    public void generateHueHistogram(Planar<GrayU8> image) {
        int[] histogram = new int[360];

        for (int i = 0; i < image.width; i++) {
            for (int j = 0; j < image.height; j++) {
                int r = image.get(0, i, j).get(i, j);
                int g = image.get(1, i, j).get(i, j);
                int b = image.get(2, i, j).get(i, j);

                // Conversion RGB -> HSV
                float[] hsv = new float[3];
                ColorHsv.rgbToHsv(r, g, b, hsv);

                int hue = Math.round(hsv[0]);
                histogram[hue]++;
            }
        }

        // Afficher ou analyser l'histogramme
    }

    // 8. Histogramme 2D (Teinte / Saturation)
    public void generateHueSaturationHistogram(Planar<GrayU8> image) {
        int[][] histogram = new int[360][101]; // 101 pour saturation (0 à 100)

        for (int i = 0; i < image.width; i++) {
            for (int j = 0; j < image.height; j++) {
                int r = image.get(0, i, j).get(i, j);
                int g = image.get(1, i, j).get(i, j);
                int b = image.get(2, i, j).get(i, j);

                // Conversion RGB -> HSV
                float[] hsv = new float[3];
                ColorHsv.rgbToHsv(r, g, b, hsv);

                int hue = Math.round(hsv[0]);
                int saturation = Math.round(hsv[1] * 100);

                histogram[hue][saturation]++;
            }
        }

        // Afficher ou analyser l'histogramme 2D
    }

    public static void main(String[] args) {
        // Exemple d'utilisation des méthodes
        String inputPath = "image.jpg";  // Spécifie le chemin de l'image
        BufferedImage input = UtilImageIO.loadImage(inputPath);
        Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);

        ImageProcessing processor = new ImageProcessing();
        
        // Exemples d'application des filtres
        processor.adjustBrightness(image, 50);  // Augmenter la luminosité de 50
        processor.grayscaleImage(image);        // Convertir l'image en niveaux de gris
        processor.colorizeImage(image, 270);    // Colorer l'image avec la teinte 270
    }
}
