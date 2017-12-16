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

public class SetArea {
	static VideoFrame vertexFrame = new VideoFrame();
	static MatOfPoint2f approx = new MatOfPoint2f();
	public static int marker[][] = new int[4][2];
	public static Point[] sortedPoint = new Point[4];

	public Boolean set_a(Mat camvideo) {
		vertexFrame.setVisible(true);
		Mat video = camvideo;
		Mat tempImg = new Mat();
		Mat blackVideo = new Mat();
		Mat resultVideo = new Mat();
		Mat mask = new Mat();
		MatOfPoint2f approxTemp = new MatOfPoint2f();
		int sensitivity = 15;
		int idx = 0;
		Boolean test = false;
		Imgproc.resize(video, video, new Size(700,700));
		
		Imgproc.cvtColor(video, blackVideo, Imgproc.COLOR_BGR2HSV);
		Core.inRange(blackVideo, new Scalar(120 - sensitivity, 90, 90, 0), new Scalar(120 + sensitivity, 255, 255, 0),mask);
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
			if (areaSize > 300 && areaSize < 2000) {
				int size = approx.rows();
				int[] getPoint = new int[2];
				getPoint = getMarkerPoint(size);
				Imgproc.putText(resultVideo, "" + areaSize, new Point(getPoint[0], getPoint[1]), 1, 2, new Scalar(0,0,255));
				Imgproc.drawContours(resultVideo, contours, i, new Scalar(0, 0, 255));
				//if (checkMarker(getPoint) && (size == 3 || size == 4)) {
					System.out.println(idx + " - OK");
					// Imgproc.circle(video, new Point(getPoint[0], getPoint[1]), 10, new Scalar(0, 255, 255));
					marker[idx][0] = getPoint[0];
					marker[idx++][1] = getPoint[1];
					if(idx == 4)	test = true;
				//}
				/*
				if (size == 5) {
					getPoint = getMarkerPoint(size);
					// Imgproc.circle(video, new Point(getPoint[0], getPoint[1]), 10, new Scalar(0, 255, 255));
					Imgproc.drawContours(video, contours, i, new Scalar(0, 0, 255));
					marker[0][0] = getPoint[0];
					marker[0][1] = getPoint[1];
					markerChk[0] = true;
				}
				else if (size == 12) {
					getPoint = getMarkerPoint(size);
					// Imgproc.circle(video, new Point(getPoint[0], getPoint[1]), 10, new Scalar(0, 255, 255));
					Imgproc.drawContours(video, contours, i, new Scalar(0, 0, 255));
					marker[1][0] = getPoint[0];
					marker[1][1] = getPoint[1];
					markerChk[1] = true;
				}
				else if (size == 3) {
					getPoint = getMarkerPoint(size);
					// Imgproc.circle(video, new Point(getPoint[0], getPoint[1]), 10, new Scalar(0, 255, 255));
					Imgproc.drawContours(video, contours, i, new Scalar(0, 0, 255));
					marker[2][0] = getPoint[0];
					marker[2][1] = getPoint[1];
					markerChk[2] = true;
				}
				*/
			}
			if(!video.empty()) { vertexFrame.render(resultVideo); }	
		}
		if(test) {
			int[] centerPoint = new int[2];
			
			centerPoint[0] = (marker[0][0] + marker[1][0] + marker[2][0] + marker[3][0]) / 4;
			centerPoint[1] = (marker[0][1] + marker[1][1] + marker[2][1] + marker[3][1]) / 4;
			
			for(int i = 0;i < marker.length;i++) {
				if(marker[i][0] < centerPoint[0] && marker[i][1] < centerPoint[1])
					sortedPoint[0] = new Point(marker[i][0], marker[i][1]);
				else if(marker[i][0] > centerPoint[0] && marker[i][1] < centerPoint[1])
					sortedPoint[1] = new Point(marker[i][0], marker[i][1]);
				else if(marker[i][0] < centerPoint[0] && marker[i][1] > centerPoint[1])
					sortedPoint[2] = new Point(marker[i][0], marker[i][1]);
				else if(marker[i][0] > centerPoint[0] && marker[i][1] > centerPoint[1])
					sortedPoint[3] = new Point(marker[i][0], marker[i][1]);
			}
			setSortedPoint(sortedPoint);
		    MatOfPoint2f src = new MatOfPoint2f(sortedPoint[0], sortedPoint[1], sortedPoint[2],sortedPoint[3]);
		    int w = video.cols();
		    int h = video.rows();
		    MatOfPoint2f dst = new MatOfPoint2f(new Point(0, 0), new Point(w-1,0), new Point(0,h-1), new Point(w-1,h-1));
		    Mat wrapMat = Imgproc.getPerspectiveTransform(src,dst);
		    Imgproc.warpPerspective(video, video, wrapMat, video.size());
			if(!video.empty()) { vertexFrame.render(video);	}
			
		}
		return test;
	}
	
	public static Point[] getSortedPoint() {
		return sortedPoint;
	}

	public static void setSortedPoint(Point[] sortedPoint) {
		SetArea.sortedPoint = sortedPoint;
	}

	public int[][] getMarker() {
		return marker;
	}

	public static void setMarker(int[][] marker) {
		SetArea.marker = marker;
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
	
	public static Boolean checkMarker(int[] point) {
		Boolean chk = true;
		int diffX = 0;
		int diffY = 0;
		for(int i = 0;i < marker.length;i++) {
			diffX = Math.abs(marker[i][0] - point[0]);
			diffY = Math.abs(marker[i][1] - point[1]);
			if(diffX <= 50 && diffY <= 50)	chk = false;
		}
		
		return chk;
	}
	
	public static int[] swapPoint(int[] point, int index) {
		int[] pointTemp = new int[2];
		pointTemp[0] = marker[index][0];
		pointTemp[1] = marker[index][1];
		marker[index][0] = point[0];
		marker[index][1] = point[1];
		point[0] = pointTemp[0];
		point[1] = pointTemp[1];
		
		return point;
	}
}