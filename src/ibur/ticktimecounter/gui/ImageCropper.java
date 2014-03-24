package ibur.ticktimecounter.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImageCropper extends JPanel implements MouseListener, MouseMotionListener {
	private List<BufferedImage> images;
	
	private volatile int click;
	private int x0, y0, x1, y1;
	
	private int mx, my;
	
	private int index;
	
	private Image scaledImage;
	
	public ImageCropper(List<BufferedImage> images) {
		super();
		this.images = images;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	public List<BufferedImage> showCropDialog(Component parent) {
		if(parent != null) {
			parent.setEnabled(false);
		}
		JFrame j = new JFrame("Cropper");
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(images.get(0).getWidth()/2, images.get(0).getHeight()/2));
		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.add(this);
		root.add(new JLabel("Click on the top left and then bottom right corner of desired area.  Rightclick to undo the first click."));
		j.getContentPane().add(root);
		j.pack();
		j.setVisible(true);
		List<BufferedImage> cropped = new ArrayList<BufferedImage>();
		for(index = 0; index < images.size(); index++) {
			scaledImage = null;
			j.repaint();
			this.repaint();
			click = 0;
			System.out.println("wait for click start");
			while(click != 2);
			System.out.println("waiting for clicks done");
			try{
				cropped.add(images.get(index).getSubimage(x0, y0, x1-x0, y1-y0));
			}
			catch(RasterFormatException e) {
				e.printStackTrace();
			}
		}
		j.dispose();
		if(parent != null) {
			parent.setEnabled(true);
		}
		return cropped;
	}

	private int max(int x, int y) {
		return x > y ? x : y;
	}
	
	private int min(int x, int y) {
		return x < y ? x : y;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if(images != null) {
			if(scaledImage == null) {
				scaledImage = images.get(index).getScaledInstance(images.get(index).getWidth()/2, -1, 0);
			}
			g.drawImage(scaledImage, 0, 0, null);
			if(click == 1 && x0/2 < mx && y0/2 < my) {
				g.setColor(Color.RED);
				int lx = min(x0/2, mx), rx = max(x0/2, mx);
				int ty = min(y0/2, my), by = max(y0/2, my);
				g.drawRect(lx, ty, rx-lx, by-ty);
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(click == 0) {
				x0 = e.getX() * 2;
				y0 = e.getY() * 2;
				click++;
			} else if(click == 1) {
				x1 = e.getX() * 2;
				y1 = e.getY() * 2;
				click++;
			}
		} else if(click == 1) {
			click--;
		}
		try {
			System.out.println("(" + e.getX() + "," + e.getY() + ")");
		}
		catch(IndexOutOfBoundsException exc) {
			
		}
		System.out.println(click);
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
		this.repaint();
	}
}
