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
        //�迭�� top pointer ������ �� ���� (m*2)(n*2)
        stack=new int[(m+2)*(n+2)][3];
	}
	
	public void path(int[][] path) {
		//this.path = path;
		//�� ó���� (1,1)���� �����Ѵ�.
        mark[1][1] = 1;
        stack[0][0] = 1; //���� ��ġ ��
        stack[0][1] = 1; //���� ��ġ ��
        stack[0][2] = 0; //������ ������ ����

        //i, j : ���� ���� ��ġ
        //g, h : ���� �̵��� ��ġ(���� ��ġ)
        int top=0, i, j, mov, g, h, check = 0;

        while(top >= 0) { 
        	//�� �κ��� ó���� ���� �������� ã�ƺ��� 
        	//������ ���� ��� ���ÿ��� ������ġ�� ������ 
        	//����ȴ�. 
        	i = stack[top][0]; 
        	j = stack[top][1]; 
        	mov = stack[top][2]; 
        	top--; 
        	//mov�� 0����7 �� 8������ ������ ã�� 
        	while(mov < 8) {
        		int a = 0;
        		while(mov < 8) {
        			g = i + move[mov][0]; //���� �̵��� �� 
            		h = j + move[mov][1]; //���� �̵��� �� 
            		if(path[g][h] == 0) {
    					save[a++] = path[g][h];
            		}
            		mov++;
        		}
        		g = i + move[mov][0]; //���� �̵��� �� 
        		h = j + move[mov][1]; //���� �̵��� �� 
        		
        		//�̷� �ſ����� 0�̸� �̹� ������ ����(mark[g][h]�� 0)������ �� ��ǥ�� �̵�
                if(path[g][h] ==0 && mark[g][h] == 0) {
                    mark[g][h] = 1; //�����̶�� ǥ��
                    //���ÿ� ���� ��ġ �� �̵��� ������ ����
                    top++;
                    stack[top][0] = i;
                    stack[top][1] = j;
                    stack[top][2] = mov;
                    mov = -1; //while ���� ���������� �ʰ� �ϱ�����
                    i = g;
                    j = h;
                }
                
                if (mark[i+1][j] == 1 && mark[i+1][j] == 1 && mark[i+1][j] == 1 && mark[i+1][j] == 1 && mark[i+1][j] == 1 && mark[i+1][j] == 1 && mark[i+1][j] == 1 && mark[i+1][j] == 1) { //������ ���� �� ��� 
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