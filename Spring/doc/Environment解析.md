# 处理占位符

- Spring 3.1 之前
  - 组件：org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
  - 接口：org.springframework.util.StringValueResolver
- Spring 3.1 之后
  - 组件：org.springframework.context.support.PropertySourcesPlaceholderConfigurer
  - 接口：org.springframework.beans.factory.config.EmbeddedValueResolver

# @Profile

3.1 提供了

ConfigurableEnvironment 提供的 API 处理。

处理 Profile 的类—— org.springframework.context.annotation.ProfileCondition

Condition 接口是 Spring 4 提供的

# @Value

后置处理器

- 底层实现：org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
  - DefaultListableBeanFactory#doResolveDependency
- 底层服务：TypeConverter
  - TypeConverterDelegate
    - java.beans.PropertyEditor
    - ConversionService（这个可以替换掉）

默认构造器提供 @Autowired 和 @Value 解析的能力，如果有 @Inject 注解，那么就加入 @Inject 解析。

```java
public AutowiredAnnotationBeanPostProcessor() {
   this.autowiredAnnotationTypes.add(Autowired.class);
   this.autowiredAnnotationTypes.add(Value.class);
   try {
      this.autowiredAnnotationTypes.add((Class<? extends Annotation>)
            ClassUtils.forName("javax.inject.Inject", AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
      logger.trace("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
   }
   catch (ClassNotFoundException ex) {
      // JSR-330 API not available - simply skip.
   }
}
```

如果 @Value 标注在字段上，由下面处理

**AutowiredMethodElement#inject**

```java
@Override
protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
   if (checkPropertySkipping(pvs)) {
      return;
   }
   Method method = (Method) this.member;
   Object[] arguments;
   if (this.cached) {
      // Shortcut for avoiding synchronization...
      arguments = resolveCachedArguments(beanName);
   }
   else {
      int argumentCount = method.getParameterCount();
      arguments = new Object[argumentCount];
      DependencyDescriptor[] descriptors = new DependencyDescriptor[argumentCount];
      Set<String> autowiredBeans = new LinkedHashSet<>(argumentCount);
      Assert.state(beanFactory != null, "No BeanFactory available");
      TypeConverter typeConverter = beanFactory.getTypeConverter();
      for (int i = 0; i < arguments.length; i++) {
         MethodParameter methodParam = new MethodParameter(method, i);
         DependencyDescriptor currDesc = new DependencyDescriptor(methodParam, this.required);
         currDesc.setContainingClass(bean.getClass());
         descriptors[i] = currDesc;
         try {
             // 在这里，会默认委托 DefaultListableBeanFactory#resolveDependency 来处理
            Object arg = beanFactory.resolveDependency(currDesc, beanName, autowiredBeans, typeConverter);
            if (arg == null && !this.required) {
               arguments = null;
               break;
            }
            arguments[i] = arg;
         }
         catch (BeansException ex) {
            throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(methodParam), ex);
         }
      }
      synchronized (this) {
         if (!this.cached) {
            if (arguments != null) {
               DependencyDescriptor[] cachedMethodArguments = Arrays.copyOf(descriptors, arguments.length);
               registerDependentBeans(beanName, autowiredBeans);
               if (autowiredBeans.size() == argumentCount) {
                  Iterator<String> it = autowiredBeans.iterator();
                  Class<?>[] paramTypes = method.getParameterTypes();
                  for (int i = 0; i < paramTypes.length; i++) {
                     String autowiredBeanName = it.next();
                     if (beanFactory.containsBean(autowiredBeanName) &&
                           beanFactory.isTypeMatch(autowiredBeanName, paramTypes[i])) {
                        cachedMethodArguments[i] = new ShortcutDependencyDescriptor(
                              descriptors[i], autowiredBeanName, paramTypes[i]);
                     }
                  }
               }
               this.cachedMethodArguments = cachedMethodArguments;
            }
            else {
               this.cachedMethodArguments = null;
            }
            this.cached = true;
         }
      }
   }
   if (arguments != null) {
      try {
         ReflectionUtils.makeAccessible(method);
         method.invoke(bean, arguments);
      }
      catch (InvocationTargetException ex) {
         throw ex.getTargetException();
      }
   }
}
```

**DefaultListableBeanFactory#resolveDependency**

```java
@Override
@Nullable
public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
      @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {

   descriptor.initParameterNameDiscovery(getParameterNameDiscoverer());
   if (Optional.class == descriptor.getDependencyType()) {
      return createOptionalDependency(descriptor, requestingBeanName);
   }
   else if (ObjectFactory.class == descriptor.getDependencyType() ||
         ObjectProvider.class == descriptor.getDependencyType()) {
      return new DependencyObjectProvider(descriptor, requestingBeanName);
   }
   else if (javaxInjectProviderClass == descriptor.getDependencyType()) {
      return new Jsr330Factory().createDependencyProvider(descriptor, requestingBeanName);
   }
   else {
      Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(
            descriptor, requestingBeanName);
      if (result == null) {
          // 如果上面是空的，这里就是把对应的依赖给解析
          // 一般情况走这里
         result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
      }
      return result;
   }
}
```

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
       // 这里是 org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver 这个处理器
       // 获取建议值
      Object value = getAutowireCandidateResolver().getSuggestedValue(descriptor);
      if (value != null) {
         if (value instanceof String) {
             // 这里是获取对应的嵌入式的值，在 AbstractBeanFactory 中出现
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

**ContextAnnotationAutowireCandidateResolver#getSuggestedValue**

```java
/**
 * Determine whether the given dependency declares a value annotation.
 * @see Value
 */
@Override
@Nullable
public Object getSuggestedValue(DependencyDescriptor descriptor) {
    // 这里是 @Qualifier 注解的 value，@Autowired 的注解是没有 value 的。
   Object value = findValue(descriptor.getAnnotations());
   if (value == null) {
      MethodParameter methodParam = descriptor.getMethodParameter();
      if (methodParam != null) {
         value = findValue(methodParam.getMethodAnnotations());
      }
   }
   return value;
}

/**
 * Determine a suggested value from any of the given candidate annotations.
 */
@Nullable
protected Object findValue(Annotation[] annotationsToSearch) {
    if (annotationsToSearch.length > 0) {   // qualifier annotations have to be local
        AnnotationAttributes attr = AnnotatedElementUtils.getMergedAnnotationAttributes(
            AnnotatedElementUtils.forAnnotations(annotationsToSearch), this.valueAnnotationType);
        if (attr != null) {
            return extractValue(attr);
        }
    }
    return null;
}

/**
 * Extract the value attribute from the given annotation.
 * @since 4.3
 */
protected Object extractValue(AnnotationAttributes attr) {
    Object value = attr.get(AnnotationUtils.VALUE);
    if (value == null) {
        throw new IllegalStateException("Value annotation must have a value attribute");
    }
    return value;
}
```

**AbstractBeanFactory#resolveEmbeddedValue**

```java
@Override
@Nullable
public String resolveEmbeddedValue(@Nullable String value) {
   if (value == null) {
      return null;
   }
   String result = value;
	// 这里把对应的 ${user.name} 这种内容处理掉，返回一个正确的值，例如是 young1lin
    // 默认就一个处理的，就是 Lambda 表达式处理的
   for (StringValueResolver resolver : this.embeddedValueResolvers) {
      result = resolver.resolveStringValue(result);
      if (result == null) {
         return null;
      }
   }
   return result;
}
```

**PropertySourcesPlaceholderConfigurer#processProperties**

```java
/**
 * Visit each bean definition in the given bean factory and attempt to replace ${...} property
 * placeholders with values from the given properties.
 */
protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
      final ConfigurablePropertyResolver propertyResolver) throws BeansException {

   propertyResolver.setPlaceholderPrefix(this.placeholderPrefix);
   propertyResolver.setPlaceholderSuffix(this.placeholderSuffix);
   propertyResolver.setValueSeparator(this.valueSeparator);

   StringValueResolver valueResolver = strVal -> {
      String resolved = (this.ignoreUnresolvablePlaceholders ?
            propertyResolver.resolvePlaceholders(strVal) :
            propertyResolver.resolveRequiredPlaceholders(strVal));
      if (this.trimValues) {
         resolved = resolved.trim();
      }
      return (resolved.equals(this.nullValue) ? null : resolved);
   };

   doProcessProperties(beanFactoryToProcess, valueResolver);
}
```

# Environment 底层实现

- 底层实现 - PropertySourcesPropertyResolver#convertValueIfNecessary（其实是它的父类的方法）
- 底层服务 - ConversionService
  - 默认实现 -  DefaultConversionService

# Spring 属性配置源 PropertySource

- API
  - 单配置源 - org.springframework.core.env.PropertySource（具体类）
  - 多配置源 - org.springframework.core.env.PropertySources（接口）（内建实现 MutablePropertySources）
- 注解
  - 单配置属性源 - org.springframework.context.annotation.PropertySource
  - 多配置属性源 - org.springframework.context.annotation.PropertySources
- 关联
  - 存储对象 - org.springframework.core.env.MutablePropertySources
  - 关联方法 - org.springframework.core.env.ConfigurableEnvironment#getPropertySources() 返回 MutablePropertySources

# Spring 内建的配置属性源

| PropertySource 类型                                          | 说明               |
| :----------------------------------------------------------- | :----------------- |
| org.springframework.core.env.CommandLinePropertySource       | 命令行             |
| org.springframework.jndi.JndiPropertySource                  | JNDI               |
| org.springframework.core.env.PropertiesPropertySource        | Properties         |
| org.springframework.web.context.support.ServletContextPropertySource | Servlet            |
| org.springframework.web.context.support.ServletContextPropertySource | ServletContxt      |
| org.springframework.core.env.SystemEnvironmentPropertySource | 环境变量配置属性源 |
| .......                                                      |                    |

