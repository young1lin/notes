# 回溯

核心思想：

- 回溯的处理过程是一个穷举（或者叫枚举）的过程，枚举所有的解，找出其中满足期望的可行解。为了有规律地枚举所有可能的解，避免遗漏和重复，我们把问题求解的过程归纳为多阶段决策模型。每个阶段的决策会对应多个选择，从可选的选择列表中，任意选择一个，然后继续进行下一个阶段的决策。
- 整个决策的过程，如果用图形来形象化表示的话，就是一个**决策树**。回溯穷举所有解来查找可行解的过程，就是**在决策树中进行遍历**的过程。遍历过程中记录的**路径**就是解。
- 回溯一般使用**递归来实现**，递归树就跟决策树完全一样。递的过程进行函数调用，对应到递归树上为从一个节点进入它的子节点，归的过程进行函数调用返回，对应到递归树上是从子节点返回上一层节点。



回溯代码模板

```
result = []
def backtrack(可选列表，决策阶段，路径)
	if 满足结束条件（所有决策都已经完成或得到可行解）
		if 路径为可行解：result.add（路径）
		return
    for 选择 in[可选列表]：
    	# 做选择
    	路径.add(选择)
    	backtrack（可选列表，决策阶段 + 1，路径）
    	# 撤销选择
    	路径.remove(选择)
```

递归代码模板

```
def recur(参数){
	递归结束条件 # 最小子问题
	前置逻辑
	recur(参数)# 子问题
	是否有现场需要手动恢复（全局变量）
	后置逻辑
}
```

1. 全排列
2. 八皇后
3. 0-1背包
4. 所有的组合
5. 正则表达式

# 例题1 全排列 给定 n 个不重复的数，求这组数的所有的排列组合

实际上，这个问题就是一个穷举问题，为了不重复、不遗漏的穷举，一般：先固定第一位是啥，再固定第二位是啥，实际上这就是个一个多阶段决策问题。决策的过程用图来表示就一个决策树。

回溯通过递归来实现。决策树跟递归树长相相同。递归的过程，沿一条路一股脑的往下走，直到无路可走之后，返回上一个岔路口（节点），重新选择新的岔路继续往下走。

 

```java
class Solution {
    
    private List<List<Integer>> result = new ArrayList<>();
    
    public List<List<Integer>> permute(int[] nums) {
        List<Integer> path = new ArrayList<>();
        backtrack(nums, 0, path);
        return result;
    }
    
    /** 
     * 路径：记录在 path 中
     * 决策阶段：k
     * 可选列表：nums 中除掉存在于 path 中的数据
     */
    private void backtrack(int[] nums, int k, List<Integer> path) {
        // 结束条件
        if (k == nums.length) {
            result.add(new ArrayList<>(path));
        	return;
        }
        for(Integer i : nums) {
            if (path.contains(i)) {
                continue;
            }
            // 做选择
            path.add(i);
            // 递归
            backtrack(nums, k + 1, path);
            // 撤销选择，因为放的最末尾，所以移除最末尾的值
            path.remove(path.size() - 1);
        }
    }
    
}
```

## 八皇后问题



```java
package me.young1lin.xzg.algorithm.week07;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 八皇后问题
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/10/28 下午9:57
 * @version 1.0
 */
public class EightQueen {

	List<char[][]> result = new ArrayList<>();

	public List<char[][]> eightQeueue() {
		char[][] board = new char[8][8];
		for (int i = 0; i < 8; ++i) {
			for (int j = 0; j < 8; ++j) {
				board[i][j] = '*';
			}
		}
		backtrack(0, board);
		return result;
	}

	/**
	 * row 阶段
	 * board：路径，记录已经做出的决策
	 * 可选列表：通过 board 推导出来，没有显式记录
	 */
	public void backtrack(int row, char[][] board) {
		// 结束条件，得到可行解
		if (row == 8) {
			char[][] snapshot = new char[8][8];
			for (int i = 0; i < 8; ++i) {
				System.arraycopy(board[i], 0, snapshot[i], 0, 8);
			}
			result.add(snapshot);
			return;
		}
		// 每一行都有 8 种放法
		for (int col = 0; col < 8; ++col) {
			// 可选列表
			if (isOk(board, row, col)) {
				// 做选择，第 row 行的棋子放到了 col 列
				board[row][col] = 'Q';
				// 考察下一行
				backtrack(row + 1, board);
				// 恢复选择
				board[row][col] = '*';
			}
		}
	}

	private boolean isOk(char[][] board, int row, int col) {
		int n = 8;
		// 检查列表是否有冲突
		for (int i = 0; i < row; i++) {
			if (board[i][col] == 'Q') {
				return false;
			}
		}
		// 检查右上角是否有冲突
		int i = row - 1;
		int j = col + 1;
		while (i >= 0 && j < n) {
			if (board[i][j] == 'Q') {
				return false;
			}
			i--;
			j++;
		}
		// 检查左上对角线是否有冲突
		i = row - 1;
		j = col - 1;
		while (i >= 0 && j >= 0) {
			if (board[i][j] == 'Q') {
				return false;
			}
			i--;
			j--;
		}
		return true;
	}

	public static void main(String[] args) {
		EightQueen eightQueen = new EightQueen();
		List<char[][]> boards = eightQueen.eightQeueue();
		System.out.printf("Solutions' number is [%s] \r\n", boards.size());
		for (char[][] board : boards) {
			for (char[] row : board) {
				System.out.println(Arrays.toString(row));
			}
			System.out.println("=========================");
		}
	}

}
```

## 0-1 背包问题

对于 n 个物品来说，总的装法就有 $2^n$ 种。

不重复穷举，使用回溯。当递归过程中，出现选择的物品总重量超过背包承载重量时，终止继续递归，也就是剪枝。

```java
public class ZeroAndOnePackage {
    
    /** 存储背包中物品总重量的最大值 */
    private int maxW = Integer.MIN_VALUE;
    
    public int bage(int[] items, int w) {
        backtrack(items, 0, 0, w);
        return maxW;
    }
    
    /**
     * k：阶段
     * cw：路径，记录已经选择的物品的总重量
     * items[k]：选择列表，选或不选
     * w 剪枝的条件
     */
    private void backtrack(int[] items, int k, int cw, int w) {
        // cw == w 表示装满了；i == n 表示已经考察完所有的物品
        if (cw == w || k == items.length) {
            if (cw > maxW) {
                maxW = cw;
            }
            return;
        }
        // 做选择
        backtrack(items, k + 1, cw, w);
        // 不装，剪枝
        if (cw + items[k] <= w) {
            // 装
            backtrack(items, k + 1, cw + items[k], w);
        }
        // 都是局部变量，自动撤销选择
    }
    
}
```

## 子集，所有的组合

给你一个整数数组 nums ，数组中的元素 互不相同 。返回该数组所有可能的子集（幂集）。

解集 不能 包含重复的子集。你可以按 任意顺序 返回解集。

 示例 1：

```
输入：nums = [1,2,3]
输出：[[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
```


示例 2：

```
输入：nums = [0]
输出：[[],[0]]
```


提示：

- 1 <= nums.length <= 10
- -10 <= nums[i] <= 10
- nums 中的所有元素 互不相同

```java
class Solution {

    private final List<List<Integer>> result = new ArrayList<>();

    public List<List<Integer>> subsets(int[] nums) {
        backtrack(nums, 0, new ArrayList<>());
        return result;
    }

    /**
     * 按理来说私有方法不应该写注释的
     *
     * @param nums 可选列表，nums[k] 选还是不选
     * @param k 表示阶段
     * @param path 表示路径
     */
    private void backtrack(int[] nums, int k, List<Integer> path) {
        if (k == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }
        
        backtrack(nums, k + 1, path);

        path.add(nums[k]);
        backtrack(nums, k + 1, path);
        path.remove(path.size() - 1);
    }

}
```

# 正则表达式

假设正则表达式中只包含 “\*\” 和 “?” 这两种通配符，并且对两个通配符的语义稍微做些改变。其中 "\*\“ 匹配任意多个（大于等于 0 个）任意字符，"?" 匹配零个或者一个任意字符。编程判断一个给定的文本，能够跟给定的正则表达式匹配？

adcabef

a*b?f

依次考察正则表达式中的每个字符，决定每个字符跟文本中的哪些字符匹配，是一个、两个还是多个...，决定完正则表达式中的一个字符之后，再决定下一个，直到正则表达式中所有字符都考察完为止，这个时候，如果文本中的字符也都匹配完了，就说明匹配了，否则就说明没有匹配。这也符合多阶段决策模型。



```java
public class solution {
    
    private boolean matched = false;
    
    public boolean match(chat[] text, char[] pattern) {
        backtrack(text, pattern, 0, 0);
        return matched;
    }
    
    private void backtrack(char[] text, char[] pattern, int ti, int pj) {
        // 结束条件
        // 正则表达式到结尾了
        if (pj == pattern.length) {
            // 可行解，文本串也到结尾了
            if (ti == text.length) {
                matched = true;
            }
            return;
        }
        // 做选择（根据 pattern 中当前考察字符的具体情况）
        // 匹配任意个字符
        if (pattern[pj] == '*') {
            for (int k = 0; k <= text.length - ti; ++k) {
                backtrack(text, pattern, ti + k, pj + 1);
            }
        }
        // ? 匹配 0 个或 1 个字符
        else if (pattern[pj] == '?') {
            backtrack(text, patternm, ti, pj + 1);
            if (ti < text.length) {
                backtrack(text, patternm, ti + 1, pj + 1);
            }
        }
        // 纯字符匹配才行
        else if (ti < text.length && pattern[pj] == teext[ti]) {
            backtrack(text, pattern, ti + 1, pj + 1);
        }
        // 撤销选择，因为没有全局变量，局部变量会在递归返回时自动恢复
    }
    
} 
```



[面试题 08.12. 八皇后](https://leetcode-cn.com/problems/eight-queens-lcci/)（困难）

[37. 解数独](https://leetcode-cn.com/problems/sudoku-solver/)

[17. 电话号码的字母组合](https://leetcode-cn.com/problems/letter-combinations-of-a-phone-number/)（中等）



[77. 组合](https://leetcode-cn.com/problems/combinations/)（中等） 给n个数返回所有k个数的组合

[78. 子集](https://leetcode-cn.com/problems/subsets/)（中等） 所有的组合

[90. 子集 II](https://leetcode-cn.com/problems/subsets-ii/)（中等）有重复数据

[46. 全排列](https://leetcode-cn.com/problems/permutations/)（中等） 所有排列

[47. 全排列 II](https://leetcode-cn.com/problems/permutations-ii/)（中等） 有重复数据



[39. 组合总和](https://leetcode-cn.com/problems/combination-sum/)（中等） 选出某几个数相加为给定和，无重复数据，可以使用多次，不能有重复答案

[40. 组合总和 II](https://leetcode-cn.com/problems/combination-sum-ii/)（中等）选出某几个数相加为给定和，有重复数据，只能使用一次，不能有重复答案

[216. 组合总和 III](https://leetcode-cn.com/problems/combination-sum-iii/)（中等） 选出k个数相加为给定和，没有重复数据，只能使用一次



[131. 分割回文串](https://leetcode-cn.com/problems/palindrome-partitioning/)（中等）

[93. 复原 IP 地址](https://leetcode-cn.com/problems/restore-ip-addresses/)（中等）

[22. 括号生成](https://leetcode-cn.com/problems/generate-parentheses/)（中等） 



# [面试题 08.12. 八皇后](https://leetcode-cn.com/problems/eight-queens-lcci/)（困难）

设计一种算法，打印 N 皇后在 N × N 棋盘上的各种摆法，其中每个皇后都不同行、不同列，也不在对角线上。这里的“对角线”指的是所有的对角线，不只是平分整个棋盘的那两条对角线。

注意：本题相对原题做了扩展

示例:

```
 输入：4
 输出：[[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]
 解释: 4 皇后问题存在如下两个不同的解法。
[
 [".Q..",  // 解法 1
  "...Q",
  "Q...",
  "..Q."],

 ["..Q.",  // 解法 2
  "Q...",
  "...Q",
  ".Q.."]
]
```

实现代码

```java
class Solution {
    
    private List<List<String>> result = new ArrayList<>();
    
    public List<List<String>> solveNQueens(int n) {
        // init board
     	char[][] board = initBoard(n);
		backtrack(0, board, n);
        return result;
    }
    
    private char[][] initBoard(int n) {
        char[][] board = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = '.';
            }
        }
        return board;
    }
    
    private void backtrack(int row, char[][] board, int n) {
        // 结束条件，得到可行解
        if (row == n) {
            List<String> snapshot = new ArrayList<>();
            for (int i = 0; i < n; ++i) {
                snapshot.add(new String(board[i]));
            }
            result.add(snapshot);
            return;
        }
        // 每一行都有 n 种放法
        for (int col = 0; col < n; col++) {
            // 可选列表
            // 做选择，第 row 行的棋子放到了 col 列
            if (isOk(board, n, row, col)) {
                board[row][col] = 'Q';
                // 考察下一行
                backtrack(row + 1, board, n);
                // 恢复选择
                board[row][col] = '.';
            }
        }
    }

    /**
     * 判断 row 行 col 列放置是否合适
     */
    private boolean isOk(char[][] board, int n, int row, int col) {
        // 检查列冲突
        for (int i = 0; i < n; i++) {
            if (board[i][col] == 'Q') {
                return false;
            }
        }
        // 检查右上对角冲突
        int i = row - 1;
        int j = col + 1;
        while (i >= 0 && j < n) {
            if (board[i][j] == 'Q') {
                return false;
            }
            i--;
            j++;
        }
        // 检查左上对角冲突
        i = row - 1;
        j = col - 1;
        while (i >= 0 && j >= 0) {
            if (board[i][j] == 'Q') {
                return false;
            }
            i--;
            j--;
        }
        return true;
    }
    
}
```



```
### 代码模板
result = []
def backtrack(可选列表, 决策阶段, 路径)
	if 满足结束条件 (所有决策都已完成或得到可行解)
		if 路径为可行解： result.add(路径)
		return
    for 选择 in [可选列表]:
    	# 做选择
    	
    	路径.add(选择)
    	backtrack(可选列表，决策阶段，路径)
    	# 撤销选择
    	路径.remove(选择)
```



# [37. 解数独](https://leetcode-cn.com/problems/sudoku-solver/)

编写一个程序，通过填充空格来解决数独问题。

数独的解法需 遵循如下规则：

1. 数字 1-9 在每一行只能出现一次。
2. 数字 1-9 在每一列只能出现一次。
3. 数字 1-9 在每一个以粗实线分隔的 3x3 宫内只能出现一次。（请参考示例图）
4. 数独部分空格内已填入了数字，空白格用 '.' 表示。

![img](https://assets.leetcode-cn.com/aliyun-lc-upload/uploads/2021/04/12/250px-sudoku-by-l2g-20050714svg.png)

```
输入：board = [["5","3",".",".","7",".",".",".","."],["6",".",".","1","9","5",".",".","."],[".","9","8",".",".",".",".","6","."],["8",".",".",".","6",".",".",".","3"],["4",".",".","8",".","3",".",".","1"],["7",".",".",".","2",".",".",".","6"],[".","6",".",".",".",".","2","8","."],[".",".",".","4","1","9",".",".","5"],[".",".",".",".","8",".",".","7","9"]]
输出：[["5","3","4","6","7","8","9","1","2"],["6","7","2","1","9","5","3","4","8"],["1","9","8","3","4","2","5","6","7"],["8","5","9","7","6","1","4","2","3"],["4","2","6","8","5","3","7","9","1"],["7","1","3","9","2","4","8","5","6"],["9","6","1","5","3","7","2","8","4"],["2","8","7","4","1","9","6","3","5"],["3","4","5","2","8","6","1","7","9"]]
解释：输入的数独如上图所示，唯一有效的解决方案如下所示：
```

![img](https://assets.leetcode-cn.com/aliyun-lc-upload/uploads/2021/04/12/250px-sudoku-by-l2g-20050714_solutionsvg.png)



提示：

board.length == 9
board[i].length == 9
board[i][j] 是一位数字或者 '.'
题目数据 保证 输入数独仅有一个解

```
### 代码模板
result = []
def backtrack(可选列表, 决策阶段, 路径)
	if 满足结束条件 (所有决策都已完成或得到可行解)
		if 路径为可行解： result.add(路径)
		return
    for 选择 in [可选列表]:
    	# 做选择
    	
    	路径.add(选择)
    	backtrack(可选列表，决策阶段，路径)
    	# 撤销选择
    	路径.remove(选择)
```



```java
class Solution {
    
    private boolean[][] rows = new boolean[9][10];
    
    private boolean[][] cols = new boolean[9][10];

    private boolean[][][] blocks = new boolean[3][3][10];

	private boolean solved = false;
    
    
    public void solveSudoku(char[][] board) {
        // 先标记已经填好的格子，减少后续的回溯步骤
        markTheFilledBox(board);
		// 回溯求解
        backtrack(0, 0, board);
    }
    
    private void markTheFilledBox(char[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; i < 9; i++) {
                if (board[i][j] != '.') {
                    int num = board[i][j] - '0';
                    rows[i][num] = true;
                    cols[j][num] = true;
                    blocks[i/3][j/3][num] = true;
                }
            }
        }   
    }
    
    private void backtrack(int row, int col, char[][] board) {
        if (row == 9) {
            solved = true;
            return;
        }
    	if (board[row][col] != '.') {
            int nextRow = row;
            int nextCol = col + 1;
            if (col == 8) {
                nextRow = row + 1;
                nextCol = 0;
            }
            backtrack(nextRow, nextCol, board);
        	if (solved) {
                return;
            }
        }
        else {
            for (int num = 1; num <= 9; ++num) {
                if (isOk(row, col, num)) {
                    // 数字转化成 char
                    board[row][col] = String.valueOf(num).charAt(0);
                    rows[row][num] = true;
                    cols[col][num] = true;
                    blocks[row/3][col/3][num] = true;

                    int nextRow = row;
                    int nextCol = col + 1;
                    if (col == 8) {
                        nextRow = row + 1;
                        nextCol = 0;
                    }
                    backtrack(nextRow, nextCol, board);
                    
                    if (solved) {
                        return;
                    }
                    // path
                    board[row][col] = '.';
                    rows[row][num] = false;
                    cols[col][num] = false;
                    blocks[row/3][col/3][num] = false;
                }
            }
        }
    }
    
    private boolean isOk(int row, int col, int num) {
        return !rows[row][num] && !cols[col][num] && !blocks[row/3][col/3][num];
    }

}
```



# [17. 电话号码的字母组合](https://leetcode-cn.com/problems/letter-combinations-of-a-phone-number/)（中等）

给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。答案可以按 任意顺序 返回。

给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。

![img](https://assets.leetcode-cn.com/aliyun-lc-upload/uploads/2021/11/09/200px-telephone-keypad2svg.png)

```
示例 1：

输入：digits = "23"
输出：["ad","ae","af","bd","be","bf","cd","ce","cf"]
```



```
示例 2：

输入：digits = ""
输出：[]
```



```
示例 3：

输入：digits = "2"
输出：["a","b","c"]
```



```java
class Solution {
    
    private List<String> result = new ArrayList<>();
    
    public List<String> letterCombinations(String digits) {
        if (digits.length() == 0) {
            return Collections.emptyList();
        }
        String[] mappings = new String[10];
        mappings[2] = "abc";
        mappings[3] = "def";
        mappings[4] = "ghi";
        mappings[5] = "jkl";
        mappings[6] = "mno";
        mappings[7] = "pqrs";
        mappings[8] = "tuv";
        mappings[9] = "wxyz";
		char[] path = new char[digits.length()];
		backtrack(mappings, digits, 0, path);
        return result;
    }
    
    private void backtrack(String[] mappings, String digits, int k, char[] path) {
        if (k == digits.length()) {
            result.add(new String(path));
        	return;
        }
        String mapping = mappings[digits.charAt(k) - '0'];
        for (int i = 0; i< mapping.length(); i++) {
            path[k] = mapping.charAt(i);
            backtrack(mappings, digits, k + 1, path);
        }
    }
    
}
```

# [77. 组合](https://leetcode-cn.com/problems/combinations/)（中等） 给n个数返回所有k个数的组合

给定两个整数 n 和 k，返回范围 [1, n] 中所有可能的 k 个数的组合。

你可以按 任何顺序 返回答案。

```
示例 1：

输入：n = 4, k = 2
输出：
[
  [2,4],
  [3,4],
  [2,3],
  [1,2],
  [1,3],
  [1,4],
]
```

```
示例 2：

输入：n = 1, k = 1
输出：[[1]]
```



```java
class Solution {
    
    private List<List<Integer>> result = new ArrayList<>();
    
    
    public List<List<Integer>> combine(int n, int k) {
		backtrack(n, k, 1, new ArrayList<>());
        return result;
    }
    
    private void backtrack(int n, int k, int step, List<Integer> path) {
        if (path.size() == k) {
            result.add(new ArrayList<>(path));
			return;
        }
       	if (step == n + 1) {
            return;
        }
        
        backtrack(n, k, step + 1, path);
        path.add(step);
        backtrack(n, k, step + 1, path);
        path.remove(path.size() - 1);
    }
    
}
```





# [78. 子集](https://leetcode-cn.com/problems/subsets/)（中等） 所有的组合

给你一个整数数组 nums ，数组中的元素 互不相同 。返回该数组所有可能的子集（幂集）。

解集 不能 包含重复的子集。你可以按 任意顺序 返回解集。

 

示例 1：

```
输入：nums = [1,2,3]
输出：[[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
```

示例 2：

```
输入：nums = [0]
输出：[[],[0]]
```


提示：

1 <= nums.length <= 10
-10 <= nums[i] <= 10
nums 中的所有元素 互不相同

```java
class Solution {

    private List<List<Integer>> result = new ArrayList<>();

    public List<List<Integer>> subsets(int[] nums) {
        backtrack(nums, 0, new ArrayList<>());
        return result;
    }

    /**
     * 按理来说私有方法不应该写注释的
     * @param nums 可选列表，nums[k] 选还是不选
     * @param k 表示阶段
     * @param path 表示路径
     */
    private void backtrack(int[] nums, int k, List<Integer> path) {
        if (k == nums.length) {
            result.add(new ArrayList(path));
            return;
        }
        
        backtrack(nums, k + 1, path);

        path.add(nums[k]);
        backtrack(nums, k + 1, path);
        path.remove(path.size() - 1);
    }

}
```



# [90. 子集 II](https://leetcode-cn.com/problems/subsets-ii/)（中等）有重复数据

给你一个整数数组 nums ，其中可能包含重复元素，请你返回该数组所有可能的子集（幂集）。

解集 不能 包含重复的子集。返回的解集中，子集可以按 任意顺序 排列。

示例 1：

```
输入：nums = [1,2,2]
输出：[[],[1],[1,2],[1,2,2],[2],[2,2]]
```

示例 2：

```
输入：nums = [0]
输出：[[],[0]]
```


提示：

1 <= nums.length <= 10
-10 <= nums[i] <= 10

```java
class Solution {
    
    private List<List<Integer>> result = new ArrayList<>();
    
    
    public List<List<Integer>> subsetsWithDup(int[] nums) {
		Map<Integer, Integer> map = new HashMap<>();
        // 遍历一遍，统计处各个数存在的次数
    	for (int i = 0; i < nums.length; ++i) {
            int count = 1;
            if (map.containsKey(nums[i])) {
                count += map.get(nums[i]);
            }
            map.put(nums[i], count);
        }
        int n = map.size();
        int[] uniqueNums = new int[n];
        int[] counts = new int[n];
        int k = 0;
        for (int i = 0; i < nums.length; ++i) {
            if (map.containsKey(nums[i])) {
                uniqueNums[k] = nums[i];
                counts[k] = map.get(nums[i]);
                k++;
                map.remove(nums[i]);
            }
        }
        backtrack(uniqueNums, counts, 0, new ArrayList<>());
        return result;
    }
    
    private void backtrack(int[] uniqueNums, int[] counts, int k, List<Integer> path) {
     	if (k == uniqueNums.length) {
            result.add(new ArrayList<>(path));
        	return;
        }
        for (int count = 0; count <= counts[k]; ++count) {
            for (int i = 0; i < count; ++i) {
                path.add(uniqueNums[k]);
            }
            backtrack(uniqueNums, counts, k + 1, path);
            for (int i = 0; i < count; ++i) {
                path.remove(path.size() - 1);
            }
        }
    }
    
}
```



# [46. 全排列](https://leetcode-cn.com/problems/permutations/)（中等） 所有排列

给定一个不含重复数字的数组 nums ，返回其 所有可能的全排列 。你可以 按任意顺序 返回答案。

 

示例 1：

```
输入：nums = [1,2,3]
输出：[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
```

示例 2：

```
输入：nums = [0,1]
输出：[[0,1],[1,0]]
```

示例 3：

```
输入：nums = [1]
输出：[[1]]
```



```java
class Solution {
    
    private List<List<Integer>> result = new ArrayList<>();
    
    
    public List<List<Integer>> permute(int[] nums) {
        List<Integer> path = new ArrayList<>();
        backtrack(nums,0, path);
        return result;
    }
    
    private void backtrack(int[] nums, int k, List<Integer> path) {
        if (k == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }
        
        for (int i = 0; i < nums.length; i++) {
            if (path.contains(nums[i])) {
                continue;
            }
            path.add(nums[i]);
            backtrack(nums, k + 1, path);
            path.remove(path.size() - 1);
        }
    }
    
}
```


# [47. 全排列 II](https://leetcode-cn.com/problems/permutations-ii/)（中等） 有重复数据



# [39. 组合总和](https://leetcode-cn.com/problems/combination-sum/)（中等） 选出某几个数相加为给定和，无重复数据，可以使用多次，不能有重复答案

给你一个 无重复元素 的整数数组 candidates 和一个目标整数 target ，找出 candidates 中可以使数字和为目标数 target 的 所有 不同组合 ，并以列表形式返回。你可以按 任意顺序 返回这些组合。

candidates 中的 同一个 数字可以 无限制重复被选取 。如果至少一个数字的被选数量不同，则两种组合是不同的。 

对于给定的输入，保证和为 target 的不同组合数少于 150 个。

 

示例 1：

```
输入：candidates = [2,3,6,7], target = 7
输出：[[2,2,3],[7]]
解释：
2 和 3 可以形成一组候选，2 + 2 + 3 = 7 。注意 2 可以使用多次。
7 也是一个候选， 7 = 7 。
仅有这两种组合。
```


示例 2：

```
输入: candidates = [2,3,5], target = 8
输出: [[2,2,2,2],[2,3,3],[3,5]]
```


示例 3：

```
输入: candidates = [2], target = 1
输出: []
```


提示：

1 <= candidates.length <= 30
1 <= candidates[i] <= 200
candidate 中的每个元素都 互不相同
1 <= target <= 500

```java
class Solution {
    
    
    
}
```



# [40. 组合总和 II](https://leetcode-cn.com/problems/combination-sum-ii/)（中等）选出某几个数相加为给定和，有重复数据，只能使用一次，不能有重复答案



# [216. 组合总和 III](https://leetcode-cn.com/problems/combination-sum-iii/)（中等） 选出k个数相加为给定和，没有重复数据，只能使用一次

找出所有相加之和为 n 的 k 个数的组合，且满足下列条件：

只使用数字1到9
每个数字 最多使用一次 
返回 所有可能的有效组合的列表 。该列表不能包含相同的组合两次，组合可以以任何顺序返回。

 

示例 1:

```
输入: k = 3, n = 7
输出: [[1,2,4]]
解释:
1 + 2 + 4 = 7
没有其他符合的组合了。
```

示例 2:

```
输入: k = 3, n = 9
输出: [[1,2,6], [1,3,5], [2,3,4]]
解释:
1 + 2 + 6 = 9
1 + 3 + 5 = 9
2 + 3 + 4 = 9
没有其他符合的组合了。
```

示例 3:

```
输入: k = 4, n = 1
输出: []
解释: 不存在有效的组合。
在[1,9]范围内使用4个不同的数字，我们可以得到的最小和是1+2+3+4 = 10，因为10 > 1，没有有效的组合。
```


提示:

2 <= k <= 9
1 <= n <= 60



```java
class Solution {

    private List<List<Integer>> result = new ArrayList<>();


    public List<List<Integer>> combinationSum3(int k, int n) {
        backtrack(k, n, 1, 0, new ArrayList<>());
        return result;
    }

        private void backtrack(int k, int n, int step, int sum, List<Integer> path) {
        if (sum == n && path.size() == k) {
            result.add(new ArrayList<>(path));
            return;
        }
        if (sum >= n || path.size() >= k || step > 9) {
            return;
        }
        backtrack(k, n, step + 1, sum, path);
        path.add(step);
        backtrack(k, n, step + 1, sum + step, path);
        path.remove(path.size() - 1);
    }
    

}
```





# [131. 分割回文串](https://leetcode-cn.com/problems/palindrome-partitioning/)（中等）



# [93. 复原 IP 地址](https://leetcode-cn.com/problems/restore-ip-addresses/)（中等）



# [22. 括号生成](https://leetcode-cn.com/problems/generate-parentheses/)（中等）

数字 n 代表生成括号的对数，请你设计一个函数，用于能够生成所有可能的并且 有效的 括号组合。

 

示例 1：

```
输入：n = 3
输出：["((()))","(()())","(())()","()(())","()()()"]
```

示例 2：

```
输入：n = 1
输出：["()"]
```


提示：

1 <= n <= 8

```java
class Solution {
    
    private List<String> result = new ArrayList<>();
    
    
    public List<String> generateParenthesis(int n) {
		char[] path = new char[2 * n];
        backtrack(n, 0, 0, 0, path);
        return result;
    }
    
    private void backtrack(int n, int leftUsed, int rightUsed, int k, char[] path) {
    	if (k == 2 * n) {
            result.add(String.valueOf(path));
        	return;
        }
        if (leftUsed < n) {
            path[k] = '(';
            backtrack(n, leftUsed + 1, rightUsed, k + 1, path);
        }
        if (leftUsed > rightUsed && rightUsed < n) {
            path[k] = ')';
            backtrack(n, leftUsed, rightUsed + 1, k + 1, path);
        }
    }
    
}
```

