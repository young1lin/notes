package me.young1lin.spring.my.resolve.termlevel;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/4 下午1:47
 * @version 1.0
 */
public enum TermLevel {

	/** term query */
	TERM,

	/** match all columns */
	TERMS,

	/** match prefix columns, like Chris*, search Chris1, Chris2 and so on*/
	PREFIX,

}
