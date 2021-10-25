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

