package me.young1lin.algorithm.search.str;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/27 下午4:27
 * @version 1.0
 */
public class Trie {

	private static final TrieNode ROOT = new TrieNode('/');

	static class TrieNode {

		char data;

		TrieNode[] children = new TrieNode[26];

		boolean isEndingChar = false;

		TrieNode(char data) {
			this.data = data;
		}

	}

	/**
	 * only support a-z ASCII.
	 * @param text the text of String
	 */
	static void insert(char[] text) {
		TrieNode p = ROOT;
		for (char c : text) {
			int index = c - 'a';
			if (p.children[index] == null) {
				TrieNode newNode = new TrieNode(c);
				p.children[index] = newNode;
			}
			p = p.children[index];
		}
		p.isEndingChar = true;
	}

	/**
	 * 	在Trie树中查找一个字符串
	 */
	public static boolean find(char[] pattern) {
		TrieNode p = ROOT;
		for (char c : pattern) {
			int index = c - 'a';
			if (p.children[index] == null) {
				// 不存在pattern
				return false;
			}
			p = p.children[index];
		}
		// 不能完全匹配，只是前缀
		// 找到pattern
		return p.isEndingChar;
	}

	public static void main(String[] args) {
		String[] strArr = {"hello", "hi", "hey", "so", "see", "how", "what"};
		for (String str : strArr) {
			insert(str.toCharArray());
		}
		System.out.println(find("hey".toCharArray()));
	}

}
