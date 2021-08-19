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

针对待删除节点的子节点个树不同，分三种情况来处理。

1. 要删除的节点没有子节点。只需直接将父节点中指向要删除节点的指针置为 null 即可。
2. 要删除的节点只有一个子节点。只需要更新父节点中指向要删除的节点的指针，让它重新指向要删除节点的子节点即可。
3. 要删除的节点有两个节点。需要找到这个节点的右子树中的“最小节点”，把它踢喊道要删除的节点上（或者左子树的最大节点，总之就是越接近这个被删除节点值的节点）。然后再删除掉这个“最小节点”，因为”最小节点“肯定没有左子节点，所以，可以用上面两条规则来删除这个最小节点。