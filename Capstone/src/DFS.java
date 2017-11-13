import java.util.HashSet;
import java.util.Stack;
 
public class DFS {
    public static int[][] map = null;
    public static int mapSize = 8;
    public static void main(String[] args) {
        // Ʈ���� ��������
        map = new int[][] {
            // col  :    0  1  2  3  4  5  6  7 
            /*row : 0*/    {0, 1, 1, 0, 0, 0, 0, 0},
            /*row : 1*/    {1, 0, 0, 1, 1, 0, 0, 0},
            /*row : 2*/    {1, 0, 0, 0, 0, 1, 0, 0},
            /*row : 3*/    {0, 1, 0, 0, 0, 0, 1, 1},
            /*row : 4*/    {0, 1, 0, 0, 0, 0, 0, 0},
            /*row : 5*/    {0, 0, 1, 0, 0, 0, 0, 0},
            /*row : 6*/    {0, 0, 0, 1, 0, 0, 0, 0},
            /*row : 7*/    {0, 0, 0, 1, 0, 0, 0, 0}
            };
        
        long begin = System.currentTimeMillis();
            
        dfs();
 
        long end = System.currentTimeMillis();
        System.out.printf("\n%.3f (secs)\n", (end-begin)/1000.0);
    }
 
    private static void dfs() {
        
        // ������
        int curNode = 0;
        
        // �湮��� ����
        HashSet<Integer> visitedSet = new HashSet<Integer>();
        
        // DFS Ž���� ���� Stack�� �����.
        Stack<Integer> stack = new Stack<Integer>();
        
        // 0�� ���۳��� ����
        stack.push(curNode);
        
        while(!stack.isEmpty()) {
            curNode = (int) stack.pop();
            
            // �湮�� ����̸� skip
            if(visitedSet.contains(curNode) ) {
                continue;
            }
            
            // ���� ��带 �湮�ߴٰ� ������.
            visitedSet.add(curNode);
            
            // �湮�� ��带 ���
            System.out.print(curNode + " ");
            
            // ������� ������ ��带 Queue�� ������.
            for (int i = mapSize - 1; i >= 0; i--) {
                if(map[curNode][i] == 1) {
                    stack.push(i);
                }
            }
        }
    }
}