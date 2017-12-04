import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JPanel;

//혼합 자바

public class Algoritm extends SocketTest {
	public static ArrayList<Integer> penList = new ArrayList<Integer>();
	public static ArrayList<P> Points = new ArrayList<P>();
	public static ArrayList<Double> AngleList = new ArrayList<Double>();
	public static ArrayList<Integer> pathList = new ArrayList<Integer>();
	public static HashMap<String, Object> test = new HashMap<String, Object>();
	public static int se1 = 0, se2 = 0, se3 = 0, pen = 0;
	public static P nextP;
	public static int m, n, count = 0;
	public static int[][] mark;
	public static int[][] stack;
	public int[] save;
	public static double predegree = 0, movedegree;
	public static boolean control = true;
	public static int check_path = 0, change = 0;
	public static int[][] move = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, -1 },
			{ -1, 1 } };
	public static int top = 0, i, j, g, h, mov;
	public static int[][] input;
	public static double x_length;
	public static double y_length;
	public static double xpixel_length, ypixel_length;

	public static void Algo(int[][] marker1) {
		// TODO Auto-generated method stub

		new SocketTest();

		input = FindVertex.pattern(SocketTest.getImgToArr());
		
		for(int c = 0; c < input.length; c++) {
			for(int r = 0; r < input[0].length; r++) {
				input[0][c] = 0;
				input[input.length-1][c] = 0;
				input[r][0] = 0;
				input[r][input[0].length-1] = 0;
			}
		}

		x_length = ImageProcessing.getX_length();
		y_length = ImageProcessing.getY_length();
		xpixel_length = x_length / input[0].length;
		ypixel_length = y_length / input.length;

		Dimension dim = new Dimension(800, 700);

		JFrame frame = new JFrame();
		frame.setLocation(0, 0);
		frame.setPreferredSize(dim);

		i = marker1[2][0]; // x
		j = marker1[2][1]; // y
		System.out.println(i + " , " + j);
		g = i;
		h = j;

		pathList.add(pen);
		pathList.add(i);
		pathList.add(j);

		while (control) {
			int finish_check = 0;
			check_path++;
			if (check_path == 9)
				check_path++;
			start(i, j, check_path);

			for (int a = 1; a < input.length - 1; a++) {
				for (int b = 1; b < input[0].length - 1; b++) {
					if (input[a][b] != 0 && input[a][b] != 9) {
						finish_check++;
					}
				}
			}

			for (int a = 0; a < AngleList.size() - 1; a++) {
				double angle = AngleList.get(a);
				double preAngle = AngleList.get(a + 1);
				// System.out.println(angle + " / " + preAngle);
				if (angle == preAngle) {
					penList.remove(a);
					Points.remove(a);
					AngleList.remove(a);
					a--;
				}
			}

			/*
			 * String LastOutput = mArrayList.toString(); String LastOutput2; LastOutput2 =
			 * LastOutput.replace("[", ""); LastOutput2 = LastOutput2.replace("]", "");
			 * LastOutput2 = LastOutput2.replace(", ", "");
			 * 
			 * String date[] = LastOutput2.split("-");
			 * 
			 * for(int i=0 ; i< date.length ; i++) { System.out.println(date[i]); }
			 * 
			 * System.out.println(pathList);
			 */
			if (finish_check == 0) {
				control = false;
			}
		}
		test.clear();
		test.put("pen", penList);
		test.put("Point", Points);
		test.put("Angle", AngleList);
		test.put("Width", input.length);
		test.put("Height", input[0].length);

		System.out.println(test.get("pen"));
		System.out.println(test.get("Point"));
		System.out.println(test.get("Angle"));

		System.out.println("최종");
		/*
		 * for(int k = 0;k < input.length;k++) { for(int m = 0;m < input[0].length;m++)
		 * System.out.print(input[k][m] + " "); System.out.println(""); }
		 * System.out.println("");
		 */
		/*
		 * System.out.println(pathList);
		 */
		Draw draw = new Draw();
		frame.add(draw);
		frame.pack();
		frame.setVisible(true);

	}

	public static double getXpixel_length() {
		return xpixel_length;
	}

	public static void setXpixel_length(double xpixel_length) {
		Algoritm.xpixel_length = xpixel_length;
	}

	public static double getYpixel_length() {
		return ypixel_length;
	}

	public static void setYpixel_length(double ypixel_length) {
		Algoritm.ypixel_length = ypixel_length;
	}

	public static ArrayList<Integer> getPathList() {
		return pathList;
	}

	public static HashMap<String, Object> getHash() {
		return Algoritm.test;
	}

	public static void start(int x, int y, int check_path) {
		m = input.length;// 행의 길이
		n = input[0].length;// 열의 길이

		// 배열의 top pointer 용으로 쓸 행은 (m*2)(n*2)
		stack = new int[(m + 2) * (n + 2)][3];

		i = stack[0][0] = x; // 현재 위치 행
		j = stack[0][1] = y; // 현재 위치 열
		stack[0][2] = 0; // 마지막 움직인 방향
		mov = 0;

		MovePath(i, j, check_path);

		count = countloop(check_path);

		while (count != 0) {
			/*
			 * System.out.println("떨어져 있는 길이 존재합니다.");
			 */
			minDistance(g, h, check_path);

			count = countloop(check_path);
		}
	}

	public static int countloop(int check_path) {
		int count = 0;

		for (int a = 1; a < input.length - 1; a++) {
			for (int b = 1; b < input[0].length - 1; b++) {
				if (input[a][b] == check_path)
					count++;
			}
		}
		return count;
	}

	public static void minDistance(int g, int h, int check_path) {
		P StartP = new P(g, h);
		/*
		 * System.out.println("시작 좌표값:(" + (int)StartP.x+","+ (int)StartP.y+")");
		 */
		double min = 10000;

		for (int a = 0; a < input.length; a++) {
			for (int b = 0; b < input[0].length; b++) {
				if (input[a][b] == check_path) {
					P minP = new P(a, b);
					double dist = distance(StartP, minP);
					if (min > dist) {
						min = dist;
						nextP = minP;
					}
				}
			}
		}
		double getangle = getangle = getAngle((int) StartP.x, (int) StartP.y, (int) nextP.x, (int) nextP.y);
		/*
		 * System.out.println("point : (" + (int)nextP.x + "," + (int)nextP.y +")");
		 * System.out.println("최소  dist: "+ Math.round(min) + "   각도 : "+ getangle +
		 * "도");
		 * System.out.println("펜을 들고("+(int)StartP.x+","+(int)StartP.y+") > ("+(int)
		 * nextP.x+","+(int)nextP.y+")로 이동"); System.out.println("");
		 */

		P pointA = new P(nextP.x, nextP.y);
		pen = 1;
		arrayOutput(pen, pointA, getangle);
		pathOutput((int) nextP.x, (int) nextP.y, pen);
		pen = 0;

		i = (int) nextP.x;
		j = (int) nextP.y;
		MovePath(i, j, check_path);
	}

	static double distance(P p1, P p2) {
		return p1.distance(p2);
	}

	public static void pathOutput(int cal, int row, int pen) {
		pathList.add(pen);
		pathList.add(cal);
		pathList.add(row);
	}

	public static void arrayOutput(int pen, P point, double degree) {
		penList.add(pen);
		Points.add(point);
		AngleList.add(degree);
	}

	public static void DirectionCheck(int col, int row, int direct, int c) {
		double newdegree;

		if (c == 1) {
			g = col;
			h = row;
			i = g;
			j = h;
		}

		else {
			g = col + move[direct][0];
			h = row + move[direct][1];

			P q = new P(i, j);
			P w = new P(g, h);
			double di = distance(q, w);
			/*
			 * if(di > Math.sqrt(2)) {
			 * System.out.println("펜을 들고("+i+","+j+") > ("+g+","+h+")로 이동");
			 * System.out.println(""); }
			 * 
			 * System.out.println("("+i+","+j+") > ("+g+","+h+")");
			 */
			pathOutput(g, h, pen);

			P startP = new P(i, j);
			P points = new P(g, h);

			newdegree = getAngle(i, j, g, h);

			// System.out.println(newdegree+"도");
			P pointB = new P(g, h);
			arrayOutput(pen, pointB, newdegree);

			i = g;
			j = h;

			/*
			 * for(int k = 0;k < input.length;k++) { for(int m = 0;m < input[0].length;m++)
			 * System.out.print(input[k][m] + " "); System.out.println(""); }
			 * System.out.println("");
			 */

		}
	}

	public static void MovePath(int col, int row, int checkpath) {
		int movedirect = 0;
		pen = 0;

		if (input[col][row] == checkpath && input[col + 1][row] != checkpath && input[col][row + 1] != checkpath
				&& input[col + 1][row + 1] != checkpath && input[col - 1][row] != checkpath
				&& input[col][row - 1] != checkpath && input[col - 1][row - 1] != checkpath
				&& input[col + 1][row - 1] != checkpath && input[col - 1][row + 1] != checkpath) {

			input[i][j] = 0;
			top++;
			stack[top][0] = i;
			stack[top][1] = j;
			stack[top][2] = movedirect;

			mov = 0;

			DirectionCheck(col, row, movedirect, 1);
			MovePath(i, j, checkpath);
		}

		input[i][j] = 0;
		top++;
		stack[top][0] = i;
		stack[top][1] = j;
		stack[top][2] = movedirect;

		mov = 0;

		if (input[col + 1][row] == checkpath) {
			// System.out.println("+0");
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col][row + 1] == checkpath) {
			// System.out.println("0+");
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col - 1][row] == checkpath) {
			// System.out.println("-0");
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col][row - 1] == checkpath) {
			// System.out.println("0-");
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col + 1][row + 1] == checkpath) {
			// System.out.println("++");
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col + 1][row - 1] == checkpath) {
			// System.out.println("-+");
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col - 1][row - 1] == checkpath) {
			// System.out.println("--");
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col - 1][row + 1] == checkpath) {
			// System.out.println("+-");
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		pen = 1;
	}

	private static double getAngle(int x1, int y1, int x2, int y2) {
		double dx = x2 - x1;
		double dy = y2 - y1;

		double rad = Math.atan2(dx, dy);
		double degree = (rad * 180) / Math.PI;

		degree = Math.round(degree * 1000) / 1000.0;

		return degree;
	}
}