package me.young1lin.spring.my.resolve.termlevel;

import me.young1lin.spring.my.resolve.ScanMain;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @date 2021/1/4 下午2:15
 * @version 1.0
 */
public interface TermLevelResolver {

	/**
	 * different TermLevel with different resolver
	 * @param value to be resolved {@code String} array
	 * @return resolved value
	 * @see ScanMain#TERM_LEVEL_RESOLVE_MAP
	 */
	Object resolve(String[] value);

}
