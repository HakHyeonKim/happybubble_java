import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Draw extends JPanel{
	public static ArrayList<Integer> list = new ArrayList<Integer>();
	public static int i = 6;
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		list = Algoritm.getPathList();
		
		for(int a = 0; a < list.size()-4; a += 3) {
			if(list.get(a+3) == 0) {
				g.setColor(Color.black);
				g.drawLine(list.get(a+1)*i, list.get(a+2)*i, list.get(a+4)*i, list.get(a+5)*i);
			}
			
			else if(list.get(a+3) != 0){
				g.setColor(Color.red);
				g.drawLine(list.get(a+1)*i, list.get(a+2)*i, list.get(a+4)*i, list.get(a+5)*i);
			}
		}
	}
}