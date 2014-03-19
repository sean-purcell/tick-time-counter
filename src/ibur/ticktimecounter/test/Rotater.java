package ibur.ticktimecounter.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class Rotater {
	public static void main(String[] args) { 
		JFileChooser jfc = new JFileChooser(new File("/Users/Sean/Documents/tick-time-counter"));
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int val = jfc.showOpenDialog(null);
		if(val != JFileChooser.APPROVE_OPTION) {
			System.exit(1);
		}
		System.out.println(jfc.getSelectedFile().getAbsolutePath());
		List<BufferedImage> images = getImages(jfc.getSelectedFile());
		List<BufferedImage> rotated = new ArrayList<BufferedImage>(images.size());
		for(BufferedImage b : images) {
			rotated.add(rotateImage180(b));
		}
		File outFolder = new File(jfc.getSelectedFile().getAbsoluteFile() + "_rotated");
		if(!outFolder.exists()) {
			outFolder.mkdir();
		}
		for(int i = 0; i < rotated.size(); i++) {
			try{
				ImageIO.write(rotated.get(i), "png", new File(outFolder.getAbsolutePath() + "/tape_1_" + i + ".png"));
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static List<BufferedImage> getImages(File d) {
		if(!d.isDirectory()) {
			throw new IllegalArgumentException("Argument must be directory");
		}
		List<BufferedImage> l = new ArrayList<BufferedImage>();
		for(File f : d.listFiles()) {
			if(f.isFile() && f.canRead()) {
				try{
					BufferedImage b = ImageIO.read(f);
					if(b != null) {
						System.out.println(f.getAbsolutePath() + " is an image");
						l.add(b);
					} else {
						System.out.println(f.getAbsolutePath() + " is not an image");
					}
					
				}
				catch(IOException e) {
					System.out.println(f.getAbsolutePath() + " is not an image");
				}
			}
		}
		return l;
	}
	
	private static BufferedImage rotateImage180(BufferedImage b) {
		BufferedImage n = new BufferedImage(b.getWidth(), b.getHeight(), BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < b.getWidth(); x++) {
			for(int y = 0; y < b.getHeight(); y++) {
				n.setRGB(b.getWidth() - x - 1, b.getHeight() - y - 1, b.getRGB(x, y));
			}
		}
		return n;
	}
}
