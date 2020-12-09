作为一个 Spring “老手”，不知不觉，45分钟左右，看完了 70 页（不含自序和前言），要是包括前面两个，是 90 页。前面的内容完全是新手教程+小马哥对我们说的话。

第二天 124 也就是 144。没有想做笔记的冲动。

第三天 174 也就是 194。照旧

第四天 194 也就是 214。开始源码分析了，所以理解得比较慢，这个多层次 @Component 派生的具体实现在 4.0 开始支持，那时候才有递归实现。

第五天 212 。两种解析注解元信息的类，一个 ASM 实现，一个 JDK 实现，执行效率在高负载下天差地别。解释隐形覆盖和显性覆盖，刚看到源码分析前面。有点拖沓其实。



# 第一天

**关于 sun.net.www.protocol.*.Handler 的内容，在小马哥的极客时间课程里面也讲过，后者讲得更为详细，也有实际操作。**

SpringBootConfiguration 派生自 Configuration

Configuration 派生自 Component

其他 Repository、Service 、Controller 均派生自 Component

# 第二天 

CGLIB 会提升 @Configuration 的类，而不是 @Bean 的类。

# 第三天

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

# 第四天

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

# 第五天

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



