package ibur.ticktimecounter.gui;

import ibur.ticktimecounter.Dot;
import ibur.ticktimecounter.ImageAnalysis;
import ibur.ticktimecounter.Util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI extends JFrame {
	private final JPanel root;
	
	private List<BufferedImage> images;
	
	private JPanel loadImagesPanel = generateLoadImagesPanel();
	private JPanel imageManipPanel = generateImageManipPanel();
	
	private JLabel show;
	
	public GUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.root = new JPanel();
		this.getContentPane().add(root);
		this.root.add(loadImagesPanel);
		this.pack();
		this.setVisible(true);
	}
	
	private JPanel generateLoadImagesPanel() {
		JPanel p = new JPanel();
		p.add(new JLabel("Choose folder containing images"));
		JButton b = new JButton("Show file chooser");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(new File("/Users/Sean/Documents/tick-time-counter"));
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int val = jfc.showOpenDialog(null);
				if(val == JFileChooser.APPROVE_OPTION) {
					GUI.this.setImages(getImages(jfc.getSelectedFile()));
					imageManipStage();
				}
			}
		});
		p.add(b);
		return p;
	}
	
	private JPanel generateImageManipPanel() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(new JLabel("The end result must be that the images are boolized and cropped to have the only black be dots.  It must also be left to right."));
		JButton rot = new JButton("Rotate Images");
		rot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ImageRotator(GUI.this.images).showRotateDialog(GUI.this);
			}
		});
		p.add(rot);
		JButton crop = new JButton("Crop images");
		crop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				(new Thread(new Runnable() {
					@Override
					public void run() {
						GUI.this.setImages((new ImageCropper(images)).showCropDialog(GUI.this));
						GUI.this.pack();
						GUI.this.repaint();
					}
				})).start();
			}
		});
		p.add(crop);
		JButton bool = new JButton("Boolize");
		bool.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				(new Thread(new Runnable() {
					@Override
					public void run() {
						GUI.this.setImages((new ImageBooler(images)).showBoolDialog(GUI.this));
						GUI.this.pack();
						GUI.this.repaint();
					}
				})).start();
			}
		});
		p.add(bool);
		JButton output = new JButton("Output to csv");
		output.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				(new Thread(new Runnable() {
					@Override
					public void run() {
						List<Dot> data = Dot.analyzeTape(images);
						data = ImageAnalysis.amalgamateShorts(data);
						data = ImageAnalysis.fixBigs(data);
						data = CloseDotClicker.insertClicks(data, new CloseDotClicker(images.get(0)).showClickWindow());
						for(Dot d : data) {
							System.out.println(d);
						}
						JFileChooser jfc = new JFileChooser();
						jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
						jfc.showSaveDialog(null);
						try{
							Util.writeDataToCSV(data, jfc.getSelectedFile());
						} catch(IOException e) {
							e.printStackTrace();
						}
					}
				})).start();
			}
		});
		show = new JLabel(new ImageIcon());
		p.add(show);
		return p;
	}
	
	private void imageManipStage() {
		root.remove(loadImagesPanel);
		root.add(imageManipPanel);
		this.pack();
		this.repaint();
	}
	
	public static void main(String[] args) {
		new GUI();
	}
	
	private static List<BufferedImage> getImages(File d) {
		if(!d.isDirectory()) {
			throw new IllegalArgumentException("Argument must be directory");
		}
		List<BufferedImage> l = new ArrayList<BufferedImage>();
		File[] files = d.listFiles();
		Arrays.sort(files);
		for(File f : files) {
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

	public void setImages(List<BufferedImage> images) {
		imageManipPanel.remove(show);
		this.images = images;
		show = new JLabel(new ImageIcon(images.get(0).getScaledInstance(400, -1, 0)));
		imageManipPanel.add(show);
		this.pack();
		this.repaint();
	}
}
