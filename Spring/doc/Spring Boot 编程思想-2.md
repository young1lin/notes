

第十三天，写了自定义 starter，介绍了 @Conditional\*Class、@Conditional\*Bean

第十四天，第 400 页，各种条件注解源码讲解。

## 第十三天

## 自定义 Spring Boot Starter

> A full Spring Boot Starter for a library may contain the following components:
>
> + The autoconfigure module that contains the auto-configuration code.
> + The starter moudule that provides a dependency to auto-configure moudule as well as the library and any additional dependencies that are typically useful. In a nutshell, adding the starter should provide everything needed to start using that library.

官方建议将自动装配代码放在 autoconfigure 模块中，starter 模块以来该模块，并且附加其他需要的依赖。参考 swagger-ui。

不要用 server、management、spring 等作为配置key 的前缀。这些 namespace 是外部化配置 @ConfigurationProperties 前缀属性 prefix，同时，sping-boot-configuration-processor能够帮助 @ConfigurationProperties Bean 生成 IDE 辅助元信息。

@Configuration 类是自动装配的底层实现，并且搭配 @Conditional 注解，使其能够合理地在不同环境中运作。

## @Conditional*Class

@ConditionalOnClass 类存在

@ConditionalOnMissingClass 类不存在

一般是成对存在的，为了防止某个 Class 不存在导致自动装配失败。

## @Conditional*Bean

@ConditionalOnBean

@ConditionalOnMissingBean

也是成对存在的。

| 属性方法     | 属性类型       | 语义说明                 | 使用场景                               | 起始版本 |
| ------------ | -------------- | ------------------------ | -------------------------------------- | -------- |
| value()      | Class[]        | Bean 类型集合            | 类型安全的属性设置                     | 1.0      |
| type()       | String[]       | Bean 类名集合            | 当类型不存在时的属性设置               | 1.3      |
| annotation() | Class[]        | Bean 声明注解类型集合    | 当 Bean 标注了某种注解类型时           | 1.0      |
| name()       | String[]       | Bean 名称集合            | 指定具体 Bean 名称集合                 | 1.0      |
| search()     | SearchStrategy | 层次性应用上下文搜索策略 | 三种应用上下文搜索策略：当前、父及所有 | 1.0      |

# 第十四天

## @Conditional*Property

属性来源 Spring Environment。Java 系统属性和环境变量时典型的 Spring Environment 属性配置来源（PropertySource）。在 Spring Boot 场景中，application.properties 也是其中来源之一。整合了三个地方的属性。

|     属性方法     |                           使用说明                           | 默认值 | 多值属性 | 起始版本 |
| :--------------: | :----------------------------------------------------------: | :----: | :------: | :------: |
|     prefix()     |                       配置属性名称前缀                       |   “”   |    否    |   1.1    |
|     value()      |                  Name() 的别名，参考 name()                  | 空数组 |    是    |   1.1    |
|      name()      | 如果 prefix() 不为空，则完整配置属性名称为 prefix()+name)，否则为 name()  的内容 | 空数组 |    是    |   1.2    |
|  havingValue()   |           表示期望的配置属性值，并且禁止使用 false           |   “”   |    否    |   1.2    |
| matchIfMissing() |              用于判断当前属性值不存在时是否匹配              | false  |    否    |   1.2    |

解决 Spring Boot Environment 某些变量缺失问题

1. 增加属性设置——`new SpringApplicationBuilder(CurrentClazz.class).properties("formatter.enable=true").run(args);`// “=”前后不能有空格。
2. 调整 @ConditionalOnProperty#matchIfMissing 属性——`matchIfMissing = true` 表示当属性配置不存在时，同样视作匹配。

三个 Environment 来源，会覆盖，优先级是 // TODO 写出优先级

## Resource 条件注解

@ConditionalOnResource

默认用 `classpath://` 做协议前缀，其他协议 Spring framework 没有默认实现。

## Web 应用条件注解

@ConditionalOnWebApplication

@ConditionalOnNotWebApplication

## Spring 表达式条件注解

@ConditionalOnExpression("#{spring.aop.auto:true}")

和 

@ConditionalOnExpression("spring.aop.auto:true")

是一样的，对应的 Condition 类有个 wrapIfNecessary 方法加上这个表达式。



