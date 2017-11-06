public class FindDFS {
	public int m, n;
	public int[][] path;
	public int[][] mark;
	public int[][] stack;
	public int[] save;
	public static int[][] move = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[][]input={
	            {1,1,1,1,1,1,1,1,1,1,1},
	            {1,0,1,0,0,0,0,0,0,0,1},
	            {1,0,1,0,1,0,1,1,1,0,1},
	            {1,0,1,0,1,0,1,0,0,0,1},
	            {1,0,1,0,1,0,1,0,1,0,1},
	            {1,0,1,0,1,0,1,0,1,0,1},
	            {1,0,1,0,1,0,1,0,1,1,1},
	            {1,0,1,0,1,0,1,0,0,0,1},
	            {1,0,1,1,1,0,1,0,1,0,1},
	            {1,1,0,0,0,0,1,0,1,1,1},
	            {1,1,1,1,1,1,1,1,1,1,1}
	        };
			FindDFS dfs = new FindDFS(input.length-2, input[0].length-2, input);
			dfs.path(input);
	}

	public FindDFS(int m, int n,int[][] path){
		this.m = m;
		this.n = n;
		this.path = path;
		mark = new int[m +2][n +2];
		/*
		for (int i = 0; i < m+2; i++) {
            for (int j = 0; j < n+2; j++) {
                mark[i][j] = 0;
                System.out.println("test"); 
                System.out.print(""+i+""+j); 
            }
        }*/
        //배열의 top pointer 용으로 쓸 행은 (m*2)(n*2)
        stack=new int[(m+2)*(n+2)][3];
	}
	
	public void path(int[][] path) {
		//this.path = path;
		//맨 처음엔 (1,1)부터 시작한다.
        mark[1][1] = 1;
        stack[0][0] = 1; //현재 위치 행
        stack[0][1] = 1; //현재 위치 열
        stack[0][2] = 0; //마지막 움직인 방향

        //i, j : 현재 쥐의 위치
        //g, h : 새로 이동할 위치(계산된 위치)
        int top=0, i, j, mov, g, h, check = 0;

        while(top >= 0) { 
        	//이 부분은 처음의 경우와 동서남북 찾아봐도 
        	//갈곳이 없는 경우 스택에서 이전위치를 꺼낼때 
        	//수행된다. 
        	i = stack[top][0]; 
        	j = stack[top][1]; 
        	mov = stack[top][2]; 
        	top--; 
        	//mov가 0에서7 즉 8방으로 새길을 찾음 
        	while(mov < 8) {
        		int a = 0;
        		while(mov < 8) {
        			g = i + move[mov][0]; //새로 이동할 곳 
            		h = j + move[mov][1]; //새로 이동할 곳 
            		if(path[g][h] == 0) {
    					save[a++] = path[g][h];
            		}
            		mov++;
        		}
        		g = i + move[mov][0]; //새로 이동할 곳 
        		h = j + move[mov][1]; //새로 이동할 곳 
        		
        		//미로 매열에서 0이며 이미 간적이 없는(mark[g][h]가 0)곳으로 새 좌표를 이동
                if(path[g][h] ==0 && mark[g][h] == 0) {
                    mark[g][h] = 1; //간곳이라고 표시
                    //스택에 현재 위치 및 이동한 방향을 저장
                    top++;
                    stack[top][0] = i;
                    stack[top][1] = j;
                    stack[top][2] = mov;
                    mov = -1; //while 문을 빠져나가지 않게 하기위해
                    i = g;
                    j = h;
                }
                
                if (mark[i+1][j] == 1 && mark[i+1][j] == 1 && mark[i+1][j] == 1 && mark[i+1][j] == 1 && mark[i+1][j] == 1 && mark[i+1][j] == 1 && mark[i+1][j] == 1 && mark[i+1][j] == 1) { //마지막 까지 온 경우 
        			for(int p=0;p<=top;p++) {
        				System.out.println("test1"); 
        				System.out.println("("+stack[p][0] + ","+stack[p][1]+")"); 
        			}
        			System.out.println("test2");
        			System.out.println("(" + i + "," + j + ")"); 
        			System.out.println("(" + m + "," + n + ")"); 
        			return; 
        		}
                
                mov++;
            } //inner while
        } // outer while
        System.out.println("no path...");
    } //end of path()
}