package me.young1lin.xzg.algorithm.week03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/29 下午8:10
 * @version 1.0
 */
public class City {

	/** 直辖市们 */
	private static final String[] MUNICIPALITIES = new String[] {
			"北京市", "天津市", "上海市", "重庆市"
	};

	private static class TreeNode implements Comparable<TreeNode> {

		private final int sort;

		private final String cityName;

		private List<TreeNode> nodes;

		/** 深度，来打印这个对象的位置 */
		private int depth;


		TreeNode(int sort, String cityName, List<TreeNode> nodes, int depth) {
			this.sort = sort;
			this.cityName = cityName;
			this.nodes = nodes;
			this.depth = depth;
		}

		@Override
		public int compareTo(TreeNode treeNode) {
			if (this.sort > treeNode.sort) {
				return 1;
			}
			else if (this.sort < treeNode.sort) {
				return -1;
			}
			return 0;
		}

		@Override
		public String toString() {
			String blankSpaces = getBlankSpaces();
			String n = nodes == null ? "" : getNodes();
			return "\n" + blankSpaces +
					"-" + sort + ":" + cityName + n;
		}

		private String getBlankSpaces() {
			StringBuilder str = new StringBuilder();
			for (int i = 1; i < depth; i++) {
				str.append("  ");
			}
			return str.toString();
		}

		private String getNodes() {
			StringBuilder sb = new StringBuilder();
			for (TreeNode treeNode : nodes) {
				sb.append(treeNode.toString());
			}
			return sb.toString();
		}

	}

	public static void main(String[] args) throws IOException {
		// 读取文件
		List<TreeNode> list = readFile();
		// 排个序
		list.sort(TreeNode::compareTo);
		// 合并城市
		List<TreeNode> mergedList = mergeSameSortCityAndSort(list);
		// 这里我们合并出了 57 个城市，然后需要找出省和直辖市，以便下一步的合并
		// 找到省和直辖市的 TreeNodeList
		List<TreeNode> provinceList = findProvinceList(mergedList);
		// 合并省份和直辖市
		List<TreeNode> mergedProvince = mergedProvince(provinceList, mergedList);
		TreeNode treeNode = new TreeNode(1, "中国", new ArrayList<>(), 1);
		treeNode.nodes.addAll(mergedProvince);
		System.out.println(treeNode);
	}

	private static List<TreeNode> readFile() throws IOException {
		InputStream is = City.class.getClassLoader().getResourceAsStream("area.txt");
		assert is != null : "area.txt not found in classpath";
		InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
		BufferedReader br = new BufferedReader(isr);
		List<TreeNode> result = new ArrayList<>();
		String line;
		while ((line = br.readLine()) != null) {
			// 分隔出两个城市，一个主城市，一个子城市
			String[] cities = line.split("-");
			String cityOne = cities[0];
			String cityTwo = cities[1];
			// 分隔出城市的排序号和其对应的城市名称
			String[] cityOnesSortAndCityNameArr = cityOne.split(":");
			String[] cityTwosSortAndCityNameArr = cityTwo.split(":");
			String sortOne = cityOnesSortAndCityNameArr[0];
			String sortTwo = cityTwosSortAndCityNameArr[0];
			String cityNameOne = cityOnesSortAndCityNameArr[1];
			String cityNameTwo = cityTwosSortAndCityNameArr[1];
			List<TreeNode> subCities = new ArrayList<>(4);
			// 先产生子城市的 TreeNode
			TreeNode subCity = new TreeNode(Integer.parseInt(sortTwo), cityNameTwo,
					null, 4);
			subCities.add(subCity);
			// 再将子城市加入主城市
			TreeNode city = new TreeNode(Integer.parseInt(sortOne), cityNameOne,
					subCities, 3);
			// 添加至返回结果
			result.add(city);
		}
		return result;
	}

	private static List<TreeNode> mergeSameSortCityAndSort(List<TreeNode> list) {
		// 因为最小是 1，并且还是 "中国" ，所以，这里取 -1 作为开始的值
		int currentSort = -1;
		TreeNode currentNode = null;
		List<TreeNode> result = new ArrayList<>();
		for (TreeNode treeNode : list) {
			int sort = treeNode.sort;
			// 如果当前的 sort 值和现在的 sort 不一样，证明是不同的城市，所以需要变换 currentNode
			if (currentSort == sort) {
				assert currentNode != null : "currentNode can't be null";
				currentNode.nodes.addAll(treeNode.nodes);
				// 这里有可能是第一次
				currentNode.nodes.sort(TreeNode::compareTo);
			}
			else {
				currentSort = sort;
				currentNode = treeNode;
				result.add(currentNode);
			}
		}
		return result;
	}

	private static List<TreeNode> findProvinceList(List<TreeNode> mergedList) {
		// 目前中国有34个省级行政区
		// 34 / 0.75 = 46
		List<TreeNode> result = new ArrayList<>(46);
		for (TreeNode treeNode : mergedList) {
			if (isProvince(treeNode)) {
				// 省市级变成 2 级
				treeNode.depth = 2;
				for (TreeNode subCity : treeNode.nodes) {
					subCity.depth = 3;
				}
				result.add(treeNode);
			}
		}
		return result;
	}

	private static boolean isProvince(TreeNode treeNode) {
		String cityName = treeNode.cityName;
		if (cityName.contains("省")) {
			return true;
		}
		// 首先直辖市是固定的，就那几个，所以，直辖市可以用穷举的方式来实现和省的同级别内容
		for (String city : MUNICIPALITIES) {
			if (city.equals(cityName)) {
				return true;
			}
		}
		return false;
	}

	private static List<TreeNode> mergedProvince(List<TreeNode> provinceList,
			List<TreeNode> list) {
		lab1:
		for (TreeNode treeNode : list) {
			// 如果是直辖市或者省，直接跳过，最终我要返回的是 provinceList
			if (isProvince(treeNode)) {
				continue;
			}
			// 如果不是，则需要找到是在哪个省/直辖市下面的
			// findSubCity
			for (TreeNode province : provinceList) {
				if (province.cityName.contains("省")) {
					List<TreeNode> subCities = province.nodes;
					for (TreeNode subCity : subCities) {
						// 如果省的子的节点，等于当前节点的 sort，则就是它的市，比如 浙江省：台州市
						if (subCity.sort == treeNode.sort) {
							subCity.nodes = treeNode.nodes;
							continue lab1;
						}
					}
				}
			}
		}
		return provinceList;
	}

}
