package me.young1lin.offer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/27 上午12:45
 * @version 1.0
 */
public class RepeatNum {

	private static final int[] ARR = new int[]{0,1,2,3,2,5,3};

	public static void main(String[] args) {
		Map<Integer,Integer> num = new HashMap<>(ARR.length);
		List<Integer> list = new ArrayList<>();
		for (int i : ARR) {
			if(num.get(i) == null){
				num.put(i,0);
			}else {
				list.add(i);
				num.put(i,num.get(i)+1);
			}
		}
		list.forEach(System.out::println);
	}

}
