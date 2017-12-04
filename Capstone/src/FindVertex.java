import java.util.ArrayList;
import java.util.Iterator;
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
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class FindVertex {

	static VideoFrame vertexFrame = new VideoFrame();
	static {
		String opencvPath = "C:\\opencv\\build\\java\\x64\\";
		System.load(opencvPath + Core.NATIVE_LIBRARY_NAME + ".dll");
	}
	static int intPattern = 1;

	public static int[][] pattern(int[][] input) {
		int[][] imgArr = input;
		Mat vertexImg = Imgcodecs.imread("test.png");
		Mat tempImg = new Mat();
		MatOfPoint approxTemp = new MatOfPoint();
		// int patternIdx = 0;
		while (true) {
			Imgproc.cvtColor(vertexImg, tempImg, Imgproc.COLOR_BGR2GRAY);
			Imgproc.threshold(tempImg, tempImg, 200, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			List<Point> curve = new Vector<Point>();
			Imgproc.findContours(tempImg, contours, tempImg, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
			int[] xy = new int[2];

			if (contours.size() == 0)
				break;
			for (int i = 0; i < contours.size(); i++) {
				approxTemp = contours.get(i);
				for (int j = 0; j < approxTemp.rows(); j++) {
					String[] splitTemp = approxTemp.row(j).dump().toString().split(", ");

					xy[0] = Integer.parseInt(splitTemp[0].replaceAll("[^0-9]", ""));
					xy[1] = Integer.parseInt(splitTemp[1].replaceAll("[^0-9]", ""));
					imgArr[xy[0]][xy[1]] = intPattern;
					// System.out.println("ÁÂÇ¥ : (" + replaceTemp[0] + "," + replaceTemp[1] + ")");
					// Imgproc.circle(vertexImg, new
					// Point(Integer.parseInt(replaceTemp[0]),Integer.parseInt(replaceTemp[1])), -1,
					// new Scalar(0,0,0));
				}
				Imgproc.drawContours(vertexImg, contours, i, new Scalar(0, 0, 0), 2);
			}
			intPattern++;
			if (intPattern == 9)
				intPattern++;
			// patternIdx++;
		}
		/*
		 * for(int i = 0;i < imgArr.length;i++) { for(int j = 0;j <
		 * imgArr[0].length;j++) { System.out.print(" " + imgArr[i][j]); }
		 * System.out.println(""); }
		 */
		// vertexFrame.setVisible(true);
		// vertexFrame.render(vertexImg);

		return imgArr;
	}
}
