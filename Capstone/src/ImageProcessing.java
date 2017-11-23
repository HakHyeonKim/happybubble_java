import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
//import org.opencv.core.MatOfFloat;
//import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.Arrays;
//import java.util.ArrayList;
//import java.util.List;

//import javax.rmi.CORBA.Util;

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
   static int fix = 0;
   static int left_top = 0;
   static int right_top = 0;
   static int left_bottom = 0;
   static final int countlen = 5;
   static int size_set = 0;
   static int[] count = new int[countlen];// ÁÂÇ¥ Ã£Àº °Í
   static int[] count1 = new int[countlen];
   static int[] discord = new int[5];
   //static int ret = 0;
   static int lablesNum = 0;
   static int matrixXSize = 200;
   static int matrixYSize = 100;
   static double[] data = null;
   static double[] dataVideo = null;
   static double[][] marker = new double[6][2];
   static int[][] marker1 = new int[6][2];
   static int angle = 0;
   
   static VideoFrame colorFrame = new VideoFrame();
   static VideoFrame byteFrame = new VideoFrame();
   
   static {
      String opencvPath = "C:\\opencv\\build\\java\\x64\\";
      System.load( opencvPath + Core.NATIVE_LIBRARY_NAME + ".dll" );
   }
   
   public static void main( String[] args )
   {
      Mat mask = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3), new Point(1, 1));
      VideoCapture cap = new VideoCapture(2);
      
      if(!cap.isOpened())   System.exit(-1);
      
      colorFrame.setVisible(true);
      byteFrame.setVisible(true);

      while(true) {
    	  do {
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
    	  }while(lablesNum <= 7);
    	  
         Rect roi[] = new Rect[6];
         double[] range = new double[6];
         k = -1;
        
    	 for(int i = 0;i < lablesNum;i++) {
    		// double[] area = stat.get(i, Imgproc.CC_STAT_AREA);
    		 double[] left = stat.get(i, Imgproc.CC_STAT_LEFT);
    		 double[] top = stat.get(i, Imgproc.CC_STAT_TOP);
    		 double[] width = stat.get(i, Imgproc.CC_STAT_WIDTH);
    		 double[] height = stat.get(i, Imgproc.CC_STAT_HEIGHT);

    		 double[] x = centroid.get(i, 0);
    		 double[] y = centroid.get(i, 1);

    		 
    		 if((width[0] < 70 && width[0] > 25) && (height[0] < 70 && height[0] > 25)) {
    			 if(k <= 4	) {
    				k++;
    				marker[k][0] = x[0];
    				marker[k][1] = y[0];
                	range[0] = left[0];
                	range[1] = top[0];
                	range[2] = width[0];
                	range[3] = height[0];
                	roi[k] = new Rect(range);
    			 }
    			 else k = 0;
    			 Imgproc.circle(video, new Point(x[0],y[0]), 1, new Scalar(0,255,0));
    			 Imgproc.rectangle(video, new Point(left[0], top[0]), new Point(left[0] + width[0], top[0] + height[0]), new Scalar(255,0,0), 1);

    		 }
    	 }
    	 
    	 if(!video.empty() && !morphVideo.empty()) {
		     colorFrame.render(video);
		    // byteFrame.render(morphVideo);
		  }
		else
		     System.out.println("no frame"); 
		 
    	 if(size_set == 0) {
	    	 if (k == 2) {
		    	 for(int i = 0; i < 3; i++) {
		    		 int ret = 0;
		    		 String filename = "C:\\images\\" + i + ".jpg";
				     video.submat(roi[i]).copyTo(morphVideo);
				 	 Size sz = new Size(400, 400);
			    	 Imgproc.resize(morphVideo, morphVideo, sz);               	 
			    	 ret = compareFeature(morphVideo, filename);
			    	 
			    	 
			    	 
			    	 if(ret == 0) {
			    		 System.out.println("Two images are different." + "[" + i + "]" + " !!!!!!!!!!!!!!!!");
			    	 }else {
			    		 System.out.println("Two images are same." + "[" + i + "]" + " !!!!!!!!!!!!!!!!");
			    		 marker1[i][0] = (int)marker[i][0];
	    		 	 	 marker1[i][1] = (int)marker[i][1];
	    		 	 	 count[i] = 1;
			    	 }
			    	 if(!video.empty() && !morphVideo.empty()) {
					     colorFrame.render(video);
					    // byteFrame.render(morphVideo);
					  }
					else
					     System.out.println("no frame"); 
					 
		    	 }
	    	 }
	    	
			
			if(count[0] == 1 && count[1] == 1 && count[2] == 1) {
				if(marker1[0][0] > marker1[1][0]) {
					marker1[0][0] = (int)marker[1][0];
					marker1[0][1] = (int)marker[1][1];
					marker1[1][0] = (int)marker[0][0];
					marker1[1][1] = (int)marker[0][1];
				}
					
				 System.out.println("Left Top : " + marker1[0][0] + " , " + marker1[0][1]);
				 System.out.println("Right Top : " + marker1[1][0] + " , " + marker1[1][1]);
				 System.out.println("Left Bot : " + marker1[2][0] + " , " + marker1[2][1]);
				 size_set = 1;
			}else {
				 Arrays.fill(marker1[0], 0);
				 Arrays.fill(marker1[1], 0);
				 Arrays.fill(marker1[2], 0);
				 Arrays.fill(count, 0);
			}
    	 }
    	 
    	 Arrays.fill(count, 0);
    	 
    	 if(size_set == 1 && k == 4) {
    		 {  
    			 
    			 int ret = 0;
    			 int head_set = 0;
    			 String filename = "C:\\images\\" + 4 + ".jpg";
	    		 
    			 for(int j = 2; j <= 3; j++) {
	    			 video.submat(roi[j]).copyTo(morphVideo);
				 	 Size sz = new Size(400, 400);
			    	 Imgproc.resize(morphVideo, morphVideo, sz);               	 
			    	 ret = compareFeature(morphVideo, filename);
			    	 if(ret == 0) {
			    		 System.out.println("Two images are different." + "[" + j + "]" + " !!!!!!!!!!!!!!!!");
			    	 }else {
			    		 System.out.println("Two images are same." + "[" + j + "]" + " !!!!!!!!!!!!!!!!");
			    		 marker1[3][0] = (int)marker[j][0];
	    		 	 	 marker1[3][1] = (int)marker[j][1];
	    		 	 	 if(j == 2) {
	    		 	 		marker1[4][0] = (int)marker[3][0];
	    		 	 		marker1[4][1] = (int)marker[3][1];
	    		 	 	 } else {
	    		 	 		marker1[4][0] = (int)marker[2][0];
	    		 	 		marker1[4][1] = (int)marker[2][1];
	    		 	 	 }
	    		 	 	 head_set++;
	    		 	 	 ret = 0;
			    	 }
    			 }
    			 
	    		 if(!video.empty() && !morphVideo.empty()) {
				     colorFrame.render(video);
//				     byteFrame.render(morphVideo);
				  }
				else
				     System.out.println("no frame"); 
				 
	    		if(head_set == 1) {
	    		 	 marker1[5][0] = (int)((marker1[3][0] + marker1[4][0])/2); // marker1[5] = car's center
	    			 marker1[5][1] = (int)((marker1[3][1] + marker1[4][1])/2);
	    			 angle = (int)getAngle(marker1);
	    			 System.out.println("Head : " + marker1[3][0] + " , " + marker1[3][1]);
					 System.out.println("Tail : " + marker1[4][0] + " , " + marker1[4][1]);
					 System.out.println("Center : " + marker1[5][0] + " , " + marker1[5][1]);
					 System.out.println("Angle : " + angle);
	    		 } else {
	    			 Arrays.fill(marker1[3], 0);
					 Arrays.fill(marker1[4], 0);
					 Arrays.fill(count, 0);
	    		 }
	    	 }
    	 }
  /*
        	 try {
        	 Thread.sleep(100);
        	 }catch(InterruptedException e) {
        		 System.out.println(e.getMessage());
        	 }
   
  */
      }
      
   }
   
   public static double getAngle(int marker1[][]) {
	   int dx = marker1[3][0] - marker1[4][0];
	   int dy = marker1[3][1] - marker1[4][1];
	   
	   double rad = Math.atan2(dx,dy);
	   double degree = (rad*180)/Math.PI;
	   
	   if(degree >= 90) {
		   degree = degree - 90;
	   } else if (degree >= 0 && degree < 90) {
		   degree = degree + 270;
	   } else {
		   degree = degree + 270;
	   }
	   
	   return degree;
   }
   
   public static int compareFeature(Mat video, String filename) {
	   int retVal = 0;
	   long startTime = System.currentTimeMillis();
	   
	   //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	   
	   // Load images to compare
	   Mat img1 = video;
	   //Mat img1 = Imgcodecs.imread(video, Imgcodecs.CV_LOAD_IMAGE_COLOR);
	   Mat img2 = Imgcodecs.imread(filename, Imgcodecs.CV_LOAD_IMAGE_COLOR);
	/*   
       if(!img2.empty() && !img1.empty()) {
          colorFrame.render(img1);
          byteFrame.render(img2);
       }
*/     
       
	   // Declare key point of images
	   MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
	   MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
	   Mat descriptors1 = new Mat();
	   Mat descriptors2 = new Mat();
	   
	   // Definition of ORB key point detector and descriptor extractors.
	   FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
	   DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
	   
	   // Detect key points
	   detector.detect(img1, keypoints1);
	   detector.detect(img2, keypoints2);
	   
	   // Extract descriptors
	   extractor.compute(img1, keypoints1, descriptors1);
	   extractor.compute(img2, keypoints2, descriptors2);
	   
	   // Definition of descriptor matcher
	   DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
	  
	   
	   // Match points of two images
	   MatOfDMatch matches = new MatOfDMatch();
	    //System.out.println("Type of Image1= " + descriptors1.type() + ", Type of Image2= " + descriptors2.type());
	    //System.out.println("Cols of Image1= " + descriptors1.cols() + ", Cols of Image2= " + descriptors2.cols());
	   
	   // Avoid to assertion failed
	   // Assertion failed (type == src2.type() && src1.cols == src2.cols && (type == CV_32F || type == CV_8U)
	   if (descriptors2.cols() == descriptors1.cols()) {
		   matcher.match(descriptors1, descriptors2, matches);
		   
		   // Check matches of key points
		   DMatch[] match = matches.toArray();
		   double max_dist = 0; double min_dist = 100;
		   
		   for(int i = 0; i < descriptors1.rows(); i++) {
			   double dist = match[i].distance;
			   if(dist < min_dist) min_dist = dist;
			   if(dist > max_dist) max_dist = dist;
		   }
		  // System.out.println("max_dist=" + max_dist + ", min_dist" + min_dist);
		   
		   // Extract good images (distances are under 10)
		   for(int i= 0; i < descriptors1.rows(); i++) {
			   if (match[i].distance <= 15) {
				   retVal++;
			   }
		   }
		   // System.out.println("matching count+" + retVal);
	   }
	   
	   //long estimatedTime = System.currentTimeMillis() - startTime;
	   //System.out.println("estimatedTime=" + estimatedTime + "ms");
	   
	   return retVal;
   }
	   
   }