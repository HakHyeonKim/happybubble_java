import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

public class FindVertex {

	static VideoFrame vertexFrame = new VideoFrame();
	static {
		String opencvPath = "C:\\opencv\\build\\java\\x64\\";
		System.load(opencvPath + Core.NATIVE_LIBRARY_NAME + ".dll");
	}
	static int intPattern = 1;

	public static int[][] pattern(int[][] input) {
		int[][] imgArr = input;

		// �̹����� ������Ű�� ���� array ����
		int[][] tempArr = new int[imgArr.length][imgArr[0].length];
		for (int i = 0; i < imgArr.length; i++) {
			for (int j = 0; j < imgArr[0].length; j++) {
				tempArr[i][j] = imgArr[i][j];
			}
		}
		Mat vertexImg = Imgcodecs.imread("binary_img.png");
		Mat tempImg = new Mat();
		MatOfPoint approxTemp = new MatOfPoint();
		while (true) {
			// contours�� ã�� ���� �̹��� ����ȭ
			Imgproc.cvtColor(vertexImg, tempImg, Imgproc.COLOR_BGR2GRAY);
			Imgproc.threshold(tempImg, tempImg, 200, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);
			// contours ã��
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			List<Point> curve = new Vector<Point>();
			Imgproc.findContours(tempImg, contours, tempImg, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
			int[] xy = new int[2];

			// contours�� ã���� ��ǥ�� ���� �� ��ȯ
			if (contours.size() == 0)
				break;
			for (int i = 0; i < contours.size(); i++) {
				approxTemp = contours.get(i);
				for (int j = 0; j < approxTemp.rows(); j++) {
					String[] splitTemp = approxTemp.row(j).dump().toString().split(", ");

					xy[0] = Integer.parseInt(splitTemp[0].replaceAll("[^0-9]", ""));
					xy[1] = Integer.parseInt(splitTemp[1].replaceAll("[^0-9]", ""));
					imgArr[xy[0]][xy[1]] = intPattern;
					tempArr[xy[0]][xy[1]] = intPattern;
				}
				Imgproc.drawContours(vertexImg, contours, i, new Scalar(0, 0, 0), 2);
			}
			intPattern++;
			if (intPattern == 9)
				intPattern++;
		}

		// ������ �̹����̱� ������ ��ü �迭 ����
		for (int i = 0; i < imgArr.length; i++) {
			for (int j = 0; j < imgArr[0].length; j++) {
				imgArr[imgArr.length - 1 - i][j] = tempArr[i][j];
			}
		}
		return imgArr;
	}
}
