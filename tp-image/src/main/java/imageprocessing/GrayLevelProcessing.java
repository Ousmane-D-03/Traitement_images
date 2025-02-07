package imageprocessing;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;


public class GrayLevelProcessing {

	public static void threshold(GrayU8 input, int t) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				if (gl < t) {
					gl = 0;
				} else {
					gl = 255;
				}
				input.set(x, y, gl);
			}
		}
	}

    public static void greyChange(int delta,GrayU8 input){
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y)+delta;
				if (gl > 255) gl = 255;
				else if(gl<0) gl =0;
				input.set(x, y, gl);
			}
		}
	}

	public static int[] hist(GrayU8 input){
		int[] h = new int[256];
		for(int i=0;i<256;i++)h[i]=0;
		int i=0;
		while (i<=255) {
			for (int y = 0; y < input.height; ++y) {
				for (int x = 0; x < input.width; ++x) {
					int gl = input.get(x, y);
					if(gl==i){
						h[i]+=1;
					}
				}
			}	
			i++;
		}
		return h;
	}

	public static int[] histCumul(int [] tab){
		int k=0;
		int [] hc = new int[256];
		for(int i=0;i<256;i++)hc[i]=0;
		while (k<256) {
			for (int y = 0; y <= k; ++y) {
				hc[k]+=tab[y];
			}	
			k++;
		}
		return hc;
	}

	public static void greyDynamic(GrayU8 input){
		int min= 0;
		int max = 0;
		int[] h= hist(input);
		for(int i=0;i<=255;i++){
			if(h[i]!=0 && min ==0)min=i;
			if(h[i]!=0)max=i;
		}
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int i=input.get(x, y);
				if(i>=min && i<=max){
					int gl = ((input.get(x, y)-min)*255)/(max-min);
					input.set(x, y, gl);
				}
			}
		}
	}

	public static void greyContraste(GrayU8 input){

		int[] h= histCumul(hist(input));
	
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = (h[input.get(x, y)]*255)/(input.height*input.width);
				input.set(x, y, gl);
			}
		}
	}

    public static void main( String[] args ) {

    	// load image
		if (args.length < 2) {
			System.out.println("missing input or output image filename");
			System.exit(-1);
		}
		final String inputPath = args[0];
		//int delta = Integer.valueOf(args[0]);
		GrayU8 input = boofcv.io.image.UtilImageIO.loadImage(inputPath, GrayU8.class);
		if(input == null) {
			System.err.println("Cannot read input file '" + inputPath);
			System.exit(-1);
		}

		// processing
		
        //threshold(input, 128);
		//greyChange(delta, input);
		//greyDynamic(input);
		greyContraste(input);

		// save output image
		final String outputPath = args[1];
		UtilImageIO.saveImage(input, outputPath);
		System.out.println("Image saved in: " + outputPath);
	}

}