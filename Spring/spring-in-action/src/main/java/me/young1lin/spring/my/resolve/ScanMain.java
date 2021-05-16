package me.young1lin.spring.my.resolve;

import java.util.Map;

import me.young1lin.spring.my.resolve.annotation.EsFunctionType;
import me.young1lin.spring.my.resolve.annotation.EsTermLevel;
import me.young1lin.spring.my.resolve.func.FunctionType;
import me.young1lin.spring.my.resolve.func.FunctionTypeResolver;
import me.young1lin.spring.my.resolve.termlevel.TermLevel;
import me.young1lin.spring.my.resolve.termlevel.TermLevelResolver;
import me.young1lin.spring.my.resolve.util.ScanUtil;

/**
 * 仅为演示使用，该如何进行抽象思维示例
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/4 下午1:41
 * @version 1.0
 */
public class ScanMain {

	public static Map<TermLevel, TermLevelResolver> TERM_LEVEL_RESOLVE_MAP;

	public static Map<FunctionType, FunctionTypeResolver> FUNCTION_TYPE_RESOLVE_MAP;


	static {
		String functionTypeBasePackage = FunctionType.class.getPackage().getName();
		FUNCTION_TYPE_RESOLVE_MAP = ScanUtil.scan(functionTypeBasePackage, EsFunctionType.class, new Class[] {FunctionType.class});
		String termLevelBasePackage = TermLevel.class.getPackage().getName();
		TERM_LEVEL_RESOLVE_MAP = ScanUtil.scan(termLevelBasePackage, EsTermLevel.class, new Class[] {TermLevel.class});

	}

	public static void main(String[] args) {
		FUNCTION_TYPE_RESOLVE_MAP.forEach((k, v) -> System.out.println(v.toString() + " : \t" + k.toString()));
		TERM_LEVEL_RESOLVE_MAP.forEach((k, v) -> System.out.println(v.toString() + " : \t" + k.toString()));
	}

}
