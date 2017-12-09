import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CarDetector {
	public final static int[] markerMaxSize = {1000, 2000, 3000};
	public final static int[] markerMinSize = {100, 200, 300};
	public final static int[] panelSize = {700, 700};
	public final static int settingWidth = 100;
	public final static int settingHeight = 100;
	public final static int sensitivity = 50;
	public final static Scalar[] colorRangeMin = {
			new Scalar(50 - sensitivity, 70, 70, 0)
			, new Scalar(50 - sensitivity, 70, 70, 0)
			, new Scalar(50 - sensitivity, 70, 70, 0)};
	public final static Scalar[] colorRangeMax = {
			new Scalar(50 + sensitivity, 255, 255, 0)
			, new Scalar(50 + sensitivity, 255, 255, 0)
			, new Scalar(50 + sensitivity, 255, 255, 0)};
	// define 영역
	
	public static VideoFrame vertexFrame = new VideoFrame();
	public static int[][] markerTop = new int[2][2];
	public static int[] markerForward = new int[2];
	public static ArrayList<int[]> markerBottom = new ArrayList<int[]>();
	public static int[] carMarker = new int[2];
	public static double[] topMarker = new double[2];
	public static double carAngle;
	public static List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	public static MatOfPoint2f approx = new MatOfPoint2f();
	public static Mat blackVideo = new Mat();
	public static Mat video;

	public void CarDetect(Mat camvideo) {
		video = camvideo;
		vertexFrame.setVisible(true);
		Imgproc.resize(video, video, new Size(panelSize[0],panelSize[1]));
		Imgproc.cvtColor(video, blackVideo, Imgproc.COLOR_BGR2HSV);
		
		for(int j = 0;j < 3;j++) {
			preProcessingFindMarker(j);
			
			int a = 0; // for문 최소화
			int b = 0;
			for (int i = 0; i < contours.size(); i++) {
				MatOfPoint2f approxTemp = new MatOfPoint2f();
				contours.get(i).convertTo(approxTemp, CvType.CV_32FC2);
				double areaSize = approxContour(approxTemp);

				if (areaSize > markerMinSize[j] && areaSize < markerMaxSize[j]) {
					findMarker(areaSize, j, i, a);
					a++;
				}
				/* marker 크기 조정 후
				if (a == 2)	break;
				*/
			}
		}
		calculateCarPoint();
		carAngle = calculateCarAngle();
		
		if(!video.empty()) { vertexFrame.render(video); }		 
	}
	
	public static void preProcessingFindMarker(int position) {
		Mat tempVideo = new Mat();
		Mat resultVideo = new Mat();
		Mat mask = new Mat();
		
		Core.inRange(blackVideo, colorRangeMin[position], colorRangeMax[position], mask);
		video.copyTo(resultVideo, mask);
		
		// 그레이스케일 이미지로 변환
		Imgproc.cvtColor(resultVideo, tempVideo, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(tempVideo, tempVideo, 200, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);

		// contour를 찾는다.
		Imgproc.findContours(tempVideo, contours, tempVideo, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
	}
	
	public static double approxContour(MatOfPoint2f approxTemp) {
		// contour를 근사화한다.
		double epsilon = Imgproc.arcLength(approxTemp, true) * 0.02;
		Imgproc.approxPolyDP(approxTemp, approx, epsilon, true);
		Mat checkArea = new Mat();
		approx.convertTo(checkArea, CvType.CV_32S);

		return Math.abs(Imgproc.contourArea(checkArea));
	}
	
	public static void findMarker(double areaSize, int position, int numberOfContour, int numberOfMarker) {
		Imgproc.drawContours(video, contours, numberOfContour, new Scalar(0, 0, 255));
		int size = approx.rows();
		int[] getPoint = new int[2];
		getPoint = getMarkerPoint(size);
		Imgproc.putText(video, "" + areaSize, new Point(getPoint[0], getPoint[1]), 1, 2, new Scalar(255,255,255));
		Imgproc.circle(video, new Point(getPoint[0], getPoint[1]), 10, new Scalar(0, 255, 0));
		if(position == 0 && numberOfMarker < 2) {
			markerTop[numberOfMarker][0] = getPoint[0];
			markerTop[numberOfMarker][1] = getPoint[1];
		}
		else if(position == 1 && numberOfMarker < 2) {
			markerBottom.add(getPoint);
		}
		else if(position == 2 && numberOfMarker < 1) {
			markerForward[0] = getPoint[0];
			markerForward[1] = getPoint[1];
		}
	}

	public static int[] getMarkerPoint(int size) {
		int[] point = new int[2];
		int x = 0, y = 0;

		for (int i = 0; i < size; i++) {
			x += approx.get(i, 0)[0];
			y += approx.get(i, 0)[1];
		}
		point[0] = x / size;
		point[1] = y / size;

		return point;
	}
	
	public static void calculateCarPoint() {
		int size[] = new int[2];
		size[0] = video.cols();
		size[1] = video.rows();
		
		switch(markerBottom.size()) {
			case 1 :	// marker가 한 개만 보일 경우
				double[] distance = new double[2];
				double[] move = new double[2];
				int selectMarkerNum;
				
				distance[0] = getDistance(markerTop[0], markerBottom.get(0));
				distance[1] = getDistance(markerTop[1], markerBottom.get(0));
				if(distance[0] <= distance[1]) selectMarkerNum = 0;
				else selectMarkerNum = 1;
				
				for(int i = 0;i < 2;i++) {
					topMarker[i] = (markerTop[0][i] + markerTop[1][i]) / 2;
					move[i] = markerBottom.get(0)[i] - markerTop[selectMarkerNum][i];
					carMarker[i] = (int) (topMarker[i] + move[i]);
				}
				break;
			case 2 : 	// marker가 두 개 모두 보일 경우
				carMarker[0] = (int) ((markerBottom.get(0)[0] + markerBottom.get(1)[0]) / 2);
				carMarker[1] = (int) ((markerBottom.get(0)[1] + markerBottom.get(1)[1]) / 2); 
				break;
		}
		
		Imgproc.circle(video, new Point(carMarker[0], carMarker[1]), 2, new Scalar(0, 0, 255));
		carMarker[0] = carMarker[0] / (size[0] / settingWidth);
		carMarker[1] = carMarker[1] / (size[1] / settingHeight);
	}
	
	static double getDistance(int[] top, int[] bottom) {
        return Math.sqrt((top[0] - bottom[0]) * (top[0] - bottom[0]) + (top[1] - bottom[1]) * (top[1] - bottom[1]));
    }
	
	public static double calculateCarAngle() {
		double dx = topMarker[0] - markerForward[0];
		double dy = topMarker[1] - markerForward[1];

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

	public static int[] getCarMarker() {
		return carMarker;
	}

	public static double getCarAngle() {
		return carAngle;
	}	
}