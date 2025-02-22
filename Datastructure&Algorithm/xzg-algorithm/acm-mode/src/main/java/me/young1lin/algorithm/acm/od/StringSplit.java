package me.young1lin.algorithm.acm.od;

import java.util.Scanner;

/**
 * 给定一个非空字符串S，其被N个‘-’分隔成N+1的子串，给定正整数K，要求除第一个子串外，其余的子串每K个字符组成新的子串，并用‘-’分隔。对于新组成的每一个子串，如果它含有的小写字母比大写字母多，则将这个子串的所有大写字母转换为小写字母；反之，如果它含有的大写字母比小写字母多，则将这个子串的所有小写字母转换为大写字母；大小写字母的数量相等时，不做转换。
 * 输入描述:
 * 输入为两行，第一行为参数K，第二行为字符串S。
 * 输出描述:
 * 输出转换后的字符串。
 * 示例1
 * 输入
 * 3
 * 12abc-abCABc-4aB@
 * 输出
 * 12abc-abc-ABC-4aB-@
 * 说明
 * 子串为12abc、abCABc、4aB@，第一个子串保留，后面的子串每3个字符一组为abC、ABc、4aB、@，abC中小写字母较多，转换为abc，ABc中大写字母较多，转换为ABC，4aB中大小写字母都为1个，不做转换，@中没有字母，连起来即12abc-abc-ABC-4aB-@
 * 示例2
 * 输入
 * 12
 * 12abc-abCABc-4aB@
 * 输出
 * 12abc-abCABc4aB@
 * 说明
 * 子串为12abc、abCABc、4aB@，第一个子串保留，后面的子串每12个字符一组为abCABc4aB@，这个子串中大小写字母都为4个，不做转换，连起来即12abc-abCABc4aB@
 *
 * 作者：yaozi
 * 链接：https://leetcode.cn/circle/discuss/niKSMZ/
 * 来源：力扣（LeetCode）
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 *
 * @author young1lin
 * @version 1.0
 * @since 2024/8/22
 */
public class StringSplit {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int k = scanner.nextInt();
        String originStr = scanner.next();
        int firstIndex = findFirstSplitor(originStr);
        String first = originStr.substring(0, firstIndex);
        String other = originStr.substring(firstIndex);
        // 12abc-abCABc-4aB@
        other = other.replaceAll("-", "");
        // 第一个不用分割
        StringBuilder result = new StringBuilder(first);
        result.append("-");

        int curStrLength = 1;
        for (int i = 0; i < other.length(); i++) {
            result.append(other.charAt(i));
            curStrLength++;
            if (curStrLength > k) {
                curStrLength = 1;
                String convertStr = result.substring(result.length() - k, result.length());
                convertStr = convert(convertStr);
                result.replace(result.length() - k, result.length(), convertStr);
                result.append("-");
            }
        }
        System.out.println(result);
    }

    private static String convert(String convertStr) {
        int lowCase = 0;
        int highCase = 0;
        for (int i = 0; i < convertStr.length(); i++) {
            char c = convertStr.charAt(i);
            if (c >= 'a' && c <= 'z') {
                lowCase++;
            } else if (c >= 'A' && c <= 'Z') {
                highCase++;
            }
        }
        if (lowCase == highCase) {
            return convertStr;
        } else if (lowCase > highCase) {
            return convertStr.toLowerCase();
        } else {
            return convertStr.toUpperCase();
        }
    }

    private static int findFirstSplitor(String originStr) {
        for (int i = 0; i < originStr.length(); i++) {
            if (originStr.charAt(i) == '-') {
                return i;
            }
        }
        return originStr.length() - 1;
    }

}
