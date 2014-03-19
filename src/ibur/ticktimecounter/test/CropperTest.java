package ibur.ticktimecounter.test;

import ibur.ticktimecounter.ImageCropper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class CropperTest {
	public static void main(String[] args) { 
		JFileChooser jfc = new JFileChooser(new File("/Users/Sean/Documents/tick-time-counter"));
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int val = jfc.showOpenDialog(null);
		if(val != JFileChooser.APPROVE_OPTION) {
			System.exit(1);
		}
		System.out.println(jfc.getSelectedFile().getAbsolutePath());
		List<BufferedImage> images = getImages(jfc.getSelectedFile());
		List<BufferedImage> cropped = (new ImageCropper(images)).showCropDialog();
		File outFolder = new File(jfc.getSelectedFile().getAbsoluteFile() + "cropped");
		if(!outFolder.exists()) {
			outFolder.mkdir();
		}
		for(int i = 0; i < cropped.size(); i++) {
			try{
				ImageIO.write(cropped.get(i), "png", new File(outFolder.getAbsolutePath() + "/tape_1_0" + i + ".png"));
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
}
