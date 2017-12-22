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
	public final static int[] marker_max_size = {1000, 1000, 390};
	public final static int[] marker_min_size = {400, 100, 100};
	public final static int[] panel_size = {700, 700};
	public final static int setting_width = 100;
	public final static int setting_height = 100;
	public final static Scalar[] color_range_min = {
			new Scalar(50, 70, 70, 0)
			, new Scalar(0, 75, 75)
			, new Scalar(50, 70, 70, 0)};
	public final static Scalar[] color_range_max = {
			new Scalar(100, 255, 255, 0)
			, new Scalar(50, 255, 255)
			, new Scalar(100, 255, 255, 0)};
	final static int car_size = 100;
	final static int video_size = 700;
	final static int[] set_area = {car_size, video_size - car_size};

	public static VideoFrame vertex_frame = new VideoFrame();
	public static List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	public static MatOfPoint2f approx = new MatOfPoint2f();
	public static int[] car_marker = new int[2]; // RC car ��ǥ (���� ��ǥ)
	public static double car_angle; // RC car ����
	
	public static int[][] top_marker = new int[2][2]; // ����� ��� �簢�� 
	public static double[] topCenter = new double[2]; // ��� �簢���� ����
	public static double[] top_marker_size = new double[2]; // ��� ��� �簢�� ����
	public static ArrayList<int[]> marker_bottom = new ArrayList<int[]>(); // �ϴ��� ��� �簢��
	static int[] marker_forward = new int[2]; // ����� ���� �ﰢ��
	static Mat[] temp_video = new Mat[3];
	static Mat[] mask = new Mat[3];
	static Mat[] result_video = new Mat[3];
	static Mat[] black_video = new Mat[3];	
	
	public void CarDetect(Mat camvideo) {
		Mat video = camvideo;
		double degree;
		vertex_frame.setVisible(true);
		Imgproc.resize(video, video, new Size(panel_size[0],panel_size[1]));
		
		for(int j = 0;j < 3;j++) {
			getMarker(video, j);
		}
		
		getCenter();
		
		degree = getAngle();
		
		calculCarMarker(video, degree);

		if(!video.empty()) { vertex_frame.render(video); } 
	}
	/*
	 * RC car�� ��Ŀ���� �ν��Ѵ�. 3�� ��� ����� ��� �ν��ϱ� ������ �ν� ������ �ּ�ȭ �Ѵ�.
	 */
	public static void getMarker(Mat video, int j) {
		temp_video[j] = new Mat();
		mask[j] = new Mat();
		result_video[j] = new Mat();
		black_video[j] = new Mat();
		
		extractContour(video, j);
		
		int a = 0;
		for (int i = 0; i < contours.size(); i++) {
			MatOfPoint2f approxTemp = new MatOfPoint2f();
			contours.get(i).convertTo(approxTemp, CvType.CV_32FC2);
		
			double epsilon = Imgproc.arcLength(approxTemp, true) * 0.02;
			Imgproc.approxPolyDP(approxTemp, approx, epsilon, true);
			Mat checkArea = new Mat();
			approx.convertTo(checkArea, CvType.CV_32S);

			double area_size = Math.abs(Imgproc.contourArea(checkArea));
			
			int size = approx.rows();
			int[] get_point = new int[2];	
			get_point = getMarkerPoint(size);
			if (area_size > marker_min_size[j] && area_size <= marker_max_size[j]) {
				if(j == 0 && a < 2) {
					top_marker[a][0] = get_point[0];
					top_marker[a][1] = get_point[1];
					top_marker_size[a] = area_size;
	            }
				
				else if(j == 1 && a < 2) {
					marker_bottom.add(get_point);
				}
				else if(j == 2 && a < 1) {
					marker_forward[0] = get_point[0];
					marker_forward[1] = get_point[1];
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
		topCenter[0] = (top_marker[0][0] + top_marker[1][0]) / 2;
		topCenter[1] = (top_marker[0][1] + top_marker[1][1]) / 2;
	}
	/*
	 * ������ǥ ���� �� ���� ���ϱ�
	 */
	public static void calculCarMarker(Mat video, double degree) {
		switch(marker_bottom.size()) {
		case 1 :	// marker�� �� ���� ���� ���
			double[] distance = new double[2];
			double[] move = new double[2];
			int select_marker_num; // �ϴ��� marker�� ����� ����� marker ����
			double target[] = new double[2];
			int bottom[] = new int[2]; 
			bottom = marker_bottom.get(0);
			distance[0] = getDistance(top_marker[0], marker_bottom.get(0));
			distance[1] = getDistance(top_marker[1], marker_bottom.get(0));
			if(distance[0] <= distance[1]) select_marker_num = 0;
			else select_marker_num = 1;
			
			target = find_marker(select_marker_num, degree, bottom);				
			
			for(int i = 0;i < 2;i++) {
				move[i] = marker_bottom.get(0)[i] - target[i];
				car_marker[i] = (int) (topCenter[i] + move[i]);
			}
			break;
		case 2 : 	// marker�� �� �� ��� ���� ���
			car_marker[0] = (int) ((marker_bottom.get(0)[0] + marker_bottom.get(1)[0]) / 2);
			car_marker[1] = (int) ((marker_bottom.get(0)[1] + marker_bottom.get(1)[1]) / 2); 
			break;
		}
		Imgproc.circle(video, new Point(car_marker[0], car_marker[1]), 5, new Scalar(0, 0, 255));
		car_angle = degree;		
	}
	/*
	 * ������ ������ �ﰢ�� ��Ŀ�� ���� ���� ���
	 */
	public static double getAngle() {
		double dx = topCenter[0] - marker_forward[0];
		double dy = topCenter[1] - marker_forward[1];

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
		Imgproc.cvtColor(video, black_video[j], Imgproc.COLOR_BGR2HSV);
		Core.inRange(black_video[j], color_range_min[j], color_range_max[j], mask[j]);
		video.copyTo(result_video[j], mask[j]);
		
		Imgproc.cvtColor(result_video[j], temp_video[j], Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(temp_video[j], temp_video[j], 200, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);

		contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(temp_video[j], contours, temp_video[j], Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);	
	}
	/*
	 * �ϴ� marker�� ������ �� ã��
	 * ��� marker�� ������ ������ �ϴ� marker�� ����� ��� marker�� �������� �ϴ� ���� �������� ������ �ϴ� marker�� ������ ���� ã�´�
	 */
	public static double[] find_marker(int select_marker_num, double degree, int[] marker_bottom) {		
		double z[] = new double[2];
		double x[] = new double[2];
		double y[] = new double[2];
		double distance[] = new double[2];
		int list_point[][] = new int[2][2];
		double a, b, c, d, r;
		int select_min, far_marker_num;

		if(select_marker_num == 0)
			far_marker_num = 1;
		else
			far_marker_num = 0;
		
		r = top_marker_size[select_marker_num]; // ��� marker�� ����
		a = top_marker[select_marker_num][0]; // �ϴ� marker��  ������ ��� marker x��ǥ
		b = top_marker[select_marker_num][1]; // �ϴ� marker��  ������ ��� marker y��ǥ
		c = top_marker[far_marker_num][0]; // �ϴ� marker��  �� ��� marker x��ǥ
		d = top_marker[far_marker_num][1]; // �ϴ� marker��  �� ��� marker y��ǥ
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
			list_point[i][0] = (int)x[i];
			list_point[i][1] = (int)y[i];			
		}
		/*
		 * ��� ��� ���� �� ��ǥ �� �ϴ� marker�� ����� �� ����
		 */	
		for(int i = 0; i < 2; i++) {
			distance[i] = getDistance(list_point[i], marker_bottom);			
		}
		
		if(distance[0] > distance[1])
			select_min = 1;
		else
			select_min = 0;
		
		z[0] = x[select_min];
		z[1] = y[select_min];

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
		return car_marker;
	}

	public static double getCarAngle() {
		return car_angle;
	}
}