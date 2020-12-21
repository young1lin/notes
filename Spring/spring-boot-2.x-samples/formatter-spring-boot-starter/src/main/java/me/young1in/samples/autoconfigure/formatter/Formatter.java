package me.young1in.samples.autoconfigure.formatter;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @date 2020/12/21 8:01 上午
 * @version 1.0
 */
public interface Formatter {

	/**
	 * 格式化操作
	 * @param obj 待格式化对象
	 * @return 返回格式化后的内容
	 */
	String format(Object obj);

}
