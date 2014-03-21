package ibur.ticktimecounter.gui;

import ibur.ticktimecounter.ImageAnalysis;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImageBooler extends JPanel {
	private List<BufferedImage> images;
	
	private volatile int index;
	
	private volatile boolean click;
	
	private Image scaledImage;
	
	private JSlider slider;
	
	private JFrame j;
	
	public ImageBooler(List<BufferedImage> images) {
		super();
		this.images = images;
		this.slider = new JSlider(0, 255, 192);
		slider.addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent ce) {
	            scaledImage = null;
	            j.pack();
	            j.repaint();
	        }
	    });
	}
	
	public List<BufferedImage> showBoolDialog(Component parent) {
		if(parent != null) {
			parent.setEnabled(false);
		}
		j = new JFrame("Cropper");
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(images.get(0).getWidth()/2, images.get(0).getHeight()/2 + 30));
		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.add(this);
		root.add(new JLabel("Make the dots black and everything else white.  Threshold:"));
		root.add(slider);
		JButton done = new JButton("Done");
		done.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				click = true;
			}
		});
		root.add(done);
		j.getContentPane().add(root);
		j.pack();
		j.setVisible(true);
		List<BufferedImage> boolized = new ArrayList<BufferedImage>();
		for(index = 0; index < images.size(); index++) {
			scaledImage = null;
			j.repaint();
			this.repaint();
			click = false;
			while(!click);
			try{
				boolized.add(ImageAnalysis.boolToImage(ImageAnalysis.boolizeImage(images.get(index), slider.getValue())));
			}
			catch(RasterFormatException e) {
				e.printStackTrace();
			}
		}
		j.dispose();
		if(parent != null) {
			parent.setEnabled(true);
		}
		return boolized;
	}

	@Override
	public void paintComponent(Graphics g) {
		if(images != null) {
			if(scaledImage == null) {
				scaledImage = ImageAnalysis.boolToImage(ImageAnalysis.boolizeImage(images.get(index), slider.getValue())).getScaledInstance(images.get(index).getWidth()/2, -1, 0);
			}
			g.drawImage(scaledImage, 0, 0, null);
		}
	}
}
