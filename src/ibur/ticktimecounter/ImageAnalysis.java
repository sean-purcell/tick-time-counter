package ibur.ticktimecounter;

import java.awt.image.BufferedImage;

public class ImageAnalysis {
	
	public static final int LIGHT_THRESHOLD = 190;
	
	/**
	 * Calculates the "absolute value" of a colour squared
	 * |rgb|^2 = r^2+g^2+b^2
	 * @param rgb The colour in standard rgb form, i.e. 0xaarrggbb
	 * @return The "absolute value" of the colour squared, or |rgb|^2
	 */
	private static int colourAbs2(int rgb) {
		int r = (rgb & 0xff0000) >> 16,
			g = (rgb &   0xff00) >> 8,
			b = (rgb &     0xff);
		return r*r+g*g+b*b;
	}
	
	/**
	 * Calculates the "absolute value" of a colour
	 * |rgb| = sqrt(r^2+g^2+b^2)
	 * @param rgb The colour in standard rgb form, i.e. 0xaarrggbb
	 * @return The "absolute value" of the colour, or |rgb|
	 */
	private static double colourAbs(int rgb) {
		return Math.sqrt(colourAbs2(rgb));
	}
	
	private static double colourAvg(int rgb) {
		int r = (rgb & 0xff0000) >> 16,
				g = (rgb &   0xff00) >> 8,
				b = (rgb &     0xff);
			return (r+g+b)/3.;
	}
	
	/**
	 * Calculate the average absolute value of colours in the image
	 * @param b The image to calculate the average for
	 * @return The average absolute value of colours in the image
	 */
	public static double imageAvg(BufferedImage b) {
		double sum = 0; // sum of absolute values of colours
		for(int x = 0; x < b.getWidth(); x++) {
			for(int y = 0; y < b.getHeight(); y++) {
				//sum += colourAbs(b.getRGB(x, y));
				sum += colourAvg(b.getRGB(x, y));
			}
		}
		return sum / (b.getHeight() * b.getWidth());
	}
	
	/**
	 * From an image split each pixel into true or false, true if it is darker than the average, false if it is not
	 * @param b The image to split
	 * @return A 2-dimensional boolean array with each entry representing the side that pixel falls on
	 */
	public static boolean[][] boolizeImage(BufferedImage b) {
		double  avg   = imageAvg(b),
				avgSq = avg*avg;
		
		boolean[][] p = new boolean[b.getWidth()][b.getHeight()];
		for(int x = 0; x < b.getWidth(); x++) {
			for(int y = 0; y < b.getHeight(); y++) {
				//p[x][y] = colourAbs2(b.getRGB(x,y)) < avgSq;
				p[x][y] = colourAvg(b.getRGB(x, y)) < LIGHT_THRESHOLD;
			}
		}
		return p;
	}
	
	public static BufferedImage boolToImage(boolean[][] a) {
		BufferedImage b = new BufferedImage(a.length, a[0].length, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < a.length; x++) {
			for(int y = 0; y <a[0].length; y++) {
				b.setRGB(x, y, a[x][y] ? 0x0 : 0xffffff);
			}
		}
		return b;
	}
	
	public static boolean[][] imageToBool(BufferedImage b) {
		boolean[][] a = new boolean[b.getWidth()][b.getHeight()];
		for(int x = 0; x < a.length; x++) {
			for(int y = 0; y <a[0].length; y++) {
				a[x][y] = b.getRGB(x, y) == 0x0;
			}
		}
		return a;
	}
}
