package me.young1lin.spring.my.resolve.func;

import me.young1lin.spring.my.resolve.annotation.EsFunctionType;
import me.young1lin.spring.my.resolve.elatiscseach.Agg;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/4 下午1:48
 * @version 1.0
 */
@EsFunctionType(FunctionType.GROUP_BY)
public class GroupByFunctionTypeResolver extends AbstractFunctionTypeResolver {

	@Override
	protected Agg doResolve(String[] columns) {
		return null;
	}

}
