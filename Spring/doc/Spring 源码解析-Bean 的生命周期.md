1. Spring 应用上下文启动准备阶段
2. BeanFactory 创建阶段 
3. BeanFactory 准备阶段
4. BeanFactory 后置处理阶段
5. BeanFactory 注册 BeanPostProcessor 阶段
6. 初始化内建 Bean ：MessageSource
7. 初始化内建 Bean ： Spring 事件广播器
8. Spring 应用上下文刷新阶段
9. Spring 事件监听器注册阶段
10. BeanFactory 初始化完成阶段
11. Spring 应用上下启动完成阶段
12. Spring 应用上下文启动阶段
13. Spring 应用上下文停止阶段
14. Spring 应用上下文关闭阶段

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





1. 缓存中获取单例 Bean

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

