package ibur.ticktimecounter.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImageRotator extends JPanel {
	private List<BufferedImage> images;
	
	private BufferedImage demo;
	
	private Image scaledDemo;
	
	private JComboBox cb;
	
	private JFrame j;
	
	private GUI parent;
	
	public ImageRotator(List<BufferedImage> images) {
		super();
		this.images = images;
		this.demo = images.get(0);
		this.cb = new JComboBox(new Object[]{0, 90, 180, 270});
	}
	
	public void showRotateDialog(GUI parent) {
		this.parent = parent;
		parent.setEnabled(false);
		j = new JFrame("Cropper");
		j.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Image scaled = images.get(0).getScaledInstance(600, -1, 0);
		this.setPreferredSize(new Dimension(scaled.getWidth(null), scaled.getHeight(null)));
		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.add(this);
		root.add(new JLabel("Degrees:"));
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageRotator.this.rotate();
			}
		});
		JButton done = new JButton("Done");
		done.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				done();
			}
		});
		root.add(cb);
		root.add(done);
		j.getContentPane().add(root);
		j.pack();
		j.setVisible(true);
	}
	
	private void done() {
		j.dispose();
		parent.setEnabled(true);
		List<BufferedImage> nimages = new ArrayList<BufferedImage>();
		for(BufferedImage b : images) {
			BufferedImage n = null;
			switch(((Integer) cb.getSelectedItem()).intValue()) {
			case 0:
			default:
				n = b;
				break;
			case 90:
				n = rotateImage90(b);
				break;
			case 180:
				n = rotateImage180(b);
				break;
			case 270:
				n = rotateImage270(b);
				break;
			}
			nimages.add(n);
		}
		parent.setImages(nimages);
	}
	
	private void rotate() {
		switch(((Integer) cb.getSelectedItem()).intValue()) {
		case 0:
		default:
			demo = images.get(0);
			scaledDemo = demo.getScaledInstance(600, -1, 0);
			break;
		case 90:
			demo = rotateImage90(images.get(0));
			scaledDemo = demo.getScaledInstance(-1, 600, 0);
			break;
		case 180:
			demo = rotateImage180(images.get(0));
			scaledDemo = demo.getScaledInstance(600, -1, 0);
			break;
		case 270:
			demo = rotateImage270(images.get(0));
			scaledDemo = demo.getScaledInstance(-1, 600, 0);
			break;
		}
		this.setPreferredSize(new Dimension(scaledDemo.getWidth(null), scaledDemo.getHeight(null)));
		j.pack();
		j.repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if(demo != null) {
			if(scaledDemo == null) {
				scaledDemo = demo.getScaledInstance(600, -1, 0);
			}
			g.drawImage(scaledDemo, 0, 0, null);
		}
	}
	
	private static BufferedImage rotateImage90(BufferedImage b) {
		BufferedImage n = new BufferedImage(b.getHeight(), b.getWidth(), BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < b.getWidth(); x++) {
			for(int y = 0; y < b.getHeight(); y++) {
				n.setRGB(y, b.getWidth() - x - 1, b.getRGB(x, y));
			}
		}
		return n;
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
	
	private static BufferedImage rotateImage270(BufferedImage b) {
		BufferedImage n = new BufferedImage(b.getHeight(), b.getWidth(), BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < b.getWidth(); x++) {
			for(int y = 0; y < b.getHeight(); y++) {
				n.setRGB(b.getHeight() - y - 1, x, b.getRGB(x, y));
			}
		}
		return n;
	}
}
