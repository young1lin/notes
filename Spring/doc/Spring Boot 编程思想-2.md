

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

## @Conditional*Property

