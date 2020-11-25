#  6.4.1

customizeBeanFactory(DefaultListableBeanFactory beanFactory){

// 最后一步增加 @Qualifier @Autowired 解析器 已经移除了

}

@Aspect 注解解析器

```java
public class AopNamespaceHandler extends NamespaceHandlerSupport {
    public AopNamespaceHandler() {
    }

    public void init() {
        this.registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());
        this.registerBeanDefinitionParser("aspectj-autoproxy", new AspectJAutoProxyBeanDefinitionParser());
        this.registerBeanDefinitionDecorator("scoped-proxy", new ScopedProxyBeanDefinitionDecorator());
        this.registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
    }
}
class AspectJAutoProxyBeanDefinitionParser implements BeanDefinitionParser {}
```

和书上内容不一样了，

# 264

阀值

根本没有阀值这个词，只有阈值。阈值又叫临界值，是指一个效应能够产生的最低值或最高值。

# 182

```java
public abstract class AbstractAutoProxyCreator extends ProxyProcessorSupport implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware {

    public boolean postProcessAfterInstantiation(Object bean, String beanName) {
        return true;
    }
}
```

# 204

moke -> mock

应该是 mock 而不是 moke。

# 212

```java
书上是 /"leave
实际是 \"leave
```

# 222

书上内容： DriverManager.geiConnection

getConnection

## execuUpdate	

executeUpdate

223 又对了

```java
/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.weaving;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;

/**
 * Post-processor that registers AspectJ's
 * {@link org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter}
 * with the Spring application context's default
 * {@link org.springframework.instrument.classloading.LoadTimeWeaver}.
 *
 * @author Juergen Hoeller
 * @author Ramnivas Laddad
 * @since 2.5
 */
public class AspectJWeavingEnabler
      implements BeanFactoryPostProcessor, BeanClassLoaderAware, LoadTimeWeaverAware, Ordered {

   /**
    * The {@code aop.xml} resource location.
    */
   public static final String ASPECTJ_AOP_XML_RESOURCE = "META-INF/aop.xml";


   @Nullable
   private ClassLoader beanClassLoader;

   @Nullable
   private LoadTimeWeaver loadTimeWeaver;


   @Override
   public void setBeanClassLoader(ClassLoader classLoader) {
      this.beanClassLoader = classLoader;
   }

   @Override
   public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
      this.loadTimeWeaver = loadTimeWeaver;
   }

   @Override
   public int getOrder() {
      return HIGHEST_PRECEDENCE;
   }

   @Override
   public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
      enableAspectJWeaving(this.loadTimeWeaver, this.beanClassLoader);
   }


   /**
    * Enable AspectJ weaving with the given {@link LoadTimeWeaver}.
    * @param weaverToUse the LoadTimeWeaver to apply to (or {@code null} for a default weaver)
    * @param beanClassLoader the class loader to create a default weaver for (if necessary)
    */
   public static void enableAspectJWeaving(
         @Nullable LoadTimeWeaver weaverToUse, @Nullable ClassLoader beanClassLoader) {

      if (weaverToUse == null) {
         if (InstrumentationLoadTimeWeaver.isInstrumentationAvailable()) {
            weaverToUse = new InstrumentationLoadTimeWeaver(beanClassLoader);
         }
         else {
            throw new IllegalStateException("No LoadTimeWeaver available");
         }
      }
      weaverToUse.addTransformer(
            new AspectJClassBypassingClassFileTransformer(new ClassPreProcessorAgentAdapter()));
   }


   /**
    * 告诉 AspectJ 以 org.aspectj 开头的类不处理
    * ClassFileTransformer decorator that suppresses processing of AspectJ
    * classes in order to avoid potential LinkageErrors.
    * @see org.springframework.context.annotation.LoadTimeWeavingConfiguration
    */
   private static class AspectJClassBypassingClassFileTransformer implements ClassFileTransformer {

      private final ClassFileTransformer delegate;

      public AspectJClassBypassingClassFileTransformer(ClassFileTransformer delegate) {
         this.delegate = delegate;
      }

      @Override
      public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

         if (className.startsWith("org.aspectj") || className.startsWith("org/aspectj")) {
            return classfileBuffer;
         }
         return this.delegate.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
      }
   }

}
```

# 246

SqlSessionFactoryBean#buildSqlSessionFactory()

509 行开始，基本不一样了。

BeanDefinitionVisitor

设计模式

# 243

\<bean id="sqlSessionFactory" class="org.mybatis.Spring.SqlSessionFactoryBean"\>

中间的 Spring 正确写法是小写

252 页有同样的问题

# 258

```java
/**
 * Calls the parent search that will search and register all the candidates. Then the registered objects are post
 * processed to set them as MapperFactoryBeans
 */
@Override
public Set<BeanDefinitionHolder> doScan(String... basePackages) {
  Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

  if (beanDefinitions.isEmpty()) {
    LOGGER.warn(() -> "No MyBatis mapper was found in '" + Arrays.toString(basePackages)
        + "' package. Please check your configuration.");
  } else {
     // 书上这里是一长串代码
    processBeanDefinitions(beanDefinitions);
  }

  return beanDefinitions;
}
```

```java
private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
  GenericBeanDefinition definition;
  for (BeanDefinitionHolder holder : beanDefinitions) {
    definition = (GenericBeanDefinition) holder.getBeanDefinition();
    String beanClassName = definition.getBeanClassName();
    LOGGER.debug(() -> "Creating MapperFactoryBean with name '" + holder.getBeanName() + "' and '" + beanClassName
        + "' mapperInterface");
      // 书上和这里不一样了，这里应该是加 mapperInterface
      // 说是 issue #59，那个是解决反序列化，创建动态代理相关的内容，没有直接和这里相关，没有直接改这部分代码
    // the mapper interface is the original class of the bean
    // but, the actual class of the bean is MapperFactoryBean
    definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName); // issue #59
    definition.setBeanClass(this.mapperFactoryBeanClass);
    definition.getPropertyValues().add("addToConfig", this.addToConfig);
    boolean explicitFactoryUsed = false;
    if (StringUtils.hasText(this.sqlSessionFactoryBeanName)) {
      definition.getPropertyValues().add("sqlSessionFactory",
          new RuntimeBeanReference(this.sqlSessionFactoryBeanName));
      explicitFactoryUsed = true;
    } else if (this.sqlSessionFactory != null) {
      definition.getPropertyValues().add("sqlSessionFactory", this.sqlSessionFactory);
      explicitFactoryUsed = true;
    }

    if (StringUtils.hasText(this.sqlSessionTemplateBeanName)) {
      if (explicitFactoryUsed) {
        LOGGER.warn(
            () -> "Cannot use both: sqlSessionTemplate and sqlSessionFactory together. sqlSessionFactory is ignored.");
      }
      definition.getPropertyValues().add("sqlSessionTemplate",
          new RuntimeBeanReference(this.sqlSessionTemplateBeanName));
      explicitFactoryUsed = true;
    } else if (this.sqlSessionTemplate != null) {
      if (explicitFactoryUsed) {
        LOGGER.warn(
            () -> "Cannot use both: sqlSessionTemplate and sqlSessionFactory together. sqlSessionFactory is ignored.");
      }
      definition.getPropertyValues().add("sqlSessionTemplate", this.sqlSessionTemplate);
      explicitFactoryUsed = true;
    }

    if (!explicitFactoryUsed) {
      LOGGER.debug(() -> "Enabling autowire by type for MapperFactoryBean with name '" + holder.getBeanName() + "'.");
      definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
    }
    definition.setLazyInit(lazyInitialization);
  }
}
```

# 278

考虑到对事务的应用比

考虑到对声明式事务的应用比

# 280

对于 createTransactionIfNecessar 函数主要做了

对于 createTransactionIfNecessary 函数主要做了

少了个 y

# 281

隔离级别、timout

隔离级别、timeout

少了个 e

# 287

在将原事务还原

再将原事务还原

zaì 字错误

# 300 建议修改

`List<User> userList = new ArrayList<User>();`

泛型使用不当

# 301 

Spring-servlet.xml

中有这 tx 相关的内容，应该是复制粘贴的，建议去掉不必要的配置

org.Springframework

大写了

# 306 

org.Spring

# 309

```java
public ServletConfigPropertyValues(ServletConfig config,Set<String> requiredProperties) throws ServletException{
    // 已经改掉了，和书上不一样。这里判空用的 CollectionUtils
    Set<String> missingProps = (!CollectionUtils.isEmpty(requiredProperties) ?
                                new HashSet<>(requiredProperties) : null);
}
```

# 318 

哪来的 summer.properties

# 345

InternalResourceView#renderMergerdOutputModel 

HttpServletRequest requestToExpose = getRequestToExpost(request);

上面这一步已经没有了

# 348

实际上已经不是大写的 RMIServiceExporter 了，现在是RmiServiceExporter 还有这个 RmiProxyFactoryBean

RMI 协议也是小写的 rmi

# 350

RMIServiceExporter -> RmiServiceExporter

# 352

clientSocketFactory （书上这里多了个 serverSocketFactory）用于导出远程对象

serverSocketFactory 用户在服务端。。。。

# 356 

 return this.RMIExporter.invoke(invocation, this.wrappedObject);

RMI 实际代码时是小写 rmi。

# 359

从而**提高**使用时候的响应时间 -> 从而**降低**使用时候的响应时间

这里应该是降低响应时间。

# 414

study.enabled=true -> study.enable=true

没有 d

416 页又是有 d 的了。后面都是没 d 的了。

420、421 又有 d 了。