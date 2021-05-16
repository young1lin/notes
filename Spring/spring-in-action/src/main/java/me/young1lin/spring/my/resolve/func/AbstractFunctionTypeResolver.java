package me.young1lin.spring.my.resolve.func;

import me.young1lin.spring.my.resolve.elatiscseach.Agg;
import me.young1lin.spring.my.resolve.elatiscseach.RequestAgg;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/7 下午3:57
 * @version 1.0
 */
public abstract class AbstractFunctionTypeResolver implements FunctionTypeResolver {

	@Override
	public Agg resolve(String[] columns) {
		if (columns != null && columns.length != 0) {
			return doResolve(columns);
		}
		return new RequestAgg();
	}

	/**
	 * Template pattern, the template method to resolve columns
	 *
	 * @param columns the columns to be resolved
	 * @return RequestAgg
	 */
	protected abstract Agg doResolve(String[] columns);

}
