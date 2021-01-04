package me.young1lin.spring.my.resolve.elatiscseach;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/4 下午1:54
 * @version 1.0
 */
public class RequestAgg implements Agg {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, Object> aggMap;

	public RequestAgg() {
		this(new HashMap<>(1));
	}

	public RequestAgg(Map<String, Object> aggMap) {
		this.aggMap = aggMap;
	}

	@Override
	public Map<String, Object> getAggMap() {
		return null;
	}

	@Override
	public void setAggMap(Map<String, Object> aggMap) {
		this.aggMap = aggMap;
	}

	@Override
	public String toString() {
		return "RequestAgg{" +
				"aggMap=" + aggMap +
				'}';
	}

}
