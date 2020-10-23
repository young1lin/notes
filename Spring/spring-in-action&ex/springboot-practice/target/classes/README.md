# 这个`Project` 是重温 `Spring Boot` 相关内容
## 什么是 `Spring Boot`
首先介绍开发 `Spring Boot` 的主要动机：
简化配置和部署 `Spring` 应用程序的过程。

它使用全新的开发模型，通过避免一些繁琐的开发步骤和样板代码和配置，使 `Java` 开发非常容易。
所谓的样板代码例如
`Spring`的配置，`Spring MVC`的配置。
```xml
<?xml version="1.0" encoding="UTF-8"?>  
<web-app version="3.0" xmlns="http://java.sun.com/xml/ns/javaee"  
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd">  
<!-- Spring 的配置 -->
<listener>  
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>  
</listener>
<context-param>  
    <param-name>contextConfigLocation</param-name> 
    <!-- 这里可以配置多个 xml 文件 --> 
    <param-value>classpath:application.xml</param-value>  
</context-param>
<!-- Spring mvc 的配置 -->
<servlet>  
    <servlet-name>DispatcherServlet</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>  
    <init-param>  
        <param-name>contextConfigLocation</param-name>  
        <param-value>classpath:WEB-INF/dispatcher-servlet.xml</param-value>  
    </init-param>  
    <load-on-startup>1</load-on-startup>
</servlet>  
<servlet-mapping>  
    <servlet-name>DispatcherServlet</servlet-name>  
    <url-pattern>/</url-pattern> 
</servlet-mapping> 
<!--会话超时配置，单位分钟，Tomcat 里面默认也是 30 分钟-->  
<session-config>
    <session-timeout>30</session-timeout>  
</session-config> 
</web-app>  
```
上面这些代码都是样板代码，完全可以不用写，所以 `Spring Boot` 内置了很多 `AutoConfiguration` 类，帮我们简化开发，配合 `spring-boot-xxx-autoconfigure`
在里面的 `META-INF`有`spring.factories`里面配置了很多的自动装配的类。自定义`starter`也行。

`Spring Boot` 的主要特点
+ 能创建独立的 `Spring`应用程序
+ 直接嵌入 `Tomcat`，`Jetty`或`Undertow`
+ 提供"初始"的`POM`文件内容，以简化`Maven`配置
+ 尽可能的自动配置 `Spring`
+ 提供生产就绪的功能，如指标，健康检查和外部化配置
+ 绝无代码生成，也不需要 `XML` 配置

命令行属性优先于其他属性源
```shell script
java -jar demo.jar --server.port=8080
```
### 开启 CORS 跨域请求
需要通过对控制器方法使用@CrossOrigin注解来设置RESTful Web服务的起源。 @CrossOrigin注源支持特定的REST API，而不支持整个应用程序。

### 国际化
默认情况下，Spring Boot应用程序从类路径下的src/main/resources文件夹中获取消息源。 缺省语言环境消息文件名应为message.properties，每个语言环境的文件应命名为messages_XX.properties。 “XX”表示区域代码。


