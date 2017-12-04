import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class CarDetector {
	static VideoFrame vertexFrame = new VideoFrame();

	static MatOfPoint2f approx = new MatOfPoint2f();
	public static int marker[][] = new int[3][2];
	public static int marker1[][] = new int[4][2];
	public static int carMarker[] = new int[2];

	public void setMarker1(int[][] marker1) {
		CarDetector.marker1 = marker1;
	}
	
	public int[][] getMarker() {
		return marker;
	}

	public static void setMarker(int[][] marker) {
		CarDetector.marker = marker;
	}

	public void CarDetect(Mat camvideo) {
		// VideoCapture cap = new VideoCapture(0);
		// if(!cap.isOpened()) System.exit(-1);

		vertexFrame.setVisible(true);

		Mat video = camvideo;
		Mat tempImg = new Mat();
		Mat blackVideo = new Mat();
		Mat resultVideo = new Mat();
		Mat mask = new Mat();
		MatOfPoint2f approxTemp = new MatOfPoint2f();
		int sensitivity = 40;
		Imgproc.cvtColor(video, blackVideo, Imgproc.COLOR_BGR2HSV);
		Core.inRange(blackVideo, new Scalar(60 - sensitivity, 100, 50, 0), new Scalar(60 + sensitivity, 255, 255, 0), mask);
		video.copyTo(resultVideo, mask);

		// 그레이스케일 이미지로 변환
		Imgproc.cvtColor(resultVideo, tempImg, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(tempImg, tempImg, 200, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);

		// contour를 찾는다.
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(tempImg, contours, tempImg, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		// contour를 근사화한다.
		for (int i = 0; i < contours.size(); i++) {
			contours.get(i).convertTo(approxTemp, CvType.CV_32FC2);
			double epsilon = Imgproc.arcLength(approxTemp, true) * 0.02;
			Imgproc.approxPolyDP(approxTemp, approx, epsilon, true);
			Mat checkArea = new Mat();
			approx.convertTo(checkArea, CvType.CV_32S);
			double areaSize = Math.abs(Imgproc.contourArea(checkArea));
			if (areaSize > 50) {
				int size = approx.rows();
				int[] getPoint = new int[2];

				if (size == 10) { // tail
					//System.out.println("tail");
					getPoint = getMarkerPoint(size);
					Imgproc.circle(video, new Point(getPoint[0], getPoint[1]), 10, new Scalar(0, 255, 0));
					// Imgproc.drawContours(video, contours, i, new Scalar(0, 0, 255));
					marker[1][0] = getPoint[0];
					marker[1][1] = getPoint[1];
				}
				else if (size == 7) { // head
					//System.out.println("head");
					getPoint = getMarkerPoint(size);
					Imgproc.circle(video, new Point(getPoint[0], getPoint[1]), 10, new Scalar(0, 255, 0));
					// Imgproc.drawContours(video, contours, i, new Scalar(0, 0, 255));
					marker[0][0] = getPoint[0];
					marker[0][1] = getPoint[1];
				}
				// else {
				// System.out.println("기타");
				// Imgproc.drawContours(video, contours, i, new Scalar(255, 0, 0));
				// }
			}
		}
		carMarker[0] = (int) ((marker[0][0] + marker[1][0]) / 2);
		carMarker[1] = (int) ((marker[0][1] + marker[1][1]) / 2);
		int size[] = new int[2];
		size[0] = video.cols();
		size[1] = video.rows();
		for(int i = 1;i < 100;i++) {
			Imgproc.line(video, new Point(0,size[1] / 100 * i), new Point(size[0],(double) size[1] / 100 * i), new Scalar(255, 255, 255));
			Imgproc.line(video, new Point((double) size[0] / 100 * i,0), new Point((double) size[0] / 100 * i,size[1]), new Scalar(255, 255, 255));
		}
		Imgproc.circle(video, new Point(carMarker[0], carMarker[1]), 2, new Scalar(0, 0, 255));
		marker[2][0] = carMarker[0] / (size[0] / 100);
		marker[2][1] = carMarker[1] / (size[1] / 100);
		System.out.println(marker[2][0] + ", " + marker[2][1]);
		
		if(!video.empty()) { vertexFrame.render(video); }		 
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
}