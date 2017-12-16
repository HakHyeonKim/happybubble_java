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
	public final static int[] markerMaxSize = {1000, 1000, 390};
	public final static int[] markerMinSize = {400, 100, 100};
	public final static int[] panelSize = {700, 700};
	public final static int settingWidth = 100;
	public final static int settingHeight = 100;
//	public final static int sensitivity = 45;
	public final static Scalar[] colorRangeMin = {
			new Scalar(50, 70, 70, 0)
			, new Scalar(0, 75, 75)
			, new Scalar(50, 70, 70, 0)};
	public final static Scalar[] colorRangeMax = {
			new Scalar(100, 255, 255, 0)
			, new Scalar(50, 255, 255)
			, new Scalar(100, 255, 255, 0)};
	final static int carSize = 100;
	final static int videoSize = 700;
	final static int[] setArea = {carSize, videoSize - carSize};
	// define 영역

	public static VideoFrame vertexFrame = new VideoFrame();
//	public static VideoFrame greenFrame = new VideoFrame();
	public static List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	public static MatOfPoint2f approx = new MatOfPoint2f();
	public static int[] carMarker = new int[2];
	public static double carAngle;
	
	public static double dist[] = new double[8]; // dist1,2 사각형 어떤 변인지 dist3,dist4 다른 사각형
	public static int pointList[][] = new int[8][2];
	public static int dist_x[] = new int[5];
	public static int dist_y[] = new int[5];
	public static int tempxPoint[][] = new int[3][4];
	public static int tempyPoint[][] = new int[3][4];
	public static int[][] markerTop = new int[2][2];
	public static double[] topMarker = new double[2];
	public static double[] markerTopSize = new double[2];
	public static ArrayList<int[]> markerBottom = new ArrayList<int[]>();
	
	
	public void CarDetect(Mat camvideo) {
		Mat video = camvideo;
		vertexFrame.setVisible(true);
//		greenFrame.setVisible(true);
		int[] markerForward = new int[2];
		ArrayList<int[]> markerBottom = new ArrayList<int[]>();
		Mat[] tempVideo = new Mat[3];
		Mat[] mask = new Mat[3];
		Mat[] resultVideo = new Mat[3];
		Mat[] blackVideo = new Mat[3];
		Imgproc.resize(video, video, new Size(panelSize[0],panelSize[1]));
		for(int j = 0;j < 3;j++) {
			tempVideo[j] = new Mat();
			mask[j] = new Mat();
			resultVideo[j] = new Mat();
			blackVideo[j] = new Mat();
			
			Imgproc.cvtColor(video, blackVideo[j], Imgproc.COLOR_BGR2HSV);
			Core.inRange(blackVideo[j], colorRangeMin[j], colorRangeMax[j], mask[j]);
			video.copyTo(resultVideo[j], mask[j]);
			
			// 그레이스케일 이미지로 변환
			Imgproc.cvtColor(resultVideo[j], tempVideo[j], Imgproc.COLOR_BGR2GRAY);
			Imgproc.threshold(tempVideo[j], tempVideo[j], 200, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);

			// contour를 찾는다.
			contours = new ArrayList<MatOfPoint>();
			Imgproc.findContours(tempVideo[j], contours, tempVideo[j], Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
			
			int a = 0; // for문 최소화
			for (int i = 0; i < contours.size(); i++) {
				MatOfPoint2f approxTemp = new MatOfPoint2f();
				contours.get(i).convertTo(approxTemp, CvType.CV_32FC2);
				// contour를 근사화한다.
				double epsilon = Imgproc.arcLength(approxTemp, true) * 0.02;
				Imgproc.approxPolyDP(approxTemp, approx, epsilon, true);
				Mat checkArea = new Mat();
				approx.convertTo(checkArea, CvType.CV_32S);

				double areaSize = Math.abs(Imgproc.contourArea(checkArea));
				
				int size = approx.rows();
				int[] getPoint = new int[2];	
				getPoint = getMarkerPoint(size);
				if (areaSize > markerMinSize[j] && areaSize <= markerMaxSize[j]) {
//					Imgproc.putText(resultVideo[j], "" + areaSize, new Point(getPoint[0], getPoint[1]), 1, 2, new Scalar(255,255,255));
//					Imgproc.drawContours(resultVideo[j], contours, i, new Scalar(0, 0, 255));
//					Imgproc.circle(video, new Point(getPoint[0], getPoint[1]), 4, new Scalar(0, 255, 0));
					if(j == 0 && a < 2) {
						markerTop[a][0] = getPoint[0];
						markerTop[a][1] = getPoint[1];
						markerTopSize[a] = areaSize;
		            }
					
					else if(j == 1 && a < 2) {
						markerBottom.add(getPoint);
						//Imgproc.circle(video, new Point(markerBottom.get(a)[0], markerBottom.get(a)[1]), 3, new Scalar(0, 0, 255));
					}
					else if(j == 2 && a < 1) {
						markerForward[0] = getPoint[0];
						markerForward[1] = getPoint[1];
					}
					a++;
				}
				if (j != 2 && a == 2)	break;
				else if (j == 2 && a == 1)	break;
			}
			
			
		}
		int size[] = new int[2];
		size[0] = video.cols();
		size[1] = video.rows();
		
//		Imgproc.circle(video, new Point(pointList[target[0]][0], pointList[target[0]][1]), 3, new Scalar(0, 0, 255));
//		Imgproc.circle(video, new Point(pointList[target[1]][0], pointList[target[1]][1]), 3, new Scalar(0, 0, 255));

		topMarker[0] = (markerTop[0][0] + markerTop[1][0]) / 2;
		topMarker[1] = (markerTop[0][1] + markerTop[1][1]) / 2;
//		Imgproc.circle(resultVideo[2], new Point(topMarker[0], topMarker[1]), 3, new Scalar(255, 0, 0));
		//Imgproc.circle(video, new Point(markerTop[0][0], markerTop[0][1]), 3, new Scalar(0, 255, 0));
		//Imgproc.circle(video, new Point(markerTop[1][0], markerTop[1][1]), 3, new Scalar(0, 255, 0));						  

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

//		System.out.println("test : " + markerBottom.size());
		switch(markerBottom.size()) {
			case 1 :	// marker가 한 개만 보일 경우
				double[] distance = new double[2];
				double[] move = new double[2];
				int selectMarkerNum;
				double target[] = new double[2];
				int bottom[] = new int[2]; 
				bottom = markerBottom.get(0);
				distance[0] = getDistance(markerTop[0], markerBottom.get(0));
				distance[1] = getDistance(markerTop[1], markerBottom.get(0));
				if(distance[0] <= distance[1]) selectMarkerNum = 0;
				else selectMarkerNum = 1;
				
				target = find_marker(selectMarkerNum, degree, bottom);				
//				Imgproc.circle(video, new Point(target[0], target[1]), 3, new Scalar(255, 0, 255));
				
				for(int i = 0;i < 2;i++) {
					move[i] = markerBottom.get(0)[i] - target[i];
					carMarker[i] = (int) (topMarker[i] + move[i]);
				}
				break;
			case 2 : 	// marker가 두 개 모두 보일 경우
				carMarker[0] = (int) ((markerBottom.get(0)[0] + markerBottom.get(1)[0]) / 2);
				carMarker[1] = (int) ((markerBottom.get(0)[1] + markerBottom.get(1)[1]) / 2); 
				break;
		}

		Imgproc.circle(video, new Point(carMarker[0], carMarker[1]), 5, new Scalar(0, 0, 255));

		carAngle = degree;
//		System.out.println(degree);
//		Imgproc.rectangle(video, new Point(setArea[0], setArea[0]), new Point(setArea[1], setArea[1]), new Scalar(0,0,255));

		/*
		if(!video.empty()) { greenFrame.render(resultVideo[2]); }
		Imgproc.putText(resultVideo[2], "" + carAngle, new Point(carMarker[0], carMarker[1]), 1, 2, new Scalar(255,255,255));
		if(!video.empty()) { vertexFrame.render(resultVideo[0]); }
		 */
		if(!video.empty()) { vertexFrame.render(video); } 

	}
	
	public static double[] find_marker(int selectMarkerNum, double degree, int[] markerBottom) {
		
		double z[] = new double[2];
		double a, b, c, d;
		int selectmin;
		int farMarkerNum;
		double r;
		double x[] = new double[2];
		double y[] = new double[2];
		double distance[] = new double[2];
		int listPoint[][] = new int[2][2];
		if(selectMarkerNum == 0)
			farMarkerNum = 1;
		else
			farMarkerNum = 0;
		
		r = markerTopSize[selectMarkerNum];
		a = markerTop[selectMarkerNum][0];
		b = markerTop[selectMarkerNum][1];
		c = markerTop[farMarkerNum][0];
		d = markerTop[farMarkerNum][1];
		
		if(c != a) {
			x[0] = -Math.sqrt(r/(1 + Math.pow((d-b)/(c-a), 2))) + a;
			y[0] = (d-b)/(c-a)*(x[0]-a) + b;			
			x[1] = Math.sqrt(r/(1 + Math.pow((d-b)/(c-a), 2))) + a;
			y[1] = (d-b)/(c-a)*(x[1]-a) + b;
		}
		else {
			x[0] = a;
			y[0] = - Math.sqrt(r) + b;			
			x[1] = a;
			y[1] = Math.sqrt(r) + b;
		}
		listPoint[0][0] = (int)x[0];
		listPoint[0][1] = (int)y[0];
		listPoint[1][0] = (int)x[1];
		listPoint[1][1] = (int)y[1];
		
		distance[0] = getDistance(listPoint[0], markerBottom);
		distance[1] = getDistance(listPoint[1], markerBottom);
		if(distance[0] > distance[1])
			selectmin = 1;
		else
			selectmin = 0;
		
		z[0] = x[selectmin];
		z[1] = y[selectmin];
		/*
		dist_x[0] = tempxPoint[0][0] - tempxPoint[0][1];
		dist_y[0] = tempyPoint[0][0] - tempyPoint[0][1];
		dist_x[1] = tempxPoint[0][1] - tempxPoint[0][2];
		dist_y[1] = tempyPoint[0][1] - tempyPoint[0][2];	
		dist_x[2] = tempxPoint[1][0] - tempxPoint[1][1];
		dist_y[2] = tempyPoint[1][0] - tempyPoint[1][1];
		dist_x[3] = tempxPoint[1][1] - tempxPoint[1][2];
		dist_y[3] = tempyPoint[1][1] - tempyPoint[1][2];	

		pointList[0][0] = markerTop[0][0] + dist_x[0];
		pointList[0][1] = markerTop[0][1] + dist_y[0];
		pointList[1][0] = markerTop[0][0] + dist_x[1];
		pointList[1][1] = markerTop[0][1] + dist_y[1];
		pointList[2][0] = markerTop[0][0] - dist_x[0];
		pointList[2][1] = markerTop[0][1] - dist_y[0];
		pointList[3][0] = markerTop[0][0] - dist_x[1];
		pointList[3][1] = markerTop[0][1] - dist_y[1];
		pointList[4][0] = markerTop[1][0] + dist_x[2];
		pointList[4][1] = markerTop[1][1] + dist_y[2];
		pointList[5][0] = markerTop[1][0] + dist_x[3];
		pointList[5][1] = markerTop[1][1] + dist_y[3];
		pointList[6][0] = markerTop[1][0] - dist_x[2];
		pointList[6][1] = markerTop[1][1] - dist_y[2];
		pointList[7][0] = markerTop[1][0] - dist_x[3];
		pointList[7][1] = markerTop[1][1] - dist_y[3];	
		
		double center[] = new double[2];
		
		center[0] = (markerTop[0][0] + markerTop[1][0]) / 2;
		center[1] = (markerTop[0][1] + markerTop[1][1]) / 2;
		for(int k = 0 ; k < 8 ; k++) {
			dist[k] = Math.sqrt((pointList[k][0] - center[0]) * (pointList[k][0]- center[0]) + (pointList[k][1]- center[1]) * (pointList[k][1]- center[1]));	
		}
		
		double temp;
		double dist_temp[] = new double[8];
		
		for(int k = 0; k < 8 ; k++) {
			dist_temp[k] = dist[k];
		}
		
		int cnt = 0;
		for(int k = 8; k > 0 ; k-- ) {
			for(int l = 0; l < k-1 ; l++) {
				cnt++;
				if(dist[l]>dist[l+1]) {
					temp = dist[l];
					dist[l] = dist[l+1];
					dist[l+1] = temp;
				}
			}
		}
		for(a[0] = 0; a[0] < 8; a[0]++) {
			if(dist_temp[a[0]] == dist[7])
				break;
		}
		for(a[1] = 0; a[1] < 8; a[1]++) {
			if(dist_temp[a[1]] == dist[6])
				break;
		}
		for(int k = 0; k<8; k++) {
//			System.out.println("[" + k + "]" + dist_temp[k]);
		}
		*/
//		System.out.println(a[0]);
//		System.out.println(a[1]);
		return z;
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
	
	static double getDistance(int[] top, int[] bottom) {
        return Math.sqrt((top[0] - bottom[0]) * (top[0] - bottom[0]) + (top[1] - bottom[1]) * (top[1] - bottom[1]));
    }
	
	public static int[] getCarMarker() {
		return carMarker;
	}

	public static double getCarAngle() {
		return carAngle;
	}
}