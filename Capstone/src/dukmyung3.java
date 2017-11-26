

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


import java.text.NumberFormat;

public class dukmyung3 {
   public static ArrayList<String> mArrayList = new ArrayList<String>();
   public static int m, n;
   public static int pen = 0;
   public static int tempDist = 0;
   public static int[][] mark;
   public static int[][] stack;
   public int[] save;
   public static double predegree = 0;
   public static int[][] move = {
         {1, 0}
         , {0, 1}
         , {-1, 0}
         , {0, -1}
         , {1, 1}
         , {1, -1}
         , {-1, -1}
         , {-1, 1}};
   public static int top = 0, i, j, g, h, mov;
   public static int[][] input={
            {1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,0,0,0,0,1,1,1},
            {1,1,1,0,1,1,1,1,0,1,1},
            {1,1,0,1,1,1,1,1,1,0,1},
            {1,1,0,1,1,1,1,1,1,0,1},
            {1,1,0,1,1,1,1,1,1,0,1},
            {1,1,0,1,1,1,1,1,1,0,1},
            {1,1,0,1,1,1,1,1,1,0,1},
            {1,1,1,0,1,1,1,1,0,1,1},
            {1,1,1,1,0,0,0,0,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1},
        };
   
   public static void main(String[] args) {
      
        
      // TODO Auto-generated method stub

   
      m = input.length;//행의 길이
      n = input[0].length;//열의 길이
   
      //배열의 top pointer 용으로 쓸 행은 (m*2)(n*2)
        stack=new int[(m+2)*(n+2)][3];
        
        i = stack[0][0] = 1; //현재 위치 행
        j = stack[0][1] = 1; //현재 위치 열
        stack[0][2] = 0; //마지막 움직인 방향
        mov = 0;
        
      MovePath(i, j);

      
      for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                if(input[i][j] == 0) {
                   System.out.println("떨어져 있는 다른 길이 존재합니다.");
                 minDistance();
                }
            }
      }

	    // 길 다찾고 최종 경로 출력 부분 각도가 +일때 오른쪽으로 회전 -일때 왼쪽으로 ,, 회전 펜 up은 1 down은 0
	 System.out.println("*Start*");
	 	mArrayList.add("*End*");
     for(int i = 0; i < mArrayList.size(); i++) {
         int count = 0;
         while(mArrayList.get(i).equals("W1-")) {
        	  mArrayList.remove(i);
             count++;
             if(!mArrayList.get(i).equals("W1-")){
            	 mArrayList.add(i, "Z0-"+ "W" + count +'-'); 
             }
         }
     }

   	 String LastOutput = mArrayList.toString();
   	 String LastOutput2;
     LastOutput2 = LastOutput.replace("[", "");
     LastOutput2 = LastOutput2.replace("]", "");
     LastOutput2 = LastOutput2.replace(", ", "");
   	 
   	 String date[] = LastOutput2.split("-");
     
     for(int i=0 ; i< date.length ; i++)
     {		 
	    	 	System.out.println(date[i]);
     }
   }
  
   
   public static void minDistance(){
      ArrayList<P> points = new ArrayList<P>();
      P[] temp = {new P(1, 1), new P(1, 4), new P(1, 9), new P(4, 1), new P(4, 4), new P(4, 9), new P(1, 7), new P(4, 7), new P(9, 7)};
      P StartP = new P(i,j);
      System.out.println("시작 좌표값:" + StartP);
      double min = 0;
      
        P minP = StartP;
        
        for (int i = 0; i < temp.length; i++) points.add(temp[i]);
        System.out.println(points);
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
            	P awayPath = new P(i, j);
                if(input[i][j] == 1) {
                   points.remove(awayPath);
                }
            }
        }
         System.out.println(points);
            for(int j=0; j< points.size(); j++){
            double dist = distance(StartP,points.get(j));
            System.out.println("dist: "+ dist);
            if(j == 0 || min > dist){
               min = dist;
               minP = points.get(j);
           }
       }
        System.out.println("최소  dist: "+ min + "  " + "point : " + minP);
        System.out.println("");
        System.out.println("펜을 들고("+(int)StartP.x+","+(int)StartP.y+") > ("+(int)minP.x+","+(int)minP.y+")로 이동");
        double Movedegree = getAngle((int)StartP.x,(int)StartP.y,(int)minP.x, (int)minP.y);
        System.out.println("거라: " + (int)min + "  각도: " + (int)Movedegree);
        pen = 1;
        arrayOutput((int)min, (int)Movedegree, pen);
        pen = 0;
        System.out.println();
      
        i = (int)minP.x;
        j = (int)minP.y;
        MovePath(i, j);
   }
   
    static double distance(P p1, P p2) {
        return p1.distance(p2); // Java api, Euclidean dist
    }
    
   public static void DirectionCheck(int col, int row, int direct) {
      double degree;

      g = col + move[direct][0];
      h = row + move[direct][1];
      
      System.out.println("("+i+","+j+") > ("+g+","+h+")");
      
      P startP = new P(i, j);
      P points = new P(g, h);
      double dist = distance(startP,points);
      System.out.println("dist:" + dist);
 
      degree = getAngle(i,j,g,h);
           
      System.out.println(degree+"도");
      
      int distanc = (int)dist;
      int degrees = (int)degree;  
      
      arrayOutput(distanc, degrees, pen);
      
      i = g;
      j = h;
      
      for(int k = 0;k < input.length;k++) {
         for(int m = 0;m < input[0].length;m++)
            System.out.print(input[k][m] + " ");
         System.out.println("");
      }    

      System.out.println("");
   }
   
   public static void arrayOutput(int dist, int degree, int pen){
	      
	      if(degree < 0){
	      mArrayList.add("Z" + String.valueOf(pen)+"-");
	      mArrayList.add("L" + String.valueOf(Math.abs(degree))+"-"); 
	      mArrayList.add("W" + String.valueOf(dist)+"-");
	      }
	      if(degree > 0){
	      mArrayList.add("Z" + String.valueOf(pen)+"-");
	      mArrayList.add("R" + String.valueOf(degree)+"-"); 
	      mArrayList.add("W" + String.valueOf(dist)+"-");
	      }
	
	      if(degree == 0){
	    	  mArrayList.add("W" + String.valueOf(dist)+"-");
	      }
	      
	      System.out.println(mArrayList);
	      }
   
   
   public static void MovePath(int col, int row){
      int movedirect = 0;
      pen = 0;
      input[i][j] = 1;
      top++;
      stack[top][0] = i;
      stack[top][1] = j;
      stack[top][2] = movedirect;
      
      mov = 0;
            
      if( input[col+1][row] == 0){
         //System.out.println("+0");
         DirectionCheck(col, row, movedirect);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col][row+1] == 0){
         //System.out.println("0+");
         DirectionCheck(col, row, movedirect);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col-1][row] == 0){
         //System.out.println("-0");
         DirectionCheck(col, row, movedirect);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col][row-1] == 0){
         //System.out.println("0-");
         DirectionCheck(col, row, movedirect);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col+1][row+1] == 0){
         //System.out.println("++");
         DirectionCheck(col, row, movedirect);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col+1][row-1] == 0){
         //System.out.println("-+");
         DirectionCheck(col, row, movedirect);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col-1][row-1] == 0){
         //System.out.println("--");
         DirectionCheck(col, row, movedirect);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col-1][row+1] == 0){
         //System.out.println("+-");
         DirectionCheck(col, row, movedirect);
         MovePath(i, j);
      }
      pen = 1;
   }
   
   private static double getAngle(int x1,int y1, int x2,int y2){

	  double dx = x2 - x1;
      double dy = y2 - y1;
      
      double rad= Math.atan2(dx, dy);   
      double degree = (rad*180)/Math.PI ;
      
      double Movedegree = degree - predegree;
      
      predegree = degree;
      
      if(Math.abs(Movedegree) > 180){
    	  Movedegree = 360 - Math.abs(Movedegree);
      }
      
      return Movedegree;
      }
}