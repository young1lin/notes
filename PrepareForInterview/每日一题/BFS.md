# BFS

Owner: lin young

- **[112. 路径总和](https://leetcode.cn/problems/path-sum/)**
    
    给你二叉树的根节点 `root` 和一个表示目标和的整数 `targetSum` 。判断该树中是否存在 **根节点到叶子节点** 的路径，这条路径上所有节点值相加等于目标和 `targetSum` 。如果存在，返回 `true` ；否则，返回 `false` 。
    
    **叶子节点** 是指没有子节点的节点。
    
    **示例 1：**
    
    ![https://assets.leetcode.com/uploads/2021/01/18/pathsum1.jpg](https://assets.leetcode.com/uploads/2021/01/18/pathsum1.jpg)
    
    ```
    输入：root = [5,4,8,11,null,13,4,7,2,null,null,null,1], targetSum = 22
    输出：true
    解释：等于目标和的根节点到叶节点路径如上图所示。
    
    ```
    
    **示例 2：**
    
    ![https://assets.leetcode.com/uploads/2021/01/18/pathsum2.jpg](https://assets.leetcode.com/uploads/2021/01/18/pathsum2.jpg)
    
    ```
    输入：root = [1,2,3], targetSum = 5
    输出：false
    解释：树中存在两条根节点到叶子节点的路径：
    (1 --> 2): 和为 3
    (1 --> 3): 和为 4
    不存在 sum = 5 的根节点到叶子节点的路径。
    ```
    
    **示例 3：**
    
    ```
    输入：root = [], targetSum = 0
    输出：false
    解释：由于树是空的，所以不存在根节点到叶子节点的路径。
    
    ```
    
    **提示：**
    
    - 树中节点的数目在范围 `[0, 5000]` 内
    - `1000 <= Node.val <= 1000`
    - `1000 <= targetSum <= 1000`
    
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
    
        public boolean hasPathSum(TreeNode root, int targetSum) {
            if (root == null) {
                return false;
            }
            Queue<TreeNode> queueNode = new LinkedList<>();
            Queue<Integer> queVal = new LinkedList<>();
            queueNode.offer(root);
            queVal.offer(root.val);
            while (!queueNode.isEmpty()) {
                TreeNode current = queueNode.poll();
                int temp = queVal.poll();
                if (isLeafNode(current)) {
                    if (temp == targetSum) {
                        return true;
                    }
                    continue;
                }
                if (current.left != null) {
                    queueNode.offer(current.left);
                    queVal.offer(current.left.val + temp);
                }
                if (current.right != null) {
                    queueNode.offer(current.right);
                    queVal.offer(current.right.val + temp);
                }
            }
            return false;
        }
    
        private boolean isLeafNode(TreeNode node) {
            return node.left == null && node.right == null;
        }
    
    }
    ```
    
- **[399. 除法求值](https://leetcode.cn/problems/evaluate-division/)**
    
    给你一个变量对数组 `equations` 和一个实数值数组 `values` 作为已知条件，其中 `equations[i] = [Ai, Bi]` 和 `values[i]` 共同表示等式 `Ai / Bi = values[i]` 。每个 `Ai` 或 `Bi` 是一个表示单个变量的字符串。
    
    另有一些以数组 `queries` 表示的问题，其中 `queries[j] = [Cj, Dj]` 表示第 `j` 个问题，请你根据已知条件找出 `Cj / Dj = ?` 的结果作为答案。
    
    返回 **所有问题的答案** 。如果存在某个无法确定的答案，则用 `-1.0` 替代这个答案。如果问题中出现了给定的已知条件中没有出现的字符串，也需要用 `-1.0` 替代这个答案。
    
    **注意：**输入总是有效的。你可以假设除法运算中不会出现除数为 0 的情况，且不存在任何矛盾的结果。
    
    **注意：**未在等式列表中出现的变量是未定义的，因此无法确定它们的答案。
    
    **示例 1：**
    
    ```
    输入：equations = [["a","b"],["b","c"]], values = [2.0,3.0], queries = [["a","c"],["b","a"],["a","e"],["a","a"],["x","x"]]
    输出：[6.00000,0.50000,-1.00000,1.00000,-1.00000]
    解释：
    条件：a / b = 2.0,b / c = 3.0
    问题：a / c = ?,b / a = ?,a / e = ?,a / a = ?,x / x = ?
    结果：[6.0, 0.5, -1.0, 1.0, -1.0 ]
    注意：x 是未定义的 => -1.0
    ```
    
    **示例 2：**
    
    ```
    输入：equations = [["a","b"],["b","c"],["bc","cd"]], values = [1.5,2.5,5.0], queries = [["a","c"],["c","b"],["bc","cd"],["cd","bc"]]
    输出：[3.75000,0.40000,5.00000,0.20000]
    
    ```
    
    **示例 3：**
    
    ```
    输入：equations = [["a","b"]], values = [0.5], queries = [["a","b"],["b","a"],["a","c"],["x","y"]]
    输出：[0.50000,2.00000,-1.00000,-1.00000]
    
    ```
    
    **提示：**
    
    - `1 <= equations.length <= 20`
    - `equations[i].length == 2`
    - `1 <= Ai.length, Bi.length <= 5`
    - `values.length == equations.length`
    - `0.0 < values[i] <= 20.0`
    - `1 <= queries.length <= 20`
    - `queries[i].length == 2`
    - `1 <= Cj.length, Dj.length <= 5`
    - `Ai, Bi, Cj, Dj` 由小写英文字母与数字组成
    
    ```java
    class Solution {
    
        public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
            int nvars = 0;
            Map<String, Integer> variables = new HashMap<>();
            int n = equations.size();
            for (int i = 0; i <  n; i++) {
                if (!variables.containsKey(equations.get(i).get(0))) {
                    variables.put(equations.get(i).get(0), nvars++);
                }
                if (!variables.containsKey(equations.get(i).get(1))) {
                    variables.put(equations.get(i).get(1), nvars++);
                }
            }
    
            List<Pair>[] edges = new List[nvars];
            for (int i = 0; i < nvars; i++) {
                edges[i] = new ArrayList<>();
            }
            for (int i = 0; i < n; i++) {
                int va = variables.get(equations.get(i).get(0));
                int vb = variables.get(equations.get(i).get(1));
                edges[va].add(new Pair(vb, values[i]));
                edges[vb].add(new Pair(va, 1.0 / values[i]));
            }
    
            int queriesCount = queries.size();
            double[] resultArray = new double[queriesCount];
            for (int i = 0; i < queriesCount; i++) {
                List<String> query = queries.get(i);
                double result = -1.0;
                if (variables.containsKey(query.get(0)) && variables.containsKey(query.get(1))) {
                    int ia = variables.get(query.get(0));
                    int ib = variables.get(query.get(1));
                    if (ia == ib) {
                        result = 1.0;
                    }
                    else {
                        Queue<Integer> points = new LinkedList<>();
                        points.offer(ia);
                        double[] rations = new double[nvars];
                        Arrays.fill(rations, -1.0);
                        rations[ia] = 1.0;
    
                        while (!points.isEmpty() && rations[ib] < 0) {
                            int x = points.poll();
                            for (Pair pair : edges[x]) {
                                int y = pair.index;
                                double val = pair.value;
                                if (rations[y] < 0) {
                                    rations[y] = rations[x] * val;
                                    points.offer(y);
                                }
                            }
                        }
                        result = rations[ib];
    
                    }
                }
                resultArray[i] = result;
            }
            return resultArray;
        }
    
    }
    
    class Pair {
        int index;
        double value;
    
        Pair(int index, double value) {
            this.index = index;
            this.value = value;
        }
    }
    ```