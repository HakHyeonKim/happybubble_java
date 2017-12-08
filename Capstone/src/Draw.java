import java.awt.event.*;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;

public class Draw extends JPanel implements ActionListener {

	public static ArrayList<Integer> list = new ArrayList<Integer>();

	Timer time = new Timer(100, (ActionListener) this);
	int a = 0;
	int b = 3;
	int s = 10;
	int size = 0;

	public void animateLine(Graphics2D g2d) {
		list = Algoritm.getPathList();
		if (b == 3)
			size = list.size();
		// System.out.println(b);
		if (b < size)
			b += 3;
		synchronized (this) {
			for (a = 0; a < b; a += 3) {

				if (list.get(a + 3) == 0) {
					g2d.setColor(Color.black);
					g2d.drawLine(list.get(a + 1) * s, list.get(a + 2) * s, list.get(a + 4) * s, list.get(a + 5) * s);
					time.start();
				}

				else if (list.get(a + 3) != 0) {
					g2d.setColor(Color.red);
					g2d.drawLine(list.get(a + 1) * s, list.get(a + 2) * s, list.get(a + 4) * s, list.get(a + 5) * s);
					time.start();
				}
				if (b == size) {
					time.stop();
					notify();
				}
			}
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		repaint();
		// System.out.println("그리는중");
		// System.out.println(b);
		if (b == size - 3)
			System.out.println("그리기 완료");
		// System.out.println(b);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		animateLine(g2d);
	}
}