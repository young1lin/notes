package me.young1lin.spring.my.resolve.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.MapUtils;

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/4 下午2:19
 * @version 1.0
 */
public class ScanUtil {

	private static final Logger log = LoggerFactory.getLogger(ScanUtil.class);

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
		Map<K, V> resultMap = new LinkedHashMap<>(resources.length);
		String annotationName = targetAnnotation.getName();
		// 错误信息收集
		StringBuilder errorMsg = new StringBuilder();
		for (Resource resource : resources) {
			// 验证是否可读，如果不可读，一切都不能操作，跳过到下一个 resource
			if (resource.isReadable()) {
				String resourceFileName = resource.getFilename();
				try {
					// 获得当前类的元信息（通过字节码的方式，不是反射来弄，前者非常高效无需实例化类）Spring 通过 MetadataReader 抽象出了类的元信息
					MetadataReader metadataReader =
							DEFAULT_METADATA_READER_FACTORY.getMetadataReader(resource);
					// 和 findCandidateComponents 里面的过滤方法一样,寻找候选组件
					if (isCandidateComponent(metadataReader, excludeFilters)) {
						Map<String, Object> annotationAttributes =
								metadataReader.getAnnotationMetadata().getAnnotationAttributes(annotationName);
						if (MapUtils.isEmpty(annotationAttributes)) {
							errorMsg.append(resourceFileName).append(" don't have ").append(annotationName)
									.append(" Annotation, ");
						}
						else {
							// 获得解析器类的实例
							V resolver = getResolverInstance(metadataReader);
							// 获得对应解析器上的注解的值
							Object value = annotationAttributes.get("value");
							if (Objects.isNull(value)) {
								errorMsg.append(resourceFileName).append("'s ").append(annotationName)
										.append(" annotation attribute is empty,");
							}
							else {
								putResult(resultMap, value, resolver);
							}
						}
					}
				}
				catch (IOException e) {
					log.error("resource [{}] can't find", resourceFileName, e);
				}
				catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
					log.error("class can't newInstance", e);
				}
			}
		}
		printErrorMsg(errorMsg);
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
		return ClassUtils.convertClassNameToResourcePath(
				new StandardEnvironment().resolvePlaceholders(basePackage));
	}

	private static boolean isCandidateComponent(MetadataReader metadataReader, Class<?>[] excludeFilters) {
		ClassMetadata classMetadata = metadataReader.getClassMetadata();
		if (classMetadata.isAbstract() || classMetadata.isInterface()) {
			return false;
		}
		if (excludeFilters != null && excludeFilters.length != 0) {
			for (Class<?> clazz : excludeFilters) {
				if (classMetadata.getClassName().equals(clazz.getName())) {
					return false;
				}
			}

		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private static <V> V getResolverInstance(MetadataReader metadataReader) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		Class<V> clazz =
				(Class<V>) Objects.requireNonNull(ClassUtils.getDefaultClassLoader())
						.loadClass(metadataReader.getClassMetadata().getClassName());
		return clazz.newInstance();
	}

	@SuppressWarnings("unchecked")
	private static <V, K> void putResult(Map<K, V> resultMap, Object value, V resolver) {
		if (value.getClass().isArray()) {
			K[] resolverTypes = (K[]) value;
			for (K resolverType : resolverTypes) {
				resultMap.put(resolverType, resolver);
			}
		}
		else {
			resultMap.put((K) value, resolver);
		}
	}

	private static void printErrorMsg(StringBuilder errorMsg) {
		if (errorMsg.length() != 0) {
			log.error(errorMsg.toString());
		}
	}

}
