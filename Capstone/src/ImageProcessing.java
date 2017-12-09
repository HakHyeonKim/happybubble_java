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
	static VideoFrame colorFrame = new VideoFrame();
	static VideoFrame byteFrame = new VideoFrame();

	static {
		String opencvPath = "C:\\opencv\\build\\java\\x64\\";
		System.load(opencvPath + Core.NATIVE_LIBRARY_NAME + ".dll");
	}
	
	public static void main(String[] args) throws Throwable {
		VideoCapture cap = new VideoCapture(1);

		if (!cap.isOpened())
			System.exit(-1);

		colorFrame.setVisible(true);
		byteFrame.setVisible(true);

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
			int diffX = 0;
			int diffY = 0;
			cap.read(video);
			areaChk = set_area.set_a(video);
			marker1 = set_area.getMarker();
			if(areaChk) {
				sortedPoint = set_area.getSortedPoint();
				break;
			}
		}

		int stack = 0;
		while (stack != 2) {
			cropArea(cap);
			int[] marker_temp = new int[2];
			car_detection(car_mark);
			marker_temp[0] = car_marker[0];
			marker_temp[1] = car_marker[1];
			for (int i = 0; i < 2; i++) {
				car_detection(car_mark);
				// System.out.println(stack);
				if (marker_temp[0] == car_marker[0] && marker_temp[1] == car_marker[1] && marker_temp[0] != 0)
					stack++;
				else {
					stack = 0;
					break;
				}
			}
		}
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
			cropArea(cap);
			car_detection(car_mark);
			
			//각도
			rcCar.getOrder("D");
			System.out.println("get : " + AngleList.get(i));
			
			while(j == 0) {
				cropArea(cap);
				car_detection(car_mark);
				//car_point();

				if (AngleList.get(i) == angle) {			
//				if (AngleList.get(i) <= angle + 15 && AngleList.get(i) >= angle - 15) {
					rcCar.getOrder("S");
					System.out.println("car : " + angle);
					j++;
					//System.out.println("D");
				}
			}
			
			while(true) {
				cropArea(cap);
				car_detection(car_mark);
				System.out.println("get : " + AngleList.get(i));
				System.out.println("car : " + angle);
				
				if (AngleList.get(i) <= angle + 0.5 && AngleList.get(i) >= angle - 0.5)
					break;
				
				try {
					Thread.sleep(30);
				}catch(Exception e) {
					e.printStackTrace();
				}
							
				if(AngleList.get(i) == 180.0) {
					if(angle > 0) // 차각이 더 큼
						turn_left();
					else if(angle < 0) // 차각이 더 작음
						turn_right();
				}
				else if(AngleList.get(i) < angle -0.5) // 차각이 더 큼
					turn_right();
				else if(AngleList.get(i) > angle +0.5) // 차각이 더 작음
					turn_left();
				
			}
			
			
			while(true) {
				
				//펜, 전진
				if (penList.get(i) == 0) {
					rcCar.getOrder("w");
					rcCar.getOrder("S");
					//System.out.println("w-");
					// w
				} else if (penList.get(i) == 1) {
					rcCar.getOrder("W");
					rcCar.getOrder("S");
					//System.out.println("W");
					// W
				}
					cropArea(cap);
					car_detection(car_mark);
					//car_point();
					
					try {
						Thread.sleep(100);
					}catch(Exception e) {
						e.printStackTrace();
					}
					
					
					if (tolerance(i) == 2) {
						break;
					}
				}
				
			i++;
		}
	}

	public static void turn_left() {
		rcCar.getOrder("A");
		
		rcCar.getOrder("S");
	}
	
	public static void turn_right() {
		rcCar.getOrder("D");
		
		rcCar.getOrder("S");
	}
	public static int tolerance(int i) {
		int a = 0;
		if (Points.get(i).x <= (marker[0][0] + 1) && Points.get(i).x >= (marker[0][0] - 1)) {
			System.out.println(Points.get(i).x + ", " + Points.get(i).y);
			System.out.println(marker[0][0] + ", " + marker[0][1]);
			a++;
		}
		if (Points.get(i).y <= (marker[0][1] + 1) && Points.get(i).y >= (marker[0][1] - 1)) {
			a++;
		}
		return a;
	}

	public static void car_point() {
		xpixel_length = Algoritm.getXpixel_length();
		ypixel_length = Algoritm.getXpixel_length();
		marker[0][0] = car_marker[0];
		marker[0][1] = car_marker[1];
		//System.out.println(marker[0][0] + "  ,  " + marker[0][1] + " , " + angle);
	}

	public static void sizeSet() {
		x_length = Math.sqrt(Math.pow((marker1[1][0] - marker1[0][0]), 2) + Math.pow((marker1[1][1] - marker1[0][1]), 2));
		y_length = Math.sqrt(Math.pow((marker1[2][0] - marker1[0][0]), 2) + Math.pow((marker1[2][1] - marker1[0][1]), 2));
		marker[0][0] = car_marker[0] - marker1[0][0];
		marker[0][1] = car_marker[1] - marker1[0][1];
	}

	public static void show_view() {
		if (!video.empty()) {
//			Imgproc.resize(video, video, new Size(500,500));
			colorFrame.render(video);
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

	public static void car_detection(CarDetector car_mark) {
//		car_mark.setMarker1(marker1);
		car_mark.CarDetect(video);
		car_marker = car_mark.getCarMarker();
		angle = car_mark.getCarAngle();
		show_view();
		
		int[] sendCarMarker = new int[2];
		sendCarMarker[0] = car_marker[0];
		sendCarMarker[1] = car_marker[1];
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