package ibur.ticktimecounter;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageCropper extends JPanel implements MouseListener, MouseMotionListener {
	private List<BufferedImage> images;
	
	private volatile int click;
	private int x0, y0, x1, y1;
	
	private int index;
	public ImageCropper(List<BufferedImage> images) {
		super();
		this.images = images;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	public List<BufferedImage> showCropDialog() {
		JFrame j = new JFrame("Cropper");
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(images.get(0).getWidth()/2, images.get(0).getHeight()/2));
		j.add(this);
		j.pack();
		j.setVisible(true);
		List<BufferedImage> cropped = new ArrayList<BufferedImage>();
		for(index = 0; index < images.size(); index++) {
			j.repaint();
			this.repaint();
			click = 0;
			System.out.println("wait for click start");
			while(click != 2);
			System.out.println("waiting for clicks done");
			cropped.add(images.get(index).getSubimage(x0, y0, x1-x0, y1-y0));
		}
		j.dispose();
		return cropped;
	}

	@Override
	public void paintComponent(Graphics g) {
		if(images != null) {
			g.drawImage(images.get(index).getScaledInstance(images.get(index).getWidth()/2, -1, 0), 0, 0, null);
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
		} else {
			click--;
		}
		System.out.println("(" + e.getX() + "," + e.getY() + ")");
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
	
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	
	}
}
