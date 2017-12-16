import java.util.ArrayList;
import java.util.HashMap;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ImageProcessing {
	static HashMap<String, Object> test = new HashMap<String, Object>();
	static Mat video, blackVideo, binaryVideo, closedVideo, outputVideo, lableVideo, morphVideo;
	static Mat stat, centroid, wrapMat;
	static int w = 0;
	static int h = 0;
	static int k = 0;
	static int fix = 0;
	static int left_top = 0;
	static int right_top = 0;
	static int left_bottom = 0;
	static final int countlen = 5;
	static int size_set = 0;
	static int[] count = new int[countlen];// 좌표 찾은 것
	static int[] count1 = new int[countlen];
	static int[] discord = new int[5];
	static int lablesNum = 0;
	static int matrixXSize = 200;
	static int matrixYSize = 100;
	public static double xpixel_length, ypixel_length;
	public static double x_length;
	public static double y_length;
	static double[] data = null;
	static double[] dataVideo = null;
	public static int[][] marker = new int[6][2];
	public static Point[] sortedPoint = new Point[4];
	static double angle = 0;
	public static ArrayList<Integer> penList = new ArrayList<Integer>();
	public static ArrayList<P> Points = new ArrayList<P>();
	public static ArrayList<Double> AngleList = new ArrayList<Double>();
	public static jssctest rcCar;
	public static int[][] marker1 = new int[4][2];
	public static int[] car_marker = new int[2];
//	static VideoFrame colorFrame = new VideoFrame();
//	static VideoFrame byteFrame = new VideoFrame();
	final static int carSize = 100;
	final static int videoSize = 700;
	final static int[] setArea = {carSize, videoSize - carSize};
	final static int pixelUnit = 100;
	public static int car_x, car_y;
	
	static {
		String opencvPath = "C:\\opencv\\build\\java\\x64\\";
		System.load(opencvPath + Core.NATIVE_LIBRARY_NAME + ".dll");
	}
	
	public static void main(String[] args) throws Throwable {
		VideoCapture cap = new VideoCapture(1);

		if (!cap.isOpened())
			System.exit(-1);

//		colorFrame.setVisible(true);
//		byteFrame.setVisible(true);

		video = new Mat();
		blackVideo = new Mat();
		binaryVideo = new Mat();
		closedVideo = new Mat();
		outputVideo = new Mat();
		lableVideo = new Mat();
		stat = new Mat();
		centroid = new Mat();
		wrapMat = new Mat();
		cap.read(video);

		SetArea set_area = new SetArea();
		CarDetector car_mark = new CarDetector();
		Boolean areaChk = false;
		while (true) {
			cap.read(video);
			areaChk = set_area.set_a(video);
			marker1 = set_area.getMarker();
			if(areaChk) {
				sortedPoint = set_area.getSortedPoint();
				break;
			}
		}

		car_detection(car_mark, cap, 5, -1);

		sizeSet();
		Algoritm.Algo(car_marker);
		test = Algoritm.getHash();
		penList = (ArrayList<Integer>) test.get("pen");
		Points = (ArrayList<P>) test.get("Point");
		AngleList = (ArrayList<Double>) test.get("Angle");
		matrixXSize = (int) test.get("Width");
		matrixYSize = (int) test.get("Height");
		/*
		System.out.println(penList);
		System.out.println(Points);
		System.out.println(AngleList);
		System.out.println(matrixXSize + "/" + matrixYSize);
		*/
		
		rcCar = new jssctest();
		rcCar.jssc();
		
		int i = 0;
		int j = 0;
		while (true) {
			rcCar.getOrder("Z");
			try {
				Thread.sleep(500);
			}catch(Exception e) {
				e.printStackTrace();
			}
			//각도
			rcCar.getOrder("D");
			System.out.println("get : " + AngleList.get(i));
			
			while(j == 0) {
				car_detection(car_mark, cap, 0, i);
				car_x = car_marker[0];
				car_y = car_marker[1];
				//car_point();

//				if (AngleList.get(i) == angle) {			
				if (AngleList.get(i) <= angle + 15 && AngleList.get(i) >= angle - 15) {
					rcCar.getOrder("S");
					System.out.println("car : " + angle);
					j++;
					//System.out.println("D");
				}
			}

			if (!(AngleList.get(i) <= angle + 0.5 && AngleList.get(i) >= angle - 0.5)) {
				while(true) {
					try {
						Thread.sleep(150);
					}catch(Exception e) {
						e.printStackTrace();
					}
					car_detection(car_mark, cap, 0, i);
	//				System.out.println("get : " + AngleList.get(i));
	//				System.out.println("car : " + angle);
	
					if (AngleList.get(i) <= angle + 0.5 && AngleList.get(i) >= angle - 0.5) {
						System.out.println("완료 car : " + angle);
						break;
					}
					else if(AngleList.get(i) == 180 && -AngleList.get(i) >= angle - 0.5 && angle < 0) {
						System.out.println("완료 car : " + angle);
						break;
					}
								
					if(AngleList.get(i) == 180.0) {
						if(angle > 0) // 차각이 더 큼
							turn_left();
						else if(angle < 0) // 차각이 더 작음
							turn_right();
					}
					else if(AngleList.get(i) < angle - 0.5) // 차각이 더 큼
						turn_right();
					else if(AngleList.get(i) > angle + 0.5) // 차각이 더 작음
						turn_left();
				}
			}
			
			try {
				Thread.sleep(1500);
			}catch(Exception e) {
				e.printStackTrace();
			}

			car_detection(car_mark, cap, 0, i);
			if (penList.get(i) == 0) {
				rcCar.getOrder("C");
				try {
					Thread.sleep(500);
				}catch(Exception e) {
					e.printStackTrace();
				}
				rcCar.getOrder("X");				
				//System.out.println("w-");
				// w
			} else if (penList.get(i) == 1) {
				rcCar.getOrder("Z");
				try {
					Thread.sleep(500);
				}catch(Exception e) {
					e.printStackTrace();
				}
				rcCar.getOrder("X");
				//System.out.println("W");
				// W
			}
			
			double check_out, min = 10000;
			int gap_x, gap_y;
			int num = 0, vaild = 3;
			
			while(true) {
				car_detection(car_mark, cap, 0, i);
				if (tolerance(i) == 2) {
					rcCar.getOrder("S");
					i++;
					break;
				}
				
				check_out = distance(i);
				/*
				if(min == 10000 && check_out < 10) {
					vaild = 2;
				}
				*/
				if(check_out > min) {
					num++;
				}
				else {
					min = check_out;
					num = 0;
				}
				System.out.println("min : " + min + " , num : "+num);
				
				if(num == vaild) {
					rcCar.getOrder("S");
					System.out.println("out");
					gap_x = (int) Points.get(i).x - car_marker[0];
					gap_y = (int) Points.get(i).y - car_marker[1];
					i++;
//					System.out.println("gap : " + gap_x + ", " + gap_y);
//					System.out.println("pre : " + Points.get(i).x + ", " + Points.get(i).y);
					P change_p = new P(Points.get(i).x-gap_x, Points.get(i).y-gap_y);
					Points.set(i, change_p);
//					System.out.println("aft : " + Points.get(i).x + ", " + Points.get(i).y);
//					AngleList.set(i, change_angle(car_marker[0], car_marker[1], Points.get(i).x, Points.get(i).y));
//					System.out.println(change_angle(car_marker[0], car_marker[1], Points.get(i).x, Points.get(i).y));
//					System.out.println(AngleList.get(i));
					break;
				}
				
	/*			
				if(set_correct(i) == 3) {
					rcCar.getOrder("S");
					System.out.println("out");
					//i++;
					AngleList.set(i, change_angle(car_marker[0], car_marker[1], Points.get(i).x, Points.get(i).y));
					//System.out.println(""+car_marker[0]+" / "+ car_marker[1]+" / "+ Points.get(i).x +" / "+ Points.get(i).y);
					//System.out.println(change_angle(car_marker[0], car_marker[1], Points.get(i).x, Points.get(i).y));
					break;
				}
				*/
				//펜, 전진
				//car_point();
/*					
					try {
						Thread.sleep(100);
					}catch(Exception e) {
						e.printStackTrace();
					}
*/
			}
			//i++;
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
	public static int tolerance(int i) {
		int a = 0;
		//int[] translateMarker = new int[2];
		//int cordLength = (setArea[0] - setArea[1]) / pixelUnit;
		//translateMarker[0] = (car_marker[0] - setArea[0]) / cordLength;
		//translateMarker[1] = (car_marker[1] - setArea[0]) / cordLength;
		//System.out.println("get : " + Points.get(i).x + ", " + Points.get(i).y);
		//System.out.println("car : " + car_marker[0] + ", " + car_marker[1]);
		//System.out.println("tca : " + translateMarker[0] + ", " + translateMarker[1]);
//		if (Points.get(i).x <= (car_marker[0] + 2) && Points.get(i).x >= (car_marker[0] - 2)) {
		if (Points.get(i).x <= (car_marker[0] + 1) && Points.get(i).x >= (car_marker[0] - 1)) {
			a++;
		}
		if (Points.get(i).y <= (car_marker[1] + 1) && Points.get(i).y >= (car_marker[1] - 1)) {
			a++;
		}
		return a;
	}
/*	
	public static int set_correct(int i) {
		int a = 0;
		//목적지보다  x,y가 작은 위치에서 이동하는 경우
		if(car_x < Points.get(i).x && car_y < Points.get(i).y) {
			if(car_marker[0] > Points.get(i).x + 2 || car_marker[1] > Points.get(i).y + 2) {
				a = 3;
			}
		}
		//목적지 보다 x는 작은 위치, y는  큰 위치에서 이동
		if(car_x < Points.get(i).x && car_y > Points.get(i).y) {
			if(car_marker[0] > Points.get(i).x + 2 || car_marker[1] < Points.get(i).y - 2) {
				a = 3;
			}
		}
		//목적지보다 x는 큰위치, y는 작은 위치에서 이동
		if(car_x > Points.get(i).x && car_y < Points.get(i).y) {
			if(car_marker[0] < Points.get(i).x - 2 || car_marker[1] > Points.get(i).y + 2) {
				a = 3;
			}
		}
		
		//목적지보다 x,y가 큰 위치에서 이동
		if(car_x > Points.get(i).x && car_y > Points.get(i).y) {
			if(car_marker[0] < Points.get(i).x - 2|| car_marker[1] < Points.get(i).y - 2) {
				a = 3;
			}
		}
		return a;
	}
*/	
	public static double distance(int i) {
		double dis;
		dis = Math.sqrt((Points.get(i).x - car_marker[0])*(Points.get(i).x - car_marker[0]) + (Points.get(i).y - car_marker[1])*(Points.get(i).y - car_marker[1]));
		
		dis = Math.round(dis * 1000) / 1000;
		
		return dis;
	}
	
	public static double change_angle(int x1, int y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		
		System.out.println("dx : "+ dx);
		System.out.println("dy : "+ dy);
/*		
		double rad = Math.atan2(dx, dy);
		double degree = (rad * 180) / Math.PI;

		degree = Math.round(degree * 1000) / 1000.0;
*/
		double rad = Math.atan2(dx, dy);
		double degree = ((rad * 180) / Math.PI);
		if(degree >= 0) {
			degree = 180 - degree;
			degree = -degree;
		} else {
			degree = degree + 180;
		}
		
		return degree;
	}

	public static void sizeSet() {
		x_length = Math.sqrt(Math.pow((marker1[1][0] - marker1[0][0]), 2) + Math.pow((marker1[1][1] - marker1[0][1]), 2));
		y_length = Math.sqrt(Math.pow((marker1[2][0] - marker1[0][0]), 2) + Math.pow((marker1[2][1] - marker1[0][1]), 2));
		marker[0][0] = car_marker[0] - marker1[0][0];
		marker[0][1] = car_marker[1] - marker1[0][1];
	}

	public static void show_view() {
		Imgproc.rectangle(video, new Point(setArea[0], setArea[0]), new Point(setArea[1], setArea[1]), new Scalar(0,0,255));
		if (!video.empty()) {
//			Imgproc.resize(video, video, new Size(500,500));
//			colorFrame.render(video);
		} else
			System.out.println("no frame");
	}

	public static void set_A(SetArea set_area) {
		int diff = 0;
		set_area.set_a(video);
		marker1 = set_area.getMarker();

		diff = (int) (marker1[0][0] - marker1[2][0]);
		if (diff <= 20 && diff >= -20 && diff != 0) {
			size_set = 1;
			System.out.println("Top_left : " + marker1[0][0] + " , " + marker1[0][1]);
			System.out.println("Top_right : " + marker1[1][0] + " , " + marker1[1][1]);
			System.out.println("Bottom_left : " + marker1[2][0] + " , " + marker1[2][1]);
		}
	}

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
//			show_view();
		}
		if(idx != -1)
			Imgproc.circle(video, new Point(Points.get(idx).x, Points.get(idx).y), 3, new Scalar(255, 0, 0));


//		System.out.println("car : " + car_marker[0] + ", " + car_marker[1]);
		translateMarker[0] = (car_marker[0] - setArea[0]) / cordLength;
		translateMarker[1] = (car_marker[1] - setArea[0]) / cordLength;
		car_marker[0] = translateMarker[0];
		car_marker[1] = translateMarker[1];
//		System.out.println("trc : " + car_marker[0] + ", " + car_marker[1]);

//		System.out.println("Center : " + sendCarMarker[0] + " , " + sendCarMarker[1]);
		// 좌 A 우D 후진X 전진 펜o W 펜x w 멈춤 S
		/*
		 * System.out.println("Haed : " + marker1[3][0] + " , " + marker1[3][1]);
		 * System.out.println("Tail : " + marker1[4][0] + " , " + marker1[4][1]);
		 * System.out.println("Center : " + marker1[5][0] + " , " + marker1[5][1]);
		 * System.out.println("Angle : " + angle);
		 */
	}
	
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

	public static int[][] getMarker() {
		return marker1;
	}

	public static void setMarker(int[][] marker1) {
		ImageProcessing.marker1 = marker1;
	}

	public static int[] getCarMarker() {
		return car_marker;
	}

	public static void setCarMarker(int[] marker1) {
		ImageProcessing.car_marker = marker1;
	}

	public static double getX_length() {
		return x_length;
	}

	public static void setX_length(double x_length) {
		ImageProcessing.x_length = x_length;
	}

	public static double getY_length() {
		return y_length;
	}

	public static void setY_length(double y_length) {
		ImageProcessing.y_length = y_length;
	}
}