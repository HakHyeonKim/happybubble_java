public class Algoritm {
	public static int m, n;
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
            {1,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,0,1,1,1,1,1},
            {1,1,1,1,1,0,1,1,1,1,1},
            {1,1,1,1,1,0,1,1,1,1,1},
            {1,1,1,1,1,0,1,1,1,1,1},
            {1,1,1,1,1,0,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,0,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1}
        };
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int count = 0;
	
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
                	count++;
                }
            }
        }
		
		if(count == 0) {
		    System.out.println("더이상 길이 존재하지 않습니다.");
		    
			return;
		}
		else if(count != 0) {
			System.out.println("떨어져 있는 다른 길이 존재합니다.");
			
			return;
		}
	}
	
	public static void DirectionCheck(int col, int row, int direct) {
		double newdegree, movedegree;
		
		g = col + move[direct][0];
		h = row + move[direct][1];
		
		System.out.println("("+i+","+j+") > ("+g+","+h+")");
	
		newdegree = getAngle(i,j,g,h);
		
		movedegree = newdegree - predegree;
		
		predegree = newdegree;
		
		System.out.println(Math.round(movedegree)+"도");
		
		i = g;
		j = h;
		
		for(int k = 0;k < input.length;k++) {
			for(int m = 0;m < input[0].length;m++)
				System.out.print(input[k][m] + " ");
			System.out.println("");
		}
		System.out.println("");
	}
	
	public static void MovePath(int col, int row){
		int movedirect = 0;

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
	}
	
	private static double getAngle(int x1,int y1, int x2,int y2){
		double dx = x2 - x1;
		double dy = y2 - y1;
		
		double rad= Math.atan2(dx, dy);
		double degree = (rad*180)/Math.PI ;
		   
		return degree;
		}
}