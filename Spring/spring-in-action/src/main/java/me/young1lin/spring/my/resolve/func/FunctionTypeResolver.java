package me.young1lin.spring.my.resolve.func;

import me.young1lin.spring.my.resolve.elatiscseach.Agg;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @date 2021/1/4 下午1:49
 * @version 1.0
 */
public interface FunctionTypeResolver {

	/**
	 * 根据传入的字段解析成聚合对象
	 * @param columns to be resolved columns. like ["Bar","Foo"]
	 * @return 聚合对象
	 */
	Agg resolve(String[] columns);

}
