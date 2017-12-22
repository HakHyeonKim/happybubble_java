import java.util.ArrayList;
import java.util.HashMap;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ImageProcessing {
	static HashMap<String, Object> path = new HashMap<String, Object>();
	static Mat video = new Mat();
	static Mat wrap_mat = new Mat();
	static int matrix_xsize = 200;
	static int matrix_ysize = 100;
	static double angle = 0; // 차각
	static double x_length, y_length; // 영역 설정 x,y 길이
	public static Point[] sorted_point = new Point[4]; // 영역 설정 마커 좌표 정보
	public static ArrayList<Integer> pen_list = new ArrayList<Integer>(); // 0 : 펜 사용x 1 : 펜사용 o
	public static ArrayList<P> Points = new ArrayList<P>(); // 경로 좌표
	public static ArrayList<Double> angle_list = new ArrayList<Double>(); // 이동 차각 정보
	public static ArduinoControl rc_car = new ArduinoControl(); 
	public static int[] car_marker = new int[2]; // rc카 좌표 (펜의 좌표)
	final static int car_size = 100;
	final static int video_size = 700;
	final static int[] set_area = {car_size, video_size - car_size};
	final static int pixel_unit = 100;
	public static int car_x, car_y;
	static int i = 0; // 경로 index
	
	static {
		String opencvPath = "C:\\opencv\\build\\java\\x64\\";
		System.load(opencvPath + Core.NATIVE_LIBRARY_NAME + ".dll");
	}
	
	public static void main(String[] args) throws Throwable {
		SetArea set_area = new SetArea();
		CarDetector car_mark = new CarDetector();
		Boolean area_chk = false;
		
		VideoCapture cap = new VideoCapture(1);
		if (!cap.isOpened())
			System.exit(-1);
		
		cap.read(video);
		setArea(cap, area_chk, set_area);
		car_detection(car_mark, cap, 5, -1);		
		getPathInfo();		
		rc_car.jssc();
		
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
	 * 펜을 내리고 가면 Z 펜을 올리고가면 C
	 */	
	public static void go() {
		if (pen_list.get(i) == 0) {
			rc_car.getOrder("C");
			try {
				Thread.sleep(500);
			}catch(Exception e) {
				e.printStackTrace();
			}
			rc_car.getOrder("X");				
		} else if (pen_list.get(i) == 1) {
			rc_car.getOrder("Z");
			try {
				Thread.sleep(500);
			}catch(Exception e) {
				e.printStackTrace();
			}
			rc_car.getOrder("X");
		}
	}
	/*
	 * 목표 좌표와 현재 좌표와의 gap을 구해 다음 목적지 수정
	 */	
	public static void feedback(VideoCapture cap, CarDetector car_mark, double min, int num, int valid) {
		while(true) {
			car_detection(car_mark, cap, 0, i);
			int gap_x, gap_y;
			double check_out;
			if (tolerance(i) == 2) {
				rc_car.getOrder("S");
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
				rc_car.getOrder("S");
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
	 * 경로 정보를 받아 온다.
	 */	
	public static void getPathInfo() {
		Algoritm.Algo(car_marker);
		path = Algoritm.getHash();
		pen_list = (ArrayList<Integer>) path.get("pen");
		Points = (ArrayList<P>) path.get("Point");
		angle_list = (ArrayList<Double>) path.get("Angle");
		matrix_xsize = (int) path.get("Width");
		matrix_ysize = (int) path.get("Height");
	}
	/*
	 * 그릴 영영 설정
	 */	
	public static void setArea(VideoCapture cap, Boolean area_chk, SetArea set_area) {
		while (true) {
			cap.read(video);
			area_chk = set_area.set_a(video);
			if(area_chk) {
				sorted_point = set_area.getSortedPoint();
				break;
			}
		}
	}
	/*
	 * 정확한 각도로 RC car 설정
	 */	
	public static void exactAngle(VideoCapture cap, CarDetector car_mark) {
		if (!(angle_list.get(i) <= angle + 0.5 && angle_list.get(i) >= angle - 0.5)) {
			while(true) {
				try {
					Thread.sleep(150);
				}catch(Exception e) {
					e.printStackTrace();
				}
				car_detection(car_mark, cap, 0, i);
	
				if (angle_list.get(i) <= angle + 0.5 && angle_list.get(i) >= angle - 0.5) {
					System.out.println("완료 car : " + angle);
					break;
				}
				else if(angle_list.get(i) == 180 && -angle_list.get(i) >= angle - 0.5 && angle < 0) {
					System.out.println("완료 car : " + angle);
					break;
				}
							
				if(angle_list.get(i) == 180.0) {
					if(angle > 0) // 차각이 더 큼
						turn_left();
					else if(angle < 0) // 차각이 더 작음
						turn_right();
				}
				else if(angle_list.get(i) < angle - 0.5) // 차각이 더 큼
					turn_right();
				else if(angle_list.get(i) > angle + 0.5) // 차각이 더 작음
					turn_left();
			}
		}
	}
	/*
	 * 대략 적인 각도로 빠르게 설정
	 */	
	public static void approxAngle(VideoCapture cap, CarDetector car_mark) {
		rc_car.getOrder("Z");
		try {
			Thread.sleep(500);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		rc_car.getOrder("D");
		System.out.println("get : " + angle_list.get(i));
		
		while(true) {
			car_detection(car_mark, cap, 0, i);
			car_x = car_marker[0];
			car_y = car_marker[1];

			if (angle_list.get(i) <= angle + 15 && angle_list.get(i) >= angle - 15) {
				rc_car.getOrder("S");
				System.out.println("car : " + angle);
				break;
			}
		}
	}
	
	public static void turn_left() {
		rc_car.getOrder("A");
		try {
			Thread.sleep(100);
		}catch(Exception e) {
			e.printStackTrace();
		}
		rc_car.getOrder("S");
	}
	
	public static void turn_right() {
		rc_car.getOrder("D");
		try {
			Thread.sleep(130);
		}catch(Exception e) {
			e.printStackTrace();
		}
		rc_car.getOrder("S");
	}
	/*
	 * x좌표와 y좌표가 모두 목표 좌표와 오차 안에 들어올시 return
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
	 * RC car marker 인식 및 펜 좌표 찍기
	 */	
	public static void car_detection(CarDetector car_mark, VideoCapture cap, int vaild, int idx) {
		int[] car_marker_temp = {0, 0};
		int a = 0;
		int[] translate_marker = new int[2];
		int cord_length = (set_area[1] - set_area[0]) / pixel_unit;
		while(true) {
			cropArea(cap);
			car_mark.CarDetect(video);
			car_marker = car_mark.getCarMarker();
			angle = car_mark.getCarAngle();
			if(car_marker[0] == car_marker_temp[0] && car_marker[1] == car_marker_temp[1] && car_marker_temp[0] != 0)
				a++;
			else {
				car_marker_temp = car_marker;
				a = 0;
			}
			if(a >= vaild)
				break;
		}
		if(idx != -1)
			Imgproc.circle(video, new Point(Points.get(idx).x, Points.get(idx).y), 3, new Scalar(255, 0, 0));

		translate_marker[0] = (car_marker[0] - set_area[0]) / cord_length;
		translate_marker[1] = (car_marker[1] - set_area[0]) / cord_length;
		car_marker[0] = translate_marker[0];
		car_marker[1] = translate_marker[1];
	}
	/*
	 * 영역 설정된 부분만 잘라서 보여주기
	 */	
	public static void cropArea(VideoCapture cap) {
		cap.read(video);
		Imgproc.resize(video, video, new Size(700,700));
	    MatOfPoint2f src = new MatOfPoint2f(sorted_point[0], sorted_point[1], sorted_point[2],sorted_point[3]);
	    int w = video.cols();
	    int h = video.rows();
	    MatOfPoint2f dst = new MatOfPoint2f(new Point(0, 0), new Point(w-1,0), new Point(0,h-1), new Point(w-1,h-1));
	    Mat wrap_mat = Imgproc.getPerspectiveTransform(src,dst);
	    Imgproc.warpPerspective(video, video, wrap_mat, video.size());
	}

	public static int[] getCarMarker() {
		return car_marker;
	}

	public static void setCarMarker(int[] marker) {
		ImageProcessing.car_marker = marker;
	}
	
	public static double getx_length() { 
		x_length = Math.sqrt(Math.pow((sorted_point[1].x - sorted_point[0].x), 2) + Math.pow((sorted_point[1].y - sorted_point[0].y), 2)); 
		return x_length; 
	} 

	public static double gety_length() { 
		y_length = Math.sqrt(Math.pow((sorted_point[2].x - sorted_point[0].x), 2) + Math.pow((sorted_point[2].y - sorted_point[0].y), 2)); 
		return y_length; 
	} 

}