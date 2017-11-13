import java.util.*;
 
public class CalDistance {
    public static void main(String[] args) throws Exception {
         
        // Load your own data for testing 
    	P startP = new P(3, 3);
        P[] points = new P[] { new P(8, 7), new P(6, 7), new P(5, 7),new P(10, -4),
                new P(13, 9), new P(15, -5), new P(17, 7), new P(19, 10), 
                new P(22, 7), new P(25, 10), new P(29, 14), new P(30, 2) };
         double min = 0;
         P minP = startP;
      	  System.out.println("시작 좌표값:" + startP);
          System.out.println("가장 가까운 좌표값:" + points[1]);
          
          for(int i=0; i< points.length; i++){
        	  double dist = distance(startP,points[i]);
        	  System.out.println("dist: "+ dist);
        	  if(i == 0 || min > dist){
        		  min = dist;
        		  minP = points[i];
        	  }
      		}
    	  System.out.println("최소  dist: "+ min + "  " + "point : " + minP);
    	  double dx = minP.x - startP.x;
    	  double dy = minP.y - startP.y;
    	  double r = Math.toDegrees(Math.atan2(dy, dx));
    	  System.out.println("각도: "+ r);
        } 
    static double distance(P p1, P p2) {
        return p1.distance(p2); // Java api, Euclidean dist
    }
 
}