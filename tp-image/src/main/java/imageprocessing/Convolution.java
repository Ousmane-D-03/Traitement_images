package imageprocessing;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU16;
import boofcv.struct.image.GrayU8;


public class Convolution {


    public static void meanFilter(GrayU8 input, GrayU8 output, int size) {
        int halfSize = size / 2; // Décalage pour centrer la fenêtre

        for (int y = halfSize; y < input.height - halfSize; y++) {
            for (int x = halfSize; x < input.width - halfSize; x++) {
                int sum = 0;

                for (int dy = -halfSize; dy <= halfSize; dy++) {
                    for (int dx = -halfSize; dx <= halfSize; dx++) {
                        sum += input.get(x + dx, y + dy);
                    }
                }

                output.set(x, y, sum / (size * size));
            }
        }
    }

    






  
  public static void main(final String[] args) {

    // load image
    if (args.length < 2) {
      System.out.println("missing input or output image filename");
      System.exit(-1);
    }
    final String inputPath = args[0];
    GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
    GrayU8 output = input.createSameShape();
    //GrayS16 output = new GrayS16(input.height,input.width);
    // processing
    meanFilter(input, output, 3);
    int[][] kernelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    //convolution(input, output, kernelX);
    // save output image
    final String outputPath = args[1];
    UtilImageIO.saveImage(output, outputPath);
    System.out.println("Image saved in: " + outputPath);
  }

}