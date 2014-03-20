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
	
	@Override
	public String toString() {
		return "[distBack: " + distBack + ", pos: " + pos + "];";
	}
	
	public static List<Dot> analyzeTape(List<BufferedImage> images) {
		List<List<Dot>> subdots = new ArrayList<List<Dot>>(); // a list of dots for each image
		for(int i = 0; i < images.size(); i++) { 
			subdots.add(analyzeImage(images.get(i)));
		}
		return stitchDotLists(subdots);
	}
	
	private static List<Dot> analyzeImage(BufferedImage b) {
		final int MIN_DOT_SIZE = 5;
		List<Dot> imgdots = new ArrayList<Dot>(); // dots only from this image
		boolean[][] a = ImageAnalysis.imageToBool(b);
		clearEdges(a);
		Dot lastDot = null; // the previous dot found 
		for(int x = 0; x < b.getWidth(); x++) {
			for(int y = 0; y < b.getHeight(); y++) {
				if(a[x][y]) {
					List<Point> clump = new ArrayList<Point>();
					recurseDot(a, x, y, 0, 3, clump);
					if(clump.size() > MIN_DOT_SIZE) {
						lastDot = new Dot(clump, lastDot);
						imgdots.add(lastDot);
					}
				}
			}
			/*
			if(whiteLayer && currentDot != null) {
				lastDot = new Dot(currentDot, lastDot);
				imgdots.add(lastDot);
				currentDot = null;
			}*/
		}
		return imgdots;
	}
	
	private static void clearEdges(boolean[][] a) {
		for(int x = 0; x < a.length; x++) {
			recurseClearEdges(a, x, 0, 0);
			recurseClearEdges(a, x, a[x].length-1, 0);
		}
		for(int y = 0; y < a.length; y++) {
			recurseClearEdges(a, 0, y, 2);
			recurseClearEdges(a, a.length-1, y, 2);
		}
	}
	
	private static void recurseClearEdges(boolean[][] a, int x, int y, int dir) {
		if(x < 0 || x >= a.length || y < 0 || y >= a[x].length || !a[x][y]) {
			return;
		}
		a[x][y] = false;
		for(int i = dir; i < dir+2; i++) {
			int nx = x + Util.dirDelta[i][0];
			int ny = y + Util.dirDelta[i][1];
			recurseClearEdges(a, nx, ny, dir);
		}
	}
	
	private static void recurseDot(final boolean[][] a, final int x, final int y, final int whiteNum, final int WHITE_THRESHHOLD, final List<Point> clump) {
		if(x < 0 || x >= a.length || y < 0 || y >= a[0].length) {
			return;
		}
		if(!a[x][y] && whiteNum >= WHITE_THRESHHOLD) {
			return;
		}
		if(a[x][y]) {
			clump.add(new Point(x, y));
			a[x][y] = false;
		}
		boolean ret = true;
		for(int i = 0; i < 4 && ret; i++) {
			int nx = x + Util.dirDelta[i][0];
			int ny = y + Util.dirDelta[i][1];
			recurseDot(a, nx, ny, (a[x][y] ? 0 : whiteNum + 1), WHITE_THRESHHOLD, clump);
		}
	}
	
	private static List<Dot> stitchDotLists(List<List<Dot>> l) {
		List<Dot> grand = new ArrayList<Dot>();
		grand.addAll(l.get(0));
		for(int i = 1; i < l.size(); i++) {
			grand.addAll(l.get(i).subList(1, l.get(i).size()));
		}
		grand.set(0, new Dot(0, 0));
		for(int i = 1; i < grand.size(); i++) {
			grand.set(i, new Dot(grand.get(i).distBack, grand.get(i).distBack + grand.get(i-1).pos)); // set pos to be distance from start
		}
		return grand;
	}
}
