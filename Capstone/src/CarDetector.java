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

	public static VideoFrame vertexFrame = new VideoFrame();
	public static List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	public static MatOfPoint2f approx = new MatOfPoint2f();
	public static int[] carMarker = new int[2]; // RC car ��ǥ (���� ��ǥ)
	public static double carAngle; // RC car ����
	
	public static int[][] topMarker = new int[2][2]; // ����� ��� �簢�� 
	public static double[] topCenter = new double[2]; // ��� �簢���� ����
	public static double[] topMarkerSize = new double[2]; // ��� ��� �簢�� ����
	public static ArrayList<int[]> markerBottom = new ArrayList<int[]>(); // �ϴ��� ��� �簢��
	static int[] markerForward = new int[2]; // ����� ���� �ﰢ��
	static Mat[] tempVideo = new Mat[3];
	static Mat[] mask = new Mat[3];
	static Mat[] resultVideo = new Mat[3];
	static Mat[] blackVideo = new Mat[3];	
	
	public void CarDetect(Mat camvideo) {
		Mat video = camvideo;
		double degree;
		vertexFrame.setVisible(true);
		Imgproc.resize(video, video, new Size(panelSize[0],panelSize[1]));
		
		for(int j = 0;j < 3;j++) {
			getMarker(video, j);
		}
		
		getCenter();
		
		degree = getAngle();
		
		calculCarMarker(video, degree);

		if(!video.empty()) { vertexFrame.render(video); } 
	}
	/*
	 * RC car�� ��Ŀ���� �ν��Ѵ�. 3�� ��� ����� ��� �ν��ϱ� ������ �ν� ������ �ּ�ȭ �Ѵ�.
	 */
	public static void getMarker(Mat video, int j) {
		tempVideo[j] = new Mat();
		mask[j] = new Mat();
		resultVideo[j] = new Mat();
		blackVideo[j] = new Mat();
		
		extractContour(video, j);
		
		int a = 0;
		for (int i = 0; i < contours.size(); i++) {
			MatOfPoint2f approxTemp = new MatOfPoint2f();
			contours.get(i).convertTo(approxTemp, CvType.CV_32FC2);
		
			double epsilon = Imgproc.arcLength(approxTemp, true) * 0.02;
			Imgproc.approxPolyDP(approxTemp, approx, epsilon, true);
			Mat checkArea = new Mat();
			approx.convertTo(checkArea, CvType.CV_32S);

			double areaSize = Math.abs(Imgproc.contourArea(checkArea));
			
			int size = approx.rows();
			int[] getPoint = new int[2];	
			getPoint = getMarkerPoint(size);
			if (areaSize > markerMinSize[j] && areaSize <= markerMaxSize[j]) {
				if(j == 0 && a < 2) {
					topMarker[a][0] = getPoint[0];
					topMarker[a][1] = getPoint[1];
					topMarkerSize[a] = areaSize;
	            }
				
				else if(j == 1 && a < 2) {
					markerBottom.add(getPoint);
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
	/*
	 * ���� ���ϱ�
	 */
	public static void getCenter() {
		topCenter[0] = (topMarker[0][0] + topMarker[1][0]) / 2;
		topCenter[1] = (topMarker[0][1] + topMarker[1][1]) / 2;
	}
	/*
	 * ������ǥ ���� �� ���� ���ϱ�
	 */
	public static void calculCarMarker(Mat video, double degree) {
		switch(markerBottom.size()) {
		case 1 :	// marker�� �� ���� ���� ���
			double[] distance = new double[2];
			double[] move = new double[2];
			int selectMarkerNum; // �ϴ��� marker�� ����� ����� marker ����
			double target[] = new double[2];
			int bottom[] = new int[2]; 
			bottom = markerBottom.get(0);
			distance[0] = getDistance(topMarker[0], markerBottom.get(0));
			distance[1] = getDistance(topMarker[1], markerBottom.get(0));
			if(distance[0] <= distance[1]) selectMarkerNum = 0;
			else selectMarkerNum = 1;
			
			target = find_marker(selectMarkerNum, degree, bottom);				
			
			for(int i = 0;i < 2;i++) {
				move[i] = markerBottom.get(0)[i] - target[i];
				carMarker[i] = (int) (topCenter[i] + move[i]);
			}
			break;
		case 2 : 	// marker�� �� �� ��� ���� ���
			carMarker[0] = (int) ((markerBottom.get(0)[0] + markerBottom.get(1)[0]) / 2);
			carMarker[1] = (int) ((markerBottom.get(0)[1] + markerBottom.get(1)[1]) / 2); 
			break;
		}
		Imgproc.circle(video, new Point(carMarker[0], carMarker[1]), 5, new Scalar(0, 0, 255));
		carAngle = degree;		
	}
	/*
	 * ������ ������ �ﰢ�� ��Ŀ�� ���� ���� ���
	 */
	public static double getAngle() {
		double dx = topCenter[0] - markerForward[0];
		double dy = topCenter[1] - markerForward[1];

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
	/*
	 * marker �𼭸� ����
	 */	
	public static void extractContour(Mat video, int j) {
		Imgproc.cvtColor(video, blackVideo[j], Imgproc.COLOR_BGR2HSV);
		Core.inRange(blackVideo[j], colorRangeMin[j], colorRangeMax[j], mask[j]);
		video.copyTo(resultVideo[j], mask[j]);
		
		Imgproc.cvtColor(resultVideo[j], tempVideo[j], Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(tempVideo[j], tempVideo[j], 200, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);

		contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(tempVideo[j], contours, tempVideo[j], Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);	
	}
	/*
	 * �ϴ� marker�� ������ �� ã��
	 * ��� marker�� ������ ������ �ϴ� marker�� ����� ��� marker�� �������� �ϴ� ���� �������� ������ �ϴ� marker�� ������ ���� ã�´�
	 */
	public static double[] find_marker(int selectMarkerNum, double degree, int[] markerBottom) {		
		double z[] = new double[2];
		double x[] = new double[2];
		double y[] = new double[2];
		double distance[] = new double[2];
		int listPoint[][] = new int[2][2];
		double a, b, c, d, r;
		int selectmin, farMarkerNum;

		if(selectMarkerNum == 0)
			farMarkerNum = 1;
		else
			farMarkerNum = 0;
		
		r = topMarkerSize[selectMarkerNum]; // ��� marker�� ����
		a = topMarker[selectMarkerNum][0]; // �ϴ� marker��  ������ ��� marker x��ǥ
		b = topMarker[selectMarkerNum][1]; // �ϴ� marker��  ������ ��� marker y��ǥ
		c = topMarker[farMarkerNum][0]; // �ϴ� marker��  �� ��� marker x��ǥ
		d = topMarker[farMarkerNum][1]; // �ϴ� marker��  �� ��� marker y��ǥ
		/*
		 * ������ ���� ������ �� ���ϱ�
		 */	
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
		
		for(int i = 0; i < 2; i++) {
			listPoint[i][0] = (int)x[i];
			listPoint[i][1] = (int)y[i];			
		}
		/*
		 * ��� ��� ���� �� ��ǥ �� �ϴ� marker�� ����� �� ����
		 */	
		for(int i = 0; i < 2; i++) {
			distance[i] = getDistance(listPoint[i], markerBottom);			
		}
		
		if(distance[0] > distance[1])
			selectmin = 1;
		else
			selectmin = 0;
		
		z[0] = x[selectmin];
		z[1] = y[selectmin];

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