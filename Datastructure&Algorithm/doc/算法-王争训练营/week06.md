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



# 堆

## 定义

1. 堆必须是一个完全二叉树。
2. 堆中每个节点的值必须大于等于（或者小于等于）其子树中每个节点的值。

大顶堆

每个节点的值都大于等于树中每个节点的值

小顶堆

每个节点的值都小于等于树中每个节点的值

堆是完全二叉树，所以，适合用数组来存储。

一般是从 1 开始存的，从 0 开始存处理的时候比较麻烦。

## 操作

1. 往堆中插入数据
2. 取堆顶元素
3. 删除堆顶元素
4. 更新元素值



### 往堆中插入数据

将新数据插入到数组的末尾，然后执行自下而上的堆化操作。

以大顶堆举例。

上浮和下沉。

```java
public class Heap {
    
    private final int a[];
    
    private final int capacity;
    
    private int size;
    
    
    public Heap(int capacity) {
        a = new int[capacity + 1];
        capacity = capacity;
        size = 0;
    }
    
    public void insert(int data) {
        if (size >= capacity) {
            throw new IndexOutOfBoundsException("Current heap can't insert more data");
        }
        // size 先加上
        ++size;
        // 现在末尾加上这个 data
        a[size] = data;
        int i = size;
        // 自下往上堆化
        while (i/2 > 0 && a[i] > [i/2]) {
            // 交换下标为 i 和 i/2 的两个元素
            swap(a, i, i/2);
            i = i/2;
        }
    }
    
    private void swap(int a[], int i, int j) {
        int tmp = a[i];
        a[i] = a[j];
    	a[j] = tmp;
    }
    
}
```

### 取堆顶元素

大顶堆的堆顶是最大的元素

小顶堆的堆顶是最小的元素

```java
public int top() {
    if (size == 0) {
        return Integer.MIN_VALUE;
    }
    return a[1];
}
```

### 删除堆顶元素

 把最后一个节点放到堆顶，然后利用自上而下的堆化方式让堆重新满足定义。

```
[/,28,25,17,18,16,13,9,8,12, , , ]
```

堆顶删除后，要把最后面的元素放到堆顶，再进行自上而下的堆化，这样就满足完全二叉树的定义。

```java
public void removeTop() {
    if (size == 0) {
        return;
    }
    a[1] = a[size];
    --size;
    heapify(a, size, 1);
}

private void heapify(int a[], int n, int i) {
    while(true) {
        int maxPos = i;
        // 和左子节点比较，如果左子节点大于当前节点，左子节点的值上来。
        if (i * 2 <= n && a[i] < a[i * 2]) {
            maxPos = i * 2;
        }
        // 和右子节点比较，如果右子节点大于这个节点，右子节点上来
        if (i * 2 + 1 <= n && a[maxPos] < a[i * 2 + 1]) {
            maxPos = i * 2 +1;
        }
        if (maxPos == i) {
            break;
        }
        swap(a, i, maxPos);
    	i = maxPos;
    }
}
```

### 更新元素值

如果更新后的值变小了，就进行：自上而下的堆化。

如果更新后的值变大了，就进行：自下而上的堆化。

```java
```

