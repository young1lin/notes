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



# 堆排序

两大步骤

1. 建堆，将数组中的数据原地组织成一个堆。
2. 排序，基于堆排序数据。

 ## 堆排序-建堆

第一种实现思路：从前往后处理每个元素，对每个元素执行自上而下的堆化。O (nlogn) 时间复杂度

第二种实现思路：从后往前处理每个元素，对每个元素执行自上而下的堆化。O(n) 时间复杂度



 ```java
 private void buildHeap(int[] a, int n) {
     for (int i = n/2; i >= 1; --i) {
         heapify(a, n, i);
     }
 }
 
 private void heapify(int[] a, int n, int i) {
     while (true) {
         int maxPos = i;
         if (i * 2 <= n && a[i] < a[i * 2]) {
             maxPos = i * 2;
         }
         if (i * 2 + 1 <= n && a[maxPos] < a[i * 2 +1]) {
             maxPos = i * 2 + 1;
         }
         swap(a, i, maxPos);
         i = maxPos;
     }
 }
 ```



## 排序

1. 将堆顶元素与最后一个元素交换。最大元素就放到了下标位 n 的位置。堆大小 -1 
2. 交换之后的堆顶元素，自上而下堆化，重新构建成堆。
3. 一直重复这个过程，直到最后堆中只剩下一个元素，排序工作就完成了。

```java
public void sort(int a[], int n) {
    buildHeap(a, n);
    int k = n;
    while (k > 1) {
        swap(a, 1, k);
        --k;
        heapify(a, k, 1);
    }
}
```

## 堆排序分析

1. 堆排序是否是原地排序算法？

   是

2. 堆排序是否是稳定排序算法？

   否

3. 堆排序的时间复杂度是多少？

   O(nlogn)

4. 堆排序的空间复杂度是多少？

   O(1)

[23. 合并K个升序链表](https://leetcode-cn.com/problems/merge-k-sorted-lists/)(困难) **（例题）**

[347. 前 K 个高频元素](https://leetcode-cn.com/problems/top-k-frequent-elements/)（中等） **（例题）**

[295. 数据流的中位数](https://leetcode-cn.com/problems/find-median-from-data-stream/)（困难）**（例题）**

[973. 最接近原点的 K 个点](https://leetcode-cn.com/problems/k-closest-points-to-origin/)（中等）

[313. 超级丑数](https://leetcode-cn.com/problems/super-ugly-number/)（中等）





[208. 实现 Trie (前缀树)](https://leetcode-cn.com/problems/implement-trie-prefix-tree/)（中等） 标准Trie树

以下为选做题目：

[面试题 17.17. 多次搜索](https://leetcode-cn.com/problems/multi-search-lcci/)（中等） 标准AC自动机，不过写AC自动机太复杂，Trie树搞定

[212. 单词搜索 II](https://leetcode-cn.com/problems/word-search-ii/)（困难）



# TopK

1. 针对静态数据（查询 TopK 操作）
2. 针对动态数据（只包含添加数据操作和查询 TopK 操作）

解决思路有 3 种：

1. 排序，然后取数组种的第 K 个元素 -> 静态数据
2. 利用快速排序算法的思想，做到 O(n) -> 静态数据
3. 利用堆，插入 O(logk)，获取 O(1) -> 动态数据 



# [23. 合并K个升序链表](https://leetcode-cn.com/problems/merge-k-sorted-lists/)(困难) **（例题）**

给你一个链表数组，每个链表都已经按升序排列。

请你将所有链表合并到一个升序链表中，返回合并后的链表。

示例 1：

 ```
 输入：lists = [[1,4,5],[1,3,4],[2,6]]
 输出：[1,1,2,3,4,4,5,6]
 解释：链表数组如下：
 [
   1->4->5,
   1->3->4,
   2->6
 ]
 将它们合并到一个有序链表中得到。
 1->1->2->3->4->4->5->6
 ```

示例 2：

```
输入：lists = []
输出：[]
```

示例 3：

```
输入：lists = [[]]
输出：[]
```


提示：

- k == lists.length
- 0 <= k <= 10^4
- 0 <= lists[i].length <= 500
- -10^4 <= lists[i][j] <= 10^4
- lists[i] 按 升序 排列
- lists[i].length 的总和不超过 10^4

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    
    private class QElement {
        
        ListNode curNode;
       	
        public QElement(ListNode curNode) {
            this.curNode = curNode;
        }
        
    }

    public ListNode mergeKLists(ListNode[] lists) {
		if (lists == null || lists.length == 0) {
            return null;
        }
        int k = lists.length;
        PriorityQueue<QElement> minQ = new PriorityQueue<>(new Comparator<QElement>() {
            
            @Override
            public int compare(QElement q1, QElement q2) {
                return q1.curNode.val - q2.curNode.val;
            }

        });
        
        for (int i = 0; i < k; ++i) {
            if (lists[i] != null) {
                minQ.offer(new QElement(lists[i]));
            }
        }
        ListNode head = new ListNode();
        ListNode tail = head;
        while (!minQ.isEmpty()) {
            QElement element = minQ.poll();
            ListNode curNode = element.curNode;
            tail.next = element.curNode;
            tail = tail.next;
            if (curNode.next != null) {
                minQ.offer(new QElement(curNode.next));
            }
        }
        return head.next;
    }
    
}
```



# [347. 前 K 个高频元素](https://leetcode-cn.com/problems/top-k-frequent-elements/)（中等） **（例题）**

给你一个整数数组 nums 和一个整数 k ，请你返回其中出现频率前 k 高的元素。你可以按 任意顺序 返回答案。

示例 1:

```
输入: nums = [1,1,1,2,2,3], k = 2
输出: [1,2]
```

示例 2:

```
输入: nums = [1], k = 1
输出: [1]
```


提示：

- 1 <= nums.length <= 105
- k 的取值范围是 [1, 数组中不相同的元素的个数]
- 题目数据保证答案唯一，换句话说，数组中前 k 个高频元素的集合是唯一的

进阶：你所设计算法的时间复杂度 必须 优于 O(n log n) ，其中 n 是数组大小。

```java
class Solution {

    private class QElement {
        
        int val;
        
        int count;
        
        public QElement(int val, int count) {
            this.val = val;
            this.count = count;
        }
        
    }
    
    public int[] topKFrequent(int[] nums, int k) {
 		// 可以用排序，因为是静态数据，还可以用 HashMap 来做，这边为了巩固堆的知识，用堆来做。
        // 哈希表统计每个数字出现的次数 count
    	Map<Integer, Integer> counts = new HashMap<>();
        for (int num : nums) {
            counts.put(num, counts.getOrDefault(num, 0) + 1);
        }
        // 按照 count 值构建小顶堆
        PriorityQueue<QElement> queue = new PriorityQueue<>(new Comparator<QElement>() {
            @Override
            public int compare(QElement e1, QElement e2) {
                return e1.count - e2.count;
            }
        });
        // 求 TopK
        counts.forEach((num, count) -> {
            if (queue.size() < k) {
                queue.offer(new QElement(num, count));
            }
            else {
                if (queue.peek().count < count) {
                    queue.poll();
                    queue.offer(new QElement(num, count));
                }
            }
        });
        int[] result = new int[k];
        for (int i = 0; i < k; ++i) {
            result[i] = queue.poll().val;
        }
        return result;
    }

}
```

# [295. 数据流的中位数](https://leetcode-cn.com/problems/find-median-from-data-stream/)（困难）**（例题）**

求中位数&百分位位数

一直维护具有以下特性的两个堆：

- 一个大顶堆，一个小顶堆。
- 每个堆中元素个数接近 n/2：如果 n 是偶数，两个堆中的数据个数都是 n/2；如果 n 是奇数，大顶堆又 n/2+1 个数据，小顶堆有 n/2 个数据。
- 大顶堆中的元素都小于等于小顶堆中的元素。

那么大顶堆中堆顶的元素就是中位数。

在插入数据时，如何维护两个堆继续满足上述特性呢？

如果新数据小于等于大顶堆的堆顶元素，就将这个新数据插入到大顶堆，否则，插入到小顶堆。不过，此时就有可能出现两个堆中的数据个数不符合前面约定。我们通过从一个堆中不停地将堆顶元素移动到另一个堆，通过这样的调整，让两个堆中的数据个数满足前面的约定。





中位数是有序列表中间的数。如果列表长度是偶数，中位数则是中间两个数的平均值。

例如，

[2,3,4] 的中位数是 3

[2,3] 的中位数是 (2 + 3) / 2 = 2.5

设计一个支持以下两种操作的数据结构：

- void addNum(int num) - 从数据流中添加一个整数到数据结构中。
- double findMedian() - 返回目前所有元素的中位数。
  示例：

```
addNum(1)
addNum(2)
findMedian() -> 1.5
addNum(3) 
findMedian() -> 2
```


进阶:

1. 如果数据流中所有整数都在 0 到 100 范围内，你将如何优化你的算法？
2. 如果数据流中 99% 的整数都在 0 到 100 范围内，你将如何优化你的算法？



```java
class MedianFinder {
    /**
     * 一个小顶堆
     */
    private PriorityQueue<Integer> minQueue = new PriorityQueue<>((o1, o2) -> o1 - o2);
    
    /**
     * 一个大顶堆
     */
    private PriorityQueue<Integer> maxQueue = new PriorityQueue<>((o1, o2) -> o2 - o1);
    
    
    public MedianFinder() {}
    
    public void addNum(int num) {
		if (maxQueue.isEmpty() || num <= maxQueue.peek()) {
            maxQueue.add(num);
        }
        else {
            minQueue.add(num);
        }
        // 每个堆中元素个数接近 n/2：如果 n 是偶数，两个堆中的数据个数都是 n/2；如果 n 是奇数，大顶堆又 n/2+1 个数据，小顶堆有 n/2 个数据。
        while (maxQueue.size() <= minQueue.size()) {
            Integer e = minQueue.poll();
            maxQueue.add(e);
        }
        while (minQueue.size() < maxQueue.size() - 1) {
            Integer e = maxQueue.poll();
            minQueue.add(e);
        }
    }
    
    public double findMedian() {
        // 那么大顶堆中堆顶的元素就是中位数
		if (maxQueue.size() > minQueue.size()) {
            return maxQueue.peek();
        }
        else {
            return (maxQueue.peek() + minQueue.peek()) / 2f;
        }
    }
    
}

/**
 * Your MedianFinder object will be instantiated and called as such:
 * MedianFinder obj = new MedianFinder();
 * obj.addNum(num);
 * double param_2 = obj.findMedian();
 */
```



# [973. 最接近原点的 K 个点](https://leetcode-cn.com/problems/k-closest-points-to-origin/)（中等）



# [313. 超级丑数](https://leetcode-cn.com/problems/super-ugly-number/)（中等）



Trie

# [208. 实现 Trie (前缀树)](https://leetcode-cn.com/problems/implement-trie-prefix-tree/)（中等） 标准Trie树

以下为选做题目：

# [面试题 17.17. 多次搜索](https://leetcode-cn.com/problems/multi-search-lcci/)（中等） 标准AC自动机，不过写AC自动机太复杂，Trie树搞定

# [212. 单词搜索 II](https://leetcode-cn.com/problems/word-search-ii/)（困难）



# 字符串匹配

## 单模式串匹配算法：在主串中查找一个模式串

- **BF 算法**
- **RK 算法**
- BM 算法
- KMP 算法

### BF 算法，暴力匹配算法、朴素字符串匹配算法

如果模式串长度为 m，主串长度为 n，那么在主串中就会有 n-m+1 个长度为 m 的子串，我们只需要暴力地对比这 n-m+1 个子串与模式串，就可以找出主串与模式串匹配的子串。

```java
int bf(char[] a, int n, char[] b, int m) {
    for (int i = 0; i <= n - m; ++i) {
        int j = 0;
        while (j < m) {
            if (a[i + j] != b[j]) {
                break;
            }
            j++;
        }
        if (j == m) {
            return i;
        } 
    }
    return -1;
}
```

### RK 算法，Rabin-Karp 算法

通过哈希算法对主串中的 n-m+1 个子串分别求哈希值，然后逐个与模式串的哈希值比较。如果某个子串的哈希值与模式串的哈希值相等，那么就说明这个子串和模式串匹配了。

1. 无冲突
2. H1->h2

## 多模式串匹配算法：在主串中查找多个模式串

- **Trie 树**
- AC自动机

### Trie 树

```java
public class Trie {
    
    public class TrieNode {
        
        public char data;
    	
        public TrieNode[] children = new TrieNode[26];
        
        public boolean isEndingChar = false;
        
        public TrieNode (char data) {
            this.data = data;
        }
        
    }
    
    private TrieNode root = new TrieNode('/');
    
    public void insert (char[] text) {
    	TrieNode p = root;
        for (int i = 0, i < text.length; i++) {
            int index = text[i] - 'a';
            if (p.children[index] == null) {
                TrieNode newNode = new TrieNode(text[i]);
            	p.children[index] = newNode;
            }
            p = p.children[index];
        }
        p.isEndingChar = true;
    }
    
    /**
     *  字符串查找（完全匹配，就是普通的查找）
     */
    public boolean find(char[] target) {
        TrieNode p = root;
        for (int i = 0; i < target.length; i++) {
            int index = target[i] - 'a';
            if (p.children[index] == null) {
                // 不存在 target
                return false;
            }
            p = p.children[index];
        }
        // 不能完全匹配，只是前缀
        if (p.isEndingChar == false) {
            return false;
        }
        // 找到 target
        else {
            return true;
        }
    }
    
    /**
     * 多模式串匹配
     * 例如敏感词过滤系统，字符串集合包含 how, hi, her, hello, so, see 这几个字符串，
     * 频繁地查询某个长字符串中，比如 abdhellosayhiok，是否包含字符串集合中的字符串？
     */
    public void match(char[] mainstr) {
        for (int i = 0; i < mainstr.length; i++) {
            TrieNode p = root;
            for (int j = i; j < mainstr.length; j++) {
                int index = mainstr[j] - 'a';
                if (p.children[index] == null) {
                    break;
                }
                p = p.children[index];
                if (p.isEndingChar) {
                    System.out.println("matched, mainstr index ["+ i + "," + j +"].");
                }
            }
        }
    }
    
    /**
     * 前缀匹配，例如 he*** 来匹配
     */
    public void prefixMatch(char[] prefix) {
        TrieNode p = root;
        for (int i = 0; i < prefix.length; i++) {
            int index = prefix[i] - 'a';
            if (p.children[index] == null) {
                // 没有前缀匹配的字符串
                return;
            }
            p = p.children[index];
        }
        List<Character> path = new ArrayList<>();
        traveTree(p, prefix, path);
    }
    
    private void travelTree(TrieNode p, char[] prefix, List<Character> path) {
        if (p.isEndingChar) {
            StringBuilder resultString = new StringBuilder();
            resultString.append(prefix);
            resultString.append(path);
            System.out.println(resultString.toString());
        }
        path.add(p.data);
        for (int i = 0; i < p.children.length; i++) {
            if (p.children[i] != null) {
                traveTree(p.children[i], prefix, path);
            }
        }
        path.remove(path.size() - 1);
    }
    
}
```



