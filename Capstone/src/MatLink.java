import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class MatLink {
	static {
		String opencvPath = "C:\\opencv\\build\\java\\x64\\";
		System.load( opencvPath + Core.NATIVE_LIBRARY_NAME + ".dll" );
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Mat mat = Mat.eye(10, 10, CvType.CV_8UC1);
		System.out.println(mat.dump());
		
		for(int i = 0; i < mat.rows(); i++) {
			for(int j = 0; j < mat.cols(); j++) {
				System.out.println("ÁÂÇ¥: " + "(" + i + "," + j + ")" + " °ª: "+ mat.get(i, j)[0]);
			}
		}
	}
}