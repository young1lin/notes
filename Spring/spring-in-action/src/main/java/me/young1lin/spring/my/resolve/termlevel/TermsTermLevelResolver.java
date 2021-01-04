package me.young1lin.spring.my.resolve.termlevel;

import me.young1lin.spring.my.resolve.annotation.EsTermLevel;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/4 下午2:02
 * @version 1.0
 */
@EsTermLevel(TermLevel.TERMS)
public class TermsTermLevelResolver implements TermLevelResolver {

	@Override
	public Object resolve(String[] value) {
		return value;
	}

}
