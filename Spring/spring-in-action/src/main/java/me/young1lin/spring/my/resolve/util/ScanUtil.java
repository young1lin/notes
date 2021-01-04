package me.young1lin.spring.my.resolve.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/4 下午2:19
 * @version 1.0
 */
public class ScanUtil {

	private static Logger log = LoggerFactory.getLogger(ScanUtil.class);

	private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

	private static final ResourcePatternResolver DEFAULT_RESOURCE_RESOLVER = new PathMatchingResourcePatternResolver();

	private static final MetadataReaderFactory DEFAULT_METADATA_READER_FACTORY = new SimpleMetadataReaderFactory();


	private ScanUtil() {
	}

	/**
	 *
	 * @param basePackage scan basePackage
	 * @param targetAnnotation targetAnnotation
	 * @param excludeFilters excludeFilter classes
	 * @param <K> ResolverType
	 * @param <V> Resolver
	 * @return <ResolverType,Resolver> Map
	 * @see ClassPathScanningCandidateComponentProvider#findCandidateComponents
	 */
	public static <K, V> Map<K, V> scan(String basePackage,
			Class<? extends Annotation> targetAnnotation, Class<?>[] excludeFilters) {

		Resource[] resources = getResourceByBasePackage(basePackage);
		Map<K, V> resultMap = new HashMap<>(resources.length);
		for (Resource resource : resources) {
			// 验证是否可读，如果不可读，一切都不能操作，跳过到下一个 resource
			if (resource.isReadable()) {

			}
		}
		return resultMap;
	}

	private static Resource[] getResourceByBasePackage(String basePackage) {
		String basePackageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
				+ resolveBasePackage(basePackage) + "/" + DEFAULT_RESOURCE_PATTERN;
		try {
			return DEFAULT_RESOURCE_RESOLVER.getResources(basePackageSearchPath);
		}
		catch (IOException e) {
			throw new NoClassDefFoundError(basePackage + DEFAULT_RESOURCE_PATTERN);
		}
	}

	private static String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(new StandardEnvironment().resolvePlaceholders(basePackage));
	}

}
