 1.IOC Container
====

```java
ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");
```

用于加载bean。

```xml
<beans>    
  <import resource="services.xml"/>    
  <import resource="resources/messageSource.xml"/>    
  <import resource="/resources/themeSource.xml"/>     
  <bean id="bean1" class="..."/>    <bean id="bean2" class="..."/> 
</beans>
```

也可以在beans文件中声明 ``` <import resource="classpath:/resources/messageSource.xml"/>```

也可以用${}表示through "${…}" placeholders that are resolved against JVM system properties at runtime.

xml文件可以用groovy文件代替如：

```groovy
beans {
    dataSource(BasicDataSource) {
        driverClassName = "org.hsqldb.jdbcDriver"
        url = "jdbc:hsqldb:mem:grailsDB"
        username = "sa"
        password = ""
        settings = [mynew:"setting"]
    }
    sessionFactory(SessionFactory) {
        dataSource = dataSource
    }
    myService(MyService) {
        nestedBean = { AnotherBean bean ->
            dataSource = dataSource
        }
    }
}
```

```java
GenericApplicationContext context = new GenericApplicationContext();
new GroovyBeanDefinitionReader(context).loadBeanDefinitions("services.groovy","daos.groovy");
context.refresh();
```
但实际中不建议用groovy甚至混用。

bean的定义

| 属性 | 解释 |
|:---|:---:|
| Class | [Instantiating Beans](https://docs.spring.io/spring/docs/5.2.1.RELEASE/spring-framework-reference/core.html#beans-factory-class)，必须输入全限定名，以便beanFactory反射实例化对象。在xml里面的配置就是class=“com.example.BeanExample” |
| Name |NameBeans|
| Scope |作用域|
| Constructor arguments ||
| Properties ||
| Autowiring mode ||
| Lazy initialization mode ||
| Initialization method | |
| Destruction method | |









<u>collaborators	协作者</u>
<u>specify	指明</u>

### Intantiating Beans 
[IntantiatingBeans]:	When defining a bean that you create with a static factory method, use the `class` attribute to specify the class that contains the `static` factory method and an attribute named `factory-method` to specify the name of the factory method itself. You should be able to call this method (with optional arguments, as described later) and return a live object, which subsequently is treated as if it had been created through a constructor. One use for such a bean definition is to call `static` factories in legacy code.The following bean definition specifies that the bean be created by calling a factory method. The definition does not specify the type (class) of the returned object, only the class containing the factory method. In this example, the `createInstance()` method must be a static method. The following example shows how to specify a factory method:

```xml
<bean id="clientService"
    class="examples.ClientService"
    factory-method="createInstance"/>
```

The following example shows a class that would work with the preceding bean definition:

```java
public class ClientService {
    private static ClientService clientService = new ClientService();
    private ClientService() {}

    public static ClientService createInstance() {
        return clientService;
    }
}
```
### NameBeans

> Every bean has one or more identifiers. These identifiers must be unique within the container that hosts the bean. A bean usually has only one identifier. However, if it requires more than one, the extra ones can be considered aliases.

每个bean都有一个或多个身份标识。这些标识符必须在承载bean容器中是唯一的。一个bean（这里的bean通常指class类型）通常只有一个标志符。然而，如果它需要多于一个以上（例如配置多数据源），可以给额外的bean取别名。

