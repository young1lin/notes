package me.young1lin.algorithm.acm.od;

import java.util.*;

/**
 * 给定一个射击比赛成绩单
 * 包含多个选手若干次射击的成绩分数
 * 请对每个选手按其最高三个分数之和进行降序排名
 * 输出降序排名后的选手ID序列
 * 条件如下:
 * <p>
 * 一个选手可以有多个射击成绩的分数 且次序不固定
 * 如果一个选手成绩小于三个 则认为选手的所有成绩无效 排名忽略该选手
 * 如果选手的成绩之和相等,则成绩相等的选手按照其ID降序排列
 * 输入描述
 * 输入第一行:一个整数 N
 * 表示该场比赛总共进行了N次射击
 * 产生N个成绩分数 2 <= N <= 100
 * 输入第二行 一个长度为N的整数序列
 * 表示参与本次射击的选手Id
 * 0 <= ID <= 99
 * 输入第三行是长度为N的整数序列
 * 表示参与每次射击的选手对应的成绩
 * 0 <= 成绩 <= 100
 * <p>
 * 输出描述
 * 符合题设条件的降序排名后的选手ID序列
 * <p>
 * 示例一
 * 输入
 * 13
 * 3,3,7,4,4,4,4,7,7,3,5,5,5
 * 53,80,68,24,39,76,66,16,100,55,53,80,55
 * 输出
 * 5,3,7,4
 * 说明
 * 该场射击比赛进行了13次,参赛选手为3 4 5 7
 * 3号选手的成绩为53 80 55最高三个成绩的和为 188
 * 4号选手的成绩为24 39 76 66最高三个和为181
 * 5号选手的成绩为53 80 55 最高三个和为188
 * 7号选手成绩为68 16 100 最高三个和184
 * 比较各个选手最高三个成绩的和
 * 3 = 5 > 7 > 4
 * 由于3和5成绩相等 且5 > 3 所以输出为5,3,7,4
 *
 * @author young1lin
 * @version 1.0
 * @since 2024/8/22
 */
public class ShootGame {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine(); // 处理换行
        String[] idStrings = scanner.nextLine().split(",");
        String[] scoreStrings = scanner.nextLine().split(",");

        Map<Integer, List<Integer>> playerScores = new HashMap<>();

        // 收集每个选手的所有成绩
        for (int i = 0; i < n; i++) {
            int id = Integer.parseInt(idStrings[i]);
            int score = Integer.parseInt(scoreStrings[i]);
            playerScores.computeIfAbsent(id, k -> new ArrayList<>()).add(score);
        }

        // 用于保存有效选手的最高三分总和及其ID
        List<int[]> validPlayers = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> entry : playerScores.entrySet()) {
            List<Integer> scores = entry.getValue();
            if (scores.size() >= 3) {
                // 对选手的成绩进行排序，找出最高的三个成绩
                scores.sort(Collections.reverseOrder());
                int sum = scores.get(0) + scores.get(1) + scores.get(2);
                validPlayers.add(new int[]{entry.getKey(), sum});
            }
        }

        // 对有效选手进行排序：先按总成绩降序，再按ID降序
        validPlayers.sort((a, b) -> {
            if (b[1] != a[1]) {
                return b[1] - a[1]; // 按成绩和排序
            } else {
                return b[0] - a[0]; // 成绩相等时按ID排序
            }
        });

        // 输出结果
        StringBuilder result = new StringBuilder();
        for (int[] player : validPlayers) {
            result.append(player[0]).append(",");
        }

        // 去掉最后一个多余的逗号并输出
        System.out.println(result.substring(0, result.length() - 1));
    }

}
