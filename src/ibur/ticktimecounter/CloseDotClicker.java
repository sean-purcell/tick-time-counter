package ibur.ticktimecounter;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class CloseDotClicker extends JPanel implements MouseListener, MouseMotionListener {
	private BufferedImage image;
	
	private volatile boolean done;
	
	private List<Point> clicks;
	
	public CloseDotClicker(BufferedImage image) {
		super();
		this.image = image;
		this.clicks = new ArrayList<Point>();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	public List<Point> showClickWindow() {
		JFrame j = new JFrame("Close when done clicking on points");
		System.out.println("Click on the ones too close to separate in order and then the first separate one");
		j.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		j.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				done = true;
			}
		});
		this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		j.add(this);
		j.pack();
		j.setVisible(true);
		while(!done){}
		j.dispose();
		return clicks;
	}

	@Override
	public void paintComponent(Graphics g) {
			g.drawImage(image, 0, 0, null);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			clicks.add(new Point(e.getX(), e.getY()));
		} else {
			clicks.remove(clicks.size()-1);
		}
		System.out.println("(" + e.getX() + "," + e.getY() + ")");
		System.out.println(clicks.size());
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
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}
	
	public static List<Dot> insertClicks(List<Dot> data, List<Point> clicks) {
		Dot first = new Dot(0, clicks.get(0).x);
		List<Dot> ndata = new ArrayList<Dot>(data.size());
		ndata.add(new Dot(0, 0));
		for(int i = 1; i < clicks.size()-1; i++) {
			ndata.add(new Dot(clicks.get(i).x - clicks.get(i-1).x, clicks.get(i).x - clicks.get(i-1).x + ndata.get(i-1).pos));
		}
		for(int i = 2; i < data.size(); i++) {
			ndata.add(new Dot(data.get(i).distBack, data.get(i).distBack + ndata.get(ndata.size()-1).pos)); // set pos to be distance from start
		}
		return ndata;
	}
}
