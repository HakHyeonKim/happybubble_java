import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.HashMap;

//import javax.rmi.CORBA.Util;

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
	static int[] count = new int[countlen];// ÁÂÇ¥ Ã£Àº °Í
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
	static double angle = 0;
	public static ArrayList<Integer> penList = new ArrayList<Integer>();
	public static ArrayList<P> Points = new ArrayList<P>();
	public static ArrayList<Double> AngleList = new ArrayList<Double>();
	public static jssctest rcCar;
	public static int[][] marker1 = new int[4][2];
	public static int[][] car_marker = new int[4][2];
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
				set_area.getWrapMat().copyTo(wrapMat);
				break;
			}
		}

		int stack = 0;
		while (stack != 2) {
			show_view();
			int[][] marker_temp = new int[3][2];
			car_detection(car_mark);
			marker_temp[0][0] = car_marker[2][0];
			marker_temp[0][1] = car_marker[2][1];
			for (int i = 0; i < 2; i++) {
				car_detection(car_mark);
				// System.out.println(stack);
				if (marker_temp[0][0] == car_marker[2][0] && marker_temp[0][1] == car_marker[2][1]
						&& marker_temp[0][0] != 0)
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
			cap.read(video);
		    Imgproc.warpPerspective(video, video, wrapMat, video.size());
			car_detection(car_mark);
			//car_point();
			//System.out.println("send order");
			
			//°¢µµ
			rcCar.getOrder("D");
			
			while(j == 0) {
				cap.read(video);
			    Imgproc.warpPerspective(video, video, wrapMat, video.size());
				car_detection(car_mark);
				System.out.println(angle);
				System.out.println(AngleList.get(i));
				//car_point();
				
				if (AngleList.get(i) <= angle + 1 && AngleList.get(i) >= angle - 1) {
					rcCar.getOrder("S");
					j++;
					//System.out.println("D");
				}
			}
				//Ææ, ÀüÁø
			if (penList.get(i) == 0) {
				rcCar.getOrder("w");
				//System.out.println("w-");
				// w
			} else if (penList.get(i) == 1) {
				rcCar.getOrder("W");
				//System.out.println("W");
				// W
			}
			while(j == 1) {
				cap.read(video);
			    Imgproc.warpPerspective(video, video, wrapMat, video.size());
				car_detection(car_mark);
				//car_point();
				
					if (tolerance(i) == 2) {
						rcCar.getOrder("S");
						j++;
					}
				}
				i++;
			
		}

	}

	public static int tolerance(int i) {
		int a = 0;
		if (Points.get(i).x <= (car_marker[2][0] + 5) && Points.get(i).x >= (car_marker[2][0] - 5)) {
			a++;
		}
		if (Points.get(i).y <= (car_marker[2][1] + 5) && Points.get(i).y >= (car_marker[2][1] - 5)) {
			a++;
		}
		return a;
	}

	public static void car_point() {
		xpixel_length = Algoritm.getXpixel_length();
		ypixel_length = Algoritm.getXpixel_length();
		marker[0][0] = car_marker[2][0];
		marker[0][1] = car_marker[2][1];
		//System.out.println(marker[0][0] + "  ,  " + marker[0][1] + " , " + angle);
	}

	public static void sizeSet() {
		x_length = Math.sqrt(Math.pow((marker1[1][0] - marker1[0][0]), 2) + Math.pow((marker1[1][1] - marker1[0][1]), 2));
		y_length = Math.sqrt(Math.pow((marker1[2][0] - marker1[0][0]), 2) + Math.pow((marker1[2][1] - marker1[0][1]), 2));
		marker[0][0] = car_marker[2][0] - marker1[0][0];
		marker[0][1] = car_marker[2][1] - marker1[0][1];
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
		car_mark.setMarker1(marker1);
		car_mark.CarDetect(video);
		car_marker = car_mark.getMarker();
		show_view();
		
		angle = getAngle(car_marker);
		int[] sendCarMarker = new int[2];
		sendCarMarker[0] = car_marker[2][0];
		sendCarMarker[1] = car_marker[2][1];
		System.out.println("Center : " + sendCarMarker[0] + " , " + sendCarMarker[1]);
		// ÁÂ A ¿ìD ÈÄÁøX ÀüÁø Ææo W Ææx w ¸ØÃã S
		/*
		 * System.out.println("Haed : " + marker1[3][0] + " , " + marker1[3][1]);
		 * System.out.println("Tail : " + marker1[4][0] + " , " + marker1[4][1]);
		 * System.out.println("Center : " + marker1[5][0] + " , " + marker1[5][1]);
		 * System.out.println("Angle : " + angle);
		 */
	}

	public static double getAngle(int marker1[][]) {
		int dx = marker1[1][0] - marker1[0][0];
		int dy = marker1[1][1] - marker1[0][1];

		double rad = Math.atan2(dx, dy);
		double degree = (rad * 180) / Math.PI;

		return degree;
	}

	public static int[][] getMarker() {
		return marker1;
	}

	public static void setMarker(int[][] marker1) {
		ImageProcessing.marker1 = marker1;
	}

	public static int[][] getCarMarker() {
		return car_marker;
	}

	public static void setCarMarker(int[][] marker1) {
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