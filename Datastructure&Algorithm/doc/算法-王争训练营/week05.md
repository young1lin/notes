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



# I  hava no idea about this

```java
class Solution {
	// 模拟函数调用栈
    private class SFrame {
        
        public int status = 1;
        
        public TreeNode node = null;
        
        public SFrame(int status, TreeNode node) {
            this.status = status;
            this.node = node;
        }
        
    }
    
    List<Integer> result = new ArrayList<>();
    
    public List<Integer preOrder(TreeNode root) {
        Stack<SFrame> stack = new Stack<>();
        TreeNode p = root;
        while (true) {
            // 一路向左
            while (p != null) {
                stack.push(new SFrame(1, p));
                result.add(p.val);
                p = p.left;
            }
            // 左右子树都遍历完，再次访问到这个节点时，2 -> 3
            while (!stack.isEmpty() && stack.peek().status == 2) {
                stack.peek().status = 3;
                stack.pop();
            }
            if (stack.isEmpty()) {
                break;
            }
            // 左子树遍历完，再次访问到这个节点时 1-> 2
            stack.peek().status = 2;
            p = statck.peek().node.right;
        }
        return result;
    }
    
}
```





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

执行用时：1 ms, 在所有 Java 提交中击败了92.66%的用户

内存消耗：38.5 MB, 在所有 Java 提交中击败了80.70%的用户


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

    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            List<Integer> curLevelNodes = new ArrayList<>();
            int curLevelNum = queue.size();
            for (int i = 0; i < curLevelNum; i++) {
                TreeNode treeNode = queue.poll();
                curLevelNodes.add(treeNode.val);
                if (treeNode.left != null) {
                    queue.add(treeNode.left);
                }
                if (treeNode.right != null) {
                    queue.add(treeNode.right);
                }
            }
            result.add(curLevelNodes);
        }
        return result;
    }

}
```

递归实现

执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户

内存消耗：38.6 MB, 在所有 Java 提交中击败了48.20%的用户

```java
class Solution {
    
    private List<List<Integer>> result = new ArrayList<>();
    
    
    public List<List<Integer>> levelOrder(TreeNode root) {
        dfs(root, 0);
        return result;
    }
    
    private void dfs(TreeNode root, int level) {
        if (root == null) {
            return;
        }
        if (level > result.size() - 1) {
            result.add(new ArrayList<>());
        }
        result.get(level).add(root.val);
        dfs(root.left, level + 1);
        dfs(root.right, level + 1);
    }
    
}
```



# [剑指 Offer 32 - III. 从上到下打印二叉树 III](https://leetcode-cn.com/problems/cong-shang-dao-xia-da-yin-er-cha-shu-iii-lcof/) （中等）

请实现一个函数按照之字形顺序打印二叉树，即第一行按照从左到右的顺序打印，第二层按照从右到左的顺序打印，第三行再按照从左到右的顺序打印，其他行以此类推。

 

例如:
给定二叉树: [3,9,20,null,null,15,7],

        3
       / \
      9  20
        /  \
       15   7

返回其层次遍历结果：

```
[
  [3],
  [20,9],
  [15,7]
]
```



一看就会，一做就懵逼系列。

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


    // 牛的，这个 BFS
    public List<List<Integer>> levelOrder(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        List<List<Integer>> res = new ArrayList<>();
        if(root != null) queue.add(root);
        while(!queue.isEmpty()) {
            LinkedList<Integer> tmp = new LinkedList<>();
            for(int i = queue.size(); i > 0; i--) {
                TreeNode node = queue.poll();
                // 偶数层 -> 队列头部
                if(res.size() % 2 == 0) {
                    tmp.addLast(node.val);
                } 
                // 奇数层 -> 队列尾部
                else {
                    tmp.addFirst(node.val); 
                }
                if(node.left != null) queue.add(node.left);
                if(node.right != null) queue.add(node.right);
            }
            res.add(tmp);
        }
        return res;
    }

}
```

递归实现

```java
class Solution {
    
    private List<List<Integer>> result = new ArrayList<>();
    
    
    public List<List<Integer>> levelOrder(TreeNode root) {
    	dfs(root, 0);
                // 将索引为奇数的数组的逆序
        for (int i = 0; i < result.size(); i++) {
            if ((i%2) == 0) {
                Collections.reverse(result.get(i));
            }
        }
        return result;
    }
  	
    private void dfs(TreeNode node, int level) {
        if (node == null) {
            return;
        }
        if (level > result.size() - 1) {
            result.add(new ArrayList<>());
        }
        result.get(level).add(node.val);
        dfs(node.right, level + 1);
        dfs(node.left, level + 1);
    }
    
}
```



# [429. N 叉树的层序遍历](https://leetcode-cn.com/problems/n-ary-tree-level-order-traversal/)（中等）

给定一个 N 叉树，返回其节点值的层序遍历。（即从左到右，逐层遍历）。

树的序列化输入是用层序遍历，每组子节点都由 null 值分隔（参见示例）。

示例 1：

![img](https://assets.leetcode.com/uploads/2018/10/12/narytreeexample.png)

```
输入：root = [1,null,3,2,4,null,5,6]
输出：[[1],[3,2,4],[5,6]]
```

示例 2：

![img](https://assets.leetcode.com/uploads/2019/11/08/sample_4_964.png)

```
输入：root = [1,null,2,3,4,5,null,null,6,7,null,8,null,9,10,null,null,11,null,12,null,13,null,null,14]
输出：[[1],[2,3,4,5],[6,7,8,9,10],[11,12,13],[14]]
```


提示：

树的高度不会超过 1000
树的节点总数在 [0, 10^4] 之间




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

// This code is a modified version of the code posted by
// #zzzliu on the discussion forums.
class Solution {

    public List<List<Integer>> levelOrder(Node root) {      
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            List<Integer> level = new ArrayList<>();
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Node node = queue.poll();
                level.add(node.val);
                queue.addAll(node.children);
            }
            result.add(level);
        }
        return result;
    }
    
}
```





# [513. 找树左下角的值](https://leetcode-cn.com/problems/find-bottom-left-tree-value/)（中等）

给定一个二叉树的 根节点 root，请找出该二叉树的 最底层 最左边 节点的值。

假设二叉树中至少有一个节点。

 

示例 1:

![img](https://assets.leetcode.com/uploads/2020/12/14/tree1.jpg)

```
输入: root = [2,1,3]
输出: 1
```

示例 2:

![img](https://assets.leetcode.com/uploads/2020/12/14/tree2.jpg)

```
输入: [1,2,3,4,null,5,6,null,null,7]
输出: 7
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

    public int findBottomLeftValue(TreeNode root) {
        // 这不就是前面的之字型，改成从右到左吗
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int result = -1;
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            result = node.val;
            // 从右到左
            if (node.right != null) {
                queue.add(node.right);
            }
            if (node.left != null) {
                queue.add(node.left);
            }
        }
        return result;
    }

}
```



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

给定一个 N 叉树，找到其最大深度。

最大深度是指从根节点到最远叶子节点的最长路径上的节点总数。

N 叉树输入按层序遍历序列化表示，每组子节点由空值分隔（请参见示例）。



示例 1：

![img](https://assets.leetcode.com/uploads/2018/10/12/narytreeexample.png)

```
输入：root = [1,null,3,2,4,null,5,6]
输出：3
```



示例 2：

![img](https://assets.leetcode.com/uploads/2019/11/08/sample_4_964.png)

```
输入：root = [1,null,2,3,4,5,null,null,6,7,null,8,null,9,10,null,null,11,null,12,null,13,null,null,14]
输出：5
```


提示：

- 树的深度不会超过 1000 。

- 树的节点数目位于 `[0, 104]` 之间。

```java
class Solution {
  
    public int maxDepth(Node root) {
        if (root == null) {
            return 0;
        } 
        else if (root.children.isEmpty()) {
            return 1;  
        } 
        else {
            List<Integer> heights = new LinkedList<>();
            for (Node item : root.children) {
                heights.add(maxDepth(item)); 
            }
            return Collections.max(heights) + 1;
        }
    }

}

```



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

给定两个二叉树，想象当你将它们中的一个覆盖到另一个上时，两个二叉树的一些节点便会重叠。

你需要将他们合并为一个新的二叉树。合并的规则是如果两个节点重叠，那么将他们的值相加作为节点合并后的新值，否则不为 NULL 的节点将直接作为新二叉树的节点。

示例 1:

```
输入: 
	Tree 1                     Tree 2                  
          1                         2                             
         / \                       / \                            
        3   2                     1   3                        
       /                           \   \                      
      5                             4   7                  
输出: 
合并后的树:
	     3
	    / \
	   4   5
	  / \   \ 
	 5   4   7
```



```java
class Solution {
    
    public TreeNode mergeTrees(TreeNode root1, TreeNode root2) {
    	if (root1== null && root2 == null) {
            return null;
        }    
        TreeNode newNode = new TreeNode(0);
        if (root1 != null) {
            newNode.val += root1.val;
        }
        if (root2 != null) {
            newNode.val += root2.val;
        }
        // 合并左子树
        TreeNode leftTree = null;
        if (root1 != null) {
            leftTree = root1.left;
        }
        TreeNode leftTree2 = null;
        if (root2 != null) {
            leftTree2 = root2.left;
        }
        TreeNode leftRoot = mergeTrees(leftTree, leftTree2);
        
       	// 合并右子树
        TreeNode rightTree = null;
        if (root1 != null) {
            rightTree = root1;
        }
        TreeNode rightTree2 = null;
        if (root2 != null) {
            rightTree2 = root2;
        }
        TreeNode rightRoot = mergeTrees(rightTree, rightTree2);
        
        // 拼接 root、左子树、右子树
        newNode.left = leftRoot;
        newNode.right = rightRoot;
        return newNode;
    }
    
}
```



# [226. 翻转二叉树](https://leetcode-cn.com/problems/invert-binary-tree/) （简单）

  翻转一棵二叉树。

示例：

输入：

          4
        /   \
      2     7
     / \   / \
    1   3 6   9

输出：

          4
        /   \
      7     2
     / \   / \
    9   6 3   1
递归

左子树反转和右子树反转，然后交换左右子树，就能得到翻转的二叉树

```java
class Solution {
    
    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        TreeNode leftNode = invertTree(root.left);
        TreeNode rightNode =  invertTree(root.right);
        root.right = leftNode;
        root.left = rightNode;
        return root;
    }

}
```

# [101. 对称二叉树](https://leetcode-cn.com/problems/symmetric-tree/)（中等）

给定一个二叉树，检查它是否是镜像对称的。

 

例如，二叉树 [1,2,2,3,4,4,3] 是对称的。

        1
       / \
      2   2
     / \ / \
    3  4 4  3

但是下面这个 [1,2,2,null,3,null,3] 则不是镜像对称的:

        1
       / \
      2   2
       \   \
       3    3



进阶：

你可以运用递归和迭代两种方法解决这个问题吗？

递归

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

    public boolean isSymmetric(TreeNode root) {
        if (root == null) {
            return true;
        }
        return isSymmetric(root.left, root.right);
    }

    public boolean isSymmetric(TreeNode left, TreeNode right) {
        if (left == null && right == null) {
            return true;
        }
        if (left != null && right != null && left.val == right.val) {
            // 【左子树的右子树】和【右子树的左子树】相等，且【左子树的左子树】和【右子树的右子树】相等
            return isSymmetric(left.right, right.left) && isSymmetric(left.left, right.right);
        }
        return false;
    }


}
```



# [98. 验证二叉搜索树](https://leetcode-cn.com/problems/validate-binary-search-tree/)（中等）



递归

```java
class Solution {
    
    private boolean isValid = true;
    
    public boolean isValidBST(TreeNode root) {
        if (root == null) {
            return true;
        }
        dfs(root);
        return isValid;
    }
    
    private int[] dfs(TreeNode root) {
        if (isValid == false) {
            return null;
        }
        int min = root.val;
        int max = root.val;
        if (root.left != null) {
            int[] leftMinMax = dfs(root.left);
            if (isValid == false) {
                return null;
            }
            if (leftMinMax[1] >= root.val) {
                isValid = false;
                return null;
            }
            min = leftMinMax[0];
        }
        if (root.right != null) {
            int[] rightMinMax = dfs(root.right);
            if (isValid == false) {
                return null;
            }
            if (rightMinMax[0] <= root.val) {
                isValid = false;
                return null;
            }
            max = rightMinMax[1];
        }
        return new int[]{min, max};
    }
    
} 
```





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


给出二叉 **搜索** 树的根节点，该树的节点值各不相同，请你将其转换为累加树（Greater Sum Tree），使每个节点 `node` 的新值等于原树中大于或等于 `node.val` 的值之和。

提醒一下，二叉搜索树满足下列约束条件：

-   节点的左子树仅包含键 **小于** 节点键的节点。
-   节点的右子树仅包含键 **大于** 节点键的节点。
-   左右子树也必须是二叉搜索树。

示例 1
![](https://assets.leetcode-cn.com/aliyun-lc-upload/uploads/2019/05/03/tree.png)
```
输入：[4,1,6,0,2,5,7,null,null,null,3,null,null,null,8]
输出：[30,36,21,36,35,26,15,null,null,null,33,null,null,null,8]
```
示例 2：
```
输入：root = [0,null,1]
输出：[1,null,1]
```
示例 3：
```
输入：root = [1,0,2]
输出：[3,3,2]
```
示例 4：
```
输入：root = [3,2,4,1]
输出：[7,9,4,10]
```
递归实现
```java
class Solution {  
  
    private int sum = 0;  
  
	 public TreeNode convertBST(TreeNode root) {  
		 if (root != null) {

		 // 二叉搜索树的中序遍历是单调递增，【左根右】

		 // 所以【右根左】是单调递减的

		 // 从 0 开始累加，

		 convertBST(root.right);

		 sum += root.val;

		 root.val = sum;

		 convertBST(root.left);

		 }
		 return root;
	 }
	 
}
```

# [面试题 04.06. 后继者](https://leetcode-cn.com/problems/successor-lcci/)（中等）
设计一个算法，找出二叉搜索树中指定节点的“下一个”节点（也即中序后继）。

如果指定节点没有对应的“下一个”节点，则返回null。
```
示例 1:

输入: root = [2,1,3], p = 1

  2
 / \
1   3

输出: 2
```
示例 2:
```
输入: root = [5,3,6,2,4,null,null,1], p = 6

      5
     / \
    3   6
   / \
  2   4
 /   
1

输出: null
```

非递归实现
```java
/**

 * Definition for a binary tree node.

 * public class TreeNode {

 *     int val;

 *     TreeNode left;

 *     TreeNode right;

 *     TreeNode(int x) { val = x; }

 * }

 */

class Solution {

  

	 public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
		 TreeNode pre = null;
		 while(root != p){
			 //右边
			 if(p.val > root.val){ 
				root = root.right;
			 }
			 //左边
			 else{ 
				pre = root;
				root = root.left;
			 }
		 }

		 //假如没有右子树

		 if(root.right == null){

		 	return pre;

		 } 
		 else{

		 	root = root.right;

			 while(root.left != null){

			 	root = root.left;

			 }

			 return root;

		 } 

	 }

  

}
```
