# 探索 SpringApplication 启动 Spring

**SpringApplication#run**

```java
/**
 * 返回可配置的 ApplicationContext
 */
public ConfigurableApplicationContext run(String... args) {
   // 计时器
   StopWatch stopWatch = new StopWatch();
   stopWatch.start();
   ConfigurableApplicationContext context = null;
   Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
   configureHeadlessProperty();
   SpringApplicationRunListeners listeners = getRunListeners(args);
   listeners.starting();
   try {
      // 这里是默认的应用参数给注入进去
      ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
      // 提前准备好 Environment，
      ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
      // 通过 enviroment 配置忽略某些 bean 的信息
      configureIgnoreBeanInfo(environment);
      // 从 Enviroment 中获取 banner 信息，这里可以是 banner.txt,banner.img 打印 Banner
      Banner printedBanner = printBanner(environment);
      // 创建上下文
      // 1
      context = createApplicationContext();
      // 从 spring.factories 中获取信息
      exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
            new Class[] { ConfigurableApplicationContext.class }, context);
      // 准备上下文信息，把环境，context 注入进去 
      prepareContext(context, environment, listeners, applicationArguments, printedBanner);
      // 调用 ConfigurableApplication#refresh，实际是 AbstractApplicationContext#refresh，回到了 Spring 经典的 refresh 环节
      // 2
      refreshContext(context);
      // 在 refresh 之后做一些操作
      afterRefresh(context, applicationArguments);
      stopWatch.stop();
      if (this.logStartupInfo) {
         new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
      }
      listeners.started(context);
      callRunners(context, applicationArguments);
   }
   catch (Throwable ex) {
      handleRunFailure(context, ex, exceptionReporters, listeners);
      throw new IllegalStateException(ex);
   }

   try {
      listeners.running(context);
   }
   catch (Throwable ex) {
      handleRunFailure(context, ex, exceptionReporters, null);
      throw new IllegalStateException(ex);
   }
   return context;
}
```

**SpringApplication#createApplicationContext**

```java
// 1
protected ConfigurableApplicationContext createApplicationContext() {
   Class<?> contextClass = this.applicationContextClass;
   if (contextClass == null) {
      try {
         switch (this.webApplicationType) {
         case SERVLET:
            // org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
            // 就是反射创建对象
            contextClass = Class.forName(DEFAULT_SERVLET_WEB_CONTEXT_CLASS);
            break;
         case REACTIVE:
            // org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext
            // 其实这里面和标准的没差多少，就是为了扩展为 Reactive 应用做准备。Standard
            contextClass = Class.forName(DEFAULT_REACTIVE_WEB_CONTEXT_CLASS);
            break;
         default:
            // org.springframework.context.annotation.AnnotationConfigApplicationContext     
            contextClass = Class.forName(DEFAULT_CONTEXT_CLASS);
         }
      }
      catch (ClassNotFoundException ex) {
         throw new IllegalStateException(
               "Unable create a default ApplicationContext, please specify an ApplicationContextClass", ex);
      }
   }
   return (ConfigurableApplicationContext) BeanUtils.instantiateClass(contextClass);
}
```

**SpringApplication#prepareContext**

```java
private void prepareContext(ConfigurableApplicationContext context, ConfigurableEnvironment environment,
      SpringApplicationRunListeners listeners, ApplicationArguments applicationArguments, Banner printedBanner) {
   // 上下文设置环境，如果不设置，就会在 getEnviroment 创建默认的环境。详情参见 AbstractApplicationContext#getEnviroment。如下所示
   /** 	@Override
	* public ConfigurableEnvironment getEnvironment() {
	* 	if (this.environment == null) {
	* 		this.environment = createEnvironment();
	* 	}
	* 	return this.environment;
	* }
	*/
   context.setEnvironment(environment);
   postProcessApplicationContext(context);
   applyInitializers(context);
   listeners.contextPrepared(context);
   if (this.logStartupInfo) {
      logStartupInfo(context.getParent() == null);
      logStartupProfileInfo(context);
   }
   // Add boot specific singleton beans
   ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
   beanFactory.registerSingleton("springApplicationArguments", applicationArguments);
   if (printedBanner != null) {
      beanFactory.registerSingleton("springBootBanner", printedBanner);
   }
   if (beanFactory instanceof DefaultListableBeanFactory) {
      ((DefaultListableBeanFactory) beanFactory)
            .setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
   }
   if (this.lazyInitialization) {
      context.addBeanFactoryPostProcessor(new LazyInitializationBeanFactoryPostProcessor());
   }
   // Load the sources
   Set<Object> sources = getAllSources();
   Assert.notEmpty(sources, "Sources must not be empty");
   // 4
   load(context, sources.toArray(new Object[0]));
   listeners.contextLoaded(context);
}
```

**SpringApplication#load**

```java
/**
 * Load beans into the application context.
 * @param context the context to load beans into
 * @param sources the sources to load
 */
// 4
protected void load(ApplicationContext context, Object[] sources) {
   if (logger.isDebugEnabled()) {
      logger.debug("Loading source " + StringUtils.arrayToCommaDelimitedString(sources));
   }
   BeanDefinitionLoader loader = createBeanDefinitionLoader(getBeanDefinitionRegistry(context), sources);
   if (this.beanNameGenerator != null) {
      loader.setBeanNameGenerator(this.beanNameGenerator);
   }
   if (this.resourceLoader != null) {
      loader.setResourceLoader(this.resourceLoader);
   }
   if (this.environment != null) {
      loader.setEnvironment(this.environment);
   }
   // TODO BeanDefinitionLoader 和第一章开始讲的一样
   loader.load();
}
```

# Stater 自动化配置原理

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

   /**
    * Exclude specific auto-configuration classes such that they will never be applied.
    * @return the classes to exclude
    */
   Class<?>[] exclude() default {};

   /**
    * Exclude specific auto-configuration class names such that they will never be
    * applied.
    * @return the class names to exclude
    * @since 1.3.0
    */
   String[] excludeName() default {};

}
```

**AutoConfigurationImportSelector#isEnabled**

```java
protected boolean isEnabled(AnnotationMetadata metadata) {
   if (getClass() == AutoConfigurationImportSelector.class) {
      return getEnvironment().getProperty(EnableAutoConfiguration.ENABLED_OVERRIDE_PROPERTY, Boolean.class, true);
   }
   return true;
}
```

## spring.factories 的加载

**AutoConfigurationImportSelector#selectImports**

```java
@Override
public String[] selectImports(AnnotationMetadata annotationMetadata) {
    if (!isEnabled(annotationMetadata)) {
        return NO_IMPORTS;
    }
    AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(annotationMetadata);
    return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
}
```

**AutoConfigurationImportSelectors#getAutoConfigurationEntry**

```java
protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
   if (!isEnabled(annotationMetadata)) {
      return EMPTY_ENTRY;
   }
   AnnotationAttributes attributes = getAttributes(annotationMetadata);
   // 获得候选的配置
   // 5
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

**AutoConfiurationImportSelector#getCandidateConfigurations**

```java
// 5
protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
   // 6
   List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
         getBeanClassLoader());
   Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
         + "are using a custom packaging, make sure that file is correct.");
   return configurations;
}
```

**SpringFactoriesLoader#loadFactoryNames**

```java
public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
   String factoryTypeName = factoryType.getName();
   // 7
   return loadSpringFactories(classLoader).getOrDefault(factoryTypeName, Collections.emptyList());
}
```

**SpringFactoriesLoader#loadSpringFactories** 通过硬编码的方式，来遵循约定大于配置的思想，来加载 spring.factories 文件

```java
// 7
private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
   MultiValueMap<String, String> result = cache.get(classLoader);
   if (result != null) {
      return result;
   }

   try {
    /**
	 * The location to look for factories.
	 * <p>Can be present in multiple JAR files.可能存在于多个 jar 中
	 */
	// public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
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

## factories 调用时序图

META-INF/spring.factories 如何与 Spring 整合。// TODO 画出时序图

 EmbeddedApplicationContext#refresh ->

EmbeddedApplicationContext#invkeBeanFactoryPostProcessors ->

PostProcessortRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors ->

PostProcessortRegistrationDelegate#postProcessBeanDefinitionRegistry ->

ConfigurationClassPostProcessor#processConfigBeanDefinitions ->

ConfigurationClassPostProcessor#parse - >

ConfigurationClassParaser#processDeferredImportSelectors ->

ConfigurationClassParaser#selectImports ->

AutoConfigurationImportSelector

Spring Boot 使用了 Spring 提供的 BeanDefinitionRegistryPostProcessor 扩展点并实现了 ConfigurationClassPostProcessor 类。从而实现了 Spring 之上的一系列逻辑扩展。下面是 ConfigurationClassPostProcessor 的继承关系。

![image.png](https://i.loli.net/2020/10/29/JkzcEmWxPGsTK9a.png)

## 配置类解析

书上的方法没有了，我找到最相近的的

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

这是书上的，5.0还在，5.1就改掉了 // TODO 重新打断点找到调用链
```java
private void processDeferredImportSelectors() {
		List<DeferredImportSelectorHolder> deferredImports = this.deferredImportSelectors;
		this.deferredImportSelectors = null;
		if (deferredImports == null) {
			return;
		}
		// 排个序
		deferredImports.sort(DEFERRED_IMPORT_COMPARATOR);
		Map<Object, DeferredImportSelectorGrouping> groupings = new LinkedHashMap<>();
		Map<AnnotationMetadata, ConfigurationClass> configurationClasses = new HashMap<>();
    	// 这里应该就是
		for (DeferredImportSelectorHolder deferredImport : deferredImports) {
			Class<? extends Group> group = deferredImport.getImportSelector().getImportGroup();
			DeferredImportSelectorGrouping grouping = groupings.computeIfAbsent(
					(group != null ? group : deferredImport),
					key -> new DeferredImportSelectorGrouping(createGroup(group)));
			grouping.add(deferredImport);
			configurationClasses.put(deferredImport.getConfigurationClass().getMetadata(),
					deferredImport.getConfigurationClass());
		}
		for (DeferredImportSelectorGrouping grouping : groupings.values()) {
			grouping.getImports().forEach(entry -> {
				ConfigurationClass configurationClass = configurationClasses.get(entry.getMetadata());
				try {
					processImports(configurationClass, asSourceClass(configurationClass),
							asSourceClasses(entry.getImportClassName()), false);
				}
				catch (BeanDefinitionStoreException ex) {
					throw ex;
				}
				catch (Throwable ex) {
					throw new BeanDefinitionStoreException(
							"Failed to process import candidates for configuration class [" +
							configurationClass.getMetadata().getClassName() + "]", ex);
				}
			});
		}
	}
```

Paraser 解析时序图

![Parser 解析流程.png](https://i.loli.net/2020/10/29/utXDiCo3xfbIjYm.png)

