# DFS、BFS

## 图

### 有向无权图：邻接矩阵

```java
public class Graph {
    
    private int v;
    
    private boolean[][] matrix;
    
    public Graph(int v) {
        this.v = v;
        this.matrix = new boolean[v][v];
    }
    
    public void addEdge(int s, int t) {
        matrix[s][t] = true;
    }
    
}
```

### 无向无权图：邻接表

```java
public class Graph {
    
    private int v;
    
    /**
     * 邻接表，adjace 邻接的意思
     */
    private LinkedList<Integer> adj[];
    
    public Graph(int v) {
        this.v = v;
        this.adj = new LinkedList[v];
        for(int i; i < v; i++) {
            adj[i] = new LinkedList<>();
        }
    }
    
    public void addEdge(int s, int t) {
        adj[s].add(t);
    }
    
}
```

## 图的搜索 or 遍历

- DFS
- BFS



最短路径

- Dijkstra
- Bellman-Ford
- Floyd
- A*算法

最小生成树

- Prim 算法
- Kruskal 算法

最大流、二分匹配

- Ford-Fulkerson
- Edmonds-Karp

## 代码实现

### BFS

```java
/**
 * 求 s 到 t 是否有路
 */
public boolean bfsSimple(int s, int t) {
    boolean[] visited.= new boolean[v];
    Queue<Integer> queue = new LinkedList<>();
    queue.add(s);
    visited[s] = true;
    while(!queue.isEmpty()) {
        int p = queue.poll();
        if (p == t) {
            // 找到了
            return true;
        }
        for (int i = 0; i < adj[p].size(); i++) {
            int q = adj[p].get(i);
            if (!visited[q]) {
                visited[q] = true;
                queue.add(q);
            }
        }
    }
    return false;
}
```



打印从 s 到 t 的路径

```java
public void bfs (int s, int t) {
    boolean visited = new boolean[v];
    Queue<Integer> queue = new LinkedList<>();
    queue.add(s);
    visited[s] = true;
    int[] prev = new int[v];
    // 初始化
    for (int i = 0; i < v; ++i) {
        prev[i] = -1;
    }
    while(queue.size() != 0) {
        int p = queue.poll();
        if (p == t) {
            print(prev, s, t);
            return;
        }
        for (int i = 0; i < adj[p].size(); i++) {
            int q = adj[p].get(i);
            if (!visited[q]) {
                prev[q] = p;
                visited[q] = true;
                quque.add(q);
            }
        }
    }
    
    private void print(int[] prev, int s, int t) {
        if (prev[t] != -1 && t != s) {
            print(prev, s, prev[t]);
        }
        System.out.print(t + " ");
    }
    
}
```

### DFS

```java
private boolean found = false;;

private boolean[] visited = new boolean[v];

public boolean dfsSimple(int s, int t) {
    dsfSimpleHelp(r, t);
    return found;
}

private void dsfSimpleHelp(int s, int t) {
    if (found) {
        return;
    }
    visited[s] = true;
    if (s == t) {
        found = true;
        return;
    }
    for (int i = 0; i < adj[s].size(); i++) {
        int q = adj[s].get(i);
        if (!visited[q]) {
            dsfSimpleHelp(q, t);
        }
    }
}

```

支持打印出 s 到 t 到路径

```java
private boolean[] visited = new boolean[v];

private List<Integer> resultPath = new ArrayList<>();

public List<Integer> dfs(int s, int t) {
    dfsHelp(s, t, new ArrayList<>());
    return reusltPath;
}

public void dfsHelp(int s, int t, List<Integer> path) {
    if (s == t) {
        resultPath = new ArrayList<>(path);
        return;
    }
   	visited[s] = true;
    path.add(s);
    for (int i = 0; i < adj[s].size(); i++) {
        int q = adj[s].get(i);
        if (!visited[q]) {
            dfsHelp(q, t, path);
        }
    }
    path.remove(path.size() - 1);
}
```

# 题型讲解

1. 二维矩阵搜索或遍历
2. 最短路径（BFS）
3. 连通分量/连通性
4. 拓扑排序
5. 检测环

# [机器人的运动范围](https://leetcode.cn/problems/ji-qi-ren-de-yun-dong-fan-wei-lcof/)

地上有一个m行n列的方格，从坐标 [0,0] 到坐标 [m-1,n-1] 。一个机器人从坐标 [0, 0] 的格子开始移动，它每次可以向左、右、上、下移动一格（不能移动到方格外），也不能进入行坐标和列坐标的数位之和大于k的格子。例如，当k为18时，机器人能够进入方格 [35, 37] ，因为3+5+3+7=18。但它不能进入方格 [35, 38]，因为3+5+3+8=19。请问该机器人能够到达多少个格子？

**示例 1：**

```
输入：m = 2, n = 3, k = 1
输出：3
```

**示例 2：**

```
输入：m = 3, n = 1, k = 0
输出：1
```

**提示：**

- `1 <= n,m <= 100`
- `0 <= k <= 20`

```java
class Solution {

    private boolean[][] visited;

    private int count;


    public int movingCount(int m, int n, int k) {
        visited = new boolean[m][n];
        dfs(0, 0, m, n, k);
        return count;
    }

    public void dfs(int i, int j, int m, int n, int k) {
        visited[i][j] = true;
        count++;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int di = 0; di < 4; di++) {
            int newi = i + directions[di][0];
            int newj = j + directions[di][1];
            if (newi >= m || newi < 0 || newj >= n || newj <0 || visited[newi][newj]
              || check(newi, newj, k) == false) {
                continue;
            }
            dfs(newi, newj, m, n, k);
        }
    }

    public boolean check(int i, int j, int k) {
        int sum = 0;
        while(i != 0) {
            sum += (i % 10);
            i /= 10;
        }
        while(j != 0) {
            sum += (j % 10);
            j /= 10;
        }
        return sum <= k;
    }

}
```

# [752. 打开转盘锁](https://leetcode.cn/problems/open-the-lock/)

你有一个带有四个圆形拨轮的转盘锁。每个拨轮都有10个数字： '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' 。每个拨轮可以自由旋转：例如把 '9' 变为 '0'，'0' 变为 '9' 。每次旋转都只能旋转一个拨轮的一位数字。

锁的初始数字为 '0000' ，一个代表四个拨轮的数字的字符串。

列表 deadends 包含了一组死亡数字，一旦拨轮的数字和列表里的任何一个元素相同，这个锁将会被永久锁定，无法再被旋转。

字符串 target 代表可以解锁的数字，你需要给出解锁需要的最小旋转次数，如果无论如何不能解锁，返回 -1 。

 示例 1:

```
输入：deadends = ["0201","0101","0102","1212","2002"], target = "0202"
输出：6
解释：
可能的移动序列为 "0000" -> "1000" -> "1100" -> "1200" -> "1201" -> "1202" -> "0202"。
注意 "0000" -> "0001" -> "0002" -> "0102" -> "0202" 这样的序列是不能解锁的，
因为当拨动到 "0102" 时这个锁就会被锁定。
```


示例 2:

```
输入: deadends = ["8888"], target = "0009"
输出：1
解释：把最后一位反向旋转一次即可 "0000" -> "0009"。
```


示例 3:

```
输入: deadends = ["8887","8889","8878","8898","8788","8988","7888","9888"], target = "8888"
输出：-1
解释：无法旋转到目标数字且不被锁定。
```

提示：

1 <= deadends.length <= 500
deadends[i].length == 4
target.length == 4
target 不在 deadends 之中
target 和 deadends[i] 仅由若干位数字组成

```java
class Solution {
        
    public int openLock(String[] deadends, String target) {
		Set<String> deadSet = new HashSet<>();
        for (String d : deadends) {
            deadSet.add(d);
        }
        if (deadSet.contains("0000")) {
            return -1;
        }
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.offer("0000");
        visited.add("0000");
		int depth = 0;
        while(!queue.isEmpty()) {
            int size = queue.size();
            int k = 0;
            while (k < size) {
                String node = queue.poll();
                k++;
                if (node.equals(target)) {
                    return depth;
                }
                List<String> newNodes = genNewNode(node);
                for (String newNode : newNodes) {
                    if (visited.contains(newNode) || deadSet.contains(newNode)) {
                        continue;
                    }
                    queue.add(newNode);
                    visited.add(newNode);
                }
            }
            depth++;
        }
        return -1;
    }

    public List<String> genNewNode(String node) {
        List<String> result = new ArrayList<>();
        int[] change = {-1, 1};
        for (int i = 0; i < 4 ; i++) {
            for (int k = 0; k < 2; k++) {
                char[] newNode = new char[4];
                for (int j = 0; j < i; j++) {
                    newNode[j] = node.charAt(j);
                }
                for (int j = i + 1; j < 4; j++) {
                    newNode[j] = node.charAt(j);
                }
                String newChar = (((node.charAt(i) - '0') + change[k] + 10) % 10) + "";
                newNode[i] = newChar.charAt(0);
                result.add(new String(newNode));
            }
        }
        return result;
    }
    
}
```



# [200. 岛屿数量](https://leetcode.cn/problems/number-of-islands/)

给你一个由 '1'（陆地）和 '0'（水）组成的的二维网格，请你计算网格中岛屿的数量。

岛屿总是被水包围，并且每座岛屿只能由水平方向和/或竖直方向上相邻的陆地连接形成。

此外，你可以假设该网格的四条边均被水包围。



示例 1：

```
输入：grid = [
  ["1","1","1","1","0"],
  ["1","1","0","1","0"],
  ["1","1","0","0","0"],
  ["0","0","0","0","0"]
]
输出：1
```


示例 2：

```
输入：grid = [
  ["1","1","0","0","0"],
  ["1","1","0","0","0"],
  ["0","0","1","0","0"],
  ["0","0","0","1","1"]
]
输出：3
```

提示：

m == grid.length
n == grid[i].length
1 <= m, n <= 300
grid[i][j] 的值为 '0' 或 '1'

```java
class Solution {
    
    private static final int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    
    private boolean[][] visited;
    
    private int height;
    
    private int width;
    
    public int numIslands(char[][] grid) {
		height = grid.length;
        width = grid[0].length;
        visited = new boolean[height][width];
        int result = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!visited[i][j] && grid[i][j] == '1') {
                    result++;
                    dfs(grid, i, j);
                }
            }
        }
        return result;
    }
    
    public void dfs(char[][] grid, int i, int j) {
    	visited[i][j] = true;
        for (int k = 0; k < directions.length; k++) {
            int newi = i + directions[k][0];
            int newj = j + directions[k][1];
            if (newi >= 0 && newi < h && newj >=0 && newj < w
               && !visited[newi][newj] && grid[newi][newj] == '1') {
                dfs(grid, newi, newj);
            }
        }
    }
    
}
```

# 拓扑排序

Kahn 算法

```java
public void topoSortByKahn() {
    // degree 表示度数，即该节点依赖的节点数，也可以用 HashMap 代替
    int[] inDegree = new int[v];
    for (int i = 0; i < v; ++i) {
        for (int j = 0; j < adj[i].size(); ++j) {
            int w = adj[i].get(j);
            inDegree[w]++;
        }
    }
    LinkedList<Integer> zeroSet = new LinkedList<>();
    for (int i = 0; i < v; ++i) {
        // 找到度数为 0 的节点，即不依赖任何节点的节点
        if (inDegree[i] == 0) {
            zeroSet.add(i);
        }
    }
    while(!zeroSet.isEmpty()) {
        int i = zeroSet.remove();
        System.out.print("->" + i);
        // 相关的节点度数 --
        for (int j = 0; j < adj[i].size(); j++) {
            int k = adj[i].get(j);
            inDegree[k]--;
            // 如果当前节点的度数也为 0 了，表示执行该节点
            if (inDegree[k] == 0) {
                zeroSet.add(k);
            }
        }
    }
}
```

DFS

```java
public void topoSortByDFS() {
    for (int i = 0; i < v; i++) {
        if (!visited[i]) {
            visited[i] = true;
            dfs(i);
        }
    }
}

private void dfs(int vertex) {
    for (int i = 0; i < adj[vertex].size(); i++) {
        int w = adj[vertex].get(i);
        if (visited[w]) {
            continue;
        }
        visited[w] = true;
        dfs(w);
    }
    System.out.print("->" + vertex);
}
```

## 检测环

求序列是否有循环依赖，比如 [A, B], [B, C], [C, A]], A -> B ->C -> A，就有循环依赖。

就用 Kanh 算法



**背包：**

[416. 分割等和子集](https://leetcode-cn.com/problems/partition-equal-subset-sum/)

[494. 目标和](https://leetcode-cn.com/problems/target-sum/)

[322. 零钱兑换](https://leetcode-cn.com/problems/coin-change/) 

[518. 零钱兑换 II](https://leetcode-cn.com/problems/coin-change-2/)



**路径问题**

[62. 不同路径](https://leetcode-cn.com/problems/unique-paths/)

[63. 不同路径 II](https://leetcode-cn.com/problems/unique-paths-ii/)

[64. 最小路径和](https://leetcode-cn.com/problems/minimum-path-sum/)

[剑指 Offer 47. 礼物的最大价值](https://leetcode-cn.com/problems/li-wu-de-zui-da-jie-zhi-lcof/) 

[120. 三角形最小路径和](https://leetcode-cn.com/problems/triangle/)



**打家劫舍** **&** **买卖股票：**

[198. 打家劫舍](https://leetcode-cn.com/problems/house-robber/)

[213. 打家劫舍 II](https://leetcode-cn.com/problems/house-robber-ii/)

[337. 打家劫舍 III](https://leetcode-cn.com/problems/house-robber-iii/) (树形DP)

[714. 买卖股票的最佳时机含手续](https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee/)

[309. 最佳买卖股票时机含冷冻期](https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/)



# 海量数据处理

## 海量数据处理面对两个问题

- 一台机器的内存存不下
- 一台机器处理起来太慢

## 海量数据处理的核心思想：分治

- 单机：利用内存，分批加入内存处理
- 多机：对数据分片，利用多机内存存储
- 多机：并行计算，利用多线程、多机并行处理

## 常见的问题

- 海量数据排序
- 海量数据查询
- 海量数据 TopK
- 海量数据求频率 TopK
- 海量数据去重/找重
- 海量文件找重

## 一些处理技巧

- 外部排序：多路归并、桶排序
- 哈希分片
- 位图

