package me.young1lin.spring.my.resolve.termlevel;

import me.young1lin.spring.my.resolve.annotation.EsTermLevel;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/4 下午1:44
 * @version 1.0
 */
@EsTermLevel({TermLevel.TERM, TermLevel.PREFIX})
public class GenericTermLevelResolver implements TermLevelResolver {

	@Override
	public Object resolve(String[] value) {
		if (value != null && value.length != 0) {
			return value[0];
		}
		// reveal all the details
		return "";
	}

}
