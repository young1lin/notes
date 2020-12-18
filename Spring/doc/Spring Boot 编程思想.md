作为一个 Spring “老手”，不知不觉，45分钟左右，看完了 70 页（不含自序和前言），要是包括前面两个，是 90 页。前面的内容完全是新手教程+小马哥对我们说的话。

第二天 124 也就是 144。没有想做笔记的冲动。

第三天 174 也就是 194。照旧

第四天 194 也就是 214。开始源码分析了，所以理解得比较慢，这个多层次 @Component 派生的具体实现在 4.0 开始支持，那时候才有递归实现。

第五天 212 。两种解析注解元信息的类，一个 ASM 实现，一个 JDK 实现，执行效率在高负载下天差地别。解释隐形覆盖和显性覆盖，刚看到源码分析前面。有点拖沓其实。

第六天 237 。主要是 @AliasFor 讲解以及 @Enable 模块

第七天 260。主要是 @Enable 模块装载 @Configuration Class，@ConfigurationClassPostProcessor 随着版本的变化而变化，来装载 @Configuration 和 @ImportSelector 和 ImportBeanDefinitionRegistrar。Spring Web 自动装配的前置知识。

第八天 284。Web 自动装配以及条件装配 @Profile、@Conditional

第九天 300。Spring Boot @Conditional 注解以及部分自动装配原理。

第十天 315。Spring Boot @EnableAutoConfiguration 部分实现自动装配方法。

第十一天 326。Spring Boot @EnableAutoConfiguration 实现自动装配事件、生命周期、排序。

第十二天 342。Spring @EnableAutoConfiguration 排序相关，BasePackage 相关，以及自定义Spring Boot Starter 需要遵守的不成文的规约。

# 第一天

什么都没记，讲的都是最最基本的实战内容。

# 第二天

**关于 sun.net.www.protocol.*.Handler 的内容，在小马哥的极客时间课程里面也讲过，后者讲得更为详细，也有实际操作。**

SpringBootConfiguration 派生自 Configuration

Configuration 派生自 Component

其他 Repository、Service 、Controller 均派生自 Component

# 第三天

CGLIB 会提升 @Configuration 的类，而不是 @Bean 的类。

# 第四天

Spring Cloud 核心特性

+ Distributed/versioned configuration（分布式配置）；
+ Service registration and discovery（服务注册与发现）；
+ Routing（路由）；
+ Service-to-services calls（服务调用）；
+ Load balancing（负载均衡）；
+ Circuit Breakers（熔断机制）；
+ Distributed messaginbg（分布式消息）。

Bean 也是分角色的（）

Spring 3.0 开始支持多层次 @Component 派生，2.5 并不支持

# 第五天

多层次派生原理

ClassPathSacnningCadidateComponentProvider#findCadidateComponents 在指定根包路径下，将查找所有标注 @Component 及“派生”注解的 BeanDefinition 集合，并且默认的过滤规则由 AnnotationTypeFilter 及 @Component 的元信息（ClassMetaData 和 AnnotationMetaData ）共同决定。然而由于 @Component 派生性在 Spring Framework 2.5 和 3.0 之间的差异，以上实现必然存在变更。4.0 才支持递归查找，多层次派生 @Component

**ClassPathScanningCandidateComponentProvider#registerDefaultFilters**

```java
/**
 * Register the default filter for {@link Component @Component}.
 * <p>This will implicitly register all annotations that have the
 * {@link Component @Component} meta-annotation including the
 * {@link Repository @Repository}, {@link Service @Service}, and
 * {@link Controller @Controller} stereotype annotations.
 * <p>Also supports Java EE 6's {@link javax.annotation.ManagedBean} and
 * JSR-330's {@link javax.inject.Named} annotations, if available.
 *
 */
@SuppressWarnings("unchecked")
protected void registerDefaultFilters() {
   this.includeFilters.add(new AnnotationTypeFilter(Component.class));
   ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();
   try {
      this.includeFilters.add(new AnnotationTypeFilter(
            ((Class<? extends Annotation>) ClassUtils.forName("javax.annotation.ManagedBean", cl)), false));
      logger.trace("JSR-250 'javax.annotation.ManagedBean' found and supported for component scanning");
   }
   catch (ClassNotFoundException ex) {
      // JSR-250 1.1 API (as included in Java EE 6) not available - simply skip.
   }
   try {
      this.includeFilters.add(new AnnotationTypeFilter(
            ((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Named", cl)), false));
      logger.trace("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
   }
   catch (ClassNotFoundException ex) {
      // JSR-330 API not available - simply skip.
   }
}
```

第二处差异在下面

**ClassPathScanningCandidateComponentProvider#findCandidateComponents**  为 true 的这个不用看，就是 @Indexd 注解加强扫描 Component 的。

```java
/**
 * Scan the class path for candidate components.
 * @param basePackage the package to check for annotated classes
 * @return a corresponding Set of autodetected bean definitions
 */
public Set<BeanDefinition> findCandidateComponents(String basePackage) {
    // 这个不用看，就是 @Indexd 注解加强
   if (this.componentsIndex != null && indexSupportsIncludeFilters()) {
      return addCandidateComponentsFromIndex(this.componentsIndex, basePackage);
   }
   else {
      return scanCandidateComponents(basePackage);
   }
}
```

差异在下面

**AnnotationMetadataReadingVisitor#visitAnnotation**

```java
@Override
public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
   String className = Type.getType(desc).getClassName();
   this.annotationSet.add(className);
   return new AnnotationAttributesReadingVisitor(
         className, this.attributesMap, this.metaAnnotationMap, this.classLoader);
}
```

**AnnotationAttributesReadingVisitor#visitEnd**

```java
@Override
public void visitEnd() {
   super.visitEnd();

   Class<? extends Annotation> annotationClass = this.attributes.annotationType();
   if (annotationClass != null) {
      List<AnnotationAttributes> attributeList = this.attributesMap.get(this.annotationType);
      if (attributeList == null) {
         this.attributesMap.add(this.annotationType, this.attributes);
      }
      else {
         attributeList.add(0, this.attributes);
      }
      if (!AnnotationUtils.isInJavaLangAnnotationPackage(annotationClass.getName())) {
         try {
            Annotation[] metaAnnotations = annotationClass.getAnnotations();
            if (!ObjectUtils.isEmpty(metaAnnotations)) {
               Set<Annotation> visited = new LinkedHashSet<>();
               for (Annotation metaAnnotation : metaAnnotations) {
                  recursivelyCollectMetaAnnotations(visited, metaAnnotation);
               }
               if (!visited.isEmpty()) {
                  Set<String> metaAnnotationTypeNames = new LinkedHashSet<>(visited.size());
                  for (Annotation ann : visited) {
                     metaAnnotationTypeNames.add(ann.annotationType().getName());
                  }
                  this.metaAnnotationMap.put(annotationClass.getName(), metaAnnotationTypeNames);
               }
            }
         }
         catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
               logger.debug("Failed to introspect meta-annotations on " + annotationClass + ": " + ex);
            }
         }
      }
   }
}
```

**AnnotationAttributesReadingVisitor#recursivelyCollectMetaAnnotations** 重点在这，递归查找，@Component 注解

```java
private void recursivelyCollectMetaAnnotations(Set<Annotation> visited, Annotation annotation) {
   Class<? extends Annotation> annotationType = annotation.annotationType();
   String annotationName = annotationType.getName();
   if (!AnnotationUtils.isInJavaLangAnnotationPackage(annotationName) && visited.add(annotation)) {
      try {
         // Only do attribute scanning for public annotations; we'd run into
         // IllegalAccessExceptions otherwise, and we don't want to mess with
         // accessibility in a SecurityManager environment.
         if (Modifier.isPublic(annotationType.getModifiers())) {
            this.attributesMap.add(annotationName,
                  AnnotationUtils.getAnnotationAttributes(annotation, false, true));
         }
         for (Annotation metaMetaAnnotation : annotationType.getAnnotations()) {
            recursivelyCollectMetaAnnotations(visited, metaMetaAnnotation);
         }
      }
      catch (Throwable ex) {
         if (logger.isDebugEnabled()) {
            logger.debug("Failed to introspect meta-annotations on " + annotation + ": " + ex);
         }
      }
   }
}
```



下面是 MyBatis 的 MapperScanner，整合 Spring 的比较重要的类，主要就是扫描 Mapper，寻找候选 BeanDefinition。

![image.png](https://i.loli.net/2020/12/08/CHJK73gvEMOftRU.png)

具体的代码在

Spring 抽象出 MetadataReader 接口方便读取元信息。

# 第六天

属性别名和覆盖

@AliasFor

AnnotationMetadataReadingVistor

StandardAnnotationMetadata

@Service 和 @Component 的  value 将被合并。

Spring Framework 将注解属性抽象为 AnnotationAttributes 类，扩展了 LinkedHashMap。

低层级的注解的值，会优先于高层级的注解的值。

@Component

​	｜- @Service 

​			|- @TransactionalService （这个是自定义组合注解，组合了 @Transaction 和 @Service）

@Component 是高层级注解

`Attributes from lower levels in the annotation hierarchy override attributes of the same name from higher levels.`

`An attribute override is an annotation attribute that overrides(or shadows)an annotation attribute in a meta-annotation.`

## 隐性覆盖（Implicit Overrides）

>  Implicit Overrides：given attribute A in annotation @One and attribute A in annotation @Two, if @One is meta-annotated with @Two, then attribute A in annotation A in Annotation @One is an implicit override for attribute A in annotation @Two based solely on a naming convention(i.e., both attributes are named A)

## 显性覆盖（Explicit Overrides）

> Explicit Overrides: if attribute A is declared as an alias for attribute B in a meta-annotation via @AliasFor, then A is an *explicit override* for B.

传递性的显性覆盖（Transitive Explicit Overrides）

> Transitive Explicit Overrides: if attribute A in annotation @One is a explicit override for attribute B in annotation @Two and B is an explicit override for attribute C in annotation @Three, then A is a *transitive explicit override* for C follwing the law of transitivity.

# 第七天

`@AliasFor` 如果覆盖了一个，就要覆盖另一个，如果只覆盖一个就会报错 BeanDefinitionStoreException：Failed to parse configuration class，并且其默认值必须相等，不然也会报错。如下

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RequestMapping {
	@AliasFor("path")
    String[] value() default {};

    @AliasFor("value")
    String[] path() default {};
}
```

多层次注解覆盖

@GetMapping

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(
    method = {RequestMethod.GET}
)
public @interface GetMapping {
    @AliasFor(
        annotation = RequestMapping.class
    )
    String[] params() default {};
}
```

@RequestMapping

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RequestMapping {
    String[] params() default {};
}
```

第八章 @Enable 模块驱动，由 Spring 3.1引入。

无论是“注解驱动”和“接口编程”都需要理解 3.0 的 @Import ，@Import 引入一个或多个 ConfigurationClass，将其注册为 Spring Bean，不过在 3.0 版本有个缺陷，就是只能支持 @Configuration 标注的类。

## 基于“注解驱动”实现的 @Enable 模块

@**EnableWebMvc** 这个注释很不错

```java
/**
 * Adding this annotation to an {@code @Configuration} class imports the Spring MVC
 * configuration from {@link WebMvcConfigurationSupport}, e.g.:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableWebMvc
 * &#064;ComponentScan(basePackageClasses = MyConfiguration.class)
 * public class MyConfiguration {
 *
 * }
 * </pre>
 *
 * <p>To customize the imported configuration, implement the interface
 * {@link WebMvcConfigurer} and override individual methods, e.g.:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableWebMvc
 * &#064;ComponentScan(basePackageClasses = MyConfiguration.class)
 * public class MyConfiguration implements WebMvcConfigurer {
 *
 * 	   &#064;Override
 * 	   public void addFormatters(FormatterRegistry formatterRegistry) {
 *         formatterRegistry.addConverter(new MyConverter());
 * 	   }
 *
 * 	   &#064;Override
 * 	   public void configureMessageConverters(List&lt;HttpMessageConverter&lt;?&gt;&gt; converters) {
 *         converters.add(new MyHttpMessageConverter());
 * 	   }
 *
 * }
 * </pre>
 *
 * <p><strong>Note:</strong> only one {@code @Configuration} class may have the
 * {@code @EnableWebMvc} annotation to import the Spring Web MVC
 * configuration. There can however be multiple {@code @Configuration} classes
 * implementing {@code WebMvcConfigurer} in order to customize the provided
 * configuration.
 *
 * <p>If {@link WebMvcConfigurer} does not expose some more advanced setting that
 * needs to be configured consider removing the {@code @EnableWebMvc}
 * annotation and extending directly from {@link WebMvcConfigurationSupport}
 * or {@link DelegatingWebMvcConfiguration}, e.g.:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;ComponentScan(basePackageClasses = { MyConfiguration.class })
 * public class MyConfiguration extends WebMvcConfigurationSupport {
 *
 * 	   &#064;Override
 *	   public void addFormatters(FormatterRegistry formatterRegistry) {
 *         formatterRegistry.addConverter(new MyConverter());
 *	   }
 *
 *	   &#064;Bean
 *	   public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
 *         // Create or delegate to "super" to create and
 *         // customize properties of RequestMappingHandlerAdapter
 *	   }
 * }
 * </pre>
 *
 * @author Dave Syer
 * @author Rossen Stoyanchev
 * @since 3.1
 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer
 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
 * @see org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DelegatingWebMvcConfiguration.class)
public @interface EnableWebMvc {
}
```

**DelegatingWebMvcConfiguration**

```java
@Configuration
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
    
}
```

根据源码说明上面的类是一个 @Configuration 类

## 基于“接口编程”实现的 @Enable 模块

+ **ImportSelector**

使用 Spring 注解元信息抽象 AnnotationMetadata 作为方法参数，该参数的内容为导入 ImportSelector 实现的 @Configuration 类元信息，进而动态地选择一个或多个其他 @Configuration 类进行导入。

```java
/**
 * Register bean definitions as necessary based on the given annotation metadata of
 * the importing {@code @Configuration} class.
 * <p>Note that {@link BeanDefinitionRegistryPostProcessor} types may <em>not</em> be
 * registered here, due to lifecycle constraints related to {@code @Configuration}
 * class processing.
 * @param importingClassMetadata annotation metadata of the importing class
 * @param registry current bean definition registry
 */
public interface ImportBeanDefinitionRegistrar {
	void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry);

}

public interface ImportSelector {

   /**
    * Select and return the names of which class(es) should be imported based on
    * the {@link AnnotationMetadata} of the importing @{@link Configuration} class.
    */
   String[] selectImports(AnnotationMetadata importingClassMetadata);

}
```

+ **ImportBeanDefinitionRegistrar**

除了 AnnotationMetadata，还将 BeanDefinitionRegistry 交给开发人员决定，

```java
public interface ImportBeanDefinitionRegistrar {

   /**
    * Register bean definitions as necessary based on the given annotation metadata of
    * the importing {@code @Configuration} class.
    * <p>Note that {@link BeanDefinitionRegistryPostProcessor} types may <em>not</em> be
    * registered here, due to lifecycle constraints related to {@code @Configuration}
    * class processing.
    * @param importingClassMetadata annotation metadata of the importing class
    * @param registry current bean definition registry
    */
   void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry);

}
```

**CacheConfigurationSelector**

```java
/**
 * Returns {@link ProxyCachingConfiguration} or {@code AspectJCachingConfiguration}
 * for {@code PROXY} and {@code ASPECTJ} values of {@link EnableCaching#mode()},
 * respectively. Potentially includes corresponding JCache configuration as well.
 */
@Override
public String[] selectImports(AdviceMode adviceMode) {
   switch (adviceMode) {
      case PROXY:
         return getProxyImports();
      case ASPECTJ:
         return getAspectJImports();
      default:
         return null;
   }
}
```

## @Enable 模块驱动原理

### 1. 装载 @Configuration Class

@Configuration 从 Spring Framework 3.0 开始引入，该版本还未引入 @ComponentScan，因此配套的导入注解是 @Import。ConfigurationClassPostProcessor 现在改成了最低优先级了，书上是最高优先级，实际是最低 Ordered.LOWEST_PRECEDENCE;

ConfigurationClassPostProcessor#postProcessBeanFactory 处理 @Configuration 类和 @Bean 方法。



ConfigurationClassPostProcessor 使用 CGLib 实现 ConfigurationClassEnhancer，用于提升 @Configuration Class：

### 2. 装载 ImportSelector 和 ImportBeanDefinitionRegistrar 实现

3.1 才引入。

@ConfigruationClassPostProcessor 增加了@PropertySource 和 @ComponentScan 注解处理，并更新了 processImport(ConfigurationClass, String [], boolean)方法的实现。

综上所述 ConfigurationClassPostProcessor 负责筛选 @Component Class、@Configruation Class 及 @Bean 方法定义（BeanDefinition），ConfigurationClassParser 则从候选的 Bean 定义中解析出 ConfigurationClass 集合，随后被 ConfigurationClassBeanDefinitionReader 转化并注册 BeanDefinition。

## Spring Web 自动装配

Servlet 3.0 规范

需要支持 

ServletContext#addServlet

ServletContext#addFilter

ServletContext#addListener

正是因为 Servlet3.0 的规范，才让 Spring Web 有了自动装配的能力。

# 第八天

结合 Servlet 3.0 规范，当容器或者应用启动时，`ServletContainerInitializer#onStartup(Set<Class<?>>,ServletContext)` 方法将被回调，同时为了选择关心的类型，通过 @HandlesTypes 来进行过滤，即关心类型通过 @HandlesTypes#value 属性方法来指定。该类型的子类（含抽象类）候选为类集合`Set<Class<?>>`，作为 onStartup 方法的第一个入参。不过 ServletContainerInitializer 的一个或错个实现类需要存放在一个名为“javax.servlet.ServletContainerInitializer”的文本文件中，该文件存放在独立 JAR 包中的 “META-INF/services”目录下。

**SpringServletContainerInitializer** Spring 3.0 引入的类。按照规范 WebApplicationInitializer 类的子类将会被引入`Set<Class<?>>`。

```java
@HandlesTypes({WebApplicationInitializer.class})
public class SpringServletContainerInitializer implements ServletContainerInitializer {
    	@Override
	public void onStartup(@Nullable Set<Class<?>> webAppInitializerClasses, ServletContext servletContext)
			throws ServletException {

		List<WebApplicationInitializer> initializers = new LinkedList<>();

		if (webAppInitializerClasses != null) {
			for (Class<?> waiClass : webAppInitializerClasses) {
				// Be defensive: Some servlet containers provide us with invalid classes,
				// no matter what @HandlesTypes says...
				if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers()) &&
						WebApplicationInitializer.class.isAssignableFrom(waiClass)) {
					try {
						initializers.add((WebApplicationInitializer)
								ReflectionUtils.accessibleConstructor(waiClass).newInstance());
					}
					catch (Throwable ex) {
						throw new ServletException("Failed to instantiate WebApplicationInitializer class", ex);
					}
				}
			}
		}

		if (initializers.isEmpty()) {
			servletContext.log("No Spring WebApplicationInitializer types detected on classpath");
			return;
		}

		servletContext.log(initializers.size() + " Spring WebApplicationInitializers detected on classpath");
		AnnotationAwareOrderComparator.sort(initializers);
		for (WebApplicationInitializer initializer : initializers) {
			initializer.onStartup(servletContext);
		}
	}
}
```

关于 **WebApplicationInitializer** 的实现，Spring 3.2 引入了新的三种抽象实现。

> AbstractContextLoaderInitializer
>
> ｜— AbstractDispatcherServletInitializer
>
> ​		｜— AbstractAnnotationConfigDispatcherServletInitializer

简单介绍三种抽象类使用场景

+ AbstractContextLoaderInitializer——替代 web.xml
+ AbstractDispatcherServletInitializer——替代 web.xml 注册 DispatcherServlet，有必要的话，创建 Web Root 应用上下文（WebApplicationContext）
+ AbstractAnnotationConfigDispatcherServletInitializer——具备 Annotation 配置驱动能力的 AbstractDispatcherServletInitializer

## AbstractContextLoaderInitializer 装配原理

传统的 Spring Web 应用，有一个 ContextLoaderListener 和 DispathcerServlet 在 web.xml 里面配置。

在 Servlet 3.0+ 环境时，web.xml 部署 ContextLoaderListener 的方式可替换为实现抽象类 AbstractContextLoaderInitalizer 来完成。通常情况下，子类只需要实现它的 createRootApplicationContext() 方法。

讲了半天，没有讲装配原理。

## AbstractDispatcherServletInitializer 

还是没有讲具体的装配原理

## AbstractAnnotationConfigDispatcherServletInitializer

JSR 规范。

## Spring 条件装配

注解驱动 Bean 注册途径大致如下表所示。

|       注解驱动 Bean 注册方式       |     使用场景说明     |            Bean 注解元信息处理类            |
| :--------------------------------: | :------------------: | :-----------------------------------------: |
|           @ComponentScan           | 扫描 Spring 模式注解 | ClassPathScanningCandidateComponentProvider |
| @Component 或 @Configuration Class |     @Import 导入     |       ConfigrationClassPostProcessor        |
|               @Bean                |    @Bean 方法定义    |          ConfigurationClassParser           |
| AnnotationConfigApplicationContext |   注册 Bean Class    |        AnnotatedBeanDefinitionReader        |

从 Spring 3.1 开始，以上三种 Bean 注解元信息处理类均增加了 @Profile 的处理。

### @Profile

以上三个类均有实现该注解解析。

### @Conditional

实现 Condition 接口，matches 返回为 true 则注册。ConditionContext 包含 Spring 应用上下文相关：BeanDefinitionRegistry、ConfigurableListableBeanFactory、Enviroment、ResourceLoader 和 ClassLoader。

```java
@FunctionalInterface
public interface Condition {
    
   boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);

}
```

**@ConditionalOnClass**

```java
@Conditional(OnClassCondition.class)
public @interface ConditionalOnClass {
    
    Class<?>[] value() default {};

	String[] name() default {};
    
}
@Order(Ordered.HIGHEST_PRECEDENCE)
class OnClassCondition extends FilteringSpringBootCondition {
    // 间接实现了 Condition 接口
}
```

# 第九天

## 自定义条件装配

具体代码在` spring-in-action/me/young1lin/spring/boot/thinking/conditional` 下。

**@ConditionalOnSystemProperty**

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnSystemPropertyCondition.class)
public @interface ConditionalOnSystemProperty {

    /**
     * @return System property name
     */
    String name();

    /**
     * @return System property value
     */
    String value();
    
}
```

**OnSystemPropertyCondition**

```java
public class OnSystemPropertyCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // get all of ConditionalOnSystemProperty method attribute value
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(ConditionalOnSystemProperty.class.getName());
        // get all of ConditionalOnSystemProperty#name value
        String propertyName = (String) attributes.getFirst("name");
        // get all of ConditionalOnSystemProperty#value value
        String propertyValue = (String) attributes.getFirst("value");
        // get SystemProperty value
        String systemPropertyValue = System.getProperty(propertyName);
        // match SystemPropertyValue and ConditionalOnSystemProperty#value is equals
        if (Objects.equals(systemPropertyValue, propertyValue)) {
            System.out.printf("SystemProperty [name: %s] find match [value: %s\n]", propertyName, propertyValue);
            return true;
        }
        return false;
    }

}
```

**ConditionalMessageConfiguration**

```java
@Configuration
public class ConditionalMessageConfiguration {

    @ConditionalOnSystemProperty(name = "language", value = "Chinese")
    @Bean("message")
    public String chineseMessage() {
        return "你好，世界";
    }

    @ConditionalOnSystemProperty(name = "language", value = "English")
    @Bean("message")
    public String englishMessage() {
        return "Hello World";
    }

}
```

**ConditionOnSystemPropertyBootstrap**

```java
public class ConditionOnSystemPropertyBootstrap {

    public static void main(String[] args) {
        System.setProperty("language", "Chinese");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ConditionalMessageConfiguration.class);
        context.refresh();
        String message = context.getBean("message", String.class);
        System.out.printf("message bean object is  %s", message);
    }

}
```

**ConditionEvaluator** 这个类的 shouldSkip 来评估 Condition 接口实现类是否该跳过。然后 Condition 实现类根据 @Order 来排序。

```java
public boolean shouldSkip(@Nullable AnnotatedTypeMetadata metadata, @Nullable ConfigurationPhase phase) {
   if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
      return false;
   }

   if (phase == null) {
      if (metadata instanceof AnnotationMetadata &&
            ConfigurationClassUtils.isConfigurationCandidate((AnnotationMetadata) metadata)) {
         return shouldSkip(metadata, ConfigurationPhase.PARSE_CONFIGURATION);
      }
      return shouldSkip(metadata, ConfigurationPhase.REGISTER_BEAN);
   }

   List<Condition> conditions = new ArrayList<>();
   for (String[] conditionClasses : getConditionClasses(metadata)) {
      for (String conditionClass : conditionClasses) {
         Condition condition = getCondition(conditionClass, this.context.getClassLoader());
         conditions.add(condition);
      }
   }
	// 这里有个排序
   AnnotationAwareOrderComparator.sort(conditions);

   for (Condition condition : conditions) {
      ConfigurationPhase requiredPhase = null;
      if (condition instanceof ConfigurationCondition) {
         requiredPhase = ((ConfigurationCondition) condition).getConfigurationPhase();
      }
      if ((requiredPhase == null || requiredPhase == phase) && !condition.matches(this.context, metadata)) {
         return true;
      }
   }

   return false;
}
```

## Spring Boot 自动装配

// 扫描默认根包

`@ComponentScan(basePackages="")` 不建议这么使用

**@EnableAutoConfiguration**

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
    
    String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";
    
	Class<?>[] exclude() default {};

	String[] excludeName() default {};

}
```

## 使 Spring Boot 自动装配失效

+ 代码配置方式
  + EnableAutoConfiguration#exclude
  + EnableAutoConfiguration#excludeName
+ 外部化配置方式
  + spring.autoconfigure.exclude

类似黑名单（有歧视意义，应该改成 Blocklist 禁止名单）的配置方式。

```java
public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware,
      	ResourceLoaderAware, BeanFactoryAware, EnvironmentAware, Ordered {
    // 省略一大部分代码
	@Override
	public String[] selectImports(AnnotationMetadata annotationMetadata) {
		if (!isEnabled(annotationMetadata)) {
			return NO_IMPORTS;
		}
		AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(annotationMetadata);
		return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
	}
}
```

# 第十天

## Spring Boot 自动装配原理

**@EnableAutoConfiguration**

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
    
}
```

**@AutoConfigurationPackage**

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(AutoConfigurationPackages.Registrar.class)
public @interface AutoConfigurationPackage {
    String[] basePackages() default {};
    Class<?>[] basePackageClasses() default {};
}
```

**AutoConfigruationImportSelector#getAutoConfigurationEntry** 在 selectImports 方法里面

```java
protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
   if (!isEnabled(annotationMetadata)) {
      return EMPTY_ENTRY;
   }
   AnnotationAttributes attributes = getAttributes(annotationMetadata);
   List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
   configurations = removeDuplicates(configurations);
   Set<String> exclusions = getExclusions(annotationMetadata, attributes);
   checkExcludedClasses(configurations, exclusions);
   configurations.removeAll(exclusions);
   configurations = getConfigurationClassFilter().filter(configurations);
   fireAutoConfigurationImportEvents(configurations, exclusions);
   return new AutoConfigurationEntry(configurations, exclusions);
}
```

### 获取候选装配组件 getCandidateConfigurations

**AutoConfigruationImportSelector#getCandidateConfigurations**

```java
protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
   List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
         getBeanClassLoader());
   Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
         + "are using a custom packaging, make sure that file is correct.");
   return configurations;
}
```

**AutoConfigruationImportSelector#getSpringFactoriesLoaderFactoryClass**

```java
protected Class<?> getSpringFactoriesLoaderFactoryClass() {
    return EnableAutoConfiguration.class;
}
```

**SpringFactoriesLoader#loadFactoryNames** 重点，这里就是加载 spring.factories

```java
public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
   String factoryTypeName = factoryType.getName();
   return loadSpringFactories(classLoader).getOrDefault(factoryTypeName, Collections.emptyList());
}
```

**SpringFactoriesLoader#loadSpringFactories**

```java
private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
   MultiValueMap<String, String> result = cache.get(classLoader);
   if (result != null) {
      return result;
   }

   try {
      Enumeration<URL> urls = (classLoader != null ?
            classLoader.getResources(FACTORIES_RESOURCE_LOCATION) :
            ClassLoader.getSystemResources(FACTORIES_RESOURCE_LOCATION));
      result = new LinkedMultiValueMap<>();
      while (urls.hasMoreElements()) {
         URL url = urls.nextElement();
         UrlResource resource = new UrlResource(url);
         Properties properties = PropertiesLoaderUtils.loadProperties(resource);
         for (Map.Entry<?, ?> entry : properties.entrySet()) {
            String factoryTypeName = ((String) entry.getKey()).trim();
            for (String factoryImplementationName : StringUtils.commaDelimitedListToStringArray((String) entry.getValue())) {
               result.add(factoryTypeName, factoryImplementationName.trim());
            }
         }
      }
      cache.put(classLoader, result);
      return result;
   }
   catch (IOException ex) {
      throw new IllegalArgumentException("Unable to load factories from location [" +
            FACTORIES_RESOURCE_LOCATION + "]", ex);
   }
}
```

加载 spring.factories 步骤。

1. 搜索指定 ClassLoader 下所有的 META-INF/spring.factories 资源内容（可能存在多个）。
2. 将一个或多个 META-INF/spring.factories 资源内容作为 Properties 文件读取，合并为一个 Key 为接口的全限定名，Value 时实现类全限定名列表的 Map，作为 loadSpringFactories(ClassLoader)方法的返回值。
3. 再从上一步返回的 Map 中查找并返回方法指定全限定名所映射的实现类类全限定名列表。

因为 spring.factories 中可能会存在重复的定义，所以要去重，去完重再进行执行排除操作。

## 排除自动装配组件 @EnableAutoConfiguration

简单的排除 List、exclusions

## 过滤自动装配组件 @EnableAutoConfiguration

AutoConfigurationImportFilter 实际上是过滤 META-INF/spring.factories 资源中那些当前 ClassLoader 不存在的 Class。

## AutoConfigurationImportSelector 读取自动装配 Class 的流程

1. 通过 SpringFactoriesLoader#loadFactoryNames(Class,ClassLoader) 方法读取所有 META-INF/spring.factories 资源中 @EnableAutoConfiguartion 所关联的自动装配 Class 集合。
2. 读取当前配置类所标注的 @EnableAutoConfiguration 属性 exclude 和 excludeName，并与 spring.autoconfigure.exclude 配置属性合并为自动装配 Class 集合。
3. 检查自动装配 Class 排除集合是否合法。
4. 排除候选自动装配 Class 集合中的排除名单。
5. 再次过滤后选自动装配 Class 集合中 Class 不存在的成员。

# 第十一天

## @EnableAutoConfiguration 自动装配事件

**AutoConfigurationImportSelector#fireAutoConfigurationImportEvents**

```java
private void fireAutoConfigurationImportEvents(List<String> configurations, Set<String> exclusions) {
   List<AutoConfigurationImportListener> listeners = getAutoConfigurationImportListeners();
   if (!listeners.isEmpty()) {
      AutoConfigurationImportEvent event = new AutoConfigurationImportEvent(this, configurations, exclusions);
      for (AutoConfigurationImportListener listener : listeners) {
         invokeAwareMethods(listener);
         listener.onAutoConfigurationImportEvent(event);
      }
   }
}

protected List<AutoConfigurationImportListener> getAutoConfigurationImportListeners() {
    return SpringFactoriesLoader.loadFactories(AutoConfigurationImportListener.class, this.beanClassLoader);
}
```

实际例子，参照 `me.young1lin.spring.boot.thinking.autoconfig` 下面的类，以及 `META-INF/spring.factories`

## @EnableAutoConfiguration 自动装配生命周期

AutoConfigurationImportSelector 实现了 DeferredImportSelector 接口，这个接口由 Spring 4.0 引入，主要目的是在于在 @Configuration Bean 处理完毕后才运作。它在 @Conditional 场景中尤其有用，同时实现 Order 接口来台哦正其优先执行顺序。

![image.png](https://i.loli.net/2020/12/17/5zdvpfSbB9ZGyXK.png)

**ConfigurationClassParser#processImports** 在这之前，是处理 @ComponnetScan、@Component、@PropertySources

```java
// Process any @Import annotations
private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass,
      Collection<SourceClass> importCandidates, Predicate<String> exclusionFilter,
      boolean checkForCircularImports) {

   if (importCandidates.isEmpty()) {
      return;
   }

   if (checkForCircularImports && isChainedImportOnStack(configClass)) {
      this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
   }
   else {
      this.importStack.push(configClass);
      try {
         for (SourceClass candidate : importCandidates) {
            if (candidate.isAssignable(ImportSelector.class)) {
               // Candidate class is an ImportSelector -> delegate to it to determine imports
               Class<?> candidateClass = candidate.loadClass();
               ImportSelector selector = ParserStrategyUtils.instantiateClass(candidateClass, ImportSelector.class,
                     this.environment, this.resourceLoader, this.registry);
               Predicate<String> selectorFilter = selector.getExclusionFilter();
               if (selectorFilter != null) {
                  exclusionFilter = exclusionFilter.or(selectorFilter);
               }
               if (selector instanceof DeferredImportSelector) {
                  this.deferredImportSelectorHandler.handle(configClass, (DeferredImportSelector) selector);
               }
               else {
                  String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
                  Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames, exclusionFilter);
                  processImports(configClass, currentSourceClass, importSourceClasses, exclusionFilter, false);
               }
            }
            else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
               // Candidate class is an ImportBeanDefinitionRegistrar ->
               // delegate to it to register additional bean definitions
               Class<?> candidateClass = candidate.loadClass();
               ImportBeanDefinitionRegistrar registrar =
                     ParserStrategyUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class,
                           this.environment, this.resourceLoader, this.registry);
               configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
            }
            else {
               // Candidate class not an ImportSelector or ImportBeanDefinitionRegistrar ->
               // process it as an @Configuration class
               this.importStack.registerImport(
                     currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
               processConfigurationClass(candidate.asConfigClass(configClass), exclusionFilter);
            }
         }
      }
      catch (BeanDefinitionStoreException ex) {
         throw ex;
      }
      catch (Throwable ex) {
         throw new BeanDefinitionStoreException(
               "Failed to process import candidates for configuration class [" +
               configClass.getMetadata().getClassName() + "]", ex);
      }
      finally {
         this.importStack.pop();
      }
   }
}
```

**ConfigurationClassParser#parse**

```java
public void parse(Set<BeanDefinitionHolder> configCandidates) {
   for (BeanDefinitionHolder holder : configCandidates) {
      BeanDefinition bd = holder.getBeanDefinition();
      try {
         if (bd instanceof AnnotatedBeanDefinition) {
            parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
         }
         else if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) bd).hasBeanClass()) {
            parse(((AbstractBeanDefinition) bd).getBeanClass(), holder.getBeanName());
         }
         else {
            parse(bd.getBeanClassName(), holder.getBeanName());
         }
      }
      catch (BeanDefinitionStoreException ex) {
         throw ex;
      }
      catch (Throwable ex) {
         throw new BeanDefinitionStoreException(
               "Failed to parse configuration class [" + bd.getBeanClassName() + "]", ex);
      }
   }

   this.deferredImportSelectorHandler.process();
}
```

**ConfigurationClassParser.DeferredImportSelectorHandler#process**

```java
public void process() {
   List<DeferredImportSelectorHolder> deferredImports = this.deferredImportSelectors;
   this.deferredImportSelectors = null;
   try {
      if (deferredImports != null) {
         DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
         deferredImports.sort(DEFERRED_IMPORT_COMPARATOR);
         deferredImports.forEach(handler::register);
         handler.processGroupImports();
      }
   }
   finally {
      this.deferredImportSelectors = new ArrayList<>();
   }
}
```

在 Spring 5.0 DeferredImportSelector.Group 接口辅助处理 DeferredImportSelector 导入的 Configuration Class

后面的内容比较简单。

**AutoConfigurationImportSelector#getImportGroup**

```java
@Override
public Class<? extends Group> getImportGroup() {
   return AutoConfigurationGroup.class;
}
```

## 排序自动装配组件 @EnableAutoConfiguration

两种自动装配组件的顺序手段

+ 绝对自动装配顺序——@AutoCofingureOrder；
+ 相对自动装配顺序——@AutoConfigureBefore 和 @AutoConfigureAfter。

上面三个注解的实现在 AutoConfigurationGroup#selectImports 方法实现中：

**AutoConfigurationGroup#selectImports**

```java
@Override
public Iterable<Entry> selectImports() {
   if (this.autoConfigurationEntries.isEmpty()) {
      return Collections.emptyList();
   }
   Set<String> allExclusions = this.autoConfigurationEntries.stream()
         .map(AutoConfigurationEntry::getExclusions).flatMap(Collection::stream).collect(Collectors.toSet());
   Set<String> processedConfigurations = this.autoConfigurationEntries.stream()
         .map(AutoConfigurationEntry::getConfigurations).flatMap(Collection::stream)
         .collect(Collectors.toCollection(LinkedHashSet::new));
   processedConfigurations.removeAll(allExclusions);

   return sortAutoConfigurations(processedConfigurations, getAutoConfigurationMetadata()).stream()
         .map((importClassName) -> new Entry(this.entries.get(importClassName), importClassName))
         .collect(Collectors.toList());
}
```

**AutoConfigurationGroup#sortAutoConfigurations**

```java
private List<String> sortAutoConfigurations(Set<String> configurations,
      AutoConfigurationMetadata autoConfigurationMetadata) {
   return new AutoConfigurationSorter(getMetadataReaderFactory(), autoConfigurationMetadata)
         .getInPriorityOrder(configurations);
}
```

# 第十二天

META-INF/spring-autoconfigure-metadata.properties 可认为是自动装配 Class 预处理元信息配置的资源。当该资源文件存在自动装配 Class 的注解元信息配置事，自动装配 Class 无须 ClassLoader 加载，即可得到所需的元信息，减少了运行时的计算消耗。所以相较于 1.5 有所提升。

AutoConfigurationMetadata 作为上述 metadata 资源的封装对象，再次在 sortAutoConfigurations 方法中加载，它与 MetadataReaderFactory 对象同时作为 AutoConfigurationSorter 构造参数，辅助 AutoConfigurationSorter#getInPriorityOrder(Collection)方法对自动装配 Class 进入进行排序。

```java
class AutoConfigurationSorter {

   private final MetadataReaderFactory metadataReaderFactory;

   private final AutoConfigurationMetadata autoConfigurationMetadata;

   AutoConfigurationSorter(MetadataReaderFactory metadataReaderFactory,
         AutoConfigurationMetadata autoConfigurationMetadata) {
      Assert.notNull(metadataReaderFactory, "MetadataReaderFactory must not be null");
      this.metadataReaderFactory = metadataReaderFactory;
      this.autoConfigurationMetadata = autoConfigurationMetadata;
   }
    List<String> getInPriorityOrder(Collection<String> classNames) {
        AutoConfigurationClasses classes = new AutoConfigurationClasses(
            this.metadataReaderFactory,this.autoConfigurationMetadata, classNames);
        List<String> orderedClassNames = new ArrayList<>(classNames);
        // Initially sort alphabetically
        Collections.sort(orderedClassNames);
        // Then sort by order
        orderedClassNames.sort((o1, o2) -> {
            int i1 = classes.get(o1).getOrder();
            int i2 = classes.get(o2).getOrder();
            return Integer.compare(i1, i2);
        });
        // Then respect @AutoConfigureBefore @AutoConfigureAfter
        orderedClassNames = sortByAnnotation(classes, orderedClassNames);
        return orderedClassNames;
	}
}
```

// TODO 中间比较繁杂，跳过，以后补充。

@AutoConfigureAfter 和 @AutoConfigureBefore 尽可能用 name 而不是 value，后者有坑。

## 自动装配 BasePackage

Spring Boot 1.3 开始，@EnableAutoConfiguration 元标注新注解 @AutoConfigurationPackage。

ConfigurationClassPostProcessor 提供递归处理配置 Class 和 元注解的能力。

```java
public abstract class AutoConfigurationPackages {
    	/**
	 * {@link ImportBeanDefinitionRegistrar} to store the base package from the importing
	 * configuration.
	 */
	static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {

		@Override
		public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
			register(registry, new PackageImports(metadata).getPackageNames().toArray(new String[0]));
		}

		@Override
		public Set<Object> determineImports(AnnotationMetadata metadata) {
			return Collections.singleton(new PackageImports(metadata));
		}

	}
	// 注册 BeanDefinition
	public static void register(BeanDefinitionRegistry registry, String... packageNames) {
		if (registry.containsBeanDefinition(BEAN)) {
			BeanDefinition beanDefinition = registry.getBeanDefinition(BEAN);
			ConstructorArgumentValues constructorArguments = beanDefinition.getConstructorArgumentValues();
			constructorArguments.addIndexedArgumentValue(0, addBasePackages(constructorArguments, packageNames));
		}
		else {
			GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
			beanDefinition.setBeanClass(BasePackages.class);
			beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, packageNames);
			beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			registry.registerBeanDefinition(BEAN, beanDefinition);
		}
	}
    
	/**
	 * Wrapper for a package import.
	 */
	private static final class PackageImports {

		private final List<String> packageNames;

		PackageImports(AnnotationMetadata metadata) {
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(AutoConfigurationPackage.class.getName(), false));
			List<String> packageNames = new ArrayList<>();
			for (String basePackage : attributes.getStringArray("basePackages")) {
				packageNames.add(basePackage);
			}
			for (Class<?> basePackageClass : attributes.getClassArray("basePackageClasses")) {
				packageNames.add(basePackageClass.getPackage().getName());
			}
			if (packageNames.isEmpty()) {
				packageNames.add(ClassUtils.getPackageName(metadata.getClassName()));
			}
			this.packageNames = Collections.unmodifiableList(packageNames);
		}

		List<String> getPackageNames() {
			return this.packageNames;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			return this.packageNames.equals(((PackageImports) obj).packageNames);
		}

		@Override
		public int hashCode() {
			return this.packageNames.hashCode();
		}

		@Override
		public String toString() {
			return "Package Imports " + this.packageNames;
		}
	}
    
	/**
	 * Holder for the base package (name may be null to indicate no scanning).
	 */
	static final class BasePackages {

		private final List<String> packages;

		private boolean loggedBasePackageInfo;

		BasePackages(String... names) {
			List<String> packages = new ArrayList<>();
			for (String name : names) {
				if (StringUtils.hasText(name)) {
					packages.add(name);
				}
			}
			this.packages = packages;
		}

		List<String> get() {
			if (!this.loggedBasePackageInfo) {
				if (this.packages.isEmpty()) {
					if (logger.isWarnEnabled()) {
						logger.warn("@EnableAutoConfiguration was declared on a class "
								+ "in the default package. Automatic @Repository and "
								+ "@Entity scanning is not enabled.");
					}
				}
				else {
					if (logger.isDebugEnabled()) {
						String packageNames = StringUtils.collectionToCommaDelimitedString(this.packages);
						logger.debug("@EnableAutoConfiguration was declared on a class in the package '" + packageNames
								+ "'. Automatic @Repository and @Entity scanning is enabled.");
					}
				}
				this.loggedBasePackageInfo = true;
			}
			return this.packages;
		}

	}
}
```

## 自定义 Spring Boot Starter

