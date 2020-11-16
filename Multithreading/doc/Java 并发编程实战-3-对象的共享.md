# 可见性

同步还有另一个重要的方面：内存可见性（Memory Visibility）。我们不仅希望防止某个线程正在使用对象状态而另一个线程在同时修改该状态，而且希望确保当一个线程修改了对象状态后，其他线程能够看到发生的状态变化。

重排序（Reordering）问题。

```tex
在没有同步的情况下，编译器、处理器以及运行时等都可能对操作的执行顺序进行一些意想不到的调整。在缺乏足够同步的多线程程序中，要相对内存操作的执行顺序进行判断，几乎无法得出正确的结论。（这里可以用 volatile 对共享变量进行修饰）
```

## 失效数据

就是 count++ 的时候，当前线程可能会获得另一个线程加之前的数据，另一个线程加完的数据就是“有效”的数据，而加之前的数据就是失效数据。

## 非原子的 64 位操作

Long 和 double 都是 64 bit 的，所以在 32 位机器上会把这两种变量拆成两个变量来操作，所以会出现非原子性操作。除非用 volatile 修饰。

## 加锁与可见性

加锁的含义不仅仅局限于互斥行为，还包括内存可见性。为了确保所有线程都能看到共享变量的最新值，所有执行读操作或者写操作的线程都必须在同一个锁上同步。

## Volatile 变量

轻量级同步机制 volatile 变量。用来确保将变量的更新操作通知其他线程。

1. 禁止重排序；
2. volatile 变量不会被缓存在寄存器或者对其他处理器不可见的地方（禁用 CPU 缓存）；

**volatile** 是一种比 **synchronized** 关键字更轻量级的同步机制。（这种类比并不正确，SynchronizedInteger 在内存可见性上的作用比 volatile 变量更强）

```markdown
仅当 volatile 变量能够筒化代码的实现以及对同步策略的验证时，才应该使用它们。如果在验证正确性时需要对可见性进行复杂的判断，那么就不要使用 volatile 变量。volatile 变量的正确使用方式包括：确保它们自身状态的可见性，确保它们所引用对象的状态的可见性，以及标识一些重要的程序生命周期事件的发生（例如初始化或关闭）。
```

count++ 就不是原子操作，volatile 修饰还是不能保证它的正确性。

**加锁机制既可以确保可见性有可以确保原子性，而 volatile 变量只能确保可见性。**

# 发布与逸出

发布（Publish）一个对象的意思是指，使对象能够在当前作用域之外的代码中使用，例如，将一个指向该对象的引用保存到其他代码可以访问的地方，或者在某一个非私有的方法中返回该引用，或者将引用传递到其他类的方法中。

当某个不应该发布的对象被发布时，这种情况就被称为逸出（Escape）。

其实就是锁的粒度问题，如下

```java
public static Set<Secret> knownSecrets;

public void init(){
    knownSecrets = new HashSet<Secret>();
}
```

这里发布了 knownSecrets 的 HashSet 对象，如果里面有个新的 Secret 对象，那么间接地也发布了该 Secret 对象。可以从一个非私有方法中返回一个引用，那么同样会发布返回的对象。

当发布一个对象时，在该对象的非私有域中引用的所有对象同样会被发布。一般来说，如果一个已经发布的对象能够通过非私有的变量引用和方法调用到达其他的对象，那么这些对象也都会被发布。

**安全的对象构造过程**

不要在构造过程中使 this 引用逸出。

使用静态工厂方法来防止 this 引用在构造过程中逸出。

```java
public class SafeListener{
    private final EventListener listener;
    
    private SafeListener(){
        listener = new EventListener(){
            public void onEvent(Event e){
                doSomething(e);
            }
        };
    }
    
    public static SafeListener newInstance(EventSource source){
        SafeListener safe = new SafeListener();
        source.registerListener(safe.listener);
        return safe;
    }
}
```

