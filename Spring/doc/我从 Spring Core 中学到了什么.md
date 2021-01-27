# 代码书写

让别人看代码，像看报纸一样，流畅一些。

## 类属性、方法顺序

+ public 方法早出现（可选，最好是同一访问权限的类变量和方法就近原则）。
+ 应用范围比较广的类属性，标明在哪使用。
+ static 变量和 static 代码块早出现于普通变量。
+ 代码块/方法和变量之间隔两行书写，方法和方法，属性和属性隔一行。
+ 不太复杂的类属性，单行注释即可。
+ 可能并且允许为空的对象，用 @Nullable 标记。
+ 构造方法优先于普通方法出现在变量下面。
+ 无参构造方法早出现于有参构造法。
+ 非 static 修饰的变量，不应直接引用。
+ 在不确定对象是否为空的情况下，尽量兜底。
+ final 并且未被 static 修饰的变量，可以不进行检查，通过方法调用，而不是直接使用变量。
+ 方法就近原则，方法内调用当前类其他方法时，应当在调用下面，越早调用，越早出现。
+ 方法内写代码时，一段语义可以适当与另一段语义空一行。
+ 方法与类之间留一行空格

**AbstractApplicationContext**

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader
		implements ConfigurableApplicationContext {

    /**
 	 * public 方法早出现（可选，最好是同一访问权限的类变量和方法就近原则）。
 	 * 应用范围比较广的类属性，标明在哪使用。
 	 * Name of the MessageSource bean in the factory.
 	 * If none is supplied, message resolution is delegated to the parent.
 	 * @see MessageSource
 	 */
    public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";
    
    
    //static 变量和 static 代码块早出现于普通变量。
    static {
       ContextClosedEvent.class.getName();
    }
    
    
    // 代码块/方法和变量之间隔两行书写，方法和方法，属性和属性隔一行。
    // 不太复杂的类属性，单行注释即可。
    /** Logger used by this class. Available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());
    
    /** Unique id for this context, if any. */
	private String id = ObjectUtils.identityToString(this);
    
    // 可能并且允许为空的对象，用 @Nullable 标记。
    @Nullable
	private ConfigurableEnvironment environment;

    
    // 构造方法优先于普通方法出现在变量下面。
	public AbstractApplicationContext() {
		this.resourcePatternResolver = getResourcePatternResolver();
	}
    
    // 无参构造方法早出现于有参构造法。
    public AbstractApplicationContext(@Nullable ApplicationContext parent) {
		this();
		setParent(parent);
	}

    /**
	 * 非 static 修饰的变量，不应直接引用。
	 * 在不确定对象是否为空的情况下，尽量兜底
	 */
	@Override
	public ConfigurableEnvironment getEnvironment() {
		if (this.environment == null) {
			this.environment = createEnvironment();
		}
		return this.environment;
	}
	
    // final 并且未被 static 修饰的变量，可以不进行检查，通过方法调用，而不是直接使用变量。
	public Collection<ApplicationListener<?>> getApplicationListeners() {
		return this.applicationListeners;
	}
    
    // 方法就近原则，方法内调用当前类其他方法时，应当在调用下面，越早调用，越早出现。
	@Override
	public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			// Prepare this context for refreshing.
			prepareRefresh();
            // 省略一大段代码
		}
	}

	protected void prepareRefresh() {
        // 方法内写代码时，一段语义可以适当与另一段语义空一行。
		// Switch to active.
		this.startupDate = System.currentTimeMillis();
		this.closed.set(false);
		this.active.set(true);

		if (logger.isDebugEnabled()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Refreshing " + this);
			}
			else {
				logger.debug("Refreshing " + getDisplayName());
			}
		}

		// Initialize any placeholder property sources in the context environment.
		initPropertySources();

		// Validate that all properties marked as required are resolvable:
		// see ConfigurablePropertyResolver#setRequiredProperties
		getEnvironment().validateRequiredProperties();

		// Store pre-refresh ApplicationListeners...
		if (this.earlyApplicationListeners == null) {
			this.earlyApplicationListeners = new LinkedHashSet<>(this.applicationListeners);
		}
		else {
			// Reset local application listeners to pre-refresh state.
			this.applicationListeners.clear();
			this.applicationListeners.addAll(this.earlyApplicationListeners);
		}

		// Allow for the collection of early ApplicationEvents,
		// to be published once the multicaster is available...
		this.earlyApplicationEvents = new LinkedHashSet<>();
	}        // 方法与类之间留一行空格
    
}
```

## 将复杂的代码块拆分成多个小的私有方法

具体见各个版本的方法。并且私有方法，不要有注释。

例如 **ClassPathScanningCandidateComponentProvider#findCandidateComponents** 在 3.0 版本，这个方法又臭又长。

```java
/**
 * Scan the class path for candidate components.
 * @param basePackage the package to check for annotated classes
 * @return a corresponding Set of autodetected bean definitions
 */
public Set<BeanDefinition> findCandidateComponents(String basePackage) {
   if (this.componentsIndex != null && indexSupportsIncludeFilters()) {
      return addCandidateComponentsFromIndex(this.componentsIndex, basePackage);
   }
   else {
      return scanCandidateComponents(basePackage);
   }
}
```