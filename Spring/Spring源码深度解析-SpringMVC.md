```java
    public static void main(String[] args) throws IOException {
        Resource[] resources =  new PathMatchingResourcePatternResolver().getResources("classpath*:/cn/luckyray/evaluation/**/*.class");
        for(Resource resource : resources){
            System.out.println(resource.getFilename());
        }
    }
```

Spring 在拼接的时候，默认把 `ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX="classpath*:"`加上了，还有 resourcePattern='"**/*.class'

```java
static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
private String resourcePattern = DEFAULT_RESOURCE_PATTERN;
```

能扫描出所有的类，然后对类进行处理

# SpringMVC

在 Spring Boot 中，只有在用户第一次访问应用时，会初始化 SpringMVC 所有内容。

例子：

```java
public class UserController extends AbstractController{
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest arg0,HttpResponse arg1){
        List<User> userList = new ArrayList<>();
        User uaserA = new User();
        User uaserB = new User();
		userA.setName("张三");
        userB.setName("李四")
		userList.add(userA);
        userList.add(userB);
        // 第一个参数是视图的逻辑名称，视图解析器会使用该名称查找实际的 View 对象
        // 第二个参数是传递给视图的模型对象的名称
        // 第三个参数是传递给视图的模型对象的值
        return new ModelAndView("userlist","users",userList);
    }
}
```

ModelAndView 在 SpringMVC 中占有很重要的地位，控制器执行方法都必须返回一个 ModelAndView，ModelAndView 对象保存了视图以及视图显示的模型数据。

## 配置 Spring-servlet.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans.xsd">
	<bean id="simpleUrlMapping" class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping"
 		<property name="mappings">
    		<props>
    			<prop key="/userlist.htm">userController</prop>
    		</props>
		</property>
    </bean>

	<bean id="userController" class="test.controller.UserController"/>
</beans>
```

## Servlet 生命周期

### 初始化阶段

+ Servlet 容器加载 Servlet 类，把 Servlet 类的 .class 文件中的数据读到内存中
+ Servlet 容器创建一个 ServletConfig 对象。ServletConfig 对象包含了 Servlet 的初始化配置信息。
+ Servlet 容器创建一个 servlet 对象。
+ Servlet 容器调用 Servlet 对象的 init 方法进行初始化。

### 运行阶段

当 servlet 容器接收到一个请求时，servlet 容器会针对这个请求创建一个 ServletRequest 和 ServletResponse 对象，然后调用 service 方法。并把这两个参数传递给 service 方法。service 方法通过 ServletRequest 对象获得请求的信息，并处理该请求。再通过 ServletResponse 对象生成这个请求的响应结果。然后销毁 ServletRequest 和 ServletResponse 对象。无论是 post 还是 get 请求，最终这个请求都会由 service 方法处理。

### 销毁阶段

当 Web 应用被终止时，Servlet 容器会先调用 Servlet 对象的 destroy 方法，然后再销毁 Servlet 对象，同时也会销毁与 servlet 对象相关联的 servletConfig 对象。可以在 destory 方法中，释放 Servlet 所占用的资源，如关闭数据库连接，关闭文件输入输出流等。



# WebApplicationContext 的初始化

Spring MVC 的初始化

## 寻找或创建对应的 WebApplicationContext 实例

### 通过构造函数的注入进行初始化

```java
	// FrameworkServlet
	/**
	 * Overridden method of {@link HttpServletBean}, invoked after any bean properties
	 * have been set. Creates this servlet's WebApplicationContext.
	 */
	@Override
	protected final void initServletBean() throws ServletException {
		getServletContext().log("Initializing Spring " + getClass().getSimpleName() + " '" + getServletName() + "'");
		if (logger.isInfoEnabled()) {
			logger.info("Initializing Servlet '" + getServletName() + "'");
		}
		long startTime = System.currentTimeMillis();

		try {
			this.webApplicationContext = initWebApplicationContext();
			initFrameworkServlet();
		}
		catch (ServletException | RuntimeException ex) {
			logger.error("Context initialization failed", ex);
			throw ex;
		}

		if (logger.isDebugEnabled()) {
			String value = this.enableLoggingRequestDetails ?
					"shown which may lead to unsafe logging of potentially sensitive data" :
					"masked to prevent unsafe logging of potentially sensitive data";
			logger.debug("enableLoggingRequestDetails='" + this.enableLoggingRequestDetails +
					"': request parameters and headers will be " + value);
		}

		if (logger.isInfoEnabled()) {
			logger.info("Completed initialization in " + (System.currentTimeMillis() - startTime) + " ms");
		}
	}
```

```java
/**
	 * Initialize and publish the WebApplicationContext for this servlet.
	 * <p>Delegates to {@link #createWebApplicationContext} for actual creation
	 * of the context. Can be overridden in subclasses.
	 * @return the WebApplicationContext instance
	 * @see #FrameworkServlet(WebApplicationContext)
	 * @see #setContextClass
	 * @see #setContextConfigLocation
	 */
	protected WebApplicationContext initWebApplicationContext() {
		WebApplicationContext rootContext =
				WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		WebApplicationContext wac = null;

		if (this.webApplicationContext != null) {
			// A context instance was injected at construction time -> use it
			wac = this.webApplicationContext;
			if (wac instanceof ConfigurableWebApplicationContext) {
				ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) wac;
				if (!cwac.isActive()) {
					// The context has not yet been refreshed -> provide services such as
					// setting the parent context, setting the application context id, etc
					if (cwac.getParent() == null) {
						// The context instance was injected without an explicit parent -> set
						// the root application context (if any; may be null) as the parent
						cwac.setParent(rootContext);
					}
                    // 1. ## 刷新上下文环境
					configureAndRefreshWebApplicationContext(cwac);
				}
			}
		}
		if (wac == null) {
			// No context instance was injected at construction time -> see if one
			// has been registered in the servlet context. If one exists, it is assumed
			// that the parent context (if any) has already been set and that the
			// user has performed any initialization such as setting the context id
            // 2. 根据 contextAttribute 属性加载 WebApplicationContext
			wac = findWebApplicationContext();
		}
		if (wac == null) {
			// No context instance is defined for this servlet -> create a local one
            // 3. 
			wac = createWebApplicationContext(rootContext);
		}

		if (!this.refreshEventReceived) {
			// Either the context is not a ConfigurableApplicationContext with refresh
			// support or the context injected at construction time had already been
			// refreshed -> trigger initial onRefresh manually here.
			synchronized (this.onRefreshMonitor) {
                // 4. ## 刷新
				onRefresh(wac);
			}
		}

		if (this.publishContext) {
			// Publish the context as a servlet context attribute.
			String attrName = getServletContextAttributeName();
			getServletContext().setAttribute(attrName, wac);
		}

		return wac;
	}
```



### 通过 contextAttribute 进行初始化

通过在 web.xml 文件中配置的 servlet 参数 contextAttribute 来查找 ServletContext 中对应的属性，默认为 WebapplicationContext.class.GetName()+".ROOT" ，也就是 ContextLoaderListener 加载时会创建 WebApplicationContext 实例，并将实例以 WebapplicationContext.class.getName()+".ROOT" 为 key 放入 ServletContext。可以重写 WebApplicationContext，并在 Servlet 的配置中通过初始化参数 contextAttribute 指定 key。

```java
	/**
	 * Retrieve a {@code WebApplicationContext} from the {@code ServletContext}
	 * attribute with the {@link #setContextAttribute configured name}. The
	 * {@code WebApplicationContext} must have already been loaded and stored in the
	 * {@code ServletContext} before this servlet gets initialized (or invoked).
	 * <p>Subclasses may override this method to provide a different
	 * {@code WebApplicationContext} retrieval strategy.
	 * @return the WebApplicationContext for this servlet, or {@code null} if not found
	 * @see #getContextAttribute()
	 */
	@Nullable
	protected WebApplicationContext findWebApplicationContext() {
		String attrName = getContextAttribute();
		if (attrName == null) {
			return null;
		}
		WebApplicationContext wac =
				WebApplicationContextUtils.getWebApplicationContext(getServletContext(), attrName);
		if (wac == null) {
			throw new IllegalStateException("No WebApplicationContext found: initializer not registered?");
		}
		return wac;
	}
```

### 重新创建 WebapplicationContext 实例

如果通过以上两种方式还是没能创建 wac，那只能在这重新创建新的实例。

```java
	/**
	 * Instantiate the WebApplicationContext for this servlet, either a default
	 * {@link org.springframework.web.context.support.XmlWebApplicationContext}
	 * or a {@link #setContextClass custom context class}, if set.
	 * Delegates to #createWebApplicationContext(ApplicationContext).
	 * @param parent the parent WebApplicationContext to use, or {@code null} if none
	 * @return the WebApplicationContext for this servlet
	 * @see org.springframework.web.context.support.XmlWebApplicationContext
	 * @see #createWebApplicationContext(ApplicationContext)
	 */
	protected WebApplicationContext createWebApplicationContext(@Nullable WebApplicationContext parent) {
		return createWebApplicationContext((ApplicationContext) parent);
	}

```



```java
/**
 * Instantiate the WebApplicationContext for this servlet, either a default
 * {@link org.springframework.web.context.support.XmlWebApplicationContext}
 * or a {@link #setContextClass custom context class}, if set.
 * <p>This implementation expects custom contexts to implement the
 * {@link org.springframework.web.context.ConfigurableWebApplicationContext}
 * interface. Can be overridden in subclasses.
 * <p>Do not forget to register this servlet instance as application listener on the
 * created context (for triggering its {@link #onRefresh callback}, and to call
 * {@link org.springframework.context.ConfigurableApplicationContext#refresh()}
 * before returning the context instance.
 * @param parent the parent ApplicationContext to use, or {@code null} if none
 * @return the WebApplicationContext for this servlet
 * @see org.springframework.web.context.support.XmlWebApplicationContext
 */
protected WebApplicationContext createWebApplicationContext(@Nullable ApplicationContext parent) {
   Class<?> contextClass = getContextClass();
   if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
      throw new ApplicationContextException(
            "Fatal initialization error in servlet with name '" + getServletName() +
            "': custom WebApplicationContext class [" + contextClass.getName() +
            "] is not of type ConfigurableWebApplicationContext");
   }
   ConfigurableWebApplicationContext wac =
         (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);

   wac.setEnvironment(getEnvironment());
   wac.setParent(parent);
   String configLocation = getContextConfigLocation();
   if (configLocation != null) {
      wac.setConfigLocation(configLocation);
   }
   configureAndRefreshWebApplicationContext(wac);

   return wac;
}
```

## configureAndRefreshWebApplicationContext 刷新上下文环境

无论是通过构造函数注入还是单独创建，都会调用 configureAndRefreshWebApplicationContext 方法来对已经创建的 WebApplicationContext 实例进行配置及刷新。	

FrameworkServlet#configureAndRefreshWebApplicationContext

```java
protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac) {
    if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
        // The application context id is still set to its original default value
        // -> assign a more useful id based on available information
        if (this.contextId != null) {
            wac.setId(this.contextId);
        }
        else {
            // Generate default id...
            wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
                      ObjectUtils.getDisplayString(getServletContext().getContextPath()) + '/' + getServletName());
        }
    }

    wac.setServletContext(getServletContext());
    wac.setServletConfig(getServletConfig());
    wac.setNamespace(getNamespace());
    wac.addApplicationListener(new SourceFilteringListener(wac, new ContextRefreshListener()));

    // The wac environment's #initPropertySources will be called in any case when the context
    // is refreshed; do it eagerly here to ensure servlet property sources are in place for
    // use in any post-processing or initialization that occurs below prior to #refresh
    ConfigurableEnvironment env = wac.getEnvironment();
    if (env instanceof ConfigurableWebEnvironment) {
        ((ConfigurableWebEnvironment) env).initPropertySources(getServletContext(), getServletConfig());
    }

    postProcessWebApplicationContext(wac);
    applyInitializers(wac);
    // 加载配置文件及整合 parent 到 wac
    wac.refresh();
}
```

## 刷新

onRefresh 是 FrameworkServlet 类中提供的模版方法，在其子类 DispatcherServlet 中进行了重写，主要用于刷新 Spring 在 Web 功能实现中所必须使用的全局变量。

```java
	/**
	 * This implementation calls {@link #initStrategies}.
	 */
	@Override
	protected void onRefresh(ApplicationContext context) {
		initStrategies(context);
	}

	/**
	 * Initialize the strategy objects that this servlet uses.
	 * 初始化各个解析器
	 * <p>May be overridden in subclasses in order to initialize further strategy objects.
	 */
	protected void initStrategies(ApplicationContext context) {
        // 初始化 MultipartResolver
		initMultipartResolver(context);
		initLocaleResolver(context);
		initThemeResolver(context);
		initHandlerMappings(context);
		initHandlerAdapters(context);
		initHandlerExceptionResolvers(context);
		initRequestToViewNameTranslator(context);
		initViewResolvers(context);
		initFlashMapManager(context);
	}
```

### 初始化MultipartResovler

在 Spring 中，MultipartResovler 主要用来处理文件上传。默认是没有的，如果要使用则需在 Web 应用的上下文中添加 该 bean 的定义。常用配置如下

```xml
<bean id="multipartResovler" class="org.springframework.web.multipart.commons.CommonsMultipartResovler">
    <!-- 该属性用来配置可上传的最大字节数 -->
    <property name="maximumFileSize">
    	<value>100000</value>
    </property>
</bean>
```



```java
/**
 * Initialize the MultipartResolver used by this class.
 * <p>If no bean is defined with the given name in the BeanFactory for this namespace,
 * no multipart handling is provided.
 */
private void initMultipartResolver(ApplicationContext context) {
   try {
      this.multipartResolver = context.getBean(MULTIPART_RESOLVER_BEAN_NAME, MultipartResolver.class);
      if (logger.isTraceEnabled()) {
         logger.trace("Detected " + this.multipartResolver);
      }
      else if (logger.isDebugEnabled()) {
         logger.debug("Detected " + this.multipartResolver.getClass().getSimpleName());
      }
   }
   catch (NoSuchBeanDefinitionException ex) {
      // Default is no multipart resolver.
      this.multipartResolver = null;
      if (logger.isTraceEnabled()) {
         logger.trace("No MultipartResolver '" + MULTIPART_RESOLVER_BEAN_NAME + "' declared");
      }
   }
}
```

### 初始化 initLocaleResolver

Spring 的国际化配置一共有 3 种使用方式

+ 基于 URL 参数的设置—— `<href=“?locale=zh_CN">`
+ 基于 session 的配置 `<bean id="localResolver" class="org.springframework.web.servlet.i18n.SessionLocaleResovler">`类似 Youtube，如果第一次没设置语言，则通过 Http head accept-language 确定
+ 基于 Cookie 的国际化配置 ` <bean id="localResolver" class="org.springframework.web.servlet.i18n.SessionLocaleResovler">`

不管是哪个，对于 LocaleResovler 的使用基础是在 DispatcherServlet 中的初始化

```java
	/**
	 * Initialize the LocaleResolver used by this class.
	 * <p>If no bean is defined with the given name in the BeanFactory for this namespace,
	 * we default to AcceptHeaderLocaleResolver.
	 */
	private void initLocaleResolver(ApplicationContext context) {
		try {
			this.localeResolver = context.getBean(LOCALE_RESOLVER_BEAN_NAME, LocaleResolver.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Detected " + this.localeResolver);
			}
			else if (logger.isDebugEnabled()) {
				logger.debug("Detected " + this.localeResolver.getClass().getSimpleName());
			}
		}
		catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default.
			this.localeResolver = getDefaultStrategy(context, LocaleResolver.class);
			if (logger.isTraceEnabled()) {
				logger.trace("No LocaleResolver '" + LOCALE_RESOLVER_BEAN_NAME +
						"': using default [" + this.localeResolver.getClass().getSimpleName() + "]");
			}
		}
	}
```

### 初始化 ThemeResolver

Spring 的主题解析器

配置

```xml
<!-- 主题资源 -->
<!-- 下面的类是 org.springframework.ui.context.support.ThemeSource 默认实现，Spring 需要通过这个来存在主题资源-->
<!-- DispatherServlet 会在 me.young1lin. 下找资源 -->
<bean id="themeSource" class="org.springframework.ui.context.support.ResourceBundleThemeSource">
	<property name="basenamePrefix" value="me.young1lin."></property>
</bean>
<!-- 主题解析器 -->
<!-- ThemeSource 定义了一些主题资源，那么不同的用户使用什么主题资源由谁定义，org.springframework.web.servlet.ThemeResolver 是主题解析器的接口，主题解析的工作便由他的子类完成 -->
<bean id="themeResolver" class="org.springframework.web.servlet.theme.FixedThemeResolver"></bean>
```

+ CookieThemeResolver  用于实现用户所选的主题
+ SessionThemeResolver  用于主题保存在用户的 HTTP Session 中
+ AbstracThemeResolver 被 SessionThemeResolver 和 FixedThemeResovler 继承，也可以用来自定义

```xml
<!-- 二选一 -->
<!-- CookieThemeResolver 配置 -->
<bean id="themeResolver" class="org.springframework.web.servlet.theme.CookieThemeResovler">
	<property name="defualtThemeName" value="summer"></property>
</bean>
<!-- SessionThemeResolver 配置 -->
<bean id="themeResolver" class="org.springframework.web.servlet.theme.SessionThemeResovler">
	<property name="defualtThemeName" value="summer"></property>
</bean>
```

主题拦截器，这里设置用户请求参数名为 themeName，即为 ?themeName= 具体的主题名称。此外还需要在 handlerMapping 中配置拦截器。

```xml
<bean id="themeChangeInterceptor" class="org.springframework.web.servlet.theme.ThemeChangeInterceptor">
	<property name="paramName" value="themeName"></property>
</bean>
```

### 初始化HandlerMappings

当客户端发出 Requet 时 DispatcherServlet 会将 Request 提交给 HandlerMapping ，然后 HandlerMapping 根据 Web Application Context 的配置来回传给 DispatcherServlet 相应的 Controller。

```java
// DispatcherServlet#initHandlerMappings
/**
 * Initialize the HandlerMappings used by this class.
 * <p>If no HandlerMapping beans are defined in the BeanFactory for this namespace,
 * we default to BeanNameUrlHandlerMapping.
 */
private void initHandlerMappings(ApplicationContext context) {
    this.handlerMappings = null;

    if (this.detectAllHandlerMappings) {
        // Find all HandlerMappings in the ApplicationContext, including ancestor contexts.
        Map<String, HandlerMapping> matchingBeans =
            BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
        if (!matchingBeans.isEmpty()) {
            this.handlerMappings = new ArrayList<>(matchingBeans.values());
            // We keep HandlerMappings in sorted order.
            AnnotationAwareOrderComparator.sort(this.handlerMappings);
        }
    }
    else {
        try {
            HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
            this.handlerMappings = Collections.singletonList(hm);
        }
        catch (NoSuchBeanDefinitionException ex) {
            // Ignore, we'll add a default HandlerMapping later.
        }
    }

    // Ensure we have at least one HandlerMapping, by registering
    // a default HandlerMapping if no other mappings are found.
    if (this.handlerMappings == null) {
        this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
        if (logger.isTraceEnabled()) {
            logger.trace("No HandlerMappings declared for servlet '" + getServletName() +
                         "': using default strategies from DispatcherServlet.properties");
        }
    }
}
```

### 初始化 HandlerAdapters

使用适配器，可以使接口不兼容而无法在一起工作的类协同工作。如果无法找到，系统会找到默认的

```java
/**
 * Initialize the HandlerAdapters used by this class.
 * <p>If no HandlerAdapter beans are defined in the BeanFactory for this namespace,
 * we default to SimpleControllerHandlerAdapter.
 */
private void initHandlerAdapters(ApplicationContext context) {
    this.handlerAdapters = null;

    if (this.detectAllHandlerAdapters) {
        // Find all HandlerAdapters in the ApplicationContext, including ancestor contexts.
        Map<String, HandlerAdapter> matchingBeans =
            BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerAdapter.class, true, false);
        if (!matchingBeans.isEmpty()) {
            this.handlerAdapters = new ArrayList<>(matchingBeans.values());
            // We keep HandlerAdapters in sorted order.
            AnnotationAwareOrderComparator.sort(this.handlerAdapters);
        }
    }
    else {
        try {
            HandlerAdapter ha = context.getBean(HANDLER_ADAPTER_BEAN_NAME, HandlerAdapter.class);
            this.handlerAdapters = Collections.singletonList(ha);
        }
        catch (NoSuchBeanDefinitionException ex) {
            // Ignore, we'll add a default HandlerAdapter later.
        }
    }

    // Ensure we have at least some HandlerAdapters, by registering
    // default HandlerAdapters if no other adapters are found.
    if (this.handlerAdapters == null) {
        // getDefaultStrategies 默认的适配器
        this.handlerAdapters = getDefaultStrategies(context, HandlerAdapter.class);
        if (logger.isTraceEnabled()) {
            logger.trace("No HandlerAdapters declared for servlet '" + getServletName() +
                         "': using default strategies from DispatcherServlet.properties");
        }
    }
}
```
获取默认的适配器

```java
/**
 * Create a List of default strategy objects for the given strategy interface.
 * <p>The default implementation uses the "DispatcherServlet.properties" file (in the same
 * package as the DispatcherServlet class) to determine the class names. It instantiates
 * the strategy objects through the context's BeanFactory.
 * @param context the current WebApplicationContext
 * @param strategyInterface the strategy interface
 * @return the List of corresponding strategy objects
 */
@SuppressWarnings("unchecked")
protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
    String key = strategyInterface.getName();
    String value = defaultStrategies.getProperty(key);
    if (value != null) {
        String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
        List<T> strategies = new ArrayList<>(classNames.length);
        for (String className : classNames) {
            try {
                Class<?> clazz = ClassUtils.forName(className, DispatcherServlet.class.getClassLoader());
                Object strategy = createDefaultStrategy(context, clazz);
                strategies.add((T) strategy);
            }
            catch (ClassNotFoundException ex) {
                throw new BeanInitializationException(
                    "Could not find DispatcherServlet's default strategy class [" + className +
                    "] for interface [" + key + "]", ex);
            }
            catch (LinkageError err) {
                throw new BeanInitializationException(
                    "Unresolvable class definition for DispatcherServlet's default strategy class [" +
                    className + "] for interface [" + key + "]", err);
            }
        }
        return strategies;
    }
    else {
        return new LinkedList<>();
    }
}
```

DispatcherServlet 中存在这样的初始化代码块。（将静态代码块和静态变量提升到代码最前面，其次是普通代码 -> 普通代码块 -> 静态方法 -> 普通方法，各个方法根据相关性决定排序）

```java
private static final String DEFAULT_STRATEGIES_PATH = "DispatcherServlet.properties";
static {
    // Load default strategy implementations from properties file.
    // This is currently strictly internal and not meant to be customized
    // by application developers.
    try {
        ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, DispatcherServlet.class);
        defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
    }
    catch (IOException ex) {
        throw new IllegalStateException("Could not load '" + DEFAULT_STRATEGIES_PATH + "': " + ex.getMessage());
    }
}
```

下面是当前路径的 DispatcherServlet.properties

作为总控制器的派遣器 servlet 通过处理器映射得到处理器后，会轮询处理器适配器模块，来选择某一个适当的处理器适配器的实现，从而使配当前的 Http 请求。

```properties
# Default implementation classes for DispatcherServlet's strategy interfaces.
# Used as fallback when no matching beans are found in the DispatcherServlet context.
# Not meant to be customized by application developers.

org.springframework.web.servlet.LocaleResolver=org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver

org.springframework.web.servlet.ThemeResolver=org.springframework.web.servlet.theme.FixedThemeResolver

org.springframework.web.servlet.HandlerMapping=org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping,\
	org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
# 如果没有自定义，那么 SpringMVC 会默认加载这三个适配器
org.springframework.web.servlet.HandlerAdapter=org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter,\
	org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter,\
	org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

org.springframework.web.servlet.HandlerExceptionResolver=org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver,\
	org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver,\
	org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver

org.springframework.web.servlet.RequestToViewNameTranslator=org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator

org.springframework.web.servlet.ViewResolver=org.springframework.web.servlet.view.InternalResourceViewResolver

org.springframework.web.servlet.FlashMapManager=org.springframework.web.servlet.support.SessionFlashMapManager
```

+ HttpRequestHandlerAdapter Http 请求处理器适配器

+ 简单控制器处理器适配器
+ 注解方法处理器适配器

### 初始化 HandlerExceptionResolvers

实现 HandlerExceptionResolver 后，resolverException 方法，返回一个 ModelAndView 对象，在方法内部对异常的类型进行判断，然后处理。

```java
	/**
	 * Initialize the HandlerExceptionResolver used by this class.
	 * <p>If no bean is defined with the given name in the BeanFactory for this namespace,
	 * we default to no exception resolver.
	 */
	private void initHandlerExceptionResolvers(ApplicationContext context) {
		this.handlerExceptionResolvers = null;

		if (this.detectAllHandlerExceptionResolvers) {
			// Find all HandlerExceptionResolvers in the ApplicationContext, including ancestor contexts.
			Map<String, HandlerExceptionResolver> matchingBeans = BeanFactoryUtils
					.beansOfTypeIncludingAncestors(context, HandlerExceptionResolver.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerExceptionResolvers = new ArrayList<>(matchingBeans.values());
				// We keep HandlerExceptionResolvers in sorted order.
				AnnotationAwareOrderComparator.sort(this.handlerExceptionResolvers);
			}
		}
		else {
			try {
				HandlerExceptionResolver her =
						context.getBean(HANDLER_EXCEPTION_RESOLVER_BEAN_NAME, HandlerExceptionResolver.class);
				this.handlerExceptionResolvers = Collections.singletonList(her);
			}
			catch (NoSuchBeanDefinitionException ex) {
				// Ignore, no HandlerExceptionResolver is fine too.
			}
		}

		// Ensure we have at least some HandlerExceptionResolvers, by registering
		// default HandlerExceptionResolvers if no other resolvers are found.
		if (this.handlerExceptionResolvers == null) {
			this.handlerExceptionResolvers = getDefaultStrategies(context, HandlerExceptionResolver.class);
			if (logger.isTraceEnabled()) {
				logger.trace("No HandlerExceptionResolvers declared in servlet '" + getServletName() +
						"': using default strategies from DispatcherServlet.properties");
			}
		}
	}
```

### 初始化 RequestToViewNameTranslator

当 Controller 处理器方法没有返回一个 View 对象或者逻辑视图名称，并且在该方法中没有直接往。response 的输出流李敏啊写数据的时候，Spring 就会采用约定好的方式提供一个逻辑视图名称。这个逻辑视图名称是通过 Spring 定义的接口 org.springframework.web.servlet.RequestToViewNameTranslator#getViewName 方法来实现的。它有个默认的实现，org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator。

+ prefix： 例如加上 /WEB-INF/ ， 默认为空
+ suffix： 例如加上 .jsp，默认为空
+ separator： 分隔符，默认为斜杠 "/"
+ stripLeadingSlash：如果首字符是分隔符，是否要去除，默认是 true
+ stripTrailingSlash：如果最后一个字符是分隔符，是否去除，默认是 true
+ stripExtension：如果请求路径包含扩展名是否要去除，默认是 true
+ urlDecode： 是否需要对 URL 解码，默认是true。它会采用 request 指定的编码或者 ISO-8859-1 编码对 URL 进行解码。

```java
/**
 * Initialize the RequestToViewNameTranslator used by this servlet instance.
 * <p>If no implementation is configured then we default to DefaultRequestToViewNameTranslator.
 */
private void initRequestToViewNameTranslator(ApplicationContext context) {
   try {
      this.viewNameTranslator =
            context.getBean(REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME, RequestToViewNameTranslator.class);
      if (logger.isTraceEnabled()) {
         logger.trace("Detected " + this.viewNameTranslator.getClass().getSimpleName());
      }
      else if (logger.isDebugEnabled()) {
         logger.debug("Detected " + this.viewNameTranslator);
      }
   }
   catch (NoSuchBeanDefinitionException ex) {
      // We need to use the default.
      this.viewNameTranslator = getDefaultStrategy(context, RequestToViewNameTranslator.class);
      if (logger.isTraceEnabled()) {
         logger.trace("No RequestToViewNameTranslator '" + REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME +
               "': using default [" + this.viewNameTranslator.getClass().getSimpleName() + "]");
      }
   }
}
```

## 初始化 ViewResolvers

当 Controller 将请求处理结果放入到 ModelAndView 中后，DispatcherServlet 会根据 ModelAndView 对象选择合适的视图进行渲染。

```xml
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
	<property name="prefix" value="/WEB-INF/views/"/>
    <property name="suffix" value=".jsp"/>
</bean>
```

```java
/**
 * Initialize the ViewResolvers used by this class.
 * <p>If no ViewResolver beans are defined in the BeanFactory for this
 * namespace, we default to InternalResourceViewResolver.
 */
private void initViewResolvers(ApplicationContext context) {
   this.viewResolvers = null;

   if (this.detectAllViewResolvers) {
      // Find all ViewResolvers in the ApplicationContext, including ancestor contexts.
      Map<String, ViewResolver> matchingBeans =
            BeanFactoryUtils.beansOfTypeIncludingAncestors(context, ViewResolver.class, true, false);
      if (!matchingBeans.isEmpty()) {
         this.viewResolvers = new ArrayList<>(matchingBeans.values());
         // We keep ViewResolvers in sorted order.
         AnnotationAwareOrderComparator.sort(this.viewResolvers);
      }
   }
   else {
      try {
         ViewResolver vr = context.getBean(VIEW_RESOLVER_BEAN_NAME, ViewResolver.class);
         this.viewResolvers = Collections.singletonList(vr);
      }
      catch (NoSuchBeanDefinitionException ex) {
         // Ignore, we'll add a default ViewResolver later.
      }
   }

   // Ensure we have at least one ViewResolver, by registering
   // a default ViewResolver if no other resolvers are found.
   if (this.viewResolvers == null) {
      this.viewResolvers = getDefaultStrategies(context, ViewResolver.class);
      if (logger.isTraceEnabled()) {
         logger.trace("No ViewResolvers declared for servlet '" + getServletName() +
               "': using default strategies from DispatcherServlet.properties");
      }
   }
}
```

## 初始化 FlashMapManager

SpringMVC Flash attributes 提供了一个请求存储属性，可供其他请求使用。

SpringMVC 有两个主要的抽象来支持 flash attributes。FlashMap 用于保持 flash attributes，而 FlashMapManager 用于存储、检索、管理 FlashMap 实例。

这两个 FlashMap 实例都可以通过静态方法 RequestContextUtils 从 SpringMVC 的任何位置访问。

```java
/**
 * Initialize the {@link FlashMapManager} used by this servlet instance.
 * <p>If no implementation is configured then we default to
 * {@code org.springframework.web.servlet.support.DefaultFlashMapManager}.
 */
private void initFlashMapManager(ApplicationContext context) {
   try {
      this.flashMapManager = context.getBean(FLASH_MAP_MANAGER_BEAN_NAME, FlashMapManager.class);
      if (logger.isTraceEnabled()) {
         logger.trace("Detected " + this.flashMapManager.getClass().getSimpleName());
      }
      else if (logger.isDebugEnabled()) {
         logger.debug("Detected " + this.flashMapManager);
      }
   }
   catch (NoSuchBeanDefinitionException ex) {
      // We need to use the default.
      this.flashMapManager = getDefaultStrategy(context, FlashMapManager.class);
      if (logger.isTraceEnabled()) {
         logger.trace("No FlashMapManager '" + FLASH_MAP_MANAGER_BEAN_NAME +
               "': using default [" + this.flashMapManager.getClass().getSimpleName() + "]");
      }
   }
}
```

# DispatcherServlet 的逻辑处理


FrameworkServlet#processRequest


```java
/**
 * Process this request, publishing an event regardless of the outcome.
 * <p>The actual event handling is performed by the abstract
 * {@link #doService} template method.
 */
protected final void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
	
   long startTime = System.currentTimeMillis();
   Throwable failureCause = null;
	//把 request 绑定到当前线程的 LocalContext 中
   LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
   LocaleContext localeContext = buildLocaleContext(request);
	//把 request 绑定到当前线程的 RequestAttributes 中
   RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
   ServletRequestAttributes requestAttributes = buildRequestAttributes(request, response, previousAttributes);

   WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
   asyncManager.registerCallableInterceptor(FrameworkServlet.class.getName(), new RequestBindingInterceptor());

   initContextHolders(request, localeContext, requestAttributes);

   try {
       // 这个方法是交给子类 DispatcherServlet 处理的
      doService(request, response);
   }
   catch (ServletException | IOException ex) {
      failureCause = ex;
      throw ex;
   }
   catch (Throwable ex) {
      failureCause = ex;
      throw new NestedServletException("Request processing failed", ex);
   }

   finally {
       // 请求结束后，恢复线程到原始状态
      resetContextHolders(request, previousLocaleContext, previousAttributes);
      if (requestAttributes != null) {
         requestAttributes.requestCompleted();
      }
      logResult(request, response, failureCause, asyncManager);
       // 请求处理结束后无论成功与否发布事件通知
      publishRequestHandledEvent(request, response, startTime, failureCause);
   }
}
```

DispatcherServlet#doService，这一步还是做一些准备工作，将已经初始化的功能辅助工具变量，比如 localeResolve，themeResovler 等设置在 request 属性中，下面会派上用场。

```java
/**
 * Exposes the DispatcherServlet-specific request attributes and delegates to {@link #doDispatch}
 * for the actual dispatching.
 */
@Override
protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
   logRequest(request);

   // Keep a snapshot of the request attributes in case of an include,
   // to be able to restore the original attributes after the include.
   Map<String, Object> attributesSnapshot = null;
   if (WebUtils.isIncludeRequest(request)) {
      attributesSnapshot = new HashMap<>();
      Enumeration<?> attrNames = request.getAttributeNames();
      while (attrNames.hasMoreElements()) {
         String attrName = (String) attrNames.nextElement();
         if (this.cleanupAfterInclude || attrName.startsWith(DEFAULT_STRATEGIES_PREFIX)) {
            attributesSnapshot.put(attrName, request.getAttribute(attrName));
         }
      }
   }

   // Make framework objects available to handlers and view objects.
   request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
   request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
   request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
   request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());

   if (this.flashMapManager != null) {
      FlashMap inputFlashMap = this.flashMapManager.retrieveAndUpdate(request, response);
      if (inputFlashMap != null) {
         request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, Collections.unmodifiableMap(inputFlashMap));
      }
      request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
      request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, this.flashMapManager);
   }

   try {
       // 上面几步都是在做准备工作，这一步才是真正干活的一步。
      doDispatch(request, response);
   }
   finally {
      if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
         // Restore the original attribute snapshot, in case of an include.
         if (attributesSnapshot != null) {
            restoreAttributesAfterInclude(request, attributesSnapshot);
         }
      }
   }
}
```

DispatcherServlet#doDispatch

```java
/**
 * Process the actual dispatching to the handler.
 * <p>The handler will be obtained by applying the servlet's HandlerMappings in order.
 * The HandlerAdapter will be obtained by querying the servlet's installed HandlerAdapters
 * to find the first that supports the handler class.
 * <p>All HTTP methods are handled by this method. It's up to HandlerAdapters or handlers
 * themselves to decide which methods are acceptable.
 * @param request current HTTP request
 * @param response current HTTP response
 * @throws Exception in case of any kind of processing failure
 */
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
   HttpServletRequest processedRequest = request;
   HandlerExecutionChain mappedHandler = null;
   boolean multipartRequestParsed = false;

   WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

   try {
      ModelAndView mv = null;
      Exception dispatchException = null;

      try {
         // 如果是 MutipartContent 类型的 request 则转换 request 为 MultipartHttpServletRequest 类型的 request
         processedRequest = checkMultipart(request);
         multipartRequestParsed = (processedRequest != request);
		 // 根据 request 信息寻找对应的 Handler
         // Determine handler for the current request.
         mappedHandler = getHandler(processedRequest);
         if (mappedHandler == null) {
            // 如果没有找到对应 handler 则通过 response 反馈错误信息
            noHandlerFound(processedRequest, response);
            return;
         }
		 // 根据当前 handler 寻找对应的 HandlerAdapter
         // Determine handler adapter for the current request.
         HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
		 // 如果当前 handler 支持 last-modified 头处理，也就是 304 
         // Process last-modified header, if supported by the handler.
         String method = request.getMethod();
         boolean isGet = "GET".equals(method);
         if (isGet || "HEAD".equals(method)) {
            long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
            if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
               return;
            }
         }
		 // interceptor 前置处理
         if (!mappedHandler.applyPreHandle(processedRequest, response)) {
            return;
         }
		 // 真正的激活 handler 并返回视图
         // Actually invoke the handler.
         mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
		
         if (asyncManager.isConcurrentHandlingStarted()) {
            return;
         }
         // 如果是视图的话，添加默认的视图的前缀和后缀
         applyDefaultViewName(processedRequest, mv);
         // interceptor 的后置处理
         mappedHandler.applyPostHandle(processedRequest, response, mv);
      }
      catch (Exception ex) {
         dispatchException = ex;
      }
      catch (Throwable err) {
         // As of 4.3, we're processing Errors thrown from handler methods as well,
         // making them available for @ExceptionHandler methods and other scenarios.
         dispatchException = new NestedServletException("Handler dispatch failed", err);
      }
      // 处理返回结果
      processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
   }
   catch (Exception ex) {
      // 完成处理激活触发器
      triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
   }
   catch (Throwable err) {
      triggerAfterCompletion(processedRequest, response, mappedHandler,
            new NestedServletException("Handler processing failed", err));
   }
   finally {
      if (asyncManager.isConcurrentHandlingStarted()) {
         // Instead of postHandle and afterCompletion
         if (mappedHandler != null) {
            // interceptor 完成 modelAndView 后的处理
            mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
         }
      }
      else {
         // Clean up any resources used by a multipart request.
         if (multipartRequestParsed) {
            cleanupMultipart(processedRequest);
         }
      }
   }
}
```

## MultipartContent 类型的 request 处理

如果是 MultipartContent 类型的 request 则转换 request 为 MultipartHttpServletRequest 类型的 request

```java
/**
 * Convert the request into a multipart request, and make multipart resolver available.
 * <p>If no multipart resolver is set, simply use the existing request.
 * @param request current HTTP request
 * @return the processed request (multipart wrapper if necessary)
 * @see MultipartResolver#resolveMultipart
 */
protected HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
   if (this.multipartResolver != null && this.multipartResolver.isMultipart(request)) {
      if (WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class) != null) {
         if (request.getDispatcherType().equals(DispatcherType.REQUEST)) {
            logger.trace("Request already resolved to MultipartHttpServletRequest, e.g. by MultipartFilter");
         }
      }
      else if (hasMultipartException(request)) {
         logger.debug("Multipart resolution previously failed for current request - " +
               "skipping re-resolution for undisturbed error rendering");
      }
      else {
         try {
            return this.multipartResolver.resolveMultipart(request);
         }
         catch (MultipartException ex) {
            if (request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) != null) {
               logger.debug("Multipart resolution failed for error dispatch", ex);
               // Keep processing error dispatch with regular request handle below
            }
            else {
               throw ex;
            }
         }
      }
   }
   // If not returned before: return original request.
   return request;
}
```

## 根据 request 信息寻找对应的 Handler

Spring 中最简单的映射处理器配置如下。Spring 会将类型为 SimpleUrlHandlerMapping 的实例加载到 this.handlerMappings 中。

```xml
<bean id="simpleUrlMapping" class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
	<property name="mappings">
    	<props>
        	<prop key="/userlist.html">
            	userController
            </prop>
        </props>
    </property>
</bean>
```
遍历所有的 HandlerMapping，并调用其 getHandler 方法进行封装处理。
```java
/**
 * Return the HandlerExecutionChain for this request.
 * <p>Tries all handler mappings in order.
 * @param request current HTTP request
 * @return the HandlerExecutionChain, or {@code null} if no handler could be found
 */
@Nullable
protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
   if (this.handlerMappings != null) {
      for (HandlerMapping mapping : this.handlerMappings) {
         HandlerExecutionChain handler = mapping.getHandler(request);
         if (handler != null) {
            return handler;
         }
      }
   }
   return null;
}
```

以 SimpleUrlHandlerMapping 为例，实际是它的父类 AbstractHandlerMapping 的 getHandler 方法

```java
/**
 * Look up a handler for the given request, falling back to the default
 * handler if no specific one is found.
 * @param request current HTTP request
 * @return the corresponding handler instance, or the default handler
 * @see #getHandlerInternal
 */
@Override
@Nullable
public final HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
   Object handler = getHandlerInternal(request);
   if (handler == null) {
       // 下个方法
      handler = getDefaultHandler();
   }
   if (handler == null) {
      return null;
   }
   // Bean name or resolved handler?
   if (handler instanceof String) {
      String handlerName = (String) handler;
      handler = obtainApplicationContext().getBean(handlerName);
   }
   // 下下下下个方法 #getHandlerExecutionChain
   HandlerExecutionChain executionChain = getHandlerExecutionChain(handler, request);

   if (logger.isTraceEnabled()) {
      logger.trace("Mapped to " + handler);
   }
   else if (logger.isDebugEnabled() && !request.getDispatcherType().equals(DispatcherType.ASYNC)) {
      logger.debug("Mapped to " + executionChain.getHandler());
   }

   if (hasCorsConfigurationSource(handler) || CorsUtils.isPreFlightRequest(request)) {
      CorsConfiguration config = (this.corsConfigurationSource != null ? this.corsConfigurationSource.getCorsConfiguration(request) : null);
      CorsConfiguration handlerConfig = getCorsConfiguration(handler, request);
      config = (config != null ? config.combine(handlerConfig) : handlerConfig);
      executionChain = getCorsHandlerExecutionChain(request, executionChain, config);
   }

   return executionChain;
}
```
其实是 SimpleUrlHandlerMapping 的父类 AbstractUrlHandlerMapping 重写的这个方法。根据 request 查找对应的 Handler
```java
/**
 * Look up a handler for the URL path of the given request.
 * @param request current HTTP request
 * @return the handler instance, or {@code null} if none found
 */
@Override
@Nullable
protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
   // 截取用于匹配的 url 有效路径
   String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
   request.setAttribute(LOOKUP_PATH, lookupPath);
   // 根据路径寻找 Handler 
   // 下一个方法 
   Object handler = lookupHandler(lookupPath, request);
   if (handler == null) {
      // We need to care for the default handler directly, since we need to
      // expose the PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE for it as well.
      Object rawHandler = null;
      if ("/".equals(lookupPath)) {
         rawHandler = getRootHandler();
      }
      if (rawHandler == null) {
         rawHandler = getDefaultHandler();
      }
      if (rawHandler != null) {
         // Bean name or resolved handler?
         if (rawHandler instanceof String) {
            String handlerName = (String) rawHandler;
            rawHandler = obtainApplicationContext().getBean(handlerName);
         }
         // 模板方法
         validateHandler(rawHandler, request);
         handler = buildPathExposingHandler(rawHandler, lookupPath, lookupPath, null);
      }
   }
   return handler;
}
```

```java
/**
 * Look up a handler instance for the given URL path.
 * <p>Supports direct matches, e.g. a registered "/test" matches "/test",
 * and various Ant-style pattern matches, e.g. a registered "/t*" matches
 * both "/test" and "/team". For details, see the AntPathMatcher class.
 * <p>Looks for the most exact pattern, where most exact is defined as
 * the longest path pattern.
 * @param urlPath the URL the bean is mapped to
 * @param request current HTTP request (to expose the path within the mapping to)
 * @return the associated handler instance, or {@code null} if not found
 * @see #exposePathWithinMapping
 * @see org.springframework.util.AntPathMatcher
 */
@Nullable
protected Object lookupHandler(String urlPath, HttpServletRequest request) throws Exception {
   // Direct match?
   // 直接匹配
   Object handler = this.handlerMap.get(urlPath);
   if (handler != null) {
      // Bean name or resolved handler?
      if (handler instanceof String) {
         String handlerName = (String) handler;
         handler = obtainApplicationContext().getBean(handlerName);
      }
      validateHandler(handler, request);
      return buildPathExposingHandler(handler, urlPath, urlPath, null);
   }

   // Pattern match?
   // 通配符匹配
   List<String> matchingPatterns = new ArrayList<>();
   for (String registeredPattern : this.handlerMap.keySet()) {
      if (getPathMatcher().match(registeredPattern, urlPath)) {
         matchingPatterns.add(registeredPattern);
      }
      else if (useTrailingSlashMatch()) {
         if (!registeredPattern.endsWith("/") && getPathMatcher().match(registeredPattern + "/", urlPath)) {
            matchingPatterns.add(registeredPattern + "/");
         }
      }
   }

   String bestMatch = null;
   Comparator<String> patternComparator = getPathMatcher().getPatternComparator(urlPath);
   if (!matchingPatterns.isEmpty()) {
      matchingPatterns.sort(patternComparator);
      if (logger.isTraceEnabled() && matchingPatterns.size() > 1) {
         logger.trace("Matching patterns " + matchingPatterns);
      }
      bestMatch = matchingPatterns.get(0);
   }
   if (bestMatch != null) {
      handler = this.handlerMap.get(bestMatch);
      if (handler == null) {
         if (bestMatch.endsWith("/")) {
            handler = this.handlerMap.get(bestMatch.substring(0, bestMatch.length() - 1));
         }
         if (handler == null) {
            throw new IllegalStateException(
                  "Could not find handler for best pattern match [" + bestMatch + "]");
         }
      }
      // Bean name or resolved handler?
      if (handler instanceof String) {
         String handlerName = (String) handler;
         handler = obtainApplicationContext().getBean(handlerName);
      }
      validateHandler(handler, request);
      String pathWithinMapping = getPathMatcher().extractPathWithinPattern(bestMatch, urlPath);

      // There might be multiple 'best patterns', let's make sure we have the correct URI template variables
      // for all of them
      Map<String, String> uriTemplateVariables = new LinkedHashMap<>();
      for (String matchingPattern : matchingPatterns) {
         if (patternComparator.compare(bestMatch, matchingPattern) == 0) {
            Map<String, String> vars = getPathMatcher().extractUriTemplateVariables(matchingPattern, urlPath);
            Map<String, String> decodedVars = getUrlPathHelper().decodePathVariables(request, vars);
            uriTemplateVariables.putAll(decodedVars);
         }
      }
      if (logger.isTraceEnabled() && uriTemplateVariables.size() > 0) {
         logger.trace("URI variables " + uriTemplateVariables);
      }
      // 下个方法
      return buildPathExposingHandler(handler, bestMatch, pathWithinMapping, uriTemplateVariables);
   }

   // No handler found...
   return null;
}
```
下面的函数将 Handler 封装成了 HandlerExecutionChain 类型，并且加入了两个拦截器。链处理机制，是 Spring 中非常常用的处理方式。这是 AOP 中重要组成部分，可以方便地对目标对象进行扩展及拦截，这是非常优秀的设计。
```java
/**
 * Build a handler object for the given raw handler, exposing the actual
 * handler, the {@link #PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE}, as well as
 * the {@link #URI_TEMPLATE_VARIABLES_ATTRIBUTE} before executing the handler.
 * <p>The default implementation builds a {@link HandlerExecutionChain}
 * with a special interceptor that exposes the path attribute and uri template variables
 * @param rawHandler the raw handler to expose
 * @param pathWithinMapping the path to expose before executing the handler
 * @param uriTemplateVariables the URI template variables, can be {@code null} if no variables found
 * @return the final handler object
 */
protected Object buildPathExposingHandler(Object rawHandler, String bestMatchingPattern,
      String pathWithinMapping, @Nullable Map<String, String> uriTemplateVariables) {

   HandlerExecutionChain chain = new HandlerExecutionChain(rawHandler);
   chain.addInterceptor(new PathExposingHandlerInterceptor(bestMatchingPattern, pathWithinMapping));
   if (!CollectionUtils.isEmpty(uriTemplateVariables)) {
      chain.addInterceptor(new UriTemplateVariablesHandlerInterceptor(uriTemplateVariables));
   }
   return chain;
}
```
加入拦截器到执行链，最主要的目的就是将配置中的对应拦截器加入到执行链中，以保证这些拦截器可以有效地作用于目标对象。


AbstractHandlerMapping


```java
/**
 * Build a {@link HandlerExecutionChain} for the given handler, including
 * applicable interceptors.
 * <p>The default implementation builds a standard {@link HandlerExecutionChain}
 * with the given handler, the handler mapping's common interceptors, and any
 * {@link MappedInterceptor MappedInterceptors} matching to the current request URL. Interceptors
 * are added in the order they were registered. Subclasses may override this
 * in order to extend/rearrange the list of interceptors.
 * <p><b>NOTE:</b> The passed-in handler object may be a raw handler or a
 * pre-built {@link HandlerExecutionChain}. This method should handle those
 * two cases explicitly, either building a new {@link HandlerExecutionChain}
 * or extending the existing chain.
 * <p>For simply adding an interceptor in a custom subclass, consider calling
 * {@code super.getHandlerExecutionChain(handler, request)} and invoking
 * {@link HandlerExecutionChain#addInterceptor} on the returned chain object.
 * @param handler the resolved handler instance (never {@code null})
 * @param request current HTTP request
 * @return the HandlerExecutionChain (never {@code null})
 * @see #getAdaptedInterceptors()
 */
protected HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {
   HandlerExecutionChain chain = (handler instanceof HandlerExecutionChain ?
         (HandlerExecutionChain) handler : new HandlerExecutionChain(handler));

   String lookupPath = this.urlPathHelper.getLookupPathForRequest(request, LOOKUP_PATH);
   for (HandlerInterceptor interceptor : this.adaptedInterceptors) {
      if (interceptor instanceof MappedInterceptor) {
         MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptor;
         if (mappedInterceptor.matches(lookupPath, this.pathMatcher)) {
            chain.addInterceptor(mappedInterceptor.getInterceptor());
         }
      }
      else {
         chain.addInterceptor(interceptor);
      }
   }
   return chain;
}
```

## 没找到对应的 Handler 的错误处理

每个请求都应该对应着以 Handler，一旦没有找到 Handler 的情况。DispatcherServlet#noHandlerFound

```java
/**
 * No handler found -> set appropriate HTTP response status.
 * @param request current HTTP request
 * @param response current HTTP response
 * @throws Exception if preparing the response failed
 */
protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
   if (pageNotFoundLogger.isWarnEnabled()) {
      pageNotFoundLogger.warn("No mapping for " + request.getMethod() + " " + getRequestUri(request));
   }
   if (this.throwExceptionIfNoHandlerFound) {
      throw new NoHandlerFoundException(request.getMethod(), getRequestUri(request),
            new ServletServerHttpRequest(request).getHeaders());
   }
   else {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
   }
}
```

## 根据当前的 Handler 寻找对应的 HandlerAdapter

后面还有个实现 LastModified 接口的，支持 Last-Modified 机制，返回 304 的

```java
/**
 * Return the HandlerAdapter for this handler object.
 * @param handler the handler object to find an adapter for
 * @throws ServletException if no HandlerAdapter can be found for the handler. This is a fatal error.
 */
protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
   if (this.handlerAdapters != null) {
      for (HandlerAdapter adapter : this.handlerAdapters) {
         // supports 是个策略方法，根据不同 adapter 适应不同的方法
         if (adapter.supports(handler)) {
            return adapter;
         }
      }
   }
   throw new ServletException("No adapter for handler [" + handler +
         "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
}
```

## HandlerInterceptor

已熟悉，并灵活运用

## 逻辑处理

// 已掌握

## 异常视图的处理



```java
/**
 * Determine an error ModelAndView via the registered HandlerExceptionResolvers.
 * @param request current HTTP request
 * @param response current HTTP response
 * @param handler the executed handler, or {@code null} if none chosen at the time of the exception
 * (for example, if multipart resolution failed)
 * @param ex the exception that got thrown during handler execution
 * @return a corresponding ModelAndView to forward to
 * @throws Exception if no error ModelAndView found
 */
@Nullable
protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,
      @Nullable Object handler, Exception ex) throws Exception {

   // Success and error responses may use different content types
   request.removeAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);

   // Check registered HandlerExceptionResolvers...
   ModelAndView exMv = null;
   if (this.handlerExceptionResolvers != null) {
      for (HandlerExceptionResolver resolver : this.handlerExceptionResolvers) {
         // 这里处理请求异常
         exMv = resolver.resolveException(request, response, handler, ex);
         if (exMv != null) {
            break;
         }
      }
   }
   if (exMv != null) {
      if (exMv.isEmpty()) {
         request.setAttribute(EXCEPTION_ATTRIBUTE, ex);
         return null;
      }
      // We might still need view name translation for a plain error model...
      if (!exMv.hasView()) {
         String defaultViewName = getDefaultViewName(request);
         if (defaultViewName != null) {
            exMv.setViewName(defaultViewName);
         }
      }
      if (logger.isTraceEnabled()) {
         logger.trace("Using resolved error view: " + exMv, ex);
      }
      else if (logger.isDebugEnabled()) {
         logger.debug("Using resolved error view: " + exMv);
      }
      WebUtils.exposeErrorRequestAttributes(request, ex, getServletName());
      return exMv;
   }

   throw ex;
}
```

## 根据视图跳转页面

**解析视图名称**

```java
/**
 * Render the given ModelAndView.
 * <p>This is the last stage in handling a request. It may involve resolving the view by name.
 * @param mv the ModelAndView to render
 * @param request current HTTP servlet request
 * @param response current HTTP servlet response
 * @throws ServletException if view is missing or cannot be resolved
 * @throws Exception if there's a problem rendering the view
 */
protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
   // Determine locale for request and apply it to the response.
   Locale locale =
         (this.localeResolver != null ? this.localeResolver.resolveLocale(request) : request.getLocale());
   response.setLocale(locale);

   View view;
   String viewName = mv.getViewName();
   if (viewName != null) {
      // We need to resolve the view name.解析视图名称
      view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
      if (view == null) {
         throw new ServletException("Could not resolve view with name '" + mv.getViewName() +
               "' in servlet with name '" + getServletName() + "'");
      }
   }
   else {
      // No need to lookup: the ModelAndView object contains the actual View object.
      view = mv.getView();
      if (view == null) {
         throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a " +
               "View object in servlet with name '" + getServletName() + "'");
      }
   }

   // Delegate to the View object for rendering.
   if (logger.isTraceEnabled()) {
      logger.trace("Rendering view [" + view + "] ");
   }
   try {
      if (mv.getStatus() != null) {
         response.setStatus(mv.getStatus().value());
      }
      view.render(mv.getModelInternal(), request, response);
   }
   catch (Exception ex) {
      if (logger.isDebugEnabled()) {
         logger.debug("Error rendering view [" + view + "]", ex);
      }
      throw ex;
   }
}
```

```java
/**
 * Resolve the given view name into a View object (to be rendered).
 * <p>The default implementations asks all ViewResolvers of this dispatcher.
 * Can be overridden for custom resolution strategies, potentially based on
 * specific model attributes or request parameters.
 * @param viewName the name of the view to resolve
 * @param model the model to be passed to the view
 * @param locale the current locale
 * @param request current HTTP servlet request
 * @return the View object, or {@code null} if none found
 * @throws Exception if the view cannot be resolved
 * (typically in case of problems creating an actual View object)
 * @see ViewResolver#resolveViewName
 */
@Nullable
protected View resolveViewName(String viewName, @Nullable Map<String, Object> model,
      Locale locale, HttpServletRequest request) throws Exception {

   if (this.viewResolvers != null) {
      for (ViewResolver viewResolver : this.viewResolvers) {
         View view = viewResolver.resolveViewName(viewName, locale);
         if (view != null) {
            return view;
         }
      }
   }
   return null;
}
```
AbstractCachingViewResolver

```java
@Override
@Nullable
public View resolveViewName(String viewName, Locale locale) throws Exception {
   if (!isCache()) {
      // 这个是 UrlBasedViewResolver 重写的方法
      return createView(viewName, locale);
   }
   else {
      Object cacheKey = getCacheKey(viewName, locale);
      View view = this.viewAccessCache.get(cacheKey);
      if (view == null) {
         synchronized (this.viewCreationCache) {
            view = this.viewCreationCache.get(cacheKey);
            if (view == null) {
               // Ask the subclass to create the View object.
               view = createView(viewName, locale);
               if (view == null && this.cacheUnresolved) {
                  view = UNRESOLVED_VIEW;
               }
               if (view != null && this.cacheFilter.filter(view, viewName, locale)) {
                  this.viewAccessCache.put(cacheKey, view);
                  this.viewCreationCache.put(cacheKey, view);
               }
            }
         }
      }
      else {
         if (logger.isTraceEnabled()) {
            logger.trace(formatKey(cacheKey) + "served from cache");
         }
      }
      return (view != UNRESOLVED_VIEW ? view : null);
   }
}
```

```java
/**
 * Overridden to implement check for "redirect:" prefix.
 * <p>Not possible in {@code loadView}, since overridden
 * {@code loadView} versions in subclasses might rely on the
 * superclass always creating instances of the required view class.
 * @see #loadView
 * @see #requiredViewClass
 */
@Override
protected View createView(String viewName, Locale locale) throws Exception {
   // If this resolver is not supposed to handle the given view,
   // return null to pass on to the next resolver in the chain.
   if (!canHandle(viewName, locale)) {
      return null;
   }

   // Check for special "redirect:" prefix.
   // 这个是 redirect 支持
   if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
      String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
      RedirectView view = new RedirectView(redirectUrl,
            isRedirectContextRelative(), isRedirectHttp10Compatible());
      String[] hosts = getRedirectHosts();
      if (hosts != null) {
         view.setHosts(hosts);
      }
      return applyLifecycleMethods(REDIRECT_URL_PREFIX, view);
   }

   // Check for special "forward:" prefix.
   // 这个是 froward:/user/list 这种支持
   if (viewName.startsWith(FORWARD_URL_PREFIX)) {
      String forwardUrl = viewName.substring(FORWARD_URL_PREFIX.length());
      InternalResourceView view = new InternalResourceView(forwardUrl);
      return applyLifecycleMethods(FORWARD_URL_PREFIX, view);
   }

   // Else fall back to superclass implementation: calling loadView.
   // 下面是 loadView 然后是 buildView，添加前缀和后缀
   return super.createView(viewName, locale);
}
```

对于 InternalResourceViewResolver 所提供的几个方面的处理

+ 基于效率考虑，提供了缓存支持
+ 提供了对 redirect:xx 和 forward:xx 前缀的支持
+ 添加了前缀及后缀，并向 View 中加入了必需的属性设置

**页面跳转**

当通过 viewName 解析到对应的 View 后，就可以进一步地处理跳转逻辑了。



```java
// 调用过程 
// DispatcherServlet#doDispatch
// DispatcherServlet#processDispatchResult
// DispatcherServlet#render
// 其中根据 viewName 循环遍历 viewResovler 获得对应的 viewResovler 然后对其进行返回。
// resovleViewName（解析视图名字）返回对应的 View 对象
// 然后执行对应的 View 的 render 方法
/**
 * Render the given ModelAndView.
 * <p>This is the last stage in handling a request. It may involve resolving the view by name.
 * @param mv the ModelAndView to render
 * @param request current HTTP servlet request
 * @param response current HTTP servlet response
 * @throws ServletException if view is missing or cannot be resolved
 * @throws Exception if there's a problem rendering the view
 */
protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
   // Determine locale for request and apply it to the response.
   Locale locale =
         (this.localeResolver != null ? this.localeResolver.resolveLocale(request) : request.getLocale());
   response.setLocale(locale);

   View view;
   String viewName = mv.getViewName();
   if (viewName != null) {
      // We need to resolve the view name.
      // 这一步就是来解析 ViewName，循环遍历获得对应的 View 对象
      view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
      if (view == null) {
         throw new ServletException("Could not resolve view with name '" + mv.getViewName() +
               "' in servlet with name '" + getServletName() + "'");
      }
   }
   else {
      // No need to lookup: the ModelAndView object contains the actual View object.
      view = mv.getView();
      if (view == null) {
         throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a " +
               "View object in servlet with name '" + getServletName() + "'");
      }
   }

   // Delegate to the View object for rendering.
   if (logger.isTraceEnabled()) {
      logger.trace("Rendering view [" + view + "] ");
   }
   try {
      if (mv.getStatus() != null) {
         response.setStatus(mv.getStatus().value());
      }
      // 调用对应类型 view 的 render 方法
      // 下面就是调用 AbstractView 实现的方法
      view.render(mv.getModelInternal(), request, response);
   }
   catch (Exception ex) {
      if (logger.isDebugEnabled()) {
         logger.debug("Error rendering view [" + view + "]", ex);
      }
      throw ex;
   }
}
```

  **下面就是调用 AbstractView 实现的方法**

```java
/**
 * Prepares the view given the specified model, merging it with static
 * attributes and a RequestContext attribute, if necessary.
 * Delegates to renderMergedOutputModel for the actual rendering.
 * @see #renderMergedOutputModel
 */
@Override
public void render(@Nullable Map<String, ?> model, HttpServletRequest request,
      HttpServletResponse response) throws Exception {

   if (logger.isDebugEnabled()) {
      logger.debug("View " + formatViewName() +
            ", model " + (model != null ? model : Collections.emptyMap()) +
            (this.staticAttributes.isEmpty() ? "" : ", static attributes " + this.staticAttributes));
   }
   // 								这一步把要用到的属性放入 request 中，然后可以使用 JSTL 语法。
   Map<String, Object> mergedModel = createMergedOutputModel(model, request, response);
   prepareResponse(request, response);
   // 
   renderMergedOutputModel(mergedModel, getRequestToExpose(request), response);
}
```

**AbastractView#createMergedOutputModel** 把要用到的属性塞到 request 中，解析这些属性

```java
/**
 * Creates a combined output Map (never {@code null}) that includes dynamic values and static attributes.
 * Dynamic values take precedence over static attributes.
 */
protected Map<String, Object> createMergedOutputModel(@Nullable Map<String, ?> model,
      HttpServletRequest request, HttpServletResponse response) {

   @SuppressWarnings("unchecked")
   Map<String, Object> pathVars = (this.exposePathVariables ?
         (Map<String, Object>) request.getAttribute(View.PATH_VARIABLES) : null);

   // Consolidate static and dynamic model attributes.
   int size = this.staticAttributes.size();
   size += (model != null ? model.size() : 0);
   size += (pathVars != null ? pathVars.size() : 0);

   Map<String, Object> mergedModel = new LinkedHashMap<>(size);
   mergedModel.putAll(this.staticAttributes);
   if (pathVars != null) {
      mergedModel.putAll(pathVars);
   }
   if (model != null) {
      mergedModel.putAll(model);
   }

   // Expose RequestContext?
   if (this.requestContextAttribute != null) {
      mergedModel.put(this.requestContextAttribute, createRequestContext(request, response, mergedModel));
   }

   return mergedModel;
}
```

AbstractView#renderMergedOutputModel 然后处理页面跳转，这个是交给子类来实现的

书上介绍的应该是 InternalResourceView#renderMergedOutputModel

```java
/**
 * Subclasses must implement this method to actually render the view.
 * <p>The first step will be preparing the request: In the JSP case,
 * this would mean setting model objects as request attributes.
 * The second step will be the actual rendering of the view,
 * for example including the JSP via a RequestDispatcher.
 * @param model combined output Map (never {@code null}),
 * with dynamic values taking precedence over static attributes
 * @param request current HTTP request
 * @param response current HTTP response
 * @throws Exception if rendering failed
 */

/**
 * Render the internal resource given the specified model.
 * This includes setting the model as request attributes.
 */
@Override
protected void renderMergedOutputModel(
    Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
	// 书上写的还有一步 getRequestToExpose，应该是在后面移除了 // TODO 把这个版本变迁找出来
    // Expose the model object as request attributes.
    exposeModelAsRequestAttributes(model, request);

    // Expose helpers as request attributes, if any.
    exposeHelpers(request);

    // Determine the path for the request dispatcher.
    String dispatcherPath = prepareForRendering(request, response);

    // Obtain a RequestDispatcher for the target resource (typically a JSP).
    RequestDispatcher rd = getRequestDispatcher(request, dispatcherPath);
    if (rd == null) {
        throw new ServletException("Could not get RequestDispatcher for [" + getUrl() +
                                   "]: Check that the corresponding file exists within your web application archive!");
    }

    // If already included or response already committed, perform include, else forward.
    if (useInclude(request, response)) {
        response.setContentType(getContentType());
        if (logger.isDebugEnabled()) {
            logger.debug("Including [" + getUrl() + "]");
        }
        rd.include(request, response);
    }

    else {
        // Note: The forwarded resource is supposed to determine the content type itself.
        if (logger.isDebugEnabled()) {
            logger.debug("Forwarding to [" + getUrl() + "]");
        }
        rd.forward(request, response);
    }
}

```

# RMI

## 服务端实现 RmiServiceExporter

```java
/**
 * 其中 RmiSerivceExporter 的顶级父类 RemotingSupport 实现了 BeanClassLoaderAware 来注入 ClassLoader
 * 重点是 InitializingBean，实现了这个接口，会在生命周期内调用 afterPropertiesSet 这个方法的实现，下面是完整的方法
 */
public class RmiServiceExporter extends RmiBasedExporter implements InitializingBean, DisposableBean {
    
	@Override
	public void afterPropertiesSet() throws RemoteException {
		prepare();
	}

	/**
	 * Initialize this service exporter, registering the service as RMI object.
	 * <p>Creates an RMI registry on the specified port if none exists.
	 * @throws RemoteException if service registration failed
	 */
	public void prepare() throws RemoteException {
        // 检查 service
		checkService();

		if (this.serviceName == null) {
			throw new IllegalArgumentException("Property 'serviceName' is required");
		}
		// 如果用户在配置文件中配置了 clientSocketFactory 或者 serverSocketFactory 的处理
        // 如果配置中的  clientSocketFactory 同时又实现了 RmiServerSocketFactory 接口那么会忽略配置中的
        // serverSocketFactory 而使用 clientSocketFactory 代替
        // 下面的情况类似，都是这么判断的
        // 我觉得这些检查并替换属性的，完全可以放在一个方法里面
		// Check socket factories for exported object.
		if (this.clientSocketFactory instanceof RMIServerSocketFactory) {
			this.serverSocketFactory = (RMIServerSocketFactory) this.clientSocketFactory;
		}
        // clientSocketFactory 和 serverSocketFactory 要么同时出现，要么都不出现
		if ((this.clientSocketFactory != null && this.serverSocketFactory == null) ||
				(this.clientSocketFactory == null && this.serverSocketFactory != null)) {
			throw new IllegalArgumentException(
					"Both RMIClientSocketFactory and RMIServerSocketFactory or none required");
		}

		// Check socket factories for RMI registry.
		if (this.registryClientSocketFactory instanceof RMIServerSocketFactory) {
			this.registryServerSocketFactory = (RMIServerSocketFactory) this.registryClientSocketFactory;
		}
		if (this.registryClientSocketFactory == null && this.registryServerSocketFactory != null) {
			throw new IllegalArgumentException(
					"RMIServerSocketFactory without RMIClientSocketFactory for registry not supported");
		}
		// 到这里，完全可以塞进一个方法里面	
		this.createdRegistry = false;

		// Determine RMI registry to use.
		if (this.registry == null) {
            // 下面的方法
			this.registry = getRegistry(this.registryHost, this.registryPort,
				this.registryClientSocketFactory, this.registryServerSocketFactory);
			this.createdRegistry = true;
		}
		// 初始化以及缓存导出的 Object
        // 此时通常情况是使用 RmiInvocationWrapper 封装的 JDK 代理类，切面为 RemoteInvocationTraceInterceptor
		// Initialize and cache exported object.
        // 下面下面的方法
		this.exportedObject = getObjectToExport();

		if (logger.isDebugEnabled()) {
			logger.debug("Binding service '" + this.serviceName + "' to RMI registry: " + this.registry);
		}

		// Export RMI object.
		if (this.clientSocketFactory != null) {
			UnicastRemoteObject.exportObject(
					this.exportedObject, this.servicePort, this.clientSocketFactory, this.serverSocketFactory);
		}
		else {
			UnicastRemoteObject.exportObject(this.exportedObject, this.servicePort);
		}

		// Bind RMI object to registry.
		try {
			if (this.replaceExistingBinding) {
				this.registry.rebind(this.serviceName, this.exportedObject);
			}
			else {
				this.registry.bind(this.serviceName, this.exportedObject);
			}
		}
		catch (AlreadyBoundException ex) {
			// Already an RMI object bound for the specified service name...
			unexportObjectSilently();
			throw new IllegalStateException(
					"Already an RMI object bound for name '"  + this.serviceName + "': " + ex.toString());
		}
		catch (RemoteException ex) {
			// Registry binding failed: let's unexport the RMI object as well.
			unexportObjectSilently();
			throw ex;
		}
	}
}
```

### 获取 registry

使用 LocalRegistry.getRegistry(registryHost,registryPort,clientSocketFactory) 就可以创建 Registry 实例了。

**RmiServiceExporter#getRegistry**

```java
/**
 * Locate or create the RMI registry for this exporter.
 * @param registryHost the registry host to use (if this is specified,
 * no implicit creation of a RMI registry will happen)
 * @param registryPort the registry port to use
 * @param clientSocketFactory the RMI client socket factory for the registry (if any)
 * @param serverSocketFactory the RMI server socket factory for the registry (if any)
 * @return the RMI registry
 * @throws RemoteException if the registry couldn't be located or created
 */
protected Registry getRegistry(String registryHost, int registryPort,
      @Nullable RMIClientSocketFactory clientSocketFactory, @Nullable RMIServerSocketFactory serverSocketFactory)
      throws RemoteException {

   if (registryHost != null) {
      // Host explicitly specified: only lookup possible.
      if (logger.isDebugEnabled()) {
         logger.debug("Looking for RMI registry at port '" + registryPort + "' of host [" + registryHost + "]");
      }
      // 这里是如果有 host，那就获取远程的，没有就调用下面的获取本地的
      Registry reg = LocateRegistry.getRegistry(registryHost, registryPort, clientSocketFactory);
      testRegistry(reg);
      return reg;
   }

   else {
      return getRegistry(registryPort, clientSocketFactory, serverSocketFactory);
   }
}
```

下面的 getRegistry 很简单，都是调用 rt.jar 下的 java.rmi.registry.LocalRegistry 的静态工厂方法。

**RmiServiceExporter#getObjectToExport**

```java
/**
 * Determine the object to export: either the service object itself
 * or a RmiInvocationWrapper in case of a non-RMI service object.
 * @return the RMI object to export
 * @see #setService
 * @see #setServiceInterface
 */
protected Remote getObjectToExport() {
   // determine remote object
   if (getService() instanceof Remote &&
         (getServiceInterface() == null || Remote.class.isAssignableFrom(getServiceInterface()))) {
      // conventional RMI service
      return (Remote) getService();
   }
   else {
      // RMI invoker
      if (logger.isDebugEnabled()) {
         logger.debug("RMI service [" + getService() + "] is an RMI invoker");
      }
      // 锚点 
      return new RmiInvocationWrapper(getProxyForService(), this);
   }
}
```

### 初始化将要导出的对象

**RemoteExporter#getProxyForService**

```java
/**
 * Get a proxy for the given service object, implementing the specified
 * service interface.
 * <p>Used to export a proxy that does not expose any internals but just
 * a specific interface intended for remote access. Furthermore, a
 * {@link RemoteInvocationTraceInterceptor} will be registered (by default).
 * @return the proxy
 * @see #setServiceInterface
 * @see #setRegisterTraceInterceptor
 * @see RemoteInvocationTraceInterceptor
 */
protected Object getProxyForService() {
   checkService();
   checkServiceInterface();
   // 使用 JDK 的方式创建代理
   ProxyFactory proxyFactory = new ProxyFactory();
   // 添加代理接口
   proxyFactory.addInterface(getServiceInterface());
   if (this.registerTraceInterceptor != null ? this.registerTraceInterceptor : this.interceptors == null) {
      // 加入代理的横切面 RemoteInvocationTraceInterceptor 并记录 Exporter 名称
      proxyFactory.addAdvice(new RemoteInvocationTraceInterceptor(getExporterName()));
   }
   if (this.interceptors != null) {
      AdvisorAdapterRegistry adapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
      for (Object interceptor : this.interceptors) {
         proxyFactory.addAdvisor(adapterRegistry.wrap(interceptor));
      }
   }
   // 设置要代理的目标类
   proxyFactory.setTarget(getService());
   proxyFactory.setOpaque(true);
   // 创建代理
   return proxyFactory.getProxy(getBeanClassLoader());
}
```

### RMI 服务激活调用

bean 初始化的时候做了服务名称绑定 this.registry.bind(this.serviceName.this.exportedObject)，其中的 exportedObject 其实是被 RMIInvocationWrapper 进行过封装的，也就是说当其他服务器调用 serviceName 的 RMI 服务时， Java 会为我们封装其内部操作，而直接会将代码转向 RMIInvocationWrapper 的 invoke 方法中。

**RMIInvocationWrapper#invoke**

```java
/**
 * Delegates the actual invocation handling to the RMI exporter.
 * @see RmiBasedExporter#invoke(org.springframework.remoting.support.RemoteInvocation, Object)
 */
@Override
@Nullable
public Object invoke(RemoteInvocation invocation)
   throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

   return this.rmiExporter.invoke(invocation, this.wrappedObject);
}
```

**RmiBasedExported#invoke**

```
/**
 * Redefined here to be visible to RmiInvocationWrapper.
 * Simply delegates to the corresponding superclass method.
 */
@Override
protected Object invoke(RemoteInvocation invocation, Object targetObject)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

   return super.invoke(invocation, targetObject);
}
```

**RemoteInvocationBasedExported**

```java
/**
 * Apply the given remote invocation to the given target object.
 * The default implementation delegates to the RemoteInvocationExecutor.
 * <p>Can be overridden in subclasses for custom invocation behavior,
 * possibly for applying additional invocation parameters from a
 * custom RemoteInvocation subclass. Note that it is preferable to use
 * a custom RemoteInvocationExecutor which is a reusable strategy.
 * @param invocation the remote invocation
 * @param targetObject the target object to apply the invocation to
 * @return the invocation result
 * @throws NoSuchMethodException if the method name could not be resolved
 * @throws IllegalAccessException if the method could not be accessed
 * @throws InvocationTargetException if the method invocation resulted in an exception
 * @see RemoteInvocationExecutor#invoke
 */
protected Object invoke(RemoteInvocation invocation, Object targetObject)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

   if (logger.isTraceEnabled()) {
      logger.trace("Executing " + invocation);
   }
   try {
   		// 下面的方法
      return getRemoteInvocationExecutor().invoke(invocation, targetObject);
   }
   catch (NoSuchMethodException ex) {
      if (logger.isDebugEnabled()) {
         logger.debug("Could not find target method for " + invocation, ex);
      }
      throw ex;
   }
   catch (IllegalAccessException ex) {
      if (logger.isDebugEnabled()) {
         logger.debug("Could not access target method for " + invocation, ex);
      }
      throw ex;
   }
   catch (InvocationTargetException ex) {
      if (logger.isDebugEnabled()) {
         logger.debug("Target method failed for " + invocation, ex.getTargetException());
      }
      throw ex;
   }
}
```
**DefaultRemoteInvocationExecutor#invoke**

```java
/**
 * Default implementation of the {@link RemoteInvocationExecutor} interface.
 * Simply delegates to {@link RemoteInvocation}'s invoke method.
 *
 * @author Juergen Hoeller
 * @since 1.1
 * @see RemoteInvocation#invoke
 */
public class DefaultRemoteInvocationExecutor implements RemoteInvocationExecutor {

   @Override
   public Object invoke(RemoteInvocation invocation, Object targetObject)
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{

      Assert.notNull(invocation, "RemoteInvocation must not be null");
      Assert.notNull(targetObject, "Target object must not be null");
      return invocation.invoke(targetObject);
   }
    
}
```

## 客户端实现 

当获取 Bean 时，首先通过 afterPropertiesSet 创建代理类，并使用当前类作为增强方法，而在调用该 bean 时其实返回的是代理类。

RmiProxyFactoryBean 它的父类 RmiClientInterceptor 的父类 RemoteInvocationBasedAccessor 的父类 UrlBasedRemoteAccessor 实现了 InitializingBean 接口，也就是下面的 afterPropertiesSet 方法的来源，也在 Bean 的生命周期内。

```java
public class RmiProxyFactoryBean extends RmiClientInterceptor implements FactoryBean<Object>, BeanClassLoaderAware {

   private Object serviceProxy;


   @Override
   public void afterPropertiesSet() {
      super.afterPropertiesSet();
      Class<?> ifc = getServiceInterface();
      Assert.notNull(ifc, "Property 'serviceInterface' is required");
      this.serviceProxy = new ProxyFactory(ifc, this).getProxy(getBeanClassLoader());
   }


   @Override
   public Object getObject() {
      return this.serviceProxy;
   }

   @Override
   public Class<?> getObjectType() {
      return getServiceInterface();
   }

   @Override
   public boolean isSingleton() {
      return true;
   }

}
```

**UrlBasedRemoteAccessor#afterPropertiesSet**

```java
@Override
public void afterPropertiesSet() {
   if (getServiceUrl() == null) {
      throw new IllegalArgumentException("Property 'serviceUrl' is required");
   }
}
```

**RmiClientInterceptor#afterPropertiesSet**

```java
@Override
public void afterPropertiesSet() {
   super.afterPropertiesSet();
   prepare();
}
```

RmiClientInterceptor#prepare

```java
/**
 * Fetches RMI stub on startup, if necessary.
 * @throws RemoteLookupFailureException if RMI stub creation failed
 * @see #setLookupStubOnStartup
 * @see #lookupStub
 */
public void prepare() throws RemoteLookupFailureException {
   // Cache RMI stub on initialization?
   // 如果配置了 lookupStubOnStartup 属性便会在启动时寻找 stub
   if (this.lookupStubOnStartup) {
      Remote remoteObj = lookupStub();
      if (logger.isDebugEnabled()) {
         if (remoteObj instanceof RmiInvocationHandler) {
            logger.debug("RMI stub [" + getServiceUrl() + "] is an RMI invoker");
         }
         else if (getServiceInterface() != null) {
            boolean isImpl = getServiceInterface().isInstance(remoteObj);
            logger.debug("Using service interface [" + getServiceInterface().getName() +
               "] for RMI stub [" + getServiceUrl() + "] - " +
               (!isImpl ? "not " : "") + "directly implemented");
         }
      }
      if (this.cacheStub) {
         this.cachedStub = remoteObj;
      }
   }
}
```

获取 stub 的动作就会在系统启动时被执行缓存，从而降低使用时后的响应时间。

可以用两种方式获取

+ 使用自定义的套接字工厂。如果使用这种方式，需要在构造 Registry 实例时将自定义套接字工厂传入，并使用 Registry 中提供的 lookup 方法来获取对应 stub。
+ 直接使用 RMI 提供的标准方法：Naming#lookup(getServiceUrl())

RmiClinentInterceptor#lookupStub

```java
protected Remote lookupStub() throws RemoteLookupFailureException {
   try {
      Remote stub = null;
      if (this.registryClientSocketFactory != null) {
         // RMIClientSocketFactory specified for registry access.
         // Unfortunately, due to RMI API limitations, this means
         // that we need to parse the RMI URL ourselves and perform
         // straight LocateRegistry.getRegistry/Registry.lookup calls.
         URL url = new URL(null, getServiceUrl(), new DummyURLStreamHandler());
         String protocol = url.getProtocol();
         if (protocol != null && !"rmi".equals(protocol)) {
            throw new MalformedURLException("Invalid URL scheme '" + protocol + "'");
         }
         String host = url.getHost();
         int port = url.getPort();
         String name = url.getPath();
         if (name != null && name.startsWith("/")) {
            name = name.substring(1);
         }
         Registry registry = LocateRegistry.getRegistry(host, port, this.registryClientSocketFactory);
         stub = registry.lookup(name);
      }
      else {
         // Can proceed with standard RMI lookup API...
         stub = Naming.lookup(getServiceUrl());
      }
      if (logger.isDebugEnabled()) {
         logger.debug("Located RMI stub with URL [" + getServiceUrl() + "]");
      }
      return stub;
   }
   catch (MalformedURLException ex) {
      throw new RemoteLookupFailureException("Service URL [" + getServiceUrl() + "] is invalid", ex);
   }
   catch (NotBoundException ex) {
      throw new RemoteLookupFailureException(
            "Could not find RMI service [" + getServiceUrl() + "] in RMI registry", ex);
   }
   catch (RemoteException ex) {
      throw new RemoteLookupFailureException("Lookup of RMI stub failed", ex);
   }
}
```

2. **增强器进行远程连接**

**RmiClientInterceptor#invoke。**RmiClientInterceptor 是 RmiProxyFactoryBean 的父类。

```java
@Override
public Object invoke(MethodInvocation invocation) throws Throwable {
   Remote stub = getStub();
   try {
      return doInvoke(invocation, stub);
   }
   catch (RemoteConnectFailureException ex) {
      return handleRemoteConnectFailure(invocation, ex);
   }
   catch (RemoteException ex) {
      if (isConnectFailure(ex)) {
         return handleRemoteConnectFailure(invocation, ex);
      }
      else {
         throw ex;
      }
   }
}
```

当客户端使用接口进行方法调用时是通过 Rmi 获取 stub 的，然后再通过 stub 中封装的信息进行服务器的调用工，这个 stub 就是在构建服务器时发布的对象。

**RimClientInterceptor#getStub**

```java
protected Remote getStub() throws RemoteLookupFailureException {
    // 有缓存直接使用缓存
   if (!this.cacheStub || (this.lookupStubOnStartup && !this.refreshStubOnConnectFailure)) {
      return (this.cachedStub != null ? this.cachedStub : lookupStub());
   }
   else {
      synchronized (this.stubMonitor) {
         if (this.cachedStub == null) {
            this.cachedStub = lookupStub();
         }
         return this.cachedStub;
      }
   }
}
```

Spring 中对于远程方法的调用是分两种情况考虑的。

+ 获取的 stub 是 RmiInvocationHandler 类型的，从服务端获取的 stub 是 RmiInvocationHandler，证明服务端也是用 Spring 构建的，就会走 Spring 中的约定。Spring 中的处理方式被委托给了 doInvoke 方法。
+ 获取的 stub 不是上面的类型的。被认为是非 Spring 构建的，按照普通方式处理，反射来做。如下

**RmiClientInterceptor#doInvoke**

```java
@Nullable
protected Object doInvoke(MethodInvocation invocation, Remote stub) throws Throwable {
   if (stub instanceof RmiInvocationHandler) {
      // RMI invoker
      try {
         return doInvoke(invocation, (RmiInvocationHandler) stub);
      }
      catch (RemoteException ex) {
         throw RmiClientInterceptorUtils.convertRmiAccessException(
            invocation.getMethod(), ex, isConnectFailure(ex), getServiceUrl());
      }
      catch (InvocationTargetException ex) {
         Throwable exToThrow = ex.getTargetException();
         RemoteInvocationUtils.fillInClientStackTraceIfPossible(exToThrow);
         throw exToThrow;
      }
      catch (Throwable ex) {
         throw new RemoteInvocationFailureException("Invocation of method [" + invocation.getMethod() +
               "] failed in RMI service [" + getServiceUrl() + "]", ex);
      }
   }
   else {
      // traditional RMI stub 直接用反射，传统的 RMI stub
      try {
         return RmiClientInterceptorUtils.invokeRemoteMethod(invocation, stub);
      }
      catch (InvocationTargetException ex) {
         Throwable targetEx = ex.getTargetException();
         if (targetEx instanceof RemoteException) {
            RemoteException rex = (RemoteException) targetEx;
            throw RmiClientInterceptorUtils.convertRmiAccessException(
                  invocation.getMethod(), rex, isConnectFailure(rex), getServiceUrl());
         }
         else {
            throw targetEx;
         }
      }
   }
}
```

**RmiClientInterceptor#doInvoke**

```java
@Nullable
protected Object doInvoke(MethodInvocation methodInvocation, RmiInvocationHandler invocationHandler)
   throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

   if (AopUtils.isToStringMethod(methodInvocation.getMethod())) {
      return "RMI invoker proxy for service URL [" + getServiceUrl() + "]";
   }

   return invocationHandler.invoke(createRemoteInvocation(methodInvocation));
}
```

## HttpInvoker

和 Rmi 类似，不再赘述

# Spring 消息

JMS Java Message Service 应用程序接口是一个 Java 平台中关于面向消息中间件（MOM）的 API，用于在两个应用程序之间或分布式系统中发送消息，进行异步通信。Java 消息服务是一个与具体平台无关的 API，绝大多数 MOM 提供供商都对 JMS 提供支持。

+ 点对点
+ 发布/订阅

## Spring 整合 JMS

Spring 对一些模版方法进行了一些封装。

1. Spring 配置文件

```xml
<beans>
    <bean id="connectionFactory" class="org.apache,.activemq.ActiveMQConnectionFactory">
    	<property name="brokerURL">
            <value>tcp://localhost:61616</value>
        </property>
    </bean>
    
    <bean id="jmsTemplate" class="org.springframework.jms.core.JmsTemplate">
    	<propety name="connectionFactory">
        	<ref bean="connectionFactory"/>
        </propety>
    </bean>
    
    <bean id="detination" class="org.apache.activemq.command.ActiveMQQueue">
    	<constructor-arg index="0">
        	<value>HelloWorldQueue</value>
        </constructor-arg>
    </bean>
</beans>
```

2. 发送端

模版方法，利用 JmsTemplate 进行发送和接受消息。

## JmsTemplate

1. **通用代码抽取**

**JmsTemplate#send**

```java
@Override
public void send(final Destination destination, final MessageCreator messageCreator) throws JmsException {
   execute(session -> {
      doSend(session, destination, messageCreator);
      return null;
   }, false);
}
```
**JmsTemplate#execute**

```java
@Nullable
public <T> T execute(SessionCallback<T> action, boolean startConnection) throws JmsException {
   Assert.notNull(action, "Callback object must not be null");
   Connection conToClose = null;
   Session sessionToClose = null;
   try {
      Session sessionToUse = ConnectionFactoryUtils.doGetTransactionalSession(
            obtainConnectionFactory(), this.transactionalResourceFactory, startConnection);
      if (sessionToUse == null) {
         conToClose = createConnection();
         sessionToClose = createSession(conToClose);
         if (startConnection) {
            conToClose.start();
         }
         sessionToUse = sessionToClose;
      }
      if (logger.isDebugEnabled()) {
         logger.debug("Executing callback on JMS Session: " + sessionToUse);
      }
      return action.doInJms(sessionToUse);
   }
   catch (JMSException ex) {
      throw convertJmsAccessException(ex);
   }
   finally {
      JmsUtils.closeSession(sessionToClose);
      ConnectionFactoryUtils.releaseConnection(conToClose, getConnectionFactory(), startConnection);
   }
}
```

2. 发送消息的实现

基于回调的方式，一步步封装了细节。

```java
protected void doSend(Session session, Destination destination, MessageCreator messageCreator)
      throws JMSException {

   Assert.notNull(messageCreator, "MessageCreator must not be null");
   MessageProducer producer = createProducer(session, destination);
   try {
      Message message = messageCreator.createMessage(session);
      if (logger.isDebugEnabled()) {
         logger.debug("Sending created message: " + message);
      }
      doSend(producer, message);
      // Check commit - avoid commit call within a JTA transaction.
      if (session.getTransacted() && isSessionLocallyTransacted(session)) {
         // Transacted session created by this template -> commit.
         JmsUtils.commitIfNecessary(session);
      }
   }
   finally {
      JmsUtils.closeMessageProducer(producer);
   }
}
```

3. 接收消息

和上面差不多

## 监听器容器

