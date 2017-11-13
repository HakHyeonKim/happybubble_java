import java.util.HashSet;
import java.util.Stack;
 
public class DFS {
    public static int[][] map = null;
    public static int mapSize = 8;
    public static void main(String[] args) {
        // 트리의 인접정보
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
        
        // 현재노드
        int curNode = 0;
        
        // 방문노드 저장
        HashSet<Integer> visitedSet = new HashSet<Integer>();
        
        // DFS 탐색을 위해 Stack을 사용함.
        Stack<Integer> stack = new Stack<Integer>();
        
        // 0을 시작노드로 설정
        stack.push(curNode);
        
        while(!stack.isEmpty()) {
            curNode = (int) stack.pop();
            
            // 방문한 노드이며 skip
            if(visitedSet.contains(curNode) ) {
                continue;
            }
            
            // 현재 노드를 방문했다고 설정함.
            visitedSet.add(curNode);
            
            // 방문한 노드를 출력
            System.out.print(curNode + " ");
            
            // 현재노드와 인접한 노드를 Queue에 저장함.
            for (int i = mapSize - 1; i >= 0; i--) {
                if(map[curNode][i] == 1) {
                    stack.push(i);
                }
            }
        }
    }
}