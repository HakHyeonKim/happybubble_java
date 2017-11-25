import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.smartcardio.Card;

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

import javafx.geometry.Point2D;


public class FindVertex {

	   static VideoFrame vertexFrame = new VideoFrame();
	   static {
	      String opencvPath = "C:\\opencv330\\build\\java\\x64\\";
	      System.load( opencvPath + Core.NATIVE_LIBRARY_NAME + ".dll" );
	   }
	   

	   public static void main(String[] args)
	   {
		   Mat vertexImg = Imgcodecs.imread("test.png");
		   Mat tempImg = new Mat();
		   MatOfPoint approxTemp = new MatOfPoint();
		   Size sz = new Size(800, 800);
		   Imgproc.resize(vertexImg, vertexImg, sz);
		   Imgproc.cvtColor(vertexImg, tempImg, Imgproc.COLOR_BGR2GRAY);
		   Imgproc.threshold(tempImg, tempImg, 200, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);
		   List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		   List<Point> curve = new Vector<Point>();
		   Imgproc.findContours(tempImg, contours, tempImg, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
		   String[] replaceTemp = new String[2];
		    
		   for(int i = 0;i < contours.size();i++) {
			   approxTemp = contours.get(i);
			   for(int j = 0;j < approxTemp.rows();j++) {
				   String[] splitTemp = approxTemp.row(0).dump().toString().split(", ");
	
				   replaceTemp[0] = splitTemp[0].replaceAll("[^0-9]", "");
				   replaceTemp[1] = splitTemp[1].replaceAll("[^0-9]", "");
				   System.out.println("ÁÂÇ¥ : (" + replaceTemp[0] + "," + replaceTemp[1] + ")");
				   Imgproc.circle(vertexImg, new Point(Integer.parseInt(replaceTemp[0]),Integer.parseInt(replaceTemp[1])), 4, new Scalar(0,255,0));
			   }
			   //Imgproc.drawContours(vertexImg, contours, i, new Scalar(0, 0, 255));
		   }
		   vertexFrame.setVisible(true);
		   vertexFrame.render(vertexImg);
	   }
}
