package me.young1lin.java8.base;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @date 2020/12/25 下午2:00
 * @version 1.0
 */
public interface Order<T> extends Comparable<T> {

	/**
	 * get order
	 * @return order constant
	 */
	Integer getOrder();

}
