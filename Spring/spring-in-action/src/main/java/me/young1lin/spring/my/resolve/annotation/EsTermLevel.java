package me.young1lin.spring.my.resolve.annotation;

import me.young1lin.spring.my.resolve.termlevel.TermLevel;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/4 下午1:42
 * @version 1.0
 */
public @interface EsTermLevel {

	/**
	 * @return must not be nullZ
	 */
	TermLevel[] value();

}
