package ibur.ticktimecounter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Dot {
	
	public final double distBack, pos;
	
	public Dot(double distBack, double pos) {
		this.distBack = distBack;
		this.pos = pos;
	}
	
	private Dot(List<Point> l, Dot lastDot) {
		int sumX = 0;
		for(int j = 0; j < l.size(); j++) {
			sumX += l.get(j).x;
		}
		this.pos = sumX / (double) l.size();
		this.distBack = lastDot == null ? Double.NaN : Math.abs(lastDot.pos - this.pos);
	}
	
	public static List<Dot> analyzeTape(List<BufferedImage> images) {
		List<List<Dot>> subdots = new ArrayList<List<Dot>>(); // a list of dots for each image
		for(int i = 0; i < images.size(); i++) { 
			subdots.add(analyzeImage(images.get(i)));
		}
		return stitchDotLists(subdots);
	}
	
	private static List<Dot> analyzeImage(BufferedImage b) {
		List<Dot> imgdots = new ArrayList<Dot>(); // dots only from this image
		boolean[][] a = ImageAnalysis.imageToBool(b);
		
		Dot lastDot = null; // the previous dot found 
		List<Point> currentDot = null; // the points in the current dot
		
		for(int x = 0; x < b.getWidth(); x++) {
			boolean previous = true; // the colour of the previous pixel
			boolean whiteLayer = true; // is the layer entirely white
			List<Point> sublist = null;
			for(int y = 0; y < b.getHeight(); y++) {
				if(a[x][y] && !previous) { // new set of blacks
					sublist = new ArrayList<Point>();
					whiteLayer = false;
				}
				if(a[x][y] && sublist != null) { // in the middle of a set of blacks
					sublist.add(new Point(x,y));
				}
				if(!a[x][y] && previous && sublist != null) { // done the subset
					if(currentDot == null) {
						currentDot = new ArrayList<Point>();
					}
					currentDot.addAll(sublist);
					sublist = null;
				}
				previous = a[x][y];
			}
			if(whiteLayer && currentDot != null) {
				lastDot = new Dot(currentDot, lastDot);
				imgdots.add(lastDot);
				currentDot = null;
			}
		}
		return imgdots;
	}
	
	private static List<Dot> stitchDotLists(List<List<Dot>> l) {
		List<Dot> grand = new ArrayList<Dot>();
		grand.addAll(l.get(1));
		for(int i = 1; i < l.size(); i++) {
			grand.addAll(l.get(i).subList(1, l.size()));
		}
		grand.set(0, new Dot(0, 0));
		for(int i = 1; i < grand.size(); i++) {
			grand.set(i, new Dot(grand.get(i).distBack, grand.get(i).distBack + grand.get(i-1).pos)); // set pos to be distance from start
		}
		return grand;
	}
}
