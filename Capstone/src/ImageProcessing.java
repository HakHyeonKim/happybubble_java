import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.util.Arrays;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ImageProcessing
{

	
	static Mat video, blackVideo, binaryVideo, closedVideo, outputVideo, lableVideo, morphVideo;
	static Mat stat, centroid;
	static int w = 0;
	static int h = 0;
	static int k = 0;
	static int lablesNum = 0;
	static int matrixXSize = 200;
	static int matrixYSize = 100;
	static double[] data = null;
	static double[] dataVideo = null;
	static double[][] marker = new double[5][2];
	
	static VideoFrame colorFrame = new VideoFrame();
	static VideoFrame byteFrame = new VideoFrame();
	
	static {
		String opencvPath = "C:\\opencv330\\build\\java\\x64\\";
		System.load( opencvPath + Core.NATIVE_LIBRARY_NAME + ".dll" );
	}
	
	public static void main( String[] args )
	{
		Mat mask = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3), new Point(1, 1));
		VideoCapture cap = new VideoCapture(0);
		System.out.println("test");
		
		if(!cap.isOpened())	System.exit(-1);
		colorFrame.setVisible(true);
		byteFrame.setVisible(true);
		
		while(true) {
			video = new Mat();
			blackVideo = new Mat();
			binaryVideo = new Mat();
			closedVideo = new Mat();
			outputVideo = new Mat();
			lableVideo = new Mat();
			stat = new Mat();
			centroid = new Mat();
			
			cap.read(video);

			Imgproc.cvtColor(video, blackVideo, Imgproc.COLOR_BGR2YUV);
			Core.inRange(blackVideo, new Scalar(0,0,0), new Scalar(10,0,0), blackVideo);
			video.copyTo(video, blackVideo);
			Imgproc.cvtColor(video, binaryVideo, Imgproc.COLOR_BGR2GRAY);
			Imgproc.threshold(binaryVideo, binaryVideo, 200, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);
			
			h = binaryVideo.height();
			w = binaryVideo.width();
			morphVideo = new Mat(h,w,CvType.CV_8UC1);
			Imgproc.erode(binaryVideo, closedVideo, mask, new Point(-1,-1), 1);
			Imgproc.dilate(closedVideo, closedVideo, mask, new Point(-1,-1), 1);
			//Imgproc.dilate(binaryVideo, binaryVideo, mask, new Point(-1,-1), 5);
			//Core.bitwise_not(closedVideo, closedVideo);
			video.copyTo(outputVideo, closedVideo);
			
			//Imgproc.morphologyEx(binaryVideo, outputVideo, Imgproc.MORPH_BLACKHAT, mask, new Point(-1,-1), 3);
			for(int i = 0;i < h;i++) {
				for(int j = 0;j < w;j++) {
					dataVideo = video.get(i, j);
					data = outputVideo.get(i, j);

					data[0] = Math.abs(dataVideo[0] - data[0]);
					data[1] = Math.abs(dataVideo[1] - data[1]);
					data[2] = Math.abs(dataVideo[2] - data[2]);

					morphVideo.put(i, j, data);	
				}
			}
			lablesNum = Imgproc.connectedComponentsWithStats(morphVideo, lableVideo, stat, centroid, 8, CvType.CV_32S);
			
			k = 0;
			for(int i = 0;i < lablesNum;i++) {
		        double[] area = stat.get(i, Imgproc.CC_STAT_AREA);
		        double[] left = stat.get(i, Imgproc.CC_STAT_LEFT);
		        double[] top = stat.get(i, Imgproc.CC_STAT_TOP);
		        double[] width = stat.get(i, Imgproc.CC_STAT_WIDTH);
		        double[] height = stat.get(i, Imgproc.CC_STAT_HEIGHT);

		        double[] x = centroid.get(i, 0);
		        double[] y = centroid.get(i, 1);
		        
		        if((width[0] < 20 && width[0] > 10) && (height[0] < 20 && height[0] > 10)) {
		        	if(k <= 4) {
			        	marker[k][0] = x[0];
			        	marker[k][1] = y[0];
			        	k++;
		        	}
		        	else k = 0;
		        	Imgproc.circle(video, new Point(x[0],y[0]), 1, new Scalar(0,255,0));
		        	Imgproc.rectangle(video, new Point(left[0], top[0]), new Point(left[0] + width[0], top[0] + height[0]), new Scalar(255,0,0), 1);
		        }
			}
			/*
			 * marker[0] -> 0,0
			 * marker[1] -> max,0
			 * marker[2] -> RCÄ« À§Ä¡
			 * marker[3] -> 0,max
			 * marker[4] -> max,max
			 */
			//System.out.println("RCÄ« ¿¢½º : " + (marker[2][0] - marker[0][0]));
			//System.out.println("RCÄ« ¿¢¸Æ : " + (marker[1][0] - marker[0][0]));
			//System.out.println("RCÄ« ¿ÍÀÌ : " + (marker[2][1] - marker[0][1]));
			//System.out.println("RCÄ« ¿Í¸Æ : " + (marker[3][1] - marker[0][1]));
			System.out.println("RCÄ« ÁÂÇ¥ : "
					 + Math.sqrt(Math.pow(marker[2][0] - marker[0][0], 2)) / ((marker[1][0] - marker[0][0]) / matrixXSize)
					 + ", "
					 + (marker[2][1] - marker[0][1]) / ((marker[3][1] - marker[0][1]) / matrixYSize));
			Arrays.fill(marker[0], 0);
			Arrays.fill(marker[1], 0);
			Arrays.fill(marker[2], 0);
			Arrays.fill(marker[3], 0);
			Arrays.fill(marker[4], 0);
			
			if(!video.empty() && !morphVideo.empty()) {
				colorFrame.render(video);
				byteFrame.render(morphVideo);
			}
			else
				System.out.println("no frame");
		}
	}
}