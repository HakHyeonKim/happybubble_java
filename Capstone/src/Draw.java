import java.awt.event.*;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;

public class Draw extends JPanel implements ActionListener {

	public static ArrayList<Integer> list = new ArrayList<Integer>();

	Timer time = new Timer(50, (ActionListener) this);
	int a = 0;
	int b = 3;
	int s = 5;
	int size = 0;
	
	public void animateLine(Graphics2D g2d) {
		list = Algoritm.getPathList();
		if (b == 3)
			size = list.size();
		//System.out.println(b);
		if (b < size)
			b += 3;
		
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
			if(b == size)
				time.stop();
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		repaint();
		//System.out.println("�׸�����");
		System.out.println(b);
		if(b == size -3)System.out.println("�׸��� �Ϸ�");
		System.out.println(b);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		animateLine(g2d);
	}
}