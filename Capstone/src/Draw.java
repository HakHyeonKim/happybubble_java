import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Draw extends JPanel implements ActionListener {

	public static ArrayList<Integer> list = new ArrayList<Integer>();

	Timer time = new Timer(50, (ActionListener) this);
	int a = 0; //list 시작 변수
	int b = 3; //list 끝 변수
	int s = 5; //draw 이미지 크기
	int size = 0; //pathList size
	
	public void animateLine(Graphics2D g2d) {
		list = Algorithm.getPathList();
		if (b == 3)
			size = list.size();
		if (b < size)
			b += 3;
		if(b == size)
			System.out.println("Draw End");
	
		synchronized(this){
		for (a = 0; a < b; a += 3) {

			if (list.get(a + 3) == 0) {
				g2d.setColor(Color.black);
				g2d.drawLine(list.get(a + 1)*s, list.get(a + 2)*s, list.get(a + 4)*s, list.get(a + 5)*s);
				time.start();
			}

			else if (list.get(a + 3) != 0) {
				g2d.setColor(Color.red);
				g2d.drawLine(list.get(a + 1)*s, list.get(a + 2)*s, list.get(a + 4)*s, list.get(a + 5)*s);
				time.start();
			}
			if(b == size){
				time.stop();
				notify();
				}
			}
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		repaint();
		}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		animateLine(g2d);
	}
}