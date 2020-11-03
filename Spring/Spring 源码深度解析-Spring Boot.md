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

+ ConfigurationClassPostProcessor 作为 Spring 扩展点是 Spring Boot 一系列功能的基础入口。
+ ConfigurationClassParser 作为解析职责的基本处理类，涵盖了各种解析处理的逻辑，如@Import、@Bean、@ImportResource、@ComponentScan 等注解都是在这个注解类中完成的，而这个类对外开放的函数入口就是 parse 方法。对应步骤 3。（Spring data jpa 就是以 Annotation // TODO 找到是哪个类 开头的 postProcessor 来进行方法转 SQL）

+ 完成步骤 3，所有解析的结果已经通过 3.2.2 步骤放在了 parse 的 configurationClasses 属性中，这是对这个属性进行统一的 spring bean 硬编码注册，注册逻辑统一委托给 ConfiurationClassBeanDefinitionReader，对外的接口是 loadBeanDefinitions，对应步骤 4
+ parse 中的处理是最复杂的，parse 中首先会处理自己本身能扫描到的 bean 注册逻辑，然后才会处理 spring.factories 定义的配置。处理 spring.factories 定义的配首先就是要加载配置类，这个时候 EnableAutoConfigurationImportSelector 提供的 selectImports 就被派上用场了，它返回的配置需要进行进一步解析，因为这些配置类肯跟对应不同的类型，如@Import、@Bean、@ImportResource、@PropertySource、@ComponentScan，而这些类型又有不同的处理逻辑。

## ComponentScan 的切入点

ConfigurationClassParser#doProcessConfigurationClass 	这个类是属于 spring-context 里面的

```java
/**
 * Apply processing and build a complete {@link ConfigurationClass} by reading the
 * annotations, members and methods from the source class. This method can be called
 * multiple times as relevant sources are discovered.
 * @param configClass the configuration class being build
 * @param sourceClass a source class
 * @return the superclass, or {@code null} if none found or previously processed
 */
@Nullable
protected final SourceClass doProcessConfigurationClass(
      ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter)
      throws IOException {

   if (configClass.getMetadata().isAnnotated(Component.class.getName())) {
      // Recursively process any member (nested) classes first
      processMemberClasses(configClass, sourceClass, filter);
   }

   // Process any @PropertySource annotations
   for (AnnotationAttributes propertySource : AnnotationConfigUtils.attributesForRepeatable(
         sourceClass.getMetadata(), PropertySources.class,
         org.springframework.context.annotation.PropertySource.class)) {
      if (this.environment instanceof ConfigurableEnvironment) {
         processPropertySource(propertySource);
      }
      else {
         logger.info("Ignoring @PropertySource annotation on [" + sourceClass.getMetadata().getClassName() +
               "]. Reason: Environment must implement ConfigurableEnvironment");
      }
   }

   // Process any @ComponentScan annotations
   // 1
   Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
         sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);
   if (!componentScans.isEmpty() &&
         !this.conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationPhase.REGISTER_BEAN)) {
      for (AnnotationAttributes componentScan : componentScans) {
         // The config class is annotated with @ComponentScan -> perform the scan immediately
         Set<BeanDefinitionHolder> scannedBeanDefinitions =
               // 2
               this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());
         // Check the set of scanned definitions for any further config classes and parse recursively if needed 
         for (BeanDefinitionHolder holder : scannedBeanDefinitions) {
            BeanDefinition bdCand = holder.getBeanDefinition().getOriginatingBeanDefinition();
            if (bdCand == null) {
               bdCand = holder.getBeanDefinition();
            }
            // // 对扫描出来的类进行过滤
            if (ConfigurationClassUtils.checkConfigurationClassCandidate(bdCand, this.metadataReaderFactory)) {
               // 委托给 parse 方法中递归处理
               // 3
               parse(bdCand.getBeanClassName(), holder.getBeanName());
            }
         }
      }
   }

   // Process any @Import annotations
   processImports(configClass, sourceClass, getImports(sourceClass), filter, true);

   // Process any @ImportResource annotations
   AnnotationAttributes importResource =
         AnnotationConfigUtils.attributesFor(sourceClass.getMetadata(), ImportResource.class);
   if (importResource != null) {
      String[] resources = importResource.getStringArray("locations");
      Class<? extends BeanDefinitionReader> readerClass = importResource.getClass("reader");
      for (String resource : resources) {
         String resolvedResource = this.environment.resolveRequiredPlaceholders(resource);
         configClass.addImportedResource(resolvedResource, readerClass);
      }
   }

   // Process individual @Bean methods
   Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass);
   for (MethodMetadata methodMetadata : beanMethods) {
      configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
   }

   // Process default methods on interfaces
   processInterfaces(configClass, sourceClass);

   // Process superclass, if any
   if (sourceClass.getMetadata().hasSuperClass()) {
      String superclass = sourceClass.getMetadata().getSuperClassName();
      if (superclass != null && !superclass.startsWith("java") &&
            !this.knownSuperclasses.containsKey(superclass)) {
         this.knownSuperclasses.put(superclass, configClass);
         // Superclass found, return its annotation metadata and recurse
         return sourceClass.getSuperClass();
      }
   }

   // No superclass -> processing is complete
   return null;
}
```

传入的 configClass 就是 spring.facotries 中定义的配置类。

解析 @ComponentScan({"me.young1lin.spring"})

 `Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
         sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);`

书上说最终到下面这步，但是我找不到，不管是 ConfigurationClassPareser#parse 方法，还是调用的上面的方法。

// TODO 找出到底是哪里调用的下面的这个方法。

```java
public Set<BeanDefinitionHolder> parse(AnnotationAttributes componentScan, final String declaringClass) {
   ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this.registry,
         componentScan.getBoolean("useDefaultFilters"), this.environment, this.resourceLoader);

   Class<? extends BeanNameGenerator> generatorClass = componentScan.getClass("nameGenerator");
   boolean useInheritedGenerator = (BeanNameGenerator.class == generatorClass);
   scanner.setBeanNameGenerator(useInheritedGenerator ? this.beanNameGenerator :
         BeanUtils.instantiateClass(generatorClass));

   ScopedProxyMode scopedProxyMode = componentScan.getEnum("scopedProxy");
   if (scopedProxyMode != ScopedProxyMode.DEFAULT) {
      scanner.setScopedProxyMode(scopedProxyMode);
   }
   else {
      Class<? extends ScopeMetadataResolver> resolverClass = componentScan.getClass("scopeResolver");
      scanner.setScopeMetadataResolver(BeanUtils.instantiateClass(resolverClass));
   }

   scanner.setResourcePattern(componentScan.getString("resourcePattern"));

   for (AnnotationAttributes filter : componentScan.getAnnotationArray("includeFilters")) {
      for (TypeFilter typeFilter : typeFiltersFor(filter)) {
         scanner.addIncludeFilter(typeFilter);
      }
   }
   for (AnnotationAttributes filter : componentScan.getAnnotationArray("excludeFilters")) {
      for (TypeFilter typeFilter : typeFiltersFor(filter)) {
         scanner.addExcludeFilter(typeFilter);
      }
   }

   boolean lazyInit = componentScan.getBoolean("lazyInit");
   if (lazyInit) {
      scanner.getBeanDefinitionDefaults().setLazyInit(true);
   }

   Set<String> basePackages = new LinkedHashSet<>();
   String[] basePackagesArray = componentScan.getStringArray("basePackages");
   for (String pkg : basePackagesArray) {
      String[] tokenized = StringUtils.tokenizeToStringArray(this.environment.resolvePlaceholders(pkg),
            ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
      Collections.addAll(basePackages, tokenized);
   }
   for (Class<?> clazz : componentScan.getClassArray("basePackageClasses")) {
      basePackages.add(ClassUtils.getPackageName(clazz));
   }
   if (basePackages.isEmpty()) {
      basePackages.add(ClassUtils.getPackageName(declaringClass));
   }

   scanner.addExcludeFilter(new AbstractTypeHierarchyTraversingFilter(false, false) {
      @Override
      protected boolean matchClassName(String className) {
         return declaringClass.equals(className);
      }
   });
   return scanner.doScan(StringUtils.toStringArray(basePackages));
}
```

里面调用了 ClassPathBeanDefinitionScanner，Spring 原生的解析类，这是 Spring 核心解析类。

之前 Mybatis 整合之中出现过这个。

# Conditional 机制实现

## Conditional 使用

```java
@Configuration
@ComponentScan({"me.young1lin.spring.core.resolve.auto"})
@ConditionalOnProperty(prefix = "resolve",name = "enable",havingValue = "true")
public class HelloServiceAutoConfiguration {
    @Bean
    public HelloService helloService(){
        return new HelloServiceImpl();
    }
}
```

# Conditional 原理

**OnPropertyCondition#getMatchOutcome**

```java
@Override
public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
   // 核心逻辑
   List<AnnotationAttributes> allAnnotationAttributes = annotationAttributesFromMultiValueMap(
         metadata.getAllAnnotationAttributes(ConditionalOnProperty.class.getName()));
   List<ConditionMessage> noMatch = new ArrayList<>();
   List<ConditionMessage> match = new ArrayList<>();
   for (AnnotationAttributes annotationAttributes : allAnnotationAttributes) {
      // 锚点 1
      ConditionOutcome outcome = determineOutcome(annotationAttributes, context.getEnvironment());
      (outcome.isMatch() ? match : noMatch).add(outcome.getConditionMessage());
   }
   if (!noMatch.isEmpty()) {
      return ConditionOutcome.noMatch(ConditionMessage.of(noMatch));
   }
   return ConditionOutcome.match(ConditionMessage.of(match));
}

private List<AnnotationAttributes> annotationAttributesFromMultiValueMap(
      MultiValueMap<String, Object> multiValueMap) {
   List<Map<String, Object>> maps = new ArrayList<>();
   multiValueMap.forEach((key, value) -> {
      for (int i = 0; i < value.size(); i++) {
         Map<String, Object> map;
         if (i < maps.size()) {
            map = maps.get(i);
         }
         else {
            map = new HashMap<>();
            maps.add(map);
         }
         map.put(key, value.get(i));
      }
   });
   List<AnnotationAttributes> annotationAttributes = new ArrayList<>(maps.size());
   for (Map<String, Object> map : maps) {
      annotationAttributes.add(AnnotationAttributes.fromMap(map));
   }
   return annotationAttributes;
}
```

**OnPropertyCondition#determineOutCome**

```java
// 锚点 1
private ConditionOutcome determineOutcome(AnnotationAttributes annotationAttributes, PropertyResolver resolver) {
   Spec spec = new Spec(annotationAttributes);
   List<String> missingProperties = new ArrayList<>();
   List<String> nonMatchingProperties = new ArrayList<>();
    // 这个对象是当前类的静态内部类。
   spec.collectProperties(resolver, missingProperties, nonMatchingProperties);   
   if (!missingProperties.isEmpty()) {
      return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnProperty.class, spec)
            .didNotFind("property", "properties").items(Style.QUOTE, missingProperties));
   }
   if (!nonMatchingProperties.isEmpty()) {
      return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnProperty.class, spec)
            .found("different value in property", "different value in properties")
            .items(Style.QUOTE, nonMatchingProperties));
   }
   return ConditionOutcome
         .match(ConditionMessage.forCondition(ConditionalOnProperty.class, spec).because("matched"));
}
```

**OnPropertyCondition.Sepc#collectProperties**

```java
// 这个函数尝试使用 PropertyResolver 来验证对应的属性是否存在，如果不存在则验证不通过。
private void collectProperties(PropertyResolver resolver, List<String> missing, List<String> nonMatching) {
   for (String name : this.names) {
      String key = this.prefix + name;
      if (resolver.containsProperty(key)) {
         if (!isMatch(resolver.getProperty(key), this.havingValue)) {
            nonMatching.add(name);
         }
      }
      else {
         if (!this.matchIfMissing) {
            missing.add(name);
         }
      }
   }
}
```

## 调用切入点

**ConfigurationClassParser#processConfigurationClass**

```java
protected void processConfigurationClass(ConfigurationClass configClass, Predicate<String> filter) throws IOException {
    // 第一行的判断就是 Conditional 逻辑生效的切入点，如果这里判断不通过，后面都不会执行。
   if (this.conditionEvaluator.shouldSkip(configClass.getMetadata(), ConfigurationPhase.PARSE_CONFIGURATION)) {
      return;
   }

   ConfigurationClass existingClass = this.configurationClasses.get(configClass);
   if (existingClass != null) {
      if (configClass.isImported()) {
         if (existingClass.isImported()) {
            existingClass.mergeImportedBy(configClass);
         }
         // Otherwise ignore new imported config class; existing non-imported class overrides it.
         return;
      }
      else {
         // Explicit bean definition found, probably replacing an import.
         // Let's remove the old one and go with the new one.
         this.configurationClasses.remove(configClass);
         this.knownSuperclasses.values().removeIf(configClass::equals);
      }
   }

   // Recursively process the configuration class and its superclass hierarchy.
   SourceClass sourceClass = asSourceClass(configClass, filter);
   do {
      sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
   }
   while (sourceClass != null);

   this.configurationClasses.put(configClass, configClass);
}
```

**ConditionEvaluator#shouldSkip**

```java
/**
 * Determine if an item should be skipped based on {@code @Conditional} annotations.
 * @param metadata the meta data
 * @param phase the phase of the call
 * @return if the item should be skipped
 */
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
    // 获取评估的类
   for (String[] conditionClasses : getConditionClasses(metadata)) {
      for (String conditionClass : conditionClasses) {
         Condition condition = getCondition(conditionClass, this.context.getClassLoader());
         conditions.add(condition);
      }
   }

   AnnotationAwareOrderComparator.sort(conditions);

   for (Condition condition : conditions) {
      ConfigurationPhase requiredPhase = null;
      if (condition instanceof ConfigurationCondition) {
         requiredPhase = ((ConfigurationCondition) condition).getConfigurationPhase();
      }
       // 之前这里是两个 if 现在是一个 if
      if ((requiredPhase == null || requiredPhase == phase) && !condition.matches(this.context, metadata)) {
         return true;
      }
   }

   return false;
}
```

# 属性自动化配置实现

@Value 如果塞入属性的

**DefaultListableBeanFactory#doResolveDependency**

```java
@Nullable
public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName,
      @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {

   InjectionPoint previousInjectionPoint = ConstructorResolver.setCurrentInjectionPoint(descriptor);
   try {
      Object shortcut = descriptor.resolveShortcut(this);
      if (shortcut != null) {
         return shortcut;
      }

      Class<?> type = descriptor.getDependencyType();
      Object value = getAutowireCandidateResolver().getSuggestedValue(descriptor);
      if (value != null) {
         if (value instanceof String) {
             // 这里解析 @Value 
            String strVal = resolveEmbeddedValue((String) value);
            BeanDefinition bd = (beanName != null && containsBean(beanName) ?
                  getMergedBeanDefinition(beanName) : null);
            value = evaluateBeanDefinitionString(strVal, bd);
         }
         TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());
         try {
            return converter.convertIfNecessary(value, type, descriptor.getTypeDescriptor());
         }
         catch (UnsupportedOperationException ex) {
            // A custom TypeConverter which does not support TypeDescriptor resolution...
            return (descriptor.getField() != null ?
                  converter.convertIfNecessary(value, type, descriptor.getField()) :
                  converter.convertIfNecessary(value, type, descriptor.getMethodParameter()));
         }
      }

      Object multipleBeans = resolveMultipleBeans(descriptor, beanName, autowiredBeanNames, typeConverter);
      if (multipleBeans != null) {
         return multipleBeans;
      }

      Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
      if (matchingBeans.isEmpty()) {
         if (isRequired(descriptor)) {
            raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
         }
         return null;
      }

      String autowiredBeanName;
      Object instanceCandidate;

      if (matchingBeans.size() > 1) {
         autowiredBeanName = determineAutowireCandidate(matchingBeans, descriptor);
         if (autowiredBeanName == null) {
            if (isRequired(descriptor) || !indicatesMultipleBeans(type)) {
               return descriptor.resolveNotUnique(descriptor.getResolvableType(), matchingBeans);
            }
            else {
               // In case of an optional Collection/Map, silently ignore a non-unique case:
               // possibly it was meant to be an empty collection of multiple regular beans
               // (before 4.3 in particular when we didn't even look for collection beans).
               return null;
            }
         }
         instanceCandidate = matchingBeans.get(autowiredBeanName);
      }
      else {
         // We have exactly one match.
         Map.Entry<String, Object> entry = matchingBeans.entrySet().iterator().next();
         autowiredBeanName = entry.getKey();
         instanceCandidate = entry.getValue();
      }

      if (autowiredBeanNames != null) {
         autowiredBeanNames.add(autowiredBeanName);
      }
      if (instanceCandidate instanceof Class) {
         instanceCandidate = descriptor.resolveCandidate(autowiredBeanName, type, this);
      }
      Object result = instanceCandidate;
      if (result instanceof NullBean) {
         if (isRequired(descriptor)) {
            raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
         }
         result = null;
      }
      if (!ClassUtils.isAssignableValue(type, result)) {
         throw new BeanNotOfRequiredTypeException(autowiredBeanName, type, instanceCandidate.getClass());
      }
      return result;
   }
   finally {
      ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
   }
}
```

**AbstractBeanFactory#resolveEmbeddedValue**

```java
public String resolveEmbeddedValue(@Nullable String value) {
   if (value == null) {
      return null;
   }
   String result = value;
   for (StringValueResolver resolver : this.embeddedValueResolvers) {
      result = resolver.resolveStringValue(result);
      if (result == null) {
         return null;
      }
   }
   return result;
}
```

**StringValueResolver** 是个接口，并且标注了被用作 Lambda 表达式的注解 @FunctionalInterface

**StringValueResolver** 功能实现依赖 Spring 的切入点是 **PropertySourcesPlaceholderConfigurer**。

下面是 **PropertySourcesPlaceholderConfigurer**的类层次结构

![PropertySourcesPlaceholderConfigurer类层次结构图.png](https://i.loli.net/2020/11/03/OvJyjpXVPl68cWH.png)

先从 postProcessBeanFactory 方法

```java
/**
 * Processing occurs by replacing ${...} placeholders in bean definitions by resolving each
 * against this configurer's set of {@link PropertySources}, which includes:
 * <ul>
 * <li>all {@linkplain org.springframework.core.env.ConfigurableEnvironment#getPropertySources
 * environment property sources}, if an {@code Environment} {@linkplain #setEnvironment is present}
 * <li>{@linkplain #mergeProperties merged local properties}, if {@linkplain #setLocation any}
 * {@linkplain #setLocations have} {@linkplain #setProperties been}
 * {@linkplain #setPropertiesArray specified}
 * <li>any property sources set by calling {@link #setPropertySources}
 * </ul>
 * <p>If {@link #setPropertySources} is called, <strong>environment and local properties will be
 * ignored</strong>. This method is designed to give the user fine-grained control over property
 * sources, and once set, the configurer makes no assumptions about adding additional sources.
 */
@Override
public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
   if (this.propertySources == null) {
       // 这个 MutablePropertySources 是一个配置的类，它是 PropertySource 的默认实现。内部有一个默认的 CopyOnWriteArrayList 容器 
      this.propertySources = new MutablePropertySources();
      if (this.environment != null) {
         this.propertySources.addLast(
            new PropertySource<Environment>(ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME, this.environment) {
               @Override
               @Nullable
               public String getProperty(String key) {
                  return this.source.getProperty(key);
               }
            }
         );
      }
      try {
         PropertySource<?> localPropertySource =
               new PropertiesPropertySource(LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME, mergeProperties());
         if (this.localOverride) {
            this.propertySources.addFirst(localPropertySource);
         }
         else {
            this.propertySources.addLast(localPropertySource);
         }
      }
      catch (IOException ex) {
         throw new BeanInitializationException("Could not load properties", ex);
      }
   }

   processProperties(beanFactory, new PropertySourcesPropertyResolver(this.propertySources));
   this.appliedPropertySources = this.propertySources;
}
```

StringValueResolver 初始化过程

![BsSMTS.png](https://s1.ax1x.com/2020/11/03/BsSMTS.png)

## Environment 初始化过程

