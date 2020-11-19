# 提示

组合优于继承，过度复杂的子类，会导致开发难度呈几何式增长，当然，并不是所有类都必须用组合，模板模式就是继承优于组合的设计模式。

# 设计线程安全的类

+ 找出构成对象状态的所有变量。
+ 找出约束状态变量的不可变性条件。
+ 建立对象状态的并发访问管理策略。

# 收集同步需求

如果不了解对象的不变性条件和后验条件，那么就不能确保线程安全性。要满足在状态变量的有效值或状态转换上的各种约束条件，就需要借助于原子性与封装性。

## 依赖状态的操作

类的不变性条件与后验条件约束了在对象上有哪些状态和状态转换是有效的。在某些对象的方法中还包含一些基于状态的先验条件（Preeconition）。例如，不能从空队列中移除一个元素，在删除元素前，队列必须处于“非空的”状态。如果在某个操作中包含有基于状态的先验条件（我觉得这里应该叫先决条件，详细看下面），那么这个操作就称为依赖状态的操作。

***在计算机编程中，先决条件或先验条件指在执行一段代码前必须成立的条件。***

## 状态的所有权

许多情况下，所有权与封装性总是相关联的：对象封装它拥有的状态，反之也成立，即对它封装的状态拥有所有权。

书上说 ServletContext 是要保证 setAttribute 和 getAttribute 的时候的线程安全性，在 Tomcat 的里面的实现就是 ConcurrentHashMap，通过组合的方式，将责任委托给另一个对象。*在 Spring 的 GenericApplicationContext 就是内置了个 DefaultListableBeanFactory，将实际的容器的内容委托了这个对象。AnnotationConfigServletWebServerApplicationContext 是 GenericApplicationContext 的子类，也是 Spring Boot 的 ApplicationContext。*

```java
public class ApplicationContext implements ServletContext {
    protected static final boolean STRICT_SERVLET_COMPLIANCE;
    protected static final boolean GET_RESOURCE_REQUIRE_SLASH;
    // ConcurrentHashMap 就是线程安全的，它利用了 Unsafe#compareAndSwapObject 方法
    // 和 synchronized 关键字来保证 put 的时候一定是线程安全的
    // 还有 get 时候用到了 Usafe#getObjectVolatile 来保证 get 到的对象也是线程安全的
    // Usafe 可以说是个魔法类了，调用本地（Native）方法，给 Java 开后门，要啥给你变啥。
    protected Map<String, Object> attributes = new ConcurrentHashMap();
}
```

# 实例封闭

封装简化了线程安全类的实现过程，它提供了一种实例封闭机制（Instance Confinement），通常也称为“封闭”。

***将数据封装在对象内部，可以将数据的访问限制在对象的方法上，从而更容易确保线程在访问数据时总能持有正确的锁。***

如下

```java
public class PersonSet {

    private final Set<Person> mySet = new HashSet<>();

    public synchronized void addPerson(Person s){
        mySet.add(s);
    }

    public synchronized boolean containsPerson(Person s){
        return mySet.contains(s);
    }
    
}
```

如果 Person 类是可变的话，那么在访问从 Personset 中获得的 Person 对象时，还需要额外的同步。要想安全地使用 Person 对象，最可靠的方法就是使 Person 成为一个线程安全的类。

***封闭机制更易于构造线程安全的类，因为当封闭 类的 状态时，在分析类的线程安全性就无须检查整个程序。***

## Java 监视器模式

在操作系统层面叫管程，在这里叫监视器（monitorenter 和 monitorexit）。

其实就是一把梭，管你什么 CAS，管程，上来就是 synchronized 全锁住，谁也别想跑。

典型案例 Vector、HashTable。

使用私有的锁对象而不是对象内置锁（或任何其他可通过公有方式访问的锁），有许多优点。私有的锁对象可以将锁封装起来，使得客户代码无法得到锁。如下

**AbstractApplicationContext.startupShutdownMonitor**

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader
		implements ConfigurableApplicationContext {
	/** Synchronization monitor for the "refresh" and "destroy". */
	private final Object startupShutdownMonitor = new Object();
    @Override
	public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
            // 经典 refresh 操作，Spring Boot 在 onRefresh 操作的时候启动的 Tomcat
        }
    }
}
```

这个锁其实就是在 refresh 和 close 还有注册 ShutdownHook 的时候用到了，保证启动和关闭的是唯一的 ApplicationContext。

# 线程安全性的委托

就是上面的 ServletContext 的子类 ApplicationContext 的案例，把东西委托为 ConcurrentHashMap 这个线程安全的类，自己调用就完事了。

还有如果存在复合操作，那也有可能造成线程不安全的操作。Spring 的部分代码，如下

**DefaultBeanListableFactory#registerBeanDefinition**

```java
@Override
public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
    throws BeanDefinitionStoreException {
    // 省略一大段代码
        if (hasBeanCreationStarted()) {
       // Cannot modify startup-time collection elements anymore (for stable iteration)
       synchronized (this.beanDefinitionMap) {
          this.beanDefinitionMap.put(beanName, beanDefinition);
          List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames.size() + 1);
          updatedDefinitions.addAll(this.beanDefinitionNames);
          updatedDefinitions.add(beanName);
          this.beanDefinitionNames = updatedDefinitions;
          removeManualSingletonName(beanName);
       }
    }
    // 省略一大段代码
}
```

这里是注册 BeanDefinition，然后 beanDefinitionMap 是 ConcurrentHashMap 用于保存 beanName 和 beanDefinition，

beanDefinitionNames 用来保证 beanNames 的顺序。

## 独立的状态变量

如果是多个变量，只要这些变量是彼此独立的，即组合而成的类并不会在其包含的多个状态变量上增加任何不变性条件。

```java
public class VisualComponent{
    
    private final List<KeyListener> keyListeners = new CopyOnWriteArrayList<>();
    private final List<MouseListener> mouseListeners = new CopyOnWriteArrayList<>();
	
    // 分别对两个变量进行 add 和 remove

}
```

## 当委托失效时

***如果一个类时由多个独立且线程安全的状态变量组成，并且在所有的操作中都不包含无效状态转换，那么可以将线程安全性委托给底层的状态变量。***

我愿称之为加锁下沉。

## 发布底层的状态变量

**如果一个状态变量时线程安全的，并且没有任何不变性条件来约束它的值，在变量的操作上也不存在任何不允许的状态转换，那么就可以安全地发布这个变量。**

# 在现有的线程安全类中添加功能

就是继承 Vector 这种类。我是觉得没必要。

## 客户端加锁机制

