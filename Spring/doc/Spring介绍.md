### Spring IoC依赖 查找

+ 根据 `Bean` 名称查找
  + 实时查找
  + 延迟查找
+ 根据 `Bean` 类型查找
  + 单个`Bean`对象
  + 集合`Bean`对象
+ 根据`Bean`名称+类型查找
+ 根据`Java`注解查找
  + 单个`Bean`对象
  + 集合`Bean`对象

``` java
BeanFactory beanFactory = new ClassPathXmlApplicationContext("beans.xml");
// 根据 `Bean` 名称查找
beanFactory.getBean("beanName");
// 延迟初始化，是通过其他Bean初始化后，再来初始化

// 根据 `Bean` 类型查找
beanFactory.getBean(bean.class);

// 通过Bean的类型查找到的集合Bean
if(beanFactory instanceof ListableBeanFactory){
  ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
   Map<String,Bean> beans = listableBeanFactory.getBeansOfType(Bean.class);
}

//如果有多个Bean类型，如 SubBean extends Bean,取Bean时会有多个，需要指定一个为 Primary

// 通过注解方式查找
if(beanFactory instanceof ListableBeanFactory){
  ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
   Map<String,Bean> beans = (Map)listableBeanFactory.getBeansWithAnnotation(Annotation.class);
}
System.out.println(beans);
```



BeanFactory，ObjectFactory，FactoryBean区别





### Spring IOC依赖 注入



### Spring IOC容器

谁才是IOC容器：BeanFactory和ApplicationContext

ApplicationContext继承于BeanFactory，ApplicationContext组合了BeanFactory的实现（DefaultListableBeanFactory）。



### 定义Spring Bean 

+ 什么是BeanDefinition？
BeanDefinition 是Spring FrameWork 中定义 Bean 的配置元信息接口，包含：
+ Bean的类名
  + Bean 行为配置元素，如作用域、自动绑定的模式、生命周期回调等
  + 其他 Bean 引用，又称作合作者（Coolaborators）或者依赖（Dependencies）
  + 配置设置，比如 Bean 属性（Properties）
+ BeanDefinition 元信息

<table>
  <thead>
    <tr>
      <th>
        属性
      </th>
      <th>
        说明
      </th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>
        Class
      </td>
      <td>
        Bean全限定名，必须是具体类，不能用抽象类或接口
      </td>
    </tr>
    <tr>
      <td>
        Name
      </td>
      <td>
        Bean 的名称或者ID
      </td>
    </tr>
    <tr>
      <td>
        Scope
      </td>
      <td>
        <table style="width=100%;height=100%;bgcolor=#FF0000;margin:0 0 0 0">
          <tr>
            <td>singleton</td>
            <td>(Default) Scopes a single bean definition to a single object instance for each Spring IoC container.
</td>
          </tr>
          <tr>
            <td>
            	prototype
            </td>
            <td>
              Scopes a single bean definition to any number of object instances
						</td>
          </tr>
          <tr>
            <td>
            	request
            </td>
            <td>
            	Scopes a single bean definition to the lifecycle of a single HTTP request. That is, each HTTP request has its own instance of a bean created off the back of a single bean definition. Only valid in the context of a web-aware Spring ApplicationContext.
            </td>
          </tr>
          <tr>
            <td>
            	session
            </td>
            <td>
            	Scopes a single bean definition to the lifecycle of an HTTP Session. Only valid in the context of a web-aware Spring ApplicationContext.
            </td>
          </tr>
          <tr>
            <td>
            	application
            </td>
            <td>
            	Scopes a single bean definition to the lifecycle of a ServletContext. Only valid in the context of a web-aware Spring ApplicationContext.
            </td>
          </tr>
          <tr>
            <td>
            	websocket
            </td>
            <td>
            	Scopes a single bean definition to the lifecycle of a WebSocket. Only valid in the context of a web-aware Spring ApplicationContext.
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        Constructor arguments
      </td>
      <td>
        Bean 构造器参数（用于依赖注入）
      </td>
    </tr>
    <tr>
      <td>
        Properties
      </td>
      <td>
        Bean属性设置（用于依赖注入）
      </td>
    </tr>
    <tr>
      <td>
        Autowiring mode
      </td>
      <td>
        <table style="width=100% height=100% bgcolor=#FF0000">
          <tr>
          	<th>
              Model
            </th>
            <th>
              Explanation
            </th>
          </tr>
          <tr>
            <td>
              no
            </td>
            <td>
              (Default) No autowiring. Bean references must be defined by ref elements. Changing the default setting is not recommended for larger deployments, because specifying collaborators explicitly gives greater control and clarity. To some extent, it documents the structure of a system.
            </td>
          </tr>
          <tr>
          	<td>
              byName
            </td>
            <td>
              Autowiring by property name. Spring looks for a bean with the same name as the property that needs to be autowired. For example, if a bean definition is set to autowire by name and it contains a master property (that is, it has a setMaster(..) method), Spring looks for a bean definition named master and uses it to set the property.
            </td>
          </tr>
          <tr>
          	<td>
              byType
            </td>
            <td>
              Lets a property be autowired if exactly one bean of the property type exists in the container. If more than one exists, a fatal exception is thrown, which indicates that you may not use byType autowiring for that bean. If there are no matching beans, nothing happens (the property is not set).
            </td>
          </tr>
          <tr>
          	<td>
              constructor
            </td>
            <td>
              Analogous to byType but applies to constructor arguments. If there is not exactly one bean of the constructor argument type in the container, a fatal error is raised.
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        Lazy initialization mode 
      </td>
      <td>
        Bean 延迟初始化模式（延迟和非延迟）
        lazy-init="true"或false
        <br/>
        &lt;beans default-lazy-init="true"&gt;
    &lt;!-- no beans will be pre-instantiated... --&gt;
			&lt;/beans&gt;
      </td>
    </tr>
    <tr>
      <td>
        Initialization method
      </td>
      <td>
        Bean 初始化回调方法名称
        @Bean(init-method="myInitMthod")
        java的JSR250规范中的@PostConstruct标注在init方法上，
      </td>
    </tr>
    <tr>
      <td>
        Destruction method
      </td>
      <td>
        Bean 销毁回调方法名称
        @Bean(destory-method="myDestoryMthod")
        java的JSR250规范中的@PreDestroy标注在destroy方法上
      </td>
    </tr>
  </tbody>
</table>



+ BeanDefinition 构建
  + 通过BeanDefinitionBuilder
  + 通过AbstractBeanDefinition以及派生类

``` java
BeanDefinitionBuilder beanDefinitionBuilder  = new BeanDefinitionBuilder(User.class);
//设置初始值
beanDefinitionBuilder.addPropertyValue("id",1);
//获取BeanDefinition实例
BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDeinition();

//BeanDefinition并非Bean最终形态，可以自定义修改

//2.通过 AbstracBeanDefinition
GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
//设置 Bean 类型
genericBeanDefinition.setBeanClass(User.class);
//通过 MutablePropertyValues批量操作属性
MutablePropertyValues propertyValues = new MutablePropertyValues()；
propertyValues.addPropertyValue("id",1);
genericBeanDefinition.setPropertyValues(propertyValues);

```

+ Bean的名称

每个Bean拥有一个或多个标识符（idetifiers），这些标识符在Bean所在的容器时唯一的。通常，一个Bean仅有一个标识符，如果需要额外的，可考虑使用别名（Alias）来扩充。

BeanNameGenerator：2.0.3引入，框架内建两种实现：

1. DefaultBeanNameGenerator：默认通用BeanNameGenerator实现

2. AnnotationBeanNameGenerator：基于注解扫描的BeanNameGenerator实现，起始于2.5

Spring 容器管理和游离对象





spring 中如何清理 propotype 对象

``` java
public class BeanManager implements DisposableBean{
  @AutoWired
  private ConfigurableListableBeanFactory beanFactory;
  
  @Override 
  public void destory(){
     beanFactory.getBeanDefinition(Object o);
   }
}
```



### 自定义Scope

要两个操作

1. 实现 Scope 接口
2. 通过 api 来实现

``` java
public class ThreaLocalScope implements Scope{
  
}
```

