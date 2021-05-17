package me.young1lin.offer.tree;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/2/15 下午4:36
 * @version 1.0
 */
public class BinaryTreeValidate {

	private static class TreeNode {

		int val;

		TreeNode left, right;


		TreeNode() {
		}

		TreeNode(int val) {
			this.val = val;
		}

		TreeNode(int val, TreeNode left, TreeNode right) {
			this.val = val;
			this.left = left;
			this.right = right;
		}

	}

	TreeNode generateBinaryTree() {
		TreeNode root = new TreeNode(27);
		root.left = new TreeNode(18);
		root.left.left = new TreeNode(11);
		root.left.right = new TreeNode(20);
		root.right = new TreeNode(35);
		root.right.right = new TreeNode(40);
		root.right.left = new TreeNode(33);
		return root;
	}

	TreeNode generateNonBinaryTree() {
		TreeNode root = new TreeNode(32);
		root.left = new TreeNode(26);
		root.left.left = new TreeNode(19);
		root.right = new TreeNode(47);
		root.right.right = new TreeNode(56);
		root.right.right.right = new TreeNode(27);
		return root;
	}

	public boolean isValidBSTInorder(TreeNode root) {
		Deque<TreeNode> stack = new LinkedList<>();
		double inorder = -Double.MAX_VALUE;
		while (!stack.isEmpty() || root != null) {
			while (root != null) {
				stack.push(root);
				root = root.left;
			}
			root = stack.pop();
			// 如果中序遍历得到的节点的值小于等于前一个 inorder，说明不是二叉搜索树
			if (root.val <= inorder) {
				return false;
			}
			inorder = root.val;
			root = root.right;
		}
		return true;
	}

	public boolean isValidBSTRecursion(TreeNode root) {
		return isValidBSTRecursion(root, Long.MIN_VALUE, Long.MAX_VALUE);
	}

	public boolean isValidBSTRecursion(TreeNode node, long lower, long upper) {
		if (node == null) {
			return true;
		}
		if (node.val <= lower || node.val >= upper) {
			return false;
		}
		return isValidBSTRecursion(node.left, lower, node.val) && isValidBSTRecursion(node.right, node.val, upper);
	}

	public static void main(String[] args) {
		BinaryTreeValidate validate = new BinaryTreeValidate();
		TreeNode isBinaryTreeRoot = validate.generateBinaryTree();
		TreeNode nonBinaryTreeRoot = validate.generateNonBinaryTree();
		// 中序遍历
		System.out.println(validate.isValidBSTInorder(isBinaryTreeRoot));
		System.out.println(validate.isValidBSTInorder(nonBinaryTreeRoot));
		// 递归
		System.out.println(validate.isValidBSTRecursion(isBinaryTreeRoot));
		System.out.println(validate.isValidBSTRecursion(nonBinaryTreeRoot));
	}

}
