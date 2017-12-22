import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;

public class Algorithm extends SocketComm {
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
	public static int start_point = 0;

	public static void Algo(int[] marker1) {
		// TODO Auto-generated method stub

		new SocketComm();

		input = FindVertex.pattern(SocketComm.getImgToArr());

		/*
		 * 테두리 부분은 찾을 수 없는 길로 표시를 하기 위해 모두 0으로 설정
		 */
		for (int c = 0; c < input.length; c++) {
			for (int r = 0; r < input[0].length; r++) {
				input[0][c] = 0;
				input[input.length - 1][c] = 0;
				input[r][0] = 0;
				input[r][input[0].length - 1] = 0;
			}
		}

		x_length = ImageProcessing.getx_length();
		y_length = ImageProcessing.gety_length();
		xpixel_length = x_length / input[0].length;
		ypixel_length = y_length / input.length;
		
		/*
		 * 시뮬레이션의 크기와 나타나는 위치 지정
		 */
		Dimension dim = new Dimension(800, 800);

		JFrame frame = new JFrame();
		frame.setLocation(800, 0);
		frame.setPreferredSize(dim);

		i = marker1[0]; // // 자동차가 현재 있는 x좌표
		j = marker1[1]; // // 자동차가 현재 있는 y좌표
		g = i;
		h = j;

		pen = 1;//펜을 들고 이동
		
		pathList.add(pen);
		pathList.add(i);
		pathList.add(j);

		while (control) {
			int finish_check = 0;
			check_path++;
			if (check_path == 9)
				check_path++;
			start(i, j, check_path);

			/*
			 * 더 가야할 지점이 있는지 확인
			 */
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
				int upPen = penList.get(a);
				if (angle == preAngle) {
					penList.remove(a);
					Points.remove(a);
					AngleList.remove(a);
					a--;
				}
			}
			
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
		Draw draw = new Draw();
		frame.add(draw);
		frame.pack();
		frame.setVisible(true);

		/*
		 * 시뮬레이션이 종료 된 후 자동차를 움직이기 위해 설정
		 */
		synchronized (draw) {
			try {
				draw.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static double getXpixel_length() {
		return xpixel_length;
	}

	public static void setXpixel_length(double xpixel_length) {
		Algorithm.xpixel_length = xpixel_length;
	}

	public static double getYpixel_length() {
		return ypixel_length;
	}

	public static void setYpixel_length(double ypixel_length) {
		Algorithm.ypixel_length = ypixel_length;
	}

	/*
	 * 펜을 들고 이동하는지 아닌지와 위치좌표를 리스트에 저장
	 */
	public static ArrayList<Integer> getPathList() {
		for (int k = 0; k < Points.size(); k++) {
			pathList.add((int) penList.get(k));
			pathList.add((int) Points.get(k).x);
			pathList.add((int) Points.get(k).y);
		}
		return pathList;
	}

	public static HashMap<String, Object> getHash() {
		return Algorithm.test;
	}

	/*
	 * 자동차의 초기 위치를 입력 받아서 시작하는 부분
	 */
	public static void start(int x, int y, int check_path) {
		m = input.length;// 행의 길이
		n = input[0].length;// 열의 길이

		// 배열의 top pointer 용으로 쓸 행은 (m*2)(n*2)
		stack = new int[(m + 2) * (n + 2)][3];

		i = stack[0][0] = x; // 현재 위치 행
		j = stack[0][1] = y; // 현재 위치 열
		stack[0][2] = 0; // 마지막 움직인 방향
		mov = 0;

		MovePath(i, j, check_path);//다음 다야할 길을 찾는다

		count = countloop(check_path);

		while (count != 0) {
			minDistance(g, h, check_path);//최단 거리의 점을 찾아 간다

			count = countloop(check_path);
		}
	}

	/*
	 * 가야할 지점이 있는지 체크
	 */
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

	/*
	 * 떨어져 있는 점중 최단 거리를 찾기
	 */
	public static void minDistance(int g, int h, int check_path) {
		P StartP;
		if (start_point == 0) {
			StartP = new P(j, i);
			start_point++;
		}
		else 
			StartP = new P(i, j);
		
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
		double getangle = getAngle((int) StartP.y, (int) StartP.x, (int) nextP.y, (int) nextP.x);
		
		P pointA = new P(nextP.y, nextP.x);
		pen = 1;
		arrayOutput(pen, pointA, getangle);
		pen = 0;

		i = (int) nextP.x;
		j = (int) nextP.y;
		MovePath(i, j, check_path);
	}

	static double distance(P p1, P p2) {
		return p1.distance(p2);
	}

	public static void arrayOutput(int pen, P point, double degree) {
		penList.add(pen);
		Points.add(point);
		AngleList.add(degree);
	}

	/*
	 * 이동 후 다음 위치를 현재위치로 바꿔주고 이동 각도 등을 계산한다
	 */
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

			P startP = new P(j, i);
			P points = new P(h, g);

			newdegree = getAngle(j, i, h, g);

			P pointB = new P(h, g);
			arrayOutput(pen, pointB, newdegree);

			i = g;
			j = h;
		}
	}

	/*
	 * 8방향중 어떤 방향으로 움직일지 결정
	 */
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
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col][row + 1] == checkpath) {
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col - 1][row] == checkpath) {
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col][row - 1] == checkpath) {
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col + 1][row + 1] == checkpath) {
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col + 1][row - 1] == checkpath) {
			/*
			 * 두 지점 사이의 각도 계산
			 */
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col - 1][row - 1] == checkpath) {
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		movedirect++;
		if (input[col - 1][row + 1] == checkpath) {
			DirectionCheck(col, row, movedirect, 0);
			MovePath(i, j, checkpath);
		}
		pen = 1;
	}

	/*
	 * 두 지점 사이의 각도 계산
	 */
	private static double getAngle(int x1, int y1, int x2, int y2) {
		double dx = x2 - x1;
		double dy = y2 - y1;

		double rad = Math.atan2(dx, dy);
		double degree = (rad * 180) / Math.PI;

		degree = Math.round(degree * 1000) / 1000.0;

		return degree;
	}
}