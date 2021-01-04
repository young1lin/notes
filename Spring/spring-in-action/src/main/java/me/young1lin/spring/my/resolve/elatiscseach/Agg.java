package me.young1lin.spring.my.resolve.elatiscseach;

import java.util.Map;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @date 2021/1/4 下午1:50
 * @version 1.0
 */
public interface Agg {

	/**
	 * Agg Map to JSON
	 * @return map to be resolved to JSON
	 */
	Map<String,Object> getAggMap();

	/**
	 * replace already exists aggMap
	 * @param aggMap map to be resolved to JSON
	 */
	void setAggMap(Map<String,Object> aggMap);

}
