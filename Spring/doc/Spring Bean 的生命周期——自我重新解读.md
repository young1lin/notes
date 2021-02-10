# 总览

1. Spring Bean 的元信息配置。

2. Spring Bean 的元信息解析。
3. Spring Bean 注册。
4. Spring BeanDefinition 合并（从 GenericBeanDefinition 合并成 RootBeanDefinition 方便后面操作）。
5. Spring Bean Class 加载。
6. Spring Bean 实例化前（实现了 SmartInstantiationAwareBeanPostProcessor 接口在 AbstractAutowireCapableBeanFactory#createBean -> doCreateBean -> createBeanInstance -> determineConstructorsFromBeanPostProcessors 这里检查相关实现的类）。

7. Spring Bean 实例化。
8. Spring Bean 实例化后。
9. Spring Bean 属性填充之前。（AbstractAutowireCapableBeanFactory#populateBean  592 行代码）
10. Spring Bean Aware 接口回调。（AbstractAutowireCapableBeanFactory#initializeBean -> invokeAwareMethods）
11. Spring Bean 初始化阶段。(AbstractAutowireCapableBeanFactory#invokeInitMethods)
12. Spring Bean 初始化阶段。
13. Spring Bean 初始化后阶段。
14. Spring Bean 初始化完成阶段。
15. Spring Bean 销毁前阶段。
16. Spring Bean 销毁阶段。
17. Spring Bean 垃圾收集。

# Spring Bean 元信息配置阶段

## 基于 XML、Properties 配置

XMLBeanDefinitionReader#loadBeanDefinitions(String resourceUrl);

BeanDefinitionReader 的实现类

AbstractBeanDefnitionReader 及其子类关系如下，分别对应着 XML、Properties、Groovy 文件读取方式。不过现在都不推荐用这几个。

![image.png](https://i.loli.net/2020/12/02/ubN4vi5DnSLFX7U.png)

## 基于注解配置（推荐）

配置了 @Compment、@Bean 等注解来实现，@Service 和 @Repository 其实是为了配合 DDD 思想，@Compment 的衍生注解而已，功能一样。

##  基于Spring API 配置

DefaultListableBeanFactory 实现了 BeanRegistry 接口，其中 registerBeanDefnition 方法就是在这配置。

```java
public interface BeanDefinitionRegistry extends AliasRegistry {

   /**
    * Register a new bean definition with this registry.
    * Must support RootBeanDefinition and ChildBeanDefinition.
    * @param beanName the name of the bean instance to register
    * @param beanDefinition definition of the bean instance to register
    * @throws BeanDefinitionStoreException if the BeanDefinition is invalid
    * @throws BeanDefinitionOverrideException if there is already a BeanDefinition
    * for the specified bean name and we are not allowed to override it
    * @see GenericBeanDefinition
    * @see RootBeanDefinition
    * @see ChildBeanDefinition
    */
   void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
         throws BeanDefinitionStoreException;
}
```

# Spring Bean 元信息解析阶段

## 面向资源 BeanDefinition 解析

### BeanDefinitionReader

### XML 解析器 - BeanDefnitionParser

## 面向注解 BeanDefinition 解析

### AnnotatedBeanDefinitionReader

+ 资源
  + 类对象 - java.lang.Class
+ 底层
  + 条件评估 - ConditionEvaluator
  + Bean 作用域范围解析 - ScopeMetadataResolver
  + BeanDefnition 解析 - 内部 API 实现
  + BeanDefinition 处理 - AnnotationConfigUtils#processCommonDefinitionAnnotations
  + BeanDefinition 注册 - BeanDefinitionRegistry

下面是 AnnotatedBeanDefinitionReader 类，是从 3.0 开始支持的。并且就是个单独的类。下面就有对 Primary 和 Lazy 的解析，是通过组合的方式，AnnotationConfigApplicationContext 内含此 Reader 来进行解析注解。

在 Spring Boot 中就是通过 createApplicationContext 中来创建 AnnotationConfigServletWebServerApplicationContext 其中默认的构造器中就有下面这个 Reader。

```java
/**
 * Convenient adapter for programmatic registration of bean classes.
 *
 * <p>This is an alternative to {@link ClassPathBeanDefinitionScanner}, applying
 * the same resolution of annotations but for explicitly registered classes only.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 * @author Phillip Webb
 * @since 3.0
 * @see AnnotationConfigApplicationContext#register
 */
public class AnnotatedBeanDefinitionReader {
	// 这个的实现有 DefaultListableBeanFactory,AnnotationConfigServletWebApplicationContext等等
  // 这也是个策略模式，通过构造器注入，使用不同 BeanDefinitionRegistry 来注册。
   private final BeanDefinitionRegistry registry;
    // 这里一般是 AnnotationBeanNameGenerator 以类名首字母小写生成 beanName
   private BeanNameGenerator beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;
	/** 解析元信息相关的数据，这是个接口，里面就一个方法，方法返回值是ScopeMetadata ，而ScopeMetadata包括 proxy 的一些信息 public enum ScopProxyMode {DEFAULT,NO,INTERFACE,TAGET_CLASS} */
   private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
	 // 这是做评估的 具体在它的 shouldSkip 方法是不是该跳过当前 Bean 注册与否 
   private ConditionEvaluator conditionEvaluator;


   /**
    * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry.
    * <p>If the registry is {@link EnvironmentCapable}, e.g. is an {@code ApplicationContext},
    * the {@link Environment} will be inherited, otherwise a new
    * {@link StandardEnvironment} will be created and used.
    * @param registry the {@code BeanFactory} to load bean definitions into,
    * in the form of a {@code BeanDefinitionRegistry}
    * @see #AnnotatedBeanDefinitionReader(BeanDefinitionRegistry, Environment)
    * @see #setEnvironment(Environment)
    */
   public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
      this(registry, getOrCreateEnvironment(registry));
   }

   /**
    * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry,
    * using the given {@link Environment}.
    * @param registry the {@code BeanFactory} to load bean definitions into,
    * in the form of a {@code BeanDefinitionRegistry}
    * @param environment the {@code Environment} to use when evaluating bean definition
    * profiles.
    * @since 3.1
    */
   public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
      Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
      Assert.notNull(environment, "Environment must not be null");
      this.registry = registry;
      this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
      AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
   }


   /**
    * Get the BeanDefinitionRegistry that this reader operates on.
    */
   public final BeanDefinitionRegistry getRegistry() {
      return this.registry;
   }

   /**
    * Set the {@code Environment} to use when evaluating whether
    * {@link Conditional @Conditional}-annotated component classes should be registered.
    * <p>The default is a {@link StandardEnvironment}.
    * @see #registerBean(Class, String, Class...)
    */
   public void setEnvironment(Environment environment) {
      this.conditionEvaluator = new ConditionEvaluator(this.registry, environment, null);
   }

   /**
    * Set the {@code BeanNameGenerator} to use for detected bean classes.
    * <p>The default is a {@link AnnotationBeanNameGenerator}.
    */
   public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
      this.beanNameGenerator =
            (beanNameGenerator != null ? beanNameGenerator : AnnotationBeanNameGenerator.INSTANCE);
   }

   /**
    * Set the {@code ScopeMetadataResolver} to use for registered component classes.
    * <p>The default is an {@link AnnotationScopeMetadataResolver}.
    */
   public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
      this.scopeMetadataResolver =
            (scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
   }


   /**
    * Register one or more component classes to be processed.
    * <p>Calls to {@code register} are idempotent; adding the same
    * component class more than once has no additional effect.
    * @param componentClasses one or more component classes,
    * e.g. {@link Configuration @Configuration} classes
    */
   public void register(Class<?>... componentClasses) {
      for (Class<?> componentClass : componentClasses) {
         registerBean(componentClass);
      }
   }

   /**
    * Register a bean from the given bean class, deriving its metadata from
    * class-declared annotations.
    * @param beanClass the class of the bean
    */
   public void registerBean(Class<?> beanClass) {
      doRegisterBean(beanClass, null, null, null, null);
   }

   /**
    * Register a bean from the given bean class, deriving its metadata from
    * class-declared annotations.
    * @param beanClass the class of the bean
    * @param name an explicit name for the bean
    * (or {@code null} for generating a default bean name)
    * @since 5.2
    */
   public void registerBean(Class<?> beanClass, @Nullable String name) {
      doRegisterBean(beanClass, name, null, null, null);
   }

   /**
    * Register a bean from the given bean class, deriving its metadata from
    * class-declared annotations.
    * @param beanClass the class of the bean
    * @param qualifiers specific qualifier annotations to consider,
    * in addition to qualifiers at the bean class level
    */
   @SuppressWarnings("unchecked")
   public void registerBean(Class<?> beanClass, Class<? extends Annotation>... qualifiers) {
      doRegisterBean(beanClass, null, qualifiers, null, null);
   }

   /**
    * Register a bean from the given bean class, deriving its metadata from
    * class-declared annotations.
    * @param beanClass the class of the bean
    * @param name an explicit name for the bean
    * (or {@code null} for generating a default bean name)
    * @param qualifiers specific qualifier annotations to consider,
    * in addition to qualifiers at the bean class level
    */
   @SuppressWarnings("unchecked")
   public void registerBean(Class<?> beanClass, @Nullable String name,
         Class<? extends Annotation>... qualifiers) {

      doRegisterBean(beanClass, name, qualifiers, null, null);
   }

   /**
    * Register a bean from the given bean class, deriving its metadata from
    * class-declared annotations, using the given supplier for obtaining a new
    * instance (possibly declared as a lambda expression or method reference).
    * @param beanClass the class of the bean
    * @param supplier a callback for creating an instance of the bean
    * (may be {@code null})
    * @since 5.0
    */
   public <T> void registerBean(Class<T> beanClass, @Nullable Supplier<T> supplier) {
      doRegisterBean(beanClass, null, null, supplier, null);
   }

   /**
    * Register a bean from the given bean class, deriving its metadata from
    * class-declared annotations, using the given supplier for obtaining a new
    * instance (possibly declared as a lambda expression or method reference).
    * @param beanClass the class of the bean
    * @param name an explicit name for the bean
    * (or {@code null} for generating a default bean name)
    * @param supplier a callback for creating an instance of the bean
    * (may be {@code null})
    * @since 5.0
    */
   public <T> void registerBean(Class<T> beanClass, @Nullable String name, @Nullable Supplier<T> supplier) {
      doRegisterBean(beanClass, name, null, supplier, null);
   }

   /**
    * Register a bean from the given bean class, deriving its metadata from
    * class-declared annotations.
    * @param beanClass the class of the bean
    * @param name an explicit name for the bean
    * (or {@code null} for generating a default bean name)
    * @param supplier a callback for creating an instance of the bean
    * (may be {@code null})
    * @param customizers one or more callbacks for customizing the factory's
    * {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
    * @since 5.2
    */
   public <T> void registerBean(Class<T> beanClass, @Nullable String name, @Nullable Supplier<T> supplier,
         BeanDefinitionCustomizer... customizers) {

      doRegisterBean(beanClass, name, null, supplier, customizers);
   }

   /**
    * Register a bean from the given bean class, deriving its metadata from
    * class-declared annotations.
    * @param beanClass the class of the bean
    * @param name an explicit name for the bean
    * @param qualifiers specific qualifier annotations to consider, if any,
    * in addition to qualifiers at the bean class level
    * @param supplier a callback for creating an instance of the bean
    * (may be {@code null})
    * @param customizers one or more callbacks for customizing the factory's
    * {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
    * @since 5.0
    */
   private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
         @Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
         @Nullable BeanDefinitionCustomizer[] customizers) {

      AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
      if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
         return;
      }

      abd.setInstanceSupplier(supplier);
      ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
      abd.setScope(scopeMetadata.getScopeName());
      String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));

      AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
      if (qualifiers != null) {
         for (Class<? extends Annotation> qualifier : qualifiers) {
            if (Primary.class == qualifier) {
               abd.setPrimary(true);
            }
            else if (Lazy.class == qualifier) {
               abd.setLazyInit(true);
            }
            else {
               abd.addQualifier(new AutowireCandidateQualifier(qualifier));
            }
         }
      }
      if (customizers != null) {
         for (BeanDefinitionCustomizer customizer : customizers) {
            customizer.customize(abd);
         }
      }

      BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
      definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
      BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
   }


   /**
    * Get the Environment from the given registry if possible, otherwise return a new
    * StandardEnvironment.
    */
   private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
      Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
      if (registry instanceof EnvironmentCapable) {
         return ((EnvironmentCapable) registry).getEnvironment();
      }
      return new StandardEnvironment();
   }

}
```

## Spring Boot createApplicationContext

AnnotationConfigServletWebServerApplicationContext

```java
public class AnnotationConfigServletWebServerApplicationContext extends ServletWebServerApplicationContext
      implements AnnotationConfigRegistry {
	// 内含了这个，但是其实没什么用，用的一般是 scanner
   private final AnnotatedBeanDefinitionReader reader;

   private final ClassPathBeanDefinitionScanner scanner;

   private final Set<Class<?>> annotatedClasses = new LinkedHashSet<>();

   private String[] basePackages;
}
```

## Java API 方式配置

一定有 BeanDefinition 配置了。

# Spring Bean 注册阶段





```java
/**
 * Return an instance, which may be shared or independent, of the specified bean.
 * @param name the name of the bean to retrieve
 * @param requiredType the required type of the bean to retrieve
 * @param args arguments to use when creating a bean instance using explicit arguments
 * (only applied when creating a new instance as opposed to retrieving an existing one)
 * @param typeCheckOnly whether the instance is obtained for a type check,
 * not for actual use
 * @return an instance of the bean
 * @throws BeansException if the bean could not be created
 */
@SuppressWarnings("unchecked")
protected <T> T doGetBean(final String name, @Nullable final Class<T> requiredType,
      @Nullable final Object[] args, boolean typeCheckOnly) throws BeansException {

   final String beanName = transformedBeanName(name);
   Object bean;

   // Eagerly check singleton cache for manually registered singletons.
   Object sharedInstance = getSingleton(beanName);
   if (sharedInstance != null && args == null) {
      if (logger.isTraceEnabled()) {
         if (isSingletonCurrentlyInCreation(beanName)) {
            logger.trace("Returning eagerly cached instance of singleton bean '" + beanName +
                  "' that is not fully initialized yet - a consequence of a circular reference");
         }
         else {
            logger.trace("Returning cached instance of singleton bean '" + beanName + "'");
         }
      }
      bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
   }

   else {
      // Fail if we're already creating this bean instance:
      // We're assumably within a circular reference.
      if (isPrototypeCurrentlyInCreation(beanName)) {
         throw new BeanCurrentlyInCreationException(beanName);
      }

      // Check if bean definition exists in this factory.
      BeanFactory parentBeanFactory = getParentBeanFactory();
      if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
         // Not found -> check parent.
         String nameToLookup = originalBeanName(name);
         if (parentBeanFactory instanceof AbstractBeanFactory) {
            return ((AbstractBeanFactory) parentBeanFactory).doGetBean(
                  nameToLookup, requiredType, args, typeCheckOnly);
         }
         else if (args != null) {
            // Delegation to parent with explicit args.
            return (T) parentBeanFactory.getBean(nameToLookup, args);
         }
         else if (requiredType != null) {
            // No args -> delegate to standard getBean method.
            return parentBeanFactory.getBean(nameToLookup, requiredType);
         }
         else {
            return (T) parentBeanFactory.getBean(nameToLookup);
         }
      }

      if (!typeCheckOnly) {
         markBeanAsCreated(beanName);
      }

      try {
         final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
         checkMergedBeanDefinition(mbd, beanName, args);

         // Guarantee initialization of beans that the current bean depends on.
         String[] dependsOn = mbd.getDependsOn();
         if (dependsOn != null) {
            for (String dep : dependsOn) {
               if (isDependent(beanName, dep)) {
                  throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                        "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
               }
               registerDependentBean(dep, beanName);
               try {
                  getBean(dep);
               }
               catch (NoSuchBeanDefinitionException ex) {
                  throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                        "'" + beanName + "' depends on missing bean '" + dep + "'", ex);
               }
            }
         }

         // Create bean instance.
         if (mbd.isSingleton()) {
            sharedInstance = getSingleton(beanName, () -> {
               try {
                   // 这里调用父类的 createBean 方法
                  return createBean(beanName, mbd, args);
               }
               catch (BeansException ex) {
                  // Explicitly remove instance from singleton cache: It might have been put there
                  // eagerly by the creation process, to allow for circular reference resolution.
                  // Also remove any beans that received a temporary reference to the bean.
                  destroySingleton(beanName);
                  throw ex;
               }
            });
            bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
         }

         else if (mbd.isPrototype()) {
            // It's a prototype -> create a new instance.
            Object prototypeInstance = null;
            try {
               beforePrototypeCreation(beanName);
               prototypeInstance = createBean(beanName, mbd, args);
            }
            finally {
               afterPrototypeCreation(beanName);
            }
            bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
         }

         else {
            String scopeName = mbd.getScope();
            final Scope scope = this.scopes.get(scopeName);
            if (scope == null) {
               throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
            }
            try {
               Object scopedInstance = scope.get(beanName, () -> {
                  beforePrototypeCreation(beanName);
                  try {
                     return createBean(beanName, mbd, args);
                  }
                  finally {
                     afterPrototypeCreation(beanName);
                  }
               });
               bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
            }
            catch (IllegalStateException ex) {
               throw new BeanCreationException(beanName,
                     "Scope '" + scopeName + "' is not active for the current thread; consider " +
                     "defining a scoped proxy for this bean if you intend to refer to it from a singleton",
                     ex);
            }
         }
      }
      catch (BeansException ex) {
         cleanupAfterBeanCreationFailure(beanName);
         throw ex;
      }
   }

   // Check if required type matches the type of the actual bean instance.
   if (requiredType != null && !requiredType.isInstance(bean)) {
      try {
         T convertedBean = getTypeConverter().convertIfNecessary(bean, requiredType);
         if (convertedBean == null) {
            throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
         }
         return convertedBean;
      }
      catch (TypeMismatchException ex) {
         if (logger.isTraceEnabled()) {
            logger.trace("Failed to convert bean '" + name + "' to required type '" +
                  ClassUtils.getQualifiedName(requiredType) + "'", ex);
         }
         throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
      }
   }
   return (T) bean;
}
```

# 缓存中获取单例 Bean

```java
/** Cache of singleton objects: bean name to bean instance. */
/** 缓存单例对象，beanName --> bean instance. */
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

/** Cache of singleton factories: bean name to ObjectFactory. */
/** 缓存 beanName --> ObjectFactory. */
private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

/** Cache of early singleton objects: bean name to bean instance. */
/** 缓存早一步暴露的单例对象，解决循环引用的问题，当然，如果这个 bean 没有默认的构造方法，也是解决不了循环引用的问题 */
private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

/**
 * Return the (raw) singleton object registered under the given name.
 * <p>Checks already instantiated singletons and also allows for an early
 * reference to a currently created singleton (resolving a circular reference).
 * @param beanName the name of the bean to look for
 * @param allowEarlyReference whether early references should be created or not
 * @return the registered singleton object, or {@code null} if none found
 */
@Nullable
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
   Object singletonObject = this.singletonObjects.get(beanName);
    // 如果这里为空（没有得到缓存），并且创建的时候是单例对象
   if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
       // 这里就锁住单例对象名称及实例映射
      synchronized (this.singletonObjects) {
          // 尝试在提前暴露的 SingletonObjects 获取
          // 提前暴露的在 addSingletonFactory 方法中，详情见下面
         singletonObject = this.earlySingletonObjects.get(beanName);
          // 如果为空，并且允许提前初始化（就是有默认构造方法且是单例模式）
         if (singletonObject == null && allowEarlyReference) {
             // 从 beanName -> BeanFactory 映射中获取 BeanFactory
            ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
             // 如果有这个 BeanFactory 才开始 get 这个 Bean 实例
            if (singletonFactory != null) {
               singletonObject = singletonFactory.getObject();
                // 提前暴露的对象 名称 -> 实例 的映射中放入
               this.earlySingletonObjects.put(beanName, singletonObject);
                // 移除这个 BeanFactory
               this.singletonFactories.remove(beanName);
            }
         }
      }
   }
   return singletonObject;
}
```

## 提前创建 singletonBean

```java
addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
/**
 * Obtain a reference for early access to the specified bean,
 * typically for the purpose of resolving a circular reference.
 * @param beanName the name of the bean (for error handling purposes)
 * @param mbd the merged bean definition for the bean
 * @param bean the raw bean instance
 * @return the object to expose as bean reference
 * AOP 动态织入 Advice Bean 就是从这里开始的，若没有则直接返回 bean，不做任何处理
 */
protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
    Object exposedObject = bean;
    if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
        for (BeanPostProcessor bp : getBeanPostProcessors()) {
            if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                // 是的，在这里就开始了，SmartInstatiationAwareBeanPostProcessor 
                SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
                exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
            }
        }
    }
    return exposedObject;
}

/**
 * Add the given singleton factory for building the specified singleton
 * if necessary.
 * <p>To be called for eager registration of singletons, e.g. to be able to
 * resolve circular references.
 * @param beanName the name of the bean
 * @param singletonFactory the factory for the singleton object
 */
protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
   Assert.notNull(singletonFactory, "Singleton factory must not be null");
   synchronized (this.singletonObjects) {
      if (!this.singletonObjects.containsKey(beanName)) {
         this.singletonFactories.put(beanName, singletonFactory);
         this.earlySingletonObjects.remove(beanName);
         this.registeredSingletons.add(beanName);
      }
   }
}
```

# CreateBean 之前的后处理器应用

## 准备创建 bean

**AbstractAutowireCapableBeanFactory#createBean**

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
      throws BeanCreationException {

   if (logger.isTraceEnabled()) {
      logger.trace("Creating instance of bean '" + beanName + "'");
   }
   RootBeanDefinition mbdToUse = mbd;

   // Make sure bean class is actually resolved at this point, and
   // clone the bean definition in case of a dynamically resolved Class
   // which cannot be stored in the shared merged bean definition.
   Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
   if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
      mbdToUse = new RootBeanDefinition(mbd);
      mbdToUse.setBeanClass(resolvedClass);
   }

   // Prepare method overrides.
   try {
      mbdToUse.prepareMethodOverrides();
   }
   catch (BeanDefinitionValidationException ex) {
      throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(),
            beanName, "Validation of method overrides failed", ex);
   }

   try {
      // Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
      Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
      if (bean != null) {
         return bean;
      }
   }
   catch (Throwable ex) {
      throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName,
            "BeanPostProcessor before instantiation of bean failed", ex);
   }

   try {
      Object beanInstance = doCreateBean(beanName, mbdToUse, args);
      if (logger.isTraceEnabled()) {
         logger.trace("Finished creating instance of bean '" + beanName + "'");
      }
      return beanInstance;
   }
   catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
      // A previously detected exception with proper bean creation context already,
      // or illegal singleton state to be communicated up to DefaultSingletonBeanRegistry.
      throw ex;
   }
   catch (Throwable ex) {
      throw new BeanCreationException(
            mbdToUse.getResourceDescription(), beanName, "Unexpected exception during bean creation", ex);
   }
}
```

## 实例化前解析

**AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation**

```java
/**
 * Apply before-instantiation post-processors, resolving whether there is a
 * before-instantiation shortcut for the specified bean.
 * @param beanName the name of the bean
 * @param mbd the bean definition for the bean
 * @return the shortcut-determined bean instance, or {@code null} if none
 */
@Nullable
protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
   Object bean = null;
   if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
      // Make sure bean class is actually resolved at this point.
      if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
         Class<?> targetType = determineTargetType(beanName, mbd);
         if (targetType != null) {
             // 锚点 1
            bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
            if (bean != null) {
                // 锚点 2
               bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
            }
         }
      }
      mbd.beforeInstantiationResolved = (bean != null);
   }
   return bean;
}
```

**AbstracrtAutowireCapableBeanFactory#determineTargetType** 确定目标类型

```java
/**
 * Determine the target type for the given bean definition.
 * @param beanName the name of the bean (for error handling purposes)
 * @param mbd the merged bean definition for the bean
 * @param typesToMatch the types to match in case of internal type matching purposes
 * (also signals that the returned {@code Class} will never be exposed to application code)
 * @return the type for the bean if determinable, or {@code null} otherwise
 */
@Nullable
protected Class<?> determineTargetType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
   Class<?> targetType = mbd.getTargetType();
    // 如果目标类型为空，则对他惊醒操作
   if (targetType == null) {
      targetType = (mbd.getFactoryMethodName() != null ?
            getTypeForFactoryMethod(beanName, mbd, typesToMatch) :
            resolveBeanClass(mbd, beanName, typesToMatch));
      if (ObjectUtils.isEmpty(typesToMatch) || getTempClassLoader() == null) {
         mbd.resolvedTargetType = targetType;
      }
   }
   return targetType;
}
```

## 实例化前的后处理器应用

在这一步可以对 Bean 进行“救赎”，将 Bean 替换成你想替换的任何 Bean，可以动态代理生成，也可以通过其他方式生成。

**AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation**

```java
// 锚点 1
/**
 * Apply InstantiationAwareBeanPostProcessors to the specified bean definition
 * (by class and name), invoking their {@code postProcessBeforeInstantiation} methods.
 * <p>Any returned object will be used as the bean instead of actually instantiating
 * the target bean. A {@code null} return value from the post-processor will
 * result in the target bean being instantiated.
 * @param beanClass the class of the bean to be instantiated
 * @param beanName the name of the bean
 * @return the bean object to use instead of a default instance of the target bean, or {@code null}
 * @see InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation
 */
@Nullable
protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
   for (BeanPostProcessor bp : getBeanPostProcessors()) {
      if (bp instanceof InstantiationAwareBeanPostProcessor) {
         InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
         Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
         if (result != null) {
            return result;
         }
      }
   }
   return null;
}
```

如果这里不为空，就会到这里，因为 createBean 的后面的操作这个 Bean 都不参与了（详情见下面），所以这里需要进行最后的后置处理。

**AbstractAutowireCapableBeanFactory#createBean** 

```java
Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
if (bean != null) {
   return bean;
}
```

**AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization**

```java
@Override
public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
      throws BeansException {

   Object result = existingBean;
    // 来对实例化后的 Bean 进行各个后处理。
   for (BeanPostProcessor processor : getBeanPostProcessors()) {
      Object current = processor.postProcessAfterInitialization(result, beanName);
      if (current == null) {
         return result;
      }
      result = current;
   }
   return result;
}
```

## Bean 的实例化

Bean 的实例化过程。（在实例化前 resolveBeforeInstantiation 的之后）

也就是 doCreateBean。

1. 如果是单例则需要首先清除缓存。
2. 实例化 Bean，将 BeanDefinition 转换为 BeanWrapper。将 Bean 转换成 BeanWrapper 是一个复杂的过程。

+ 如果存在工厂方法则使用工厂方法进行初始化。
+ 一个类有多个构造函数，每个构造函数都有不同的参数，所以需要根据参数锁定构造函数并进行初始化。
+ 如果既不存在工厂方法也不存在带有参数的构造函数，则使用默认的构造函数进行 bean 的实例化。

3. MergedBeanDefinitionPostProcessor 的应用。

Bean 合并后的处理，@Autowired 注解正是通过此方法实现注入类型的预解析。

4. 依赖处理。（处理循环依赖）
5. 属性填充。
6. 循环依赖检查。（prototype 的 Bean 如果有循环依赖，直接抛出异常，两个都是有参数构造的，没有提供默认的无参构造函数，一样抛出异常。）
7. 注册 DisposableBean。（如果配置了 destory-method，这里需要注册一边与在销毁时调用。注：prototype 作用域的 Bean 不会调用 destory-method。// TODO 代码解释）。
8. 完成创建并返回。

```java
/**
 * Actually create the specified bean. Pre-creation processing has already happened
 * at this point, e.g. checking {@code postProcessBeforeInstantiation} callbacks.
 * <p>Differentiates between default bean instantiation, use of a
 * factory method, and autowiring a constructor.
 * @param beanName the name of the bean
 * @param mbd the merged bean definition for the bean
 * @param args explicit arguments to use for constructor or factory method invocation
 * @return a new instance of the bean
 * @throws BeanCreationException if the bean could not be created
 * @see #instantiateBean
 * @see #instantiateUsingFactoryMethod
 * @see #autowireConstructor
 */
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
      throws BeanCreationException {

   // Instantiate the bean.
   BeanWrapper instanceWrapper = null;
   if (mbd.isSingleton()) {
      instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
   }
   if (instanceWrapper == null) {
      instanceWrapper = createBeanInstance(beanName, mbd, args);
   }
   Object bean = instanceWrapper.getWrappedInstance();
   Class<?> beanType = instanceWrapper.getWrappedClass();
   if (beanType != NullBean.class) {
      mbd.resolvedTargetType = beanType;
   }

   // Allow post-processors to modify the merged bean definition.
   synchronized (mbd.postProcessingLock) {
      if (!mbd.postProcessed) {
         try {
            applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
         }
         catch (Throwable ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                  "Post-processing of merged bean definition failed", ex);
         }
         mbd.postProcessed = true;
      }
   }

   // Eagerly cache singletons to be able to resolve circular references
   // even when triggered by lifecycle interfaces like BeanFactoryAware.
   boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
         isSingletonCurrentlyInCreation(beanName));
   if (earlySingletonExposure) {
      if (logger.isTraceEnabled()) {
         logger.trace("Eagerly caching bean '" + beanName +
               "' to allow for resolving potential circular references");
      }
      addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
   }

   // Initialize the bean instance.
   Object exposedObject = bean;
   try {
      populateBean(beanName, mbd, instanceWrapper);
      exposedObject = initializeBean(beanName, exposedObject, mbd);
   }
   catch (Throwable ex) {
      if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
         throw (BeanCreationException) ex;
      }
      else {
         throw new BeanCreationException(
               mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
      }
   }

   if (earlySingletonExposure) {
      Object earlySingletonReference = getSingleton(beanName, false);
      if (earlySingletonReference != null) {
         if (exposedObject == bean) {
            exposedObject = earlySingletonReference;
         }
         else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
            String[] dependentBeans = getDependentBeans(beanName);
            Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
            for (String dependentBean : dependentBeans) {
               if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
                  actualDependentBeans.add(dependentBean);
               }
            }
            if (!actualDependentBeans.isEmpty()) {
               throw new BeanCurrentlyInCreationException(beanName,
                     "Bean with name '" + beanName + "' has been injected into other beans [" +
                     StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
                     "] in its raw version as part of a circular reference, but has eventually been " +
                     "wrapped. This means that said other beans do not use the final version of the " +
                     "bean. This is often the result of over-eager type matching - consider using " +
                     "'getBeanNamesForType' with the 'allowEagerInit' flag turned off, for example.");
            }
         }
      }
   }

   // Register bean as disposable.
   try {
      registerDisposableBeanIfNecessary(beanName, bean, mbd);
   }
   catch (BeanDefinitionValidationException ex) {
      throw new BeanCreationException(
            mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
   }

   return exposedObject;
}
```

**AbstractAutowireCapableBeanFactory#createBeanInstance** 这里就是创建 BeanWrapper 的步骤

```java
/**
 * Create a new instance for the specified bean, using an appropriate instantiation strategy:
 * factory method, constructor autowiring, or simple instantiation.
 * @param beanName the name of the bean
 * @param mbd the bean definition for the bean
 * @param args explicit arguments to use for constructor or factory method invocation
 * @return a BeanWrapper for the new instance
 * @see #obtainFromSupplier
 * @see #instantiateUsingFactoryMethod
 * @see #autowireConstructor
 * @see #instantiateBean
 */
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
   // Make sure bean class is actually resolved at this point.
   Class<?> beanClass = resolveBeanClass(mbd, beanName);

   if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
      throw new BeanCreationException(mbd.getResourceDescription(), beanName,
            "Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
   }

   Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
   if (instanceSupplier != null) {
      return obtainFromSupplier(instanceSupplier, beanName);
   }

   if (mbd.getFactoryMethodName() != null) {
      return instantiateUsingFactoryMethod(beanName, mbd, args);
   }

   // Shortcut when re-creating the same bean...
   boolean resolved = false;
   boolean autowireNecessary = false;
   if (args == null) {
      synchronized (mbd.constructorArgumentLock) {
         if (mbd.resolvedConstructorOrFactoryMethod != null) {
            resolved = true;
            autowireNecessary = mbd.constructorArgumentsResolved;
         }
      }
   }
   if (resolved) {
      if (autowireNecessary) {
         return autowireConstructor(beanName, mbd, null, null);
      }
      else {
         return instantiateBean(beanName, mbd);
      }
   }

   // Candidate constructors for autowiring?
   Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
   if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR ||
         mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
      return autowireConstructor(beanName, mbd, ctors, args);
   }

   // Preferred constructors for default construction?
   ctors = mbd.getPreferredConstructors();
   if (ctors != null) {
      return autowireConstructor(beanName, mbd, ctors, null);
   }

   // No special handling: simply use no-arg constructor.
   return instantiateBean(beanName, mbd);
}
```

## 实例化的后处理器应用