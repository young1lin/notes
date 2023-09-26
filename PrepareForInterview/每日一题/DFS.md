# DFS

Owner: lin young

主要是树方面的内容

- **[剑指 Offer 13. 机器人的运动范围](https://leetcode.cn/problems/ji-qi-ren-de-yun-dong-fan-wei-lcof/)**
    
    地上有一个m行n列的方格，从坐标 `[0,0]` 到坐标 `[m-1,n-1]` 。一个机器人从坐标 `[0, 0]` 的格子开始移动，它每次可以向左、右、上、下移动一格（不能移动到方格外），也不能进入行坐标和列坐标的数位之和大于k的格子。例如，当k为18时，机器人能够进入方格 [35, 37] ，因为3+5+3+7=18。但它不能进入方格 [35, 38]，因为3+5+3+8=19。请问该机器人能够到达多少个格子？
    
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
    - `0 <= k <= 20`
    
    ```java
    class Solution {
    
        private int[][] directions = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    
        boolean[][] visited; 
    
        private int count;
    
        public int movingCount(int m, int n, int k) {
            visited = new boolean[m][n];
            dfs(0, 0, m, n, k);
            return count;
        }
    
        private void dfs(int i, int j, int m, int n, int k) {
            visited[i][j] = true;
            count++;
            for (int di = 0; di < directions.length; di++) {
                int newi = i + directions[di][0];
                int newj = j + directions[di][1];
                if (newi >= m || newi < 0 || newj >= n || newj < 0
                    || visited[newi][newj] || check(newi, newj, k) == false
                ) {
                    continue;
                }
                dfs(newi, newj, m, n, k);
            }
        } 
    
        private boolean check(int i, int j, int k) {
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
    
- **[恢复二叉搜索树](https://leetcode.cn/problems/recover-binary-search-tree/)**
    
    给你二叉搜索树的根节点 `root` ，该树中的 **恰好** 两个节点的值被错误地交换。*请在不改变其结构的情况下，恢复这棵树* 。
    
    **示例 1：**
    
    ![https://assets.leetcode.com/uploads/2020/10/28/recover1.jpg](https://assets.leetcode.com/uploads/2020/10/28/recover1.jpg)
    
    ```
    输入：root = [1,3,null,null,2]
    输出：[3,1,null,null,2]
    解释：3 不能是 1 的左孩子，因为 3 > 1 。交换 1 和 3 使二叉搜索树有效。
    
    ```
    
    **示例 2：**
    
    ![https://assets.leetcode.com/uploads/2020/10/28/recover2.jpg](https://assets.leetcode.com/uploads/2020/10/28/recover2.jpg)
    
    ```
    输入：root = [3,1,4,null,null,2]
    输出：[2,1,4,null,null,3]
    解释：2 不能在 3 的右子树中，因为 2 < 3 。交换 2 和 3 使二叉搜索树有效。
    ```
    
    **提示：**
    
    - 树上节点的数目在范围 `[2, 1000]` 内
    - `231 <= Node.val <= 231 - 1`
    
    ```java
    class Solution {
        public void recoverTree(TreeNode root) {
            List<Integer> nums = new ArrayList<Integer>();
            inorder(root, nums);
            int[] swapped = findTwoSwapped(nums);
            recover(root, 2, swapped[0], swapped[1]);
        }
    
        public void inorder(TreeNode root, List<Integer> nums) {
            if (root == null) {
                return;
            }
            inorder(root.left, nums);
            nums.add(root.val);
            inorder(root.right, nums);
        }
    
        public int[] findTwoSwapped(List<Integer> nums) {
            int n = nums.size();
            int index1 = -1, index2 = -1;
            for (int i = 0; i < n - 1; ++i) {
                if (nums.get(i + 1) < nums.get(i)) {
                    index2 = i + 1;
                    if (index1 == -1) {
                        index1 = i;
                    } else {
                        break;
                    }
                }
            }
            int x = nums.get(index1), y = nums.get(index2);
            return new int[]{x, y};
        }
    
        public void recover(TreeNode root, int count, int x, int y) {
            if (root != null) {
                if (root.val == x || root.val == y) {
                    root.val = root.val == x ? y : x;
                    if (--count == 0) {
                        return;
                    }
                }
                recover(root.right, count, x, y);
                recover(root.left, count, x, y);
            }
        }
    }
    ```
    
- **[110. 平衡二叉树](https://leetcode.cn/problems/balanced-binary-tree/)**
    
    给定一个二叉树，判断它是否是高度平衡的二叉树。
    
    本题中，一棵高度平衡二叉树定义为：
    
    > 一个二叉树每个节点 的左右两个子树的高度差的绝对值不超过 1 。
    > 
    
    **示例 1：**
    
    ![https://assets.leetcode.com/uploads/2020/10/06/balance_1.jpg](https://assets.leetcode.com/uploads/2020/10/06/balance_1.jpg)
    
    ```
    输入：root = [3,9,20,null,null,15,7]
    输出：true
    
    ```
    
    **示例 2：**
    
    ![https://assets.leetcode.com/uploads/2020/10/06/balance_2.jpg](https://assets.leetcode.com/uploads/2020/10/06/balance_2.jpg)
    
    ```
    输入：root = [1,2,2,3,3,null,null,4,4]
    输出：false
    
    ```
    
    **示例 3：**
    
    ```
    输入：root = []
    输出：true
    
    ```
    
    **提示：**
    
    - 树中的节点数在范围 `[0, 5000]` 内
    - `104 <= Node.val <= 104`
    
    ```java
    /**
     * Definition for a binary tree node.
     * public class TreeNode {
     *     int val;
     *     TreeNode left;
     *     TreeNode right;
     *     TreeNode() {}
     *     TreeNode(int val) { this.val = val; }
     *     TreeNode(int val, TreeNode left, TreeNode right) {
     *         this.val = val;
     *         this.left = left;
     *         this.right = right;
     *     }
     * }
     */
    class Solution {
    
        public boolean isBalanced(TreeNode root) {
            if (root == null) {
                return true;
            }
            return Math.abs(height(root.left) - height(root.right)) <= 1 && isBalanced(root.left) && isBalanced(root.right);
        }
    
        public int height(TreeNode root) {
            if (root == null) {
                return 0;
            }
            return Math.max(height(root.left), height(root.right)) + 1;
        }
    
    }
    ```
    
- **[133. 克隆图](https://leetcode.cn/problems/clone-graph/)**
    
    给你无向 **[连通](https://baike.baidu.com/item/%E8%BF%9E%E9%80%9A%E5%9B%BE/6460995?fr=aladdin)** 图中一个节点的引用，请你返回该图的 **[深拷贝](https://baike.baidu.com/item/%E6%B7%B1%E6%8B%B7%E8%B4%9D/22785317?fr=aladdin)**（克隆）。
    
    图中的每个节点都包含它的值 `val`（`int`） 和其邻居的列表（`list[Node]`）。
    
    ```
    class Node {
        public int val;
        public List<Node> neighbors;
    }
    ```
    
    **测试用例格式：**
    
    简单起见，每个节点的值都和它的索引相同。例如，第一个节点值为 1（`val = 1`），第二个节点值为 2（`val = 2`），以此类推。该图在测试用例中使用邻接列表表示。
    
    **邻接列表** 是用于表示有限图的无序列表的集合。每个列表都描述了图中节点的邻居集。
    
    给定节点将始终是图中的第一个节点（值为 1）。你必须将 **给定节点的拷贝** 作为对克隆图的引用返回。
    
    **示例 1：**
    
    ![https://assets.leetcode-cn.com/aliyun-lc-upload/uploads/2020/02/01/133_clone_graph_question.png](https://assets.leetcode-cn.com/aliyun-lc-upload/uploads/2020/02/01/133_clone_graph_question.png)
    
    ```
    输入：adjList = [[2,4],[1,3],[2,4],[1,3]]
    输出：[[2,4],[1,3],[2,4],[1,3]]
    解释：
    图中有 4 个节点。
    节点 1 的值是 1，它有两个邻居：节点 2 和 4 。
    节点 2 的值是 2，它有两个邻居：节点 1 和 3 。
    节点 3 的值是 3，它有两个邻居：节点 2 和 4 。
    节点 4 的值是 4，它有两个邻居：节点 1 和 3 。
    
    ```
    
    **示例 2：**
    
    ![https://assets.leetcode-cn.com/aliyun-lc-upload/uploads/2020/02/01/graph.png](https://assets.leetcode-cn.com/aliyun-lc-upload/uploads/2020/02/01/graph.png)
    
    ```
    输入：adjList = [[]]
    输出：[[]]
    解释：输入包含一个空列表。该图仅仅只有一个值为 1 的节点，它没有任何邻居。
    
    ```
    
    **示例 3：**
    
    ```
    输入：adjList = []
    输出：[]
    解释：这个图是空的，它不含任何节点。
    
    ```
    
    **示例 4：**
    
    ![https://assets.leetcode-cn.com/aliyun-lc-upload/uploads/2020/02/01/graph-1.png](https://assets.leetcode-cn.com/aliyun-lc-upload/uploads/2020/02/01/graph-1.png)
    
    ```
    输入：adjList = [[2],[1]]
    输出：[[2],[1]]
    ```
    
    **提示：**
    
    1. 节点数不超过 100 。
    2. 每个节点值 `Node.val` 都是唯一的，`1 <= Node.val <= 100`。
    3. 无向图是一个[简单图](https://baike.baidu.com/item/%E7%AE%80%E5%8D%95%E5%9B%BE/1680528?fr=aladdin)，这意味着图中没有重复的边，也没有自环。
    4. 由于图是无向的，如果节点 *p* 是节点 *q* 的邻居，那么节点 *q* 也必须是节点 *p* 的邻居。
    5. 图是连通图，你可以从给定节点访问到所有节点。
    
    ```java
    /*
    // Definition for a Node.
    class Node {
        public int val;
        public List<Node> neighbors;
        public Node() {
            val = 0;
            neighbors = new ArrayList<Node>();
        }
        public Node(int _val) {
            val = _val;
            neighbors = new ArrayList<Node>();
        }
        public Node(int _val, ArrayList<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }
    }
    */
    
    class Solution {
    
        Node[] visited = new Node[100];
    
        public Node cloneGraph(Node node) {
            if(node == null) {
                return node;
            }
            int idx = node.val - 1;
            if (visited[idx] != null) {
                return visited[idx];
            }
            Node cloned = new Node(node.val, new ArrayList<>());
            visited[idx] = cloned;
            for (Node neighbor : node.neighbors) {
                cloned.neighbors.add(cloneGraph(neighbor));
            }
            return cloned;
        }
    
    }
    ```
    
- **[130. 被围绕的区域](https://leetcode.cn/problems/surrounded-regions/)(直接拷贝的官方题解)**
    
    给你一个m x n的矩阵，由若干字符'X'和'O'，找到所有被'X'围绕的区域，并将这些区域里所有的'O'用'X'填充。
    
    ```
    **示例 1：**
    ```
    
    ![https://assets.leetcode.com/uploads/2021/02/19/xogrid.jpg](https://assets.leetcode.com/uploads/2021/02/19/xogrid.jpg)
    
    ```
    输入：board = [["X","X","X","X"],["X","O","O","X"],["X","X","O","X"],["X","O","X","X"]]
    输出：[["X","X","X","X"],["X","X","X","X"],["X","X","X","X"],["X","O","X","X"]]
    解释：被围绕的区间不会存在于边界上，换句话说，任何边界上的'O' 都不会被填充为'X'。 任何不在边界上，或不与边界上的'O' 相连的'O' 最终都会被填充为'X'。如果两个元素在水平或垂直方向相邻，则称它们是“相连”的。
    
    ```
    
    **示例 2：**
    
    ```
    输入：board = [["X"]]
    输出：[["X"]]
    
    ```
    
    **提示：**
    
    - `m == board.length`
    - `n == board[i].length`
    - `1 <= m, n <= 200`
    - `board[i][j]` 为 `'X'` 或 `'O'`
    
    ```java
    class Solution {
    
        int m, n;
    
        public void solve(char[][] board) {
            m = board.length;
            n = board[0].length;
            for (int i = 0; i < m; i++) {
                dfs(board, i, 0);
                dfs(board, i, n - 1);
            }
            for (int i = 1; i < n - 1; i++) {
                dfs(board, 0, i);
                dfs(board, m - 1, i);
            }
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (board[i][j] == 'A') {
                        board[i][j] = 'O';
                    } else if (board[i][j] == 'O') {
                        board[i][j] = 'X';
                    }
                }
            }
        }
    
        public void dfs(char[][] board, int x, int y) {
            if (x < 0 || x >= m || y < 0 || y >= n || board[x][y] != 'O') {
                return;
            }
            board[x][y] = 'A';
            dfs(board, x + 1, y);
            dfs(board, x - 1, y);
            dfs(board, x, y + 1);
            dfs(board, x, y - 1);
        }
    }
    ```
    
- **[207. 课程表](https://leetcode.cn/problems/course-schedule/)(直接拷贝的官方题解，图是我的弱项，这不是单纯的DFS 的题目)**
    
    你这个学期必须选修 `numCourses` 门课程，记为 `0` 到 `numCourses - 1` 。
    
    在选修某些课程之前需要一些先修课程。 先修课程按数组 `prerequisites` 给出，其中 `prerequisites[i] = [ai, bi]` ，表示如果要学习课程 `ai` 则 **必须** 先学习课程  `bi` 。
    
    - 例如，先修课程对 `[0, 1]` 表示：想要学习课程 `0` ，你需要先完成课程 `1` 。
    
    请你判断是否可能完成所有课程的学习？如果可以，返回 `true` ；否则，返回 `false` 。
    
    **示例 1：**
    
    ```
    输入：numCourses = 2, prerequisites = [[1,0]]
    输出：true
    解释：总共有 2 门课程。学习课程 1 之前，你需要完成课程 0 。这是可能的。
    ```
    
    **示例 2：**
    
    ```
    输入：numCourses = 2, prerequisites = [[1,0],[0,1]]
    输出：false
    解释：总共有 2 门课程。学习课程 1 之前，你需要先完成课程 0 ；并且学习课程 0 之前，你还应先完成课程 1 。这是不可能的。
    ```
    
    **提示：**
    
    - `1 <= numCourses <= 2000`
    - `0 <= prerequisites.length <= 5000`
    - `prerequisites[i].length == 2`
    - `0 <= ai, bi < numCourses`
    - `prerequisites[i]` 中的所有课程对 **互不相同**
    
    ```java
    class Solution {
        List<List<Integer>> edges;
        int[] visited;
        boolean valid = true;
    
        public boolean canFinish(int numCourses, int[][] prerequisites) {
            edges = new ArrayList<List<Integer>>();
            for (int i = 0; i < numCourses; ++i) {
                edges.add(new ArrayList<Integer>());
            }
            visited = new int[numCourses];
            for (int[] info : prerequisites) {
                edges.get(info[1]).add(info[0]);
            }
            for (int i = 0; i < numCourses && valid; ++i) {
                if (visited[i] == 0) {
                    dfs(i);
                }
            }
            return valid;
        }
    
        public void dfs(int u) {
            visited[u] = 1;
            for (int v: edges.get(u)) {
                if (visited[v] == 0) {
                    dfs(v);
                    if (!valid) {
                        return;
                    }
                } else if (visited[v] == 1) {
                    valid = false;
                    return;
                }
            }
            visited[u] = 2;
        }
    }
    ```
    
- **[211. 添加与搜索单词 - 数据结构设计](https://leetcode.cn/problems/design-add-and-search-words-data-structure/)**
    
    请你设计一个数据结构，支持 添加新单词 和 查找字符串是否与任何先前添加的字符串匹配 。
    
    实现词典类 `WordDictionary` ：
    
    - `WordDictionary()` 初始化词典对象
    - `void addWord(word)` 将 `word` 添加到数据结构中，之后可以对它进行匹配
    - `bool search(word)` 如果数据结构中存在字符串与 `word` 匹配，则返回 `true` ；否则，返回 `false` 。`word` 中可能包含一些 `'.'` ，每个 `.` 都可以表示任何一个字母。
    
    **示例：**
    
    ```
    输入：
    ["WordDictionary","addWord","addWord","addWord","search","search","search","search"]
    [[],["bad"],["dad"],["mad"],["pad"],["bad"],[".ad"],["b.."]]
    输出：
    [null,null,null,null,false,true,true,true]
    
    解释：
    WordDictionary wordDictionary = new WordDictionary();
    wordDictionary.addWord("bad");
    wordDictionary.addWord("dad");
    wordDictionary.addWord("mad");
    wordDictionary.search("pad"); // 返回 False
    wordDictionary.search("bad"); // 返回 True
    wordDictionary.search(".ad"); // 返回 True
    wordDictionary.search("b.."); // 返回 True
    
    ```
    
    **提示：**
    
    - `1 <= word.length <= 25`
    - `addWord` 中的 `word` 由小写英文字母组成
    - `search` 中的 `word` 由 '.' 或小写英文字母组成
    - 最多调用 `104` 次 `addWord` 和 `search`
    
    ```java
    class WordDictionary {
    
        private Trie root;
    
        public WordDictionary() {
            root = new Trie();
        }
        
        public void addWord(String word) {
            root.insert(word);
        }
        
        public boolean search(String word) {
            return dfs(word, 0, root);
        }
    
        private boolean dfs(String word, int index, Trie node) {
            if (index == word.length()) {
                return node.isEnd();
            }
            char c = word.charAt(index);
            if (Character.isLetter(c)) {
                int childrenIndex = c - 'a';
                Trie child = node.getChildren()[childrenIndex];
                if (child != null && dfs(word, index + 1, child)) {
                    return true;
                }
            }
            else {
                for (int i = 0; i < 26; i++) {
                    Trie child = node.getChildren()[i];
                    if (child != null && dfs(word, index + 1, child)) {
                        return true;
                    }
                }
            } 
            return false;
        }
    
    }
    
    class Trie {
    
        private Trie[] children;
    
        private boolean isEnd;
    
        public Trie() {
            children = new Trie[26];
            isEnd = false;
        }
    
        public void insert(String word) {
            Trie node  = this;
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                int index = c - 'a';
                if (node.children[index] == null) {
                    node.children[index] = new Trie();
                }
                node = node.children[index];
            }
            node.isEnd = true;
        }
    
        public Trie[] getChildren() {
            return this.children;
        }
    
        public boolean isEnd() {
            return this.isEnd;
        }
    
    } 
    
    /**
     * Your WordDictionary object will be instantiated and called as such:
     * WordDictionary obj = new WordDictionary();
     * obj.addWord(word);
     * boolean param_2 = obj.search(word);
     */
    ```
    
- **[337. 打家劫舍 III](https://leetcode.cn/problems/house-robber-iii/)**
    
    小偷又发现了一个新的可行窃的地区。这个地区只有一个入口，我们称之为 `root` 。
    
    除了 `root` 之外，每栋房子有且只有一个“父“房子与之相连。一番侦察之后，聪明的小偷意识到“这个地方的所有房屋的排列类似于一棵二叉树”。 如果 **两个直接相连的房子在同一天晚上被打劫** ，房屋将自动报警。
    
    给定二叉树的 `root` 。返回 ***在不触动警报的情况下** ，小偷能够盗取的最高金额* 。
    
    **示例 1:**
    
    ![https://assets.leetcode.com/uploads/2021/03/10/rob1-tree.jpg](https://assets.leetcode.com/uploads/2021/03/10/rob1-tree.jpg)
    
    ```
    输入:root = [3,2,3,null,3,null,1]
    输出: 7
    解释: 小偷一晚能够盗取的最高金额 3 + 3 + 1 = 7
    ```
    
    **示例 2:**
    
    ![https://assets.leetcode.com/uploads/2021/03/10/rob2-tree.jpg](https://assets.leetcode.com/uploads/2021/03/10/rob2-tree.jpg)
    
    ```
    输入:root = [3,4,5,1,3,null,1]
    输出: 9
    解释: 小偷一晚能够盗取的最高金额 4 + 5 = 9
    
    ```
    
    **提示：**
    
    - 树的节点数在 `[1, 104]` 范围内
    - `0 <= Node.val <= 104`
    
    ```java
    /**
     * Definition for a binary tree node.
     * public class TreeNode {
     *     int val;
     *     TreeNode left;
     *     TreeNode right;
     *     TreeNode() {}
     *     TreeNode(int val) { this.val = val; }
     *     TreeNode(int val, TreeNode left, TreeNode right) {
     *         this.val = val;
     *         this.left = left;
     *         this.right = right;
     *     }
     * }
     */
    class Solution {
    
        Map<TreeNode, Integer> f = new HashMap<>();
    
        Map<TreeNode, Integer> g = new HashMap<>();
    
        public int rob(TreeNode root) {      
            dfs(root);
            return Math.max(f.getOrDefault(root, 0), g.getOrDefault(root, 0));
        }
    
        private void dfs(TreeNode node) {
            if (node == null) {
                return;
            }
            dfs(node.left);
            dfs(node.right);
            f.put(node, node.val + g.getOrDefault(node.left, 0) + g.getOrDefault(node.right, 0));
            g.put(node, Math.max(f.getOrDefault(node.left, 0), g.getOrDefault(node.left, 0))
                         + Math.max(f.getOrDefault(node.right, 0), g.getOrDefault(node.right, 0)));
        }
    
    }
    ```
    
- **[385. 迷你语法分析器](https://leetcode.cn/problems/mini-parser/)（这种消消乐的题目，用栈最好实现）**
    
    给定一个字符串 s 表示一个整数嵌套列表，实现一个解析它的语法分析器并返回解析的结果 `NestedInteger` 。
    
    列表中的每个元素只可能是整数或整数嵌套列表
    
    **示例 1：**
    
    ```
    输入：s = "324",
    输出：324
    解释：你应该返回一个 NestedInteger 对象，其中只包含整数值 324。
    
    ```
    
    **示例 2：**
    
    ```
    输入：s = "[123,[456,[789]]]",
    输出：[123,[456,[789]]]
    解释：返回一个 NestedInteger 对象包含一个有两个元素的嵌套列表：
    1. 一个 integer 包含值 123
    2. 一个包含两个元素的嵌套列表：
        i.  一个 integer 包含值 456
        ii. 一个包含一个元素的嵌套列表
             a. 一个 integer 包含值 789
    
    ```
    
    **提示：**
    
    - `1 <= s.length <= 5 * 104`
    - `s` 由数字、方括号 `"[]"`、负号 `'-'` 、逗号 `','`组成
    - 用例保证 `s` 是可解析的 `NestedInteger`
    - 输入中的所有值的范围是 `[-106, 106]`
    
    ```java
    /**
     * // This is the interface that allows for creating nested lists.
     * // You should not implement it, or speculate about its implementation
     * public interface NestedInteger {
     * // Constructor initializes an empty nested list.
     * public NestedInteger();
     * <p>
     * // Constructor initializes a single integer.
     * public NestedInteger(int value);
     * <p>
     * // @return true if this NestedInteger holds a single integer, rather than a nested list.
     * public boolean isInteger();
     * <p>
     * // @return the single integer that this NestedInteger holds, if it holds a single integer
     * // Return null if this NestedInteger holds a nested list
     * public Integer getInteger();
     * <p>
     * // Set this NestedInteger to hold a single integer.
     * public void setInteger(int value);
     * <p>
     * // Set this NestedInteger to hold a nested list and adds a nested integer to it.
     * public void add(NestedInteger ni);
     * <p>
     * // @return the nested list that this NestedInteger holds, if it holds a nested list
     * // Return empty list if this NestedInteger holds a single integer
     * public List<NestedInteger> getList();
     * }
     */
    class Solution {
    
        private int index = 0;
    
        public NestedInteger deserialize(String s) {
            if (s.charAt(index) == '[') {
                index++;
                NestedInteger ni = new NestedInteger();
                while (s.charAt(index) != ']') {
                    ni.add(deserialize(s));
                    if (s.charAt(index) == ',') {
                        index++;
                    }
                }
                index++;
                return ni;   
            }
            else {
                boolean negative = false;
                if (s.charAt(index) == '-') {
                    negative = true;
                    index++;
                }
                int num = 0;
                while (index < s.length() && Character.isDigit(s.charAt(index))) {
                    num = num * 10 + s.charAt(index) - '0';
                    index++;
                }
                if (negative) {
                    num *= -1;
                }
                return new NestedInteger(num);
            }
        }
    
    }
    ```
    
- **[111. 二叉树的最小深度](https://leetcode.cn/problems/minimum-depth-of-binary-tree/)**
    
    给定一个二叉树，找出其最小深度。
    
    最小深度是从根节点到最近叶子节点的最短路径上的节点数量。
    
    **说明：**叶子节点是指没有子节点的节点。
    
    **示例 1：**
    
    ![https://assets.leetcode.com/uploads/2020/10/12/ex_depth.jpg](https://assets.leetcode.com/uploads/2020/10/12/ex_depth.jpg)
    
    ```
    输入：root = [3,9,20,null,null,15,7]
    输出：2
    
    ```
    
    **示例 2：**
    
    ```
    输入：root = [2,null,3,null,4,null,5,null,6]
    输出：5
    
    ```
    
    **提示：**
    
    - 树中节点数的范围在 `[0, 105]` 内
    - `1000 <= Node.val <= 1000`
    
    ```java
    /**
     * Definition for a binary tree node.
     * public class TreeNode {
     *     int val;
     *     TreeNode left;
     *     TreeNode right;
     *     TreeNode() {}
     *     TreeNode(int val) { this.val = val; }
     *     TreeNode(int val, TreeNode left, TreeNode right) {
     *         this.val = val;
     *         this.left = left;
     *         this.right = right;
     *     }
     * }
     */
    class Solution {
    
        public int minDepth(TreeNode root) {
            if (root == null) {
                return 0;
            }
            if (isLeafNode(root)) {
                return 1;
            }
            int m1 = minDepth(root.left);
            int m2 = minDepth(root.right);
    
            if (root.left == null || root.right == null) {
                return m1 + m2 + 1;
            }
            return Math.min(m1, m2) + 1;
        }
    
        private boolean isLeafNode(TreeNode node) {
            return node.left == null && node.right == null;
        }
    
    }
    ```