public class Algorithm_random {
	public static double[][][] send;
	public static int se1 = 0, se2 = 0, se3 = 0;
	public static P nextP;
   public static int m, n, count = 0;
   public static int[][] mark;
   public static int[][] stack;
   public int[] save;
   public static double predegree = 0, movedegree;
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
            {1,1,1,1,1,1,1,1,1,1,1,},
            {1,1,0,0,0,0,1,1,1,0,1},
            {1,1,1,1,1,0,1,1,1,0,1},
            {1,1,1,1,1,0,1,1,1,0,1},
            {1,1,1,1,1,0,1,1,0,1,1},
            {1,1,1,1,1,0,1,0,1,1,1},
            {1,1,1,1,1,0,0,1,1,1,1},
            {1,1,1,1,1,1,1,1,0,1,1},
            {1,1,1,1,1,1,1,1,0,0,1},
            {1,1,0,0,0,1,1,1,0,1,1},
            {1,1,1,1,1,1,1,1,1,1,1}
        };
   
   public static void main(String[] args) {
      // TODO Auto-generated method stub
   
      m = input.length;//행의 길이
      n = input[0].length;//열의 길이
   
      //배열의 top pointer 용으로 쓸 행은 (m*2)(n*2)
        stack=new int[(m+2)*(n+2)][3];
        
        i = stack[0][0] = 1; //현재 위치 행
        j = stack[0][1] = 5; //현재 위치 열
        stack[0][2] = 0; //마지막 움직인 방향
        mov = 0;
        
      MovePath(i, j);
      
      count = countloop();
      
      while(count != 0) {
    	  System.out.println("떨어져 있는 길이 존재합니다.");
    	  minDistance();
    	  
    	  count = countloop();
      }
      
      System.out.println("최종");
      for(int k = 0;k < input.length;k++) {
          for(int m = 0;m < input[0].length;m++)
             System.out.print(input[k][m] + " ");
          System.out.println("");
       }
       System.out.println("");
   }
   
   public static int countloop() {
	   int count = 0;
	   
	   for(int a = 1; a < input.length-1; a++) {
		   for(int b = 1; b < input[0].length-1; b++) {
			   if(input[a][b] == 0)
				   count++;
		   }
	   }
	   return count;
   }
   
   public static void minDistance(){
	   P StartP = new P(g, h);
	   System.out.println("시작 좌표값:(" + (int)StartP.x+","+ (int)StartP.y+")");
	   double min = 1000;

       for(int a = 0; a < input.length; a++){
    	   for(int b = 0; b < input[0].length; b++) {
    		   if(input[a][b] == 0) {
    			   P minP = new P(a,b);
    			   double dist = distance(StartP, minP);
    			   if(min > dist) {
    				   min = dist;
    				   nextP = minP;
    			   }
    		   }
    	   }
       }
       double getangle = getangle = getAngle((int)StartP.x, (int)StartP.y, (int)nextP.x, (int)nextP.y);
       System.out.println("point : (" + (int)nextP.x + "," + (int)nextP.y +")");
       System.out.println("최소  dist: "+ Math.round(min) + "   각도 : "+ getangle + "도");
       System.out.println("펜을 들고("+(int)StartP.x+","+(int)StartP.y+") > ("+(int)nextP.x+","+(int)nextP.y+")로 이동");
       System.out.println("");
       i = (int)nextP.x;
       j = (int)nextP.y;
       MovePath(i, j);
   }
   
    static double distance(P p1, P p2) {
        return p1.distance(p2);
    }
    
   public static void DirectionCheck(int col, int row, int direct, int c) {
      double newdegree;
      
      if(c == 1) {
    	  g = col;
    	  h = row;
    	  i = g;
    	  j = h;
      }

      else {
    	  g = col + move[direct][0];
          h = row + move[direct][1];
          
          P q = new P(i,j);
          P w = new P(g,h);
          double di = distance(q,w);
          
          if(di > Math.sqrt(2)) {
        	  System.out.println("펜을 들고("+i+","+j+") > ("+g+","+h+")로 이동");
        	  System.out.println("");
          }
         
	      System.out.println("("+i+","+j+") > ("+g+","+h+")");
	      
	      P startP = new P(i, j);
	      P points = new P(g, h);
	         
	      newdegree = getAngle(i,j,g,h);
	      
	      System.out.println(newdegree+"도");
	      
	      i = g;
	      j = h;
	      
	      
	      for(int k = 0;k < input.length;k++) {
	         for(int m = 0;m < input[0].length;m++)
	            System.out.print(input[k][m] + " ");
	         System.out.println("");
	      }
	      System.out.println("");
         
      }
   }
   
   public static void MovePath(int col, int row){
      int movedirect = 0;
      
      if(input[col][row] == 0 && input[col+1][row] == 1 && input[col][row+1] == 1
    		  && input[col+1][row+1] == 1 && input[col-1][row] == 1 && input[col][row-1] == 1
    		  && input[col-1][row-1] == 1 && input[col+1][row-1] == 1 && input[col-1][row+1] == 1) {
    	  
    	  input[i][j] = 1;
          top++;
          stack[top][0] = i;
          stack[top][1] = j;
          stack[top][2] = movedirect;
          
          mov = 0;
          
    	  DirectionCheck(col, row, movedirect, 1);
    	  MovePath(i,j);
      }

      input[i][j] = 1;
      top++;
      stack[top][0] = i;
      stack[top][1] = j;
      stack[top][2] = movedirect;
      
      mov = 0;
            
      if( input[col+1][row] == 0){
         //System.out.println("+0");
         DirectionCheck(col, row, movedirect, 0);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col][row+1] == 0){
         //System.out.println("0+");
         DirectionCheck(col, row, movedirect, 0);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col-1][row] == 0){
         //System.out.println("-0");
         DirectionCheck(col, row, movedirect, 0);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col][row-1] == 0){
         //System.out.println("0-");
         DirectionCheck(col, row, movedirect, 0);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col+1][row+1] == 0){
         //System.out.println("++");
         DirectionCheck(col, row, movedirect, 0);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col+1][row-1] == 0){
         //System.out.println("-+");
         DirectionCheck(col, row, movedirect, 0);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col-1][row-1] == 0){
         //System.out.println("--");
         DirectionCheck(col, row, movedirect, 0);
         MovePath(i, j);
      }
      movedirect++;
      if( input[col-1][row+1] == 0){
         //System.out.println("+-");
         DirectionCheck(col, row, movedirect, 0);
         MovePath(i, j);
      }
   }
   
   private static double getAngle(int x1,int y1, int x2,int y2){
      double dx = x2 - x1;
      double dy = y2 - y1;
      
      double rad= Math.atan2(dx, dy);
      double degree = (rad*180)/Math.PI ;
         
      movedegree = degree - predegree;
      
      predegree = degree;
      
      return Math.round(movedegree);
   }
}