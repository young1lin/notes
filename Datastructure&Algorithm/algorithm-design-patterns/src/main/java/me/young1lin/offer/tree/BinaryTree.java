package me.young1lin.offer.tree;

/**
 * 前序遍历{1,2,4,7,3,5,6,8}
 * 中序遍历{4,7,2,1,5,3,8,6}
 * 后序遍历{7,4,2,5,8,6,3,1}
 *
 * 我爱死递归了
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/29 上午12:08
 * @version 1.0
 */
public class BinaryTree {

	static class BinaryTreeNode {

		int value;

		BinaryTreeNode left;

		BinaryTreeNode right;

		BinaryTreeNode(int value) {
			this.value = value;
		}

	}

	static final BinaryTreeNode ROOT = new BinaryTreeNode(1);

	static {
		initBinaryTree();
	}

	static void initBinaryTree() {
		ROOT.left = new BinaryTreeNode(2);
		ROOT.left.left = new BinaryTreeNode(4);
		ROOT.left.left.right = new BinaryTreeNode(7);
		ROOT.right = new BinaryTreeNode(3);
		ROOT.right.left = new BinaryTreeNode(5);
		ROOT.right.right = new BinaryTreeNode(6);
		ROOT.right.right.left = new BinaryTreeNode(8);
	}

	static void preorderTraversal(BinaryTreeNode root) {
		if (root == null) {
			return;
		}
		System.out.print(root.value + "\t");
		preorderTraversal(root.left);
		preorderTraversal(root.right);
	}

	static void inorderTraversal(BinaryTreeNode root) {
		if (root == null) {
			return;
		}
		inorderTraversal(root.left);
		System.out.print(root.value + "\t");
		inorderTraversal(root.right);
	}

	static void postOrderTraversal(BinaryTreeNode root) {
		if (root == null) {
			return;
		}
		postOrderTraversal(root.left);
		postOrderTraversal(root.right);
		System.out.print(root.value + "\t");
	}

	public static void main(String[] args) {
		BinaryTreeNode node = ROOT;
		// 前序遍历
		preorderTraversal(node);
		System.out.println();
		// 中序遍历
		inorderTraversal(node);
		System.out.println();
		// 后序遍历
		postOrderTraversal(node);
	}

}
