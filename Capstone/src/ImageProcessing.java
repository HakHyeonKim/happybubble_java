import java.util.ArrayList;
import java.util.HashMap;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ImageProcessing {
	static HashMap<String, Object> path = new HashMap<String, Object>();
	static Mat video = new Mat();
	static Mat wrapMat = new Mat();
	static int matrixXSize = 200;
	static int matrixYSize = 100;
	static double angle = 0; // ����
	static double x_length, y_length; // ���� ���� x,y ����
	public static Point[] sortedPoint = new Point[4]; // ���� ���� ��Ŀ ��ǥ ����
	public static ArrayList<Integer> penList = new ArrayList<Integer>(); // 0 : �� ���x 1 : ���� o
	public static ArrayList<P> Points = new ArrayList<P>(); // ��� ��ǥ
	public static ArrayList<Double> AngleList = new ArrayList<Double>(); // �̵� ���� ����
	public static ArduinoControl rcCar = new ArduinoControl(); 
	public static int[] car_marker = new int[2]; // rcī ��ǥ (���� ��ǥ)
	final static int carSize = 100;
	final static int videoSize = 700;
	final static int[] setArea = {carSize, videoSize - carSize};
	final static int pixelUnit = 100;
	public static int car_x, car_y;
	static int i = 0; // ��� index
	
	static {
		String opencvPath = "C:\\opencv\\build\\java\\x64\\";
		System.load(opencvPath + Core.NATIVE_LIBRARY_NAME + ".dll");
	}
	
	public static void main(String[] args) throws Throwable {
		SetArea set_area = new SetArea();
		CarDetector car_mark = new CarDetector();
		Boolean areaChk = false;
		
		VideoCapture cap = new VideoCapture(1);
		if (!cap.isOpened())
			System.exit(-1);
		
		cap.read(video);
		setArea(cap, areaChk, set_area);
		car_detection(car_mark, cap, 5, -1);		
		getPathInfo();		
		rcCar.jssc();
		
		while (true) {
			double min = 10000;
			int num = 0, valid = 3;			
			
			approxAngle(cap, car_mark);

			exactAngle(cap, car_mark);
			
			delay();

			car_detection(car_mark, cap, 0, i);
			go();
			
			feedback(cap, car_mark, min, num, valid);			
		}
	}
	
	public static void delay() {
		try {
			Thread.sleep(1500);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * ���� ������ ���� Z ���� �ø����� C
	 */	
	public static void go() {
		if (penList.get(i) == 0) {
			rcCar.getOrder("C");
			try {
				Thread.sleep(500);
			}catch(Exception e) {
				e.printStackTrace();
			}
			rcCar.getOrder("X");				
		} else if (penList.get(i) == 1) {
			rcCar.getOrder("Z");
			try {
				Thread.sleep(500);
			}catch(Exception e) {
				e.printStackTrace();
			}
			rcCar.getOrder("X");
		}
	}
	/*
	 * ��ǥ ��ǥ�� ���� ��ǥ���� gap�� ���� ���� ������ ����
	 */	
	public static void feedback(VideoCapture cap, CarDetector car_mark, double min, int num, int valid) {
		while(true) {
			car_detection(car_mark, cap, 0, i);
			int gap_x, gap_y;
			double check_out;
			if (tolerance(i) == 2) {
				rcCar.getOrder("S");
				i++;
				break;
			}
			
			check_out = distance(i);
	
			if(check_out > min) {
				num++;
			}
			else {
				min = check_out;
				num = 0;
			}
			System.out.println("min : " + min + " , num : "+num);
			
			if(num == valid) {
				rcCar.getOrder("S");
				System.out.println("out");
				gap_x = (int) Points.get(i).x - car_marker[0];
				gap_y = (int) Points.get(i).y - car_marker[1];
				i++;
				P change_p = new P(Points.get(i).x-gap_x, Points.get(i).y-gap_y);
				Points.set(i, change_p);
				break;
				}
		}
	}
	/*
	 * ��� ������ �޾� �´�.
	 */	
	public static void getPathInfo() {
		Algoritm.Algo(car_marker);
		path = Algoritm.getHash();
		penList = (ArrayList<Integer>) path.get("pen");
		Points = (ArrayList<P>) path.get("Point");
		AngleList = (ArrayList<Double>) path.get("Angle");
		matrixXSize = (int) path.get("Width");
		matrixYSize = (int) path.get("Height");
	}
	/*
	 * �׸� ���� ����
	 */	
	public static void setArea(VideoCapture cap, Boolean areaChk, SetArea set_area) {
		while (true) {
			cap.read(video);
			areaChk = set_area.set_a(video);
			if(areaChk) {
				sortedPoint = set_area.getSortedPoint();
				break;
			}
		}
	}
	/*
	 * ��Ȯ�� ������ RC car ����
	 */	
	public static void exactAngle(VideoCapture cap, CarDetector car_mark) {
		if (!(AngleList.get(i) <= angle + 0.5 && AngleList.get(i) >= angle - 0.5)) {
			while(true) {
				try {
					Thread.sleep(150);
				}catch(Exception e) {
					e.printStackTrace();
				}
				car_detection(car_mark, cap, 0, i);
	
				if (AngleList.get(i) <= angle + 0.5 && AngleList.get(i) >= angle - 0.5) {
					System.out.println("�Ϸ� car : " + angle);
					break;
				}
				else if(AngleList.get(i) == 180 && -AngleList.get(i) >= angle - 0.5 && angle < 0) {
					System.out.println("�Ϸ� car : " + angle);
					break;
				}
							
				if(AngleList.get(i) == 180.0) {
					if(angle > 0) // ������ �� ŭ
						turn_left();
					else if(angle < 0) // ������ �� ����
						turn_right();
				}
				else if(AngleList.get(i) < angle - 0.5) // ������ �� ŭ
					turn_right();
				else if(AngleList.get(i) > angle + 0.5) // ������ �� ����
					turn_left();
			}
		}
	}
	/*
	 * �뷫 ���� ������ ������ ����
	 */	
	public static void approxAngle(VideoCapture cap, CarDetector car_mark) {
		rcCar.getOrder("Z");
		try {
			Thread.sleep(500);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		rcCar.getOrder("D");
		System.out.println("get : " + AngleList.get(i));
		
		while(true) {
			car_detection(car_mark, cap, 0, i);
			car_x = car_marker[0];
			car_y = car_marker[1];

			if (AngleList.get(i) <= angle + 15 && AngleList.get(i) >= angle - 15) {
				rcCar.getOrder("S");
				System.out.println("car : " + angle);
				break;
			}
		}
	}
	
	public static void turn_left() {
		rcCar.getOrder("A");
		try {
			Thread.sleep(100);
		}catch(Exception e) {
			e.printStackTrace();
		}
		rcCar.getOrder("S");
	}
	
	public static void turn_right() {
		rcCar.getOrder("D");
		try {
			Thread.sleep(130);
		}catch(Exception e) {
			e.printStackTrace();
		}
		rcCar.getOrder("S");
	}
	/*
	 * x��ǥ�� y��ǥ�� ��� ��ǥ ��ǥ�� ���� �ȿ� ���ý� return
	 */	
	public static int tolerance(int i) {
		int a = 0;
		if (Points.get(i).x <= (car_marker[0] + 1) && Points.get(i).x >= (car_marker[0] - 1)) {
			a++;
		}
		if (Points.get(i).y <= (car_marker[1] + 1) && Points.get(i).y >= (car_marker[1] - 1)) {
			a++;
		}
		return a;
	}

	public static double distance(int i) {
		double dis;
		dis = Math.sqrt((Points.get(i).x - car_marker[0])*(Points.get(i).x - car_marker[0]) + (Points.get(i).y - car_marker[1])*(Points.get(i).y - car_marker[1]));
		
		dis = Math.round(dis * 1000) / 1000;
		
		return dis;
	}
	/*
	 * RC car marker �ν� �� �� ��ǥ ���
	 */	
	public static void car_detection(CarDetector car_mark, VideoCapture cap, int vaild, int idx) {
		int[] car_markerTemp = {0, 0};
		int a = 0;
		int[] translateMarker = new int[2];
		int cordLength = (setArea[1] - setArea[0]) / pixelUnit;
		while(true) {
			cropArea(cap);
			car_mark.CarDetect(video);
			car_marker = car_mark.getCarMarker();
			angle = car_mark.getCarAngle();
			if(car_marker[0] == car_markerTemp[0] && car_marker[1] == car_markerTemp[1] && car_markerTemp[0] != 0)
				a++;
			else {
				car_markerTemp = car_marker;
				a = 0;
			}
			if(a >= vaild)
				break;
		}
		if(idx != -1)
			Imgproc.circle(video, new Point(Points.get(idx).x, Points.get(idx).y), 3, new Scalar(255, 0, 0));

		translateMarker[0] = (car_marker[0] - setArea[0]) / cordLength;
		translateMarker[1] = (car_marker[1] - setArea[0]) / cordLength;
		car_marker[0] = translateMarker[0];
		car_marker[1] = translateMarker[1];
	}
	/*
	 * ���� ������ �κи� �߶� �����ֱ�
	 */	
	public static void cropArea(VideoCapture cap) {
		cap.read(video);
		Imgproc.resize(video, video, new Size(700,700));
	    MatOfPoint2f src = new MatOfPoint2f(sortedPoint[0], sortedPoint[1], sortedPoint[2],sortedPoint[3]);
	    int w = video.cols();
	    int h = video.rows();
	    MatOfPoint2f dst = new MatOfPoint2f(new Point(0, 0), new Point(w-1,0), new Point(0,h-1), new Point(w-1,h-1));
	    Mat wrapMat = Imgproc.getPerspectiveTransform(src,dst);
	    Imgproc.warpPerspective(video, video, wrapMat, video.size());
	}

	public static int[] getCarMarker() {
		return car_marker;
	}

	public static void setCarMarker(int[] marker) {
		ImageProcessing.car_marker = marker;
	}
	
	public static double getX_length() { 
		x_length = Math.sqrt(Math.pow((sortedPoint[1].x - sortedPoint[0].x), 2) + Math.pow((sortedPoint[1].y - sortedPoint[0].y), 2)); 
		return x_length; 
	} 

	public static double getY_length() { 
		y_length = Math.sqrt(Math.pow((sortedPoint[2].x - sortedPoint[0].x), 2) + Math.pow((sortedPoint[2].y - sortedPoint[0].y), 2)); 
		return y_length; 
	} 

}import java.util.ArrayList;
import java.util.HashMap;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ImageProcessing {
	static HashMap<String, Object> path = new HashMap<String, Object>();
	static Mat video = new Mat();
	static Mat wrapMat = new Mat();
	static int matrixXSize = 200;
	static int matrixYSize = 100;
	static double angle = 0; // ����
	static double x_length, y_length; // ���� ���� x,y ����
	public static Point[] sortedPoint = new Point[4]; // ���� ���� ��Ŀ ��ǥ ����
	public static ArrayList<Integer> penList = new ArrayList<Integer>(); // 0 : �� ���x 1 : ���� o
	public static ArrayList<P> Points = new ArrayList<P>(); // ��� ��ǥ
	public static ArrayList<Double> AngleList = new ArrayList<Double>(); // �̵� ���� ����
	public static ArduinoControl rcCar = new ArduinoControl(); 
	public static int[] car_marker = new int[2]; // rcī ��ǥ (���� ��ǥ)
	final static int carSize = 100;
	final static int videoSize = 700;
	final static int[] setArea = {carSize, videoSize - carSize};
	final static int pixelUnit = 100;
	public static int car_x, car_y;
	static int i = 0; // ��� index
	
	static {
		String opencvPath = "C:\\opencv\\build\\java\\x64\\";
		System.load(opencvPath + Core.NATIVE_LIBRARY_NAME + ".dll");
	}
	
	public static void main(String[] args) throws Throwable {
		SetArea set_area = new SetArea();
		CarDetector car_mark = new CarDetector();
		Boolean areaChk = false;
		
		VideoCapture cap = new VideoCapture(1);
		if (!cap.isOpened())
			System.exit(-1);
		
		cap.read(video);
		setArea(cap, areaChk, set_area);
		car_detection(car_mark, cap, 5, -1);		
		getPathInfo();		
		rcCar.jssc();
		
		while (true) {
			double min = 10000;
			int num = 0, valid = 3;			
			
			approxAngle(cap, car_mark);

			exactAngle(cap, car_mark);
			
			delay();

			car_detection(car_mark, cap, 0, i);
			go();
			
			feedback(cap, car_mark, min, num, valid);			
		}
	}
	
	public static void delay() {
		try {
			Thread.sleep(1500);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * ���� ������ ���� Z ���� �ø����� C
	 */	
	public static void go() {
		if (penList.get(i) == 0) {
			rcCar.getOrder("C");
			try {
				Thread.sleep(500);
			}catch(Exception e) {
				e.printStackTrace();
			}
			rcCar.getOrder("X");				
		} else if (penList.get(i) == 1) {
			rcCar.getOrder("Z");
			try {
				Thread.sleep(500);
			}catch(Exception e) {
				e.printStackTrace();
			}
			rcCar.getOrder("X");
		}
	}
	/*
	 * ��ǥ ��ǥ�� ���� ��ǥ���� gap�� ���� ���� ������ ����
	 */	
	public static void feedback(VideoCapture cap, CarDetector car_mark, double min, int num, int valid) {
		while(true) {
			car_detection(car_mark, cap, 0, i);
			int gap_x, gap_y;
			double check_out;
			if (tolerance(i) == 2) {
				rcCar.getOrder("S");
				i++;
				break;
			}
			
			check_out = distance(i);
	
			if(check_out > min) {
				num++;
			}
			else {
				min = check_out;
				num = 0;
			}
			System.out.println("min : " + min + " , num : "+num);
			
			if(num == valid) {
				rcCar.getOrder("S");
				System.out.println("out");
				gap_x = (int) Points.get(i).x - car_marker[0];
				gap_y = (int) Points.get(i).y - car_marker[1];
				i++;
				P change_p = new P(Points.get(i).x-gap_x, Points.get(i).y-gap_y);
				Points.set(i, change_p);
				break;
				}
		}
	}
	/*
	 * ��� ������ �޾� �´�.
	 */	
	public static void getPathInfo() {
		Algoritm.Algo(car_marker);
		path = Algoritm.getHash();
		penList = (ArrayList<Integer>) path.get("pen");
		Points = (ArrayList<P>) path.get("Point");
		AngleList = (ArrayList<Double>) path.get("Angle");
		matrixXSize = (int) path.get("Width");
		matrixYSize = (int) path.get("Height");
	}
	/*
	 * �׸� ���� ����
	 */	
	public static void setArea(VideoCapture cap, Boolean areaChk, SetArea set_area) {
		while (true) {
			cap.read(video);
			areaChk = set_area.set_a(video);
			if(areaChk) {
				sortedPoint = set_area.getSortedPoint();
				break;
			}
		}
	}
	/*
	 * ��Ȯ�� ������ RC car ����
	 */	
	public static void exactAngle(VideoCapture cap, CarDetector car_mark) {
		if (!(AngleList.get(i) <= angle + 0.5 && AngleList.get(i) >= angle - 0.5)) {
			while(true) {
				try {
					Thread.sleep(150);
				}catch(Exception e) {
					e.printStackTrace();
				}
				car_detection(car_mark, cap, 0, i);
	
				if (AngleList.get(i) <= angle + 0.5 && AngleList.get(i) >= angle - 0.5) {
					System.out.println("�Ϸ� car : " + angle);
					break;
				}
				else if(AngleList.get(i) == 180 && -AngleList.get(i) >= angle - 0.5 && angle < 0) {
					System.out.println("�Ϸ� car : " + angle);
					break;
				}
							
				if(AngleList.get(i) == 180.0) {
					if(angle > 0) // ������ �� ŭ
						turn_left();
					else if(angle < 0) // ������ �� ����
						turn_right();
				}
				else if(AngleList.get(i) < angle - 0.5) // ������ �� ŭ
					turn_right();
				else if(AngleList.get(i) > angle + 0.5) // ������ �� ����
					turn_left();
			}
		}
	}
	/*
	 * �뷫 ���� ������ ������ ����
	 */	
	public static void approxAngle(VideoCapture cap, CarDetector car_mark) {
		rcCar.getOrder("Z");
		try {
			Thread.sleep(500);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		rcCar.getOrder("D");
		System.out.println("get : " + AngleList.get(i));
		
		while(true) {
			car_detection(car_mark, cap, 0, i);
			car_x = car_marker[0];
			car_y = car_marker[1];

			if (AngleList.get(i) <= angle + 15 && AngleList.get(i) >= angle - 15) {
				rcCar.getOrder("S");
				System.out.println("car : " + angle);
				break;
			}
		}
	}
	
	public static void turn_left() {
		rcCar.getOrder("A");
		try {
			Thread.sleep(100);
		}catch(Exception e) {
			e.printStackTrace();
		}
		rcCar.getOrder("S");
	}
	
	public static void turn_right() {
		rcCar.getOrder("D");
		try {
			Thread.sleep(130);
		}catch(Exception e) {
			e.printStackTrace();
		}
		rcCar.getOrder("S");
	}
	/*
	 * x��ǥ�� y��ǥ�� ��� ��ǥ ��ǥ�� ���� �ȿ� ���ý� return
	 */	
	public static int tolerance(int i) {
		int a = 0;
		if (Points.get(i).x <= (car_marker[0] + 1) && Points.get(i).x >= (car_marker[0] - 1)) {
			a++;
		}
		if (Points.get(i).y <= (car_marker[1] + 1) && Points.get(i).y >= (car_marker[1] - 1)) {
			a++;
		}
		return a;
	}

	public static double distance(int i) {
		double dis;
		dis = Math.sqrt((Points.get(i).x - car_marker[0])*(Points.get(i).x - car_marker[0]) + (Points.get(i).y - car_marker[1])*(Points.get(i).y - car_marker[1]));
		
		dis = Math.round(dis * 1000) / 1000;
		
		return dis;
	}
	/*
	 * RC car marker �ν� �� �� ��ǥ ���
	 */	
	public static void car_detection(CarDetector car_mark, VideoCapture cap, int vaild, int idx) {
		int[] car_markerTemp = {0, 0};
		int a = 0;
		int[] translateMarker = new int[2];
		int cordLength = (setArea[1] - setArea[0]) / pixelUnit;
		while(true) {
			cropArea(cap);
			car_mark.CarDetect(video);
			car_marker = car_mark.getCarMarker();
			angle = car_mark.getCarAngle();
			if(car_marker[0] == car_markerTemp[0] && car_marker[1] == car_markerTemp[1] && car_markerTemp[0] != 0)
				a++;
			else {
				car_markerTemp = car_marker;
				a = 0;
			}
			if(a >= vaild)
				break;
		}
		if(idx != -1)
			Imgproc.circle(video, new Point(Points.get(idx).x, Points.get(idx).y), 3, new Scalar(255, 0, 0));

		translateMarker[0] = (car_marker[0] - setArea[0]) / cordLength;
		translateMarker[1] = (car_marker[1] - setArea[0]) / cordLength;
		car_marker[0] = translateMarker[0];
		car_marker[1] = translateMarker[1];
	}
	/*
	 * ���� ������ �κи� �߶� �����ֱ�
	 */	
	public static void cropArea(VideoCapture cap) {
		cap.read(video);
		Imgproc.resize(video, video, new Size(700,700));
	    MatOfPoint2f src = new MatOfPoint2f(sortedPoint[0], sortedPoint[1], sortedPoint[2],sortedPoint[3]);
	    int w = video.cols();
	    int h = video.rows();
	    MatOfPoint2f dst = new MatOfPoint2f(new Point(0, 0), new Point(w-1,0), new Point(0,h-1), new Point(w-1,h-1));
	    Mat wrapMat = Imgproc.getPerspectiveTransform(src,dst);
	    Imgproc.warpPerspective(video, video, wrapMat, video.size());
	}

	public static int[] getCarMarker() {
		return car_marker;
	}

	public static void setCarMarker(int[] marker) {
		ImageProcessing.car_marker = marker;
	}
	
	public static double getX_length() { 
		x_length = Math.sqrt(Math.pow((sortedPoint[1].x - sortedPoint[0].x), 2) + Math.pow((sortedPoint[1].y - sortedPoint[0].y), 2)); 
		return x_length; 
	} 

	public static double getY_length() { 
		y_length = Math.sqrt(Math.pow((sortedPoint[2].x - sortedPoint[0].x), 2) + Math.pow((sortedPoint[2].y - sortedPoint[0].y), 2)); 
		return y_length; 
	} 

}