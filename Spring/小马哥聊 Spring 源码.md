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

**AbstractApplicationContext#prepareRefresh()方法**

+ 启动时间 - startupDate
+ 状态表示 - closed（false）、active（true）
+ 初始化 PropertySource - initPropertySources()
+ 检验 Environment 中必须属性
+ 初始化事件监听器集合
+ 初始化早期 Spring 事件集合



initPropertySources 这个是空的，给子类来实现

getEnvironment.validateRequiredProperties(); 表示检验 Environment 中必须属性



**BeanFactory 创建阶段** 

AbstractApplicationContext#obtainFreshBeanFactory()方法

- 刷新 Spring 应用上下文底层 BeanFactory - refreshBeanFactory()
  - 创建 BeanFactory - createBeanFactory() 其实就是 DefaultListableBeanFactory
  - 设置 BeanFactory Id
  - 设置 “是否允许 BeanDefinition 重复定义“ - cutstomizeBeanFactory(DefaultListableBeanFactory)
  - 设置 “是否允许循环依赖” - customizeBeanFactory(DefaultListableBeanFactory)
  - 加载 BeanDefinition - loadBeanDefinitions(DefaulListableBeanFactory)方法
  - 关联新建 BeanFactory 到 Spring 应用上下文
- 返回 Spring 应用上下文底层 BeanFactory - getBeanFactory();

**BeanFactory 准备阶段**

AbstractApplicationContext#prepareBeanFactory(ConfigurableListableBeanFacotry)方法

+ 关联 ClassLoader
+ 设置 Bean 表达式处理器
+ 添加 PropertyEditorRegistrar 实现 - ResourceEditorRegistrar
+ 添加 Aware 回调接口 BeanPostProcessor 实现 - ApplicationContextAwareProcessor
+ 忽略 Aware 回调接口作为依赖注入接口
+ 注册 ResolvableDependency 对象 - BeanFactory、ResourceLoader、ApplicationEventPublisher 以及ApplicationContext
+ 注册 ApplicationListenerDetector 对象
+ 注册 LoadTimeWeaverAwareProcessor 对象
+ 注册单例对象 - Environment、Java System Properties 以及 OS 环境变量



注册 六大 Aware 接口



+ AbstractApplicationContext#prepareBeanFactory(ConfigurableListableBeanFactory )方法
  + 关联 ClassLoader
  + 设置 Bean 表达式处理器
  + 添加 PropertyEditorRegistrar 实现 - ResourceEditorRegistrar
  + 添加 Aware 回调接口 BeanPostProcessor 实现 - ApplicationContextAwareProcessor
  + 忽略 Aware 回调接口作为依赖注入接口
  + 注册 ResolvableDepedency 对象 - BeanFactory、ResourceLoader、ApplicationEventPublisher 以及 ApplicationContext
  + 注册 ApplicationListenerDetector 对象
  + 注册 LoadTimeWeaverAwareProcessor 对象
  + 注册单例对象 - Environment、Java System Properties 以及 OS 环境变量





