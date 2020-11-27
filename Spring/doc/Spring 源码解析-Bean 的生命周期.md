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

# CreateBean 之前的后处理器应用

## 创建 bean

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

## 实例化后的后处理器应用

如果这里不为空，就会到这里，因为 createBean 的后面的操作这个 Bean 都不参与了（详情见下面），所以这里需要进行最后的后置处理。

```java
AbstractAutowireCapableBeanFactory#createBean 
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

