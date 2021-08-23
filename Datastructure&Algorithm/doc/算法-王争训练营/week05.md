# 二叉树

1. 二叉树（BT）
   - 树的几个常用概念
   - 二叉树的存储
   - 二叉树的前中后序遍历
2. 二叉查找树（BST）
   - 二叉查找树的定义
   - 二叉查找树的查找、插入、删除操作
3. 平衡二叉查找树（BBST）
4. Java 中的 TreeMap、TreeSet



 # 几个常用的概念

1. 根节点
2. 父节点、字节点
3. 左子节点、右子节点
4. 叶子节点
5. 兄弟节点
6. 子树、左子树、右子树
7. 高度、深度、层
8. 二叉树
9. 满二叉树
10. 完全二叉树

# 二叉树的存储

- 基于指针的存储方式（大部分情况都用这种方式）
- 基于数组的存储方式

```java
public class BinaryTree {
    
    public class Node {
        
        public int data;
        
        public Node left;
        
        public Node right;
        
    }
    
    private Node root = null;
    
}
```

数组存的，根节点存储在下标位 1 的位置。

节点之间的父子关系，通过数组下标计算得到：如果节点 x 的下标是 i

- 下标为 i * 2 的位置存储在左子节点。

- 下标位 2 * i + 1 的位置存储右子节点。
- 下标位 i/2 的位置存储就是它的父节点。

完全二叉树数据间没有空洞，所以数组一般是存完全二叉树。

前序：根左右

中序：左根右

后序：左右根

前中后序遍历都可以看做树上的深度优先遍历，也属于回溯算法。



节点个数为 n 的二叉树，高度是多少？

- 最大：n（退化成链表的时候）
- 最小：log2(n) 对应完全二叉树的情况

一棵树的高度是 h，包含多少个子节点

- 最少：h
- 最多：$2^h-1$​

# 二叉查找树

二叉查找树是一种特殊的二叉树，支持快速地查找、插入、删除数据。

对于二叉查找树中的任意一个节点。

- 其左子树每个节点的值，都小于这个节点的值。
- 其右子树中每个节点的值，都大于这个节点的值。

就可以做二选一呗。

相同的数据，如何处理？

可以把小于改成小于等于，或者大于改成大于等于。

## 查找

### 递归

```java
public class Node {
    
    private int data;
    
    private Node left;
    
    private Node right;
    
    public Node(int data) {
        this.data = data;
    }
    
}

private Node root = null;

public Node findr(Node root, int data) {
    if (root == null) {
        return null;
    }
    if (root.data == data) {
        return root;
    }
    if (data < root.data) {
        return findr(root.left, data);
    }
    else {
        return findr(root.right, data);
    }
}
```

时间复杂度O(H)，H 是高度。空间复杂度也是

### 非递归

```java
public Node find(Node root, int data) {
    Node p = root;
    while (p != null) {
        if (data < p.data) {
            p = p.left;
        }
        else if (data > p.data) {
            p = p.right;
        }
        else {
            return p;
        }
    }
    return null; 
}
```

时间复杂度O(H)，H 是高度。空间复杂度是 O(1)

## 二叉查找树的插入操作

如果要插入的数据比当前节点的值小，并且当前节点的左子树为空，我们就将新数据插入到左子及诶单的位置，如果不为空，我们就再递归遍历左子树，直到找到插入位置。

如果要插入的数据比当前节点的值大，并且当前节点的右子树为空，我们就将新数据直接插到右子节点的位置；如果右子树不为空，我们就再递归遍历右子树，直到找到插入位置。

### 递归

```java
public void in(int data) {
    // 特殊情况处理
    if (root == null) {
        root = new Node(data);
        return;
    }
    insert(root, data);
}

public void insert(Node root, int data) {
    if (data > root.data) {
        if (root.right == null) {
            root.right = new Node(data);
        }
        else {
            insert(root.right, data);
        }
    }
    else {
        if (root.left == null) {
            root.left = new Node(data);
        }
        else {
            insert(root.left, data);
        }
    }
}
```

时间复杂度最高是 O(h)，h 是高度。



### 非递归

```java
public void insert(int data) {
    if (root == null) {
        root = new Node(data);
    	return;
    }
    Node p = root;
    while (p != null) {
        if (data > p.data) {
            if (p.right == null) {
                p.right = new Node(data);
            	return;
            }
            p = p.right;
        }
        else {
            if (p.left == null) {
                p.left = new Node(data);
            	return;
           	}
            p = p.left;
        }
    }
}
```

时间O(h) 空间O(1)

## 删除操作

针对**待删除节点**的子节点个树不同，分三种情况来处理。

1. 要删除的节点没有子节点。只需直接将父节点中指向要删除节点的指针置为 null 即可。
2. 要删除的节点只有一个子节点。只需要更新父节点中指向要删除的节点的指针，让它重新指向要删除节点的子节点即可。
3. 要删除的节点有两个节点。需要找到这个节点的右子树中的“最小节点”，把它替换到要删除的节点上（或者左子树的最大节点，总之就是越接近这个被删除节点值的节点）。然后再删除掉这个“最小节点”，因为”最小节点“肯定没有左子节点，所以，可以用上面两条规则来删除这个最小节点。

```java
public void delete(int data) {
    // p 指向要删除的节点，初始化指向根节点
    Node p = root;
    // pp 是记录的是 p 的父节点
    Node pp = null;
    // 先用非递归的方式，找到 p 节点
    while(p != null && p.data != data) {
        pp = p;
        if (data > p.data) {
            p = p.right;
        }
        else {
            p = p.left;
        }
    }
    // 没有找到
    if (p == null) {
        return;
    }
    // 要删除的节点有两个子节点
    if (p.left != null && p.right != null) {
        Node minP = p.right;
        // minPP 表示 minP 的父节点
        Node minPP = p;
        while (minP.left != null) {
            minPP = minP;
            minP = minP.left;
        }
        // 将 minP 的数据替换到 P 中
        p.data = minP.data;
        // 下面就变成了删除 minP 了
        p = minP;
        pp = minPP;
    }
    // 删除节点是叶子节点或者仅有一个子节点
    // 查找待删除节点 p 的子节点
    Node child = null;
    if (p.left != null) {
        child = p.left;
    }
   	else if (p.right != null) {
        child = p.right;
    }
    
    if (pp == null) {
        // 删除的是根节点
        root = child;
    }
    else if (pp.left == p) {
        pp.left = child;
    }
    else {
        pp.right = child;
    }
}
```



二叉查找树的查找、插入、删除性能，跟树的高度成正比。

解决普通二叉查找树的性能退化问题：
二叉查找树在频繁动态更新过程中，可能会出现树的高度远远大于 $log_2n$ 的情况。

从而导致各个操作的效率下降。极端情况下，二叉树退化为链表，时间复杂度就会退化为 O(n)。



平衡二叉查找树：比如 AVL 树

任意一个节点的左右子树的高度相差不能大于 1.



近似平衡二叉查找树：比如红黑树

树的高度近似 $log_2n$

各个操作的性能：完全二叉树 > 平衡二叉树 > 近似平衡二叉树

维护平衡的成本：完全二叉树 > 平衡二叉树 > 近似平衡二叉树

# 二叉树题型套路讲解

1. 二叉树前中后序遍历
2. 二叉树按层遍历
3. 二叉树上的递归
4. 二叉查找树
5. LCA 最近公共祖先
6. 二叉树转单、双、循环链表
7. 按照遍历结果反向构建二叉树
8. 二叉树上的最长路径和

**题型****1****：二叉树前中后序遍历**

[144. 二叉树的前序遍历](https://leetcode-cn.com/problems/binary-tree-preorder-traversal/)（简单）

[94. 二叉树的中序遍历](https://leetcode-cn.com/problems/binary-tree-inorder-traversal/) （简单）

[145. 二叉树的后序遍历](https://leetcode-cn.com/problems/binary-tree-postorder-traversal/)（简单）

[589. N 叉树的前序遍历](https://leetcode-cn.com/problems/n-ary-tree-preorder-traversal/)（简单）**例题** 

[590. N 叉树的后序遍历](https://leetcode-cn.com/problems/n-ary-tree-postorder-traversal/)（简单）



**题型****2****：二叉树按层遍历**

[剑指 Offer 32 - I. 从上到下打印二叉树](https://leetcode-cn.com/problems/cong-shang-dao-xia-da-yin-er-cha-shu-lcof/)（中等）**例题** 

[102. 二叉树的层序遍历](https://leetcode-cn.com/problems/binary-tree-level-order-traversal/)（中等） 

[剑指 Offer 32 - III. 从上到下打印二叉树 III](https://leetcode-cn.com/problems/cong-shang-dao-xia-da-yin-er-cha-shu-iii-lcof/) （中等）

[429. N 叉树的层序遍历](https://leetcode-cn.com/problems/n-ary-tree-level-order-traversal/)（中等）

[513. 找树左下角的值](https://leetcode-cn.com/problems/find-bottom-left-tree-value/)（中等）



**题型****3****：二叉树上的递归**

[104. 二叉树的最大深度](https://leetcode-cn.com/problems/maximum-depth-of-binary-tree/)（简单）**例题** 

[559. N 叉树的最大深度](https://leetcode-cn.com/problems/maximum-depth-of-n-ary-tree/)（简单）

[剑指 Offer 55 - II. 平衡二叉树](https://leetcode-cn.com/problems/ping-heng-er-cha-shu-lcof/)（中等）**例题** 

[617. 合并二叉树](https://leetcode-cn.com/problems/merge-two-binary-trees/)（简单）

[226. 翻转二叉树](https://leetcode-cn.com/problems/invert-binary-tree/) （简单）

[101. 对称二叉树](https://leetcode-cn.com/problems/symmetric-tree/)（中等）

 [98. 验证二叉搜索树](https://leetcode-cn.com/problems/validate-binary-search-tree/)（中等）



**题型****4****：二叉查找树**

[剑指 Offer 54. 二叉搜索树的第k大节点](https://leetcode-cn.com/problems/er-cha-sou-suo-shu-de-di-kda-jie-dian-lcof/)（中等）

[538. 把二叉搜索树转换为累加树](https://leetcode-cn.com/problems/convert-bst-to-greater-tree/) （中等）

[面试题 04.06. 后继者](https://leetcode-cn.com/problems/successor-lcci/)（中等）





# [144. 二叉树的前序遍历](https://leetcode-cn.com/problems/binary-tree-preorder-traversal/)（简单）

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

    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> result =  new ArrayList<>();
        pre(root, result);
        return result;
    }

    public void pre(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        result.add(node.val);
        pre(node.left, result);
        pre(node.right, result);
    }

}
```



# [94. 二叉树的中序遍历](https://leetcode-cn.com/problems/binary-tree-inorder-traversal/) （简单）

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

    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        in(root, result);
        return result;
    }

    private void in(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        in(node.left, result);
        result.add(node.val);
        in(node.right, result);
    }

}
```



# [145. 二叉树的后序遍历](https://leetcode-cn.com/problems/binary-tree-postorder-traversal/)（简单）

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

    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        post(root, result);
        return result;
    }

    private void post(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        back(node.left, result);
        back(node.right, result);
        result.add(node.val);
    }

}
```



# [589. N 叉树的前序遍历](https://leetcode-cn.com/problems/n-ary-tree-preorder-traversal/)（简单）**例题**



给定一个 N 叉树，返回其节点值的 前序遍历 。

N 叉树 在输入中按层序遍历进行序列化表示，每组子节点由空值 null 分隔（请参见示例）。



进阶：

递归法很简单，你可以使用迭代法完成此题吗?

示例 1：

```
输入：root = [1,null,3,2,4,null,5,6]
输出：[1,3,5,6,2,4]
```

示例 2：

```
输入：root = [1,null,2,3,4,5,null,null,6,7,null,8,null,9,10,null,null,11,null,12,null,13,null,null,14]
输出：[1,2,3,6,7,11,14,4,8,12,5,9,13,10]
```


提示：

N 叉树的高度小于或等于 1000
节点总数在范围 [0, 10^4] 内
通过次数85,002提交次数114,150

 递归实现

```java
/*
// Definition for a Node.
class Node {
    public int val;
    public List<Node> children;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
};
*/

class Solution {

    public List<Integer> preorder(Node root) {
        List<Integer> result = new ArrayList<>();
        prev(root, result);
        return result;
    }

    private void prev(Node node, List<Integer> result) {
        if (node == null) {
            return;
        }
        result.add(node.val);
        for (Node n : node.children) {
            prev(n, result);
        }
    }

}
```

非递归实现

```java
/*
// Definition for a Node.
class Node {
    public int val;
    public List<Node> children;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
};
*/

class Solution {

    public List<Integer> preorder(Node root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        LinkedList<Node> stack = new LinkedList<>();
        stack.add(root);
        while (!stack.isEmpty()) {
            Node node = stack.pollLast();
            result.add(node.val);
            // 先反转，后面再进行弹栈，因为是根左右，所以先加上面的，再弹出左边的，最后是右边的内容弹出
            Collections.reverse(node.children);
            for (Node item : node.children) {
                stack.add(item);
            }
        }
        return result;
    }

}
```



# [590. N 叉树的后序遍历](https://leetcode-cn.com/problems/n-ary-tree-postorder-traversal/)（简单）

递归

```java
/*
// Definition for a Node.
class Node {
    public int val;
    public List<Node> children;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
};
*/

class Solution {

    public List<Integer> postorder(Node root) {
        List<Integer> result = new ArrayList<>();
        post(root, result);
        return result;
    }

    private void post(Node node, List<Integer> result) {
        if (node == null) {
            return;
        }
        for (Node n : node.children) {
            post(n, result);
        }
        result.add(node.val);
    }

}
```

迭代

```java
/*
// Definition for a Node.
class Node {
    public int val;
    public List<Node> children;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
};
*/

class Solution {

    public List<Integer> postorder(Node root) {
        LinkedList<Integer> result = new LinkedList<>();
        if (root == null) {
            return result;
        }

        Deque<Node> stack = new ArrayDeque<>();
        stack.addLast(root);
        while (!stack.isEmpty()) {
            Node node = stack.removeLast();
            result.addFirst(node.val);
            for (int i = 0; i < node.children.size(); i++) {
                stack.addLast(node.children.get(i));
            }
        }
        return result; 
    }

}
```



# [剑指 Offer 32 - I. 从上到下打印二叉树](https://leetcode-cn.com/problems/cong-shang-dao-xia-da-yin-er-cha-shu-lcof/)（中等）**例题**

从上到下打印出二叉树的每个节点，同一层的节点按照从左到右的顺序打印。

 

例如:
给定二叉树: [3,9,20,null,null,15,7],

        3
       / \
      9  20
        /  \
       15   7

返回：

```
[3,9,20,15,7]
```



```java
class Solution {
    
    public int[] levelOrder(TreeNode root) {
        if (root == null) {
            return new int[0];
        }
        List<Integer> result = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            result.add(node.val);
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        }
        int[] resultArray = new int[result.size()];
        int index = 0;
        for (Integer i : result) {
            resultArray[index++] = i;
        }
        return resultArray;
    }
    
}
```



# [102. 二叉树的层序遍历](https://leetcode-cn.com/problems/binary-tree-level-order-traversal/)（中等）

给你一个二叉树，请你返回其按 层序遍历 得到的节点值。 （即逐层地，从左到右访问所有节点）。

 

示例：
二叉树：[3,9,20,null,null,15,7],

        3
       / \
      9  20
        /  \
       15   7

返回其层序遍历结果：

```
[
  [3],
  [9,20],
  [15,7]
]
```




```java

```





# [剑指 Offer 32 - III. 从上到下打印二叉树 III](https://leetcode-cn.com/problems/cong-shang-dao-xia-da-yin-er-cha-shu-iii-lcof/) （中等）



# [429. N 叉树的层序遍历](https://leetcode-cn.com/problems/n-ary-tree-level-order-traversal/)（中等）



# [513. 找树左下角的值](https://leetcode-cn.com/problems/find-bottom-left-tree-value/)（中等）



# [104. 二叉树的最大深度](https://leetcode-cn.com/problems/maximum-depth-of-binary-tree/)（简单）**例题**



给定一个二叉树，找出其最大深度。

二叉树的深度为根节点到最远叶子节点的最长路径上的节点数。

说明: 叶子节点是指没有子节点的节点。

示例：
给定二叉树 [3,9,20,null,null,15,7]，

        3
       / \
      9  20
        /  \
       15   7
返回它的最大深度 3 

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

    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        // 递归求问题，求左子树和右子树的最大深度
        return Math.max(maxDepth(root.left), maxDepth(root.right)) + 1;
    }

}
```



# [559. N 叉树的最大深度](https://leetcode-cn.com/problems/maximum-depth-of-n-ary-tree/)（简单）



# [剑指 Offer 55 - II. 平衡二叉树](https://leetcode-cn.com/problems/ping-heng-er-cha-shu-lcof/)（中等）**例题**

输入一棵二叉树的根节点，判断该树是不是平衡二叉树。如果某二叉树中任意节点的左右子树的深度相差不超过1，那么它就是一棵平衡二叉树。

 

示例 1:

给定二叉树 [3,9,20,null,null,15,7]

        3
       / \
      9  20
        /  \
       15   7

返回 true 。

示例 2:

给定二叉树 [1,2,2,3,3,null,null,4,4]

           1
          / \
         2   2
        / \
       3   3
      / \
     4   4

返回 false 。

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
 
     private boolean isBalanced = true;
 
     public boolean isBalanced(TreeNode root) {
         height(root);
         return isBalanced;
     }
 
     private int height(TreeNode node) {
         if (node == null) {
             return 0;
         }
         if (!isBalanced) {
             // 如果已经是不平衡的二叉树了，就提前终止递归
             return 0;
         }
         int leftHeight = height(node.left);
         int rightHeight = height(node.right);
         if (Math.abs(leftHeight - rightHeight) > 1) {
             isBalanced = false;
         }
         return Math.max(leftHeight, rightHeight) + 1;
     }
 
 }
 ```





# [617. 合并二叉树](https://leetcode-cn.com/problems/merge-two-binary-trees/)（简单）



# [226. 翻转二叉树](https://leetcode-cn.com/problems/invert-binary-tree/) （简单）



# [101. 对称二叉树](https://leetcode-cn.com/problems/symmetric-tree/)（中等）



# [98. 验证二叉搜索树](https://leetcode-cn.com/problems/validate-binary-search-tree/)（中等）



# [剑指 Offer 54. 二叉搜索树的第k大节点](https://leetcode-cn.com/problems/er-cha-sou-suo-shu-de-di-kda-jie-dian-lcof/)（中等）

给定一棵二叉搜索树，请找出其中第k大的节点。

 

示例 1:

输入: root = [3,1,4,null,2], k = 1

```
   3
  / \
 1   4
  \
   2
```

输出: 4
示例 2:

输入: root = [5,3,6,2,4,null,null,1], k = 3

```
       5
      / \
     3   6
    / \
   2   4
  /
 1
```


输出: 4


限制：

1 ≤ k ≤ 二叉搜索树元素个数

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

    public int kthLargest(TreeNode root, int k) {
        // 二叉搜索树的种序遍历是有序的，所以只需要找到对应的反的序列就行
        if (root == null) {
            return 0;
        }
        List<Integer> list = new ArrayList<>();
        in(root, list);
        return list.get(list.size() - k);
    }

    private void in(TreeNode node, List<Integer> list) {
        if (node == null) {
            return;
        }
        in(node.left, list);
        list.add(node.val);
        in(node.right, list);
    } 

}



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

    int count = 0;

    int result;

    public int kthLargest(TreeNode root, int k) {
        if (root == null) {
            return 0;
        }
        in(root, k);
        return result;
    }

    private void in(TreeNode node, int k) {
        // 提前终止递归，剪枝
        if (count >= k) {
            return;
        }
        if (node == null) {
            return;
        }
        // 优先去右边找
        in(node.right, k);
        count++;
        if (count == k) {
            result = node.val;
            return;
        }
        in(node.left, k);
    } 

}
```



# [538. 把二叉搜索树转换为累加树](https://leetcode-cn.com/problems/convert-bst-to-greater-tree/) （中等）



# [面试题 04.06. 后继者](https://leetcode-cn.com/problems/successor-lcci/)（中等）

