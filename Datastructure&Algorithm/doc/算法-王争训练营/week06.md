**题型** **5**：**LCA**最近公共祖先**

[236. 二叉树的最近公共祖先](https://leetcode-cn.com/problems/lowest-common-ancestor-of-a-binary-tree/)（中等） 

[剑指 Offer 68 - I. 二叉搜索树的最近公共祖先](https://leetcode-cn.com/problems/er-cha-sou-suo-shu-de-zui-jin-gong-gong-zu-xian-lcof/)（中等） 



**题型****6****：二叉树转单、双、循环链表**

[114. 二叉树展开为链表](https://leetcode-cn.com/problems/flatten-binary-tree-to-linked-list/)（中等）

[面试题 17.12. BiNode](https://leetcode-cn.com/problems/binode-lcci/)（中等）

[剑指 Offer 36. 二叉搜索树与双向链表](https://leetcode-cn.com/problems/er-cha-sou-suo-shu-yu-shuang-xiang-lian-biao-lcof/)（中等）

[面试题 04.03. 特定深度节点链表](https://leetcode-cn.com/problems/list-of-depth-lcci/)（中等）



**题型****7****：按照遍历结果反向构建二叉树**

[105. 从前序与中序遍历序列构造二叉树](https://leetcode-cn.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/)（中等）

[889. 根据前序和后序遍历构造二叉树](https://leetcode-cn.com/problems/construct-binary-tree-from-preorder-and-postorder-traversal/)（中等）

[106. 从中序与后序遍历序列构造二叉树](https://leetcode-cn.com/problems/construct-binary-tree-from-inorder-and-postorder-traversal/)（中等）

[剑指 Offer 33. 二叉搜索树的后序遍历序列](https://leetcode-cn.com/problems/er-cha-sou-suo-shu-de-hou-xu-bian-li-xu-lie-lcof/)（中等）



以下选做，等到学完回溯之后再来做

**题型****8****：二叉树上的最长路径和**

[543. 二叉树的直径](https://leetcode-cn.com/problems/diameter-of-binary-tree/)（简单）

[剑指 Offer 34. 二叉树中和为某一值的路径](https://leetcode-cn.com/problems/er-cha-shu-zhong-he-wei-mou-yi-zhi-de-lu-jing-lcof/)（中等）

[124. 二叉树中的最大路径和](https://leetcode-cn.com/problems/binary-tree-maximum-path-sum/) （困难）

[437. 路径总和 III](https://leetcode-cn.com/problems/path-sum-iii/) （困难）



# [236. 二叉树的最近公共祖先](https://leetcode-cn.com/problems/lowest-common-ancestor-of-a-binary-tree/)（中等）

给定一个二叉树, 找到该树中两个指定节点的最近公共祖先。

百度百科中最近公共祖先的定义为：“对于有根树 T 的两个节点 p、q，最近公共祖先表示为一个节点 x，满足 x 是 p、q 的祖先且 x 的深度尽可能大（一个节点也可以是它自己的祖先）。”

 

示例 1：

```
输入：root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1
输出：3
解释：节点 5 和节点 1 的最近公共祖先是节点 3 。
```

示例 2：

```
输入：root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 4
输出：5
解释：节点 5 和节点 4 的最近公共祖先是节点 5 。因为根据定义最近公共祖先节点可以为节点本身。
```


示例 3：

```
输入：root = [1,2], p = 1, q = 2
输出：1
```

提示：

树中节点数目在范围 [2, 105] 内。
-109 <= Node.val <= 109
所有 Node.val 互不相同 。
p != q
p 和 q 均存在于给定的二叉树中。

```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
class Solution {
    
    private TreeNode lca = null;

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        dfs(root, p, q);
        return lca;
    }
    
    private int dfs(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {
            return 0;
        }
        int leftContains = dfs(root.left, p, q);
        // 提前退出
        if (lca != null) {
            return 2;
        }
        int rightContains = dfs(root.right, p, q);
        // 提前退出
        if (lca != null) {
            return 2;
        }
        int rootContains = 0;
        // 如果跟节点和其中任意一个节点一样，那么 rootContains 就是 1
        if (root == p || root == q) {
            rootContains = 1;
        }
        // 如果根节点没有，左右节点各有一个，那么就是根节点了
        if (rootContains == 0 && leftContains == 1 && rightContains == 1) {
            lca = root;
        }
        // 如果根节点有 1，左右节点或有一个，那就是根节点了
        if (rootContains == 1 && (leftContains ==1 || rightContains == 1)) {
            lca = root;
        }
        return leftContains + rightContains + rootContains;
    }
    
}
```

# [剑指 Offer 68 - I. 二叉搜索树的最近公共祖先](https://leetcode-cn.com/problems/er-cha-sou-suo-shu-de-zui-jin-gong-gong-zu-xian-lcof/)（中等）

给定一个二叉搜索树, 找到该树中两个指定节点的最近公共祖先。

百度百科中最近公共祖先的定义为：“对于有根树 T 的两个结点 p、q，最近公共祖先表示为一个结点 x，满足 x 是 p、q 的祖先且 x 的深度尽可能大（一个节点也可以是它自己的祖先）。”

例如，给定如下二叉搜索树:  root = [6,2,8,0,4,7,9,null,null,3,5]



 

示例 1:

```
输入: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 8
输出: 6 
解释: 节点 2 和节点 8 的最近公共祖先是 6。
```

示例 2:

```
输入: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 4
输出: 2
解释: 节点 2 和节点 4 的最近公共祖先是 2, 因为根据定义最近公共祖先节点可以为节点本身。
```


说明:

所有节点的值都是唯一的。
p、q 为不同节点且均存在于给定的二叉搜索树中。

 

# [114. 二叉树展开为链表](https://leetcode-cn.com/problems/flatten-binary-tree-to-linked-list/)（中等）

给你二叉树的根结点 root ，请你将它展开为一个单链表：

展开后的单链表应该同样使用 TreeNode ，其中 right 子指针指向链表中下一个结点，而左子指针始终为 null 。
展开后的单链表应该与二叉树 先序遍历 顺序相同。

![img](https://assets.leetcode.com/uploads/2021/01/14/flaten.jpg)

示例 1：

```
输入：root = [1,2,5,3,4,null,6]
输出：[1,null,2,null,3,null,4,null,5,null,6]
```


示例 2：

```
输入：root = []
输出：[]
```

示例 3：

```
输入：root = [0]
输出：[0]
```



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
    
    private TreeNode dummyHead = new TreeNode();
    
    private TreeNode tail = dummyHead;
    
    
    public void flatten(TreeNode root) {
		preorder(root);
    }
    
    private void preorder(TreeNode root) {
        if (root == null) {
            return;
        }
        TreeNode left = root.left;
        TreeNode right = root.right;
        // 把遍历到的节点放到结果链表中
        tail.right = root;
        tail = root;
        root.left = null;
        
        preorder(left);
        preorder(right);
    }
    
}
```





# [面试题 17.12. BiNode](https://leetcode-cn.com/problems/binode-lcci/)（中等）



# [剑指 Offer 36. 二叉搜索树与双向链表](https://leetcode-cn.com/problems/er-cha-sou-suo-shu-yu-shuang-xiang-lian-biao-lcof/)（中等）



# [面试题 04.03. 特定深度节点链表](https://leetcode-cn.com/problems/list-of-depth-lcci/)（中等）



# [105. 从前序与中序遍历序列构造二叉树](https://leetcode-cn.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/)（中等）



# [889. 根据前序和后序遍历构造二叉树](https://leetcode-cn.com/problems/construct-binary-tree-from-preorder-and-postorder-traversal/)（中等）



# [106. 从中序与后序遍历序列构造二叉树](https://leetcode-cn.com/problems/construct-binary-tree-from-inorder-and-postorder-traversal/)（中等）



# [剑指 Offer 33. 二叉搜索树的后序遍历序列](https://leetcode-cn.com/problems/er-cha-sou-suo-shu-de-hou-xu-bian-li-xu-lie-lcof/)（中等）