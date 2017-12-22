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
	static List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	public static int marker[][] = new int[4][2]; // 임의의 좌표 설정
	public static Point[] sortedPoint = new Point[4]; // 영역 좌표 설정
	static Mat tempImg = new Mat();
	static Mat blackVideo = new Mat();
	static Mat resultVideo = new Mat();
	static Mat mask = new Mat();
	static MatOfPoint2f approxTemp = new MatOfPoint2f();
	static int sensitivity = 15; // inrange 민감도 
	static int idx = 0; // marker index
	public static Boolean areaSetTest = false;
	/*
	 * 그릴 영역 설정
	 */
	public Boolean set_a(Mat camvideo) {
		Mat video = camvideo;
		vertexFrame.setVisible(true);
		Imgproc.resize(video, video, new Size(700,700));
		
		find_4_Marker(video);
		
		if(areaSetTest) {
			sort_Point(video);
			
			if(!video.empty()) { 
				vertexFrame.render(video);	}			
		}
		return areaSetTest;
	}
	/*
	 * 영역 설정하는 4개의 marker 찾기
	 */
	public static void find_4_Marker(Mat video) {
		extractContour(video);
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
					System.out.println(idx + " - OK");
					marker[idx][0] = getPoint[0];
					marker[idx++][1] = getPoint[1];
					if(idx == 4)	areaSetTest = true;
				}
			if(!video.empty()) { vertexFrame.render(resultVideo); }	
		}
	}
	/*
	 * marker 모서리 추출
	 */
	public static void extractContour(Mat video) {
		Imgproc.cvtColor(video, blackVideo, Imgproc.COLOR_BGR2HSV);
		Core.inRange(blackVideo, new Scalar(120 - sensitivity, 90, 90, 0), new Scalar(120 + sensitivity, 255, 255, 0),mask);
		video.copyTo(resultVideo, mask);

		Imgproc.cvtColor(resultVideo, tempImg, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(tempImg, tempImg, 200, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);

		Imgproc.findContours(tempImg, contours, tempImg, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
	}
	/*
	 * 4개의 임의의 index marker를 순서대로 indexing하기 
	 */
	public static void sort_Point(Mat video) {
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
	
	public static Point[] getSortedPoint() {
		return sortedPoint;
	}

	public static void setSortedPoint(Point[] sortedPoint) {
		SetArea.sortedPoint = sortedPoint;
	}
	
}