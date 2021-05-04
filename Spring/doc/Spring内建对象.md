下面是Spring 内建对象

| BeanName                   | BeanInstance               | use                                                          |
| -------------------------- | -------------------------- | ------------------------------------------------------------ |
| enviroment                 | Environment                | 外部化配置（-d参数 ）以及Profiles，像application.yml文件里的内容。 |
| systemProperties           | java.util.Properties       | Java系统属性                                                 |
| systemEnviroment           | java.util.Map              | 操作系统环境变量                                             |
| messageSource              | MessageSource              | 国际化文案                                                   |
| lifecycleProcessor         | LifecycleProcessor         | Lifecycle Bean处理器                                         |
| applicationEventMutilaster | ApplicationEventMutilaster | Spring事件广播器                                             |

 Spring各种Aware接口

| InterfaceName                  | introduction                                       |
| ------------------------------ | -------------------------------------------------- |
| BeanFactoryAware               | 获取 IoC 容器，BeanFactory                         |
| ApplicationContextAware        | 获取 Spring 应用上下文 - ApplicationContext 对象   |
| EnvironmentAware               | 获取 Enviroment 对象                               |
| ResourceLoaderAware            | 获取资源加载器对象，ResourceLoader                 |
| BeanClassLoaderAware           | 获取当前加载 Bean Class 的 ClassLoader 对象        |
| BeanNameAware                  | 获取当前 Bean  的名称                              |
| MessageSourceAware             | 获取 MessageSource 对象，用于国际化                |
| ApplicationEventPublisherAware | 获取 ApplicationEventPublisher 用于 Spring 事件    |
| EmbeddedValueResolverAware     | 获取 StringValueResolver  对象，用于对占位符的处理 |

