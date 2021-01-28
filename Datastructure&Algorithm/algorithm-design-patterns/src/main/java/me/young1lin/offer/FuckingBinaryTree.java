package me.young1lin.offer;

/**
 * 前序遍历{1,2,4,7,3,5,6,8}
 * 中序遍历{4,7,2,1,5,3,8,6}
 * 后序遍历{7,4,2,5,8,5,3,1}
 *
 * 顶不住了，明天来
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/29 上午12:08
 * @version 1.0
 */
public class FuckingBinaryTree {

	static class BinaryTreeNode {

		int value;

		BinaryTreeNode left;

		BinaryTreeNode right;

		BinaryTreeNode(int value) {
			this.value = value;
		}

	}

	static final BinaryTreeNode root = new BinaryTreeNode(1);

	static {
		initBinaryTree();
	}

	private static void initBinaryTree() {
		root.left = new BinaryTreeNode(2);
		root.left.left = new BinaryTreeNode(4);
		root.left.left.right = new BinaryTreeNode(7);
		root.right = new BinaryTreeNode(3);
		root.right.left = new BinaryTreeNode(5);
		root.right.right = new BinaryTreeNode(6);
		root.right.right.left = new BinaryTreeNode(8);
	}

	public static void main(String[] args) {

	}

}
