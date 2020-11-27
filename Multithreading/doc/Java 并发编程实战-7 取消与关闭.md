使用 volatile 类型的域来保存取消状态。

因为更改后线程间可见。如下：

```java
package me.young1lin.multiplethreading.cancelled;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/11/26 11:25 下午
 */
public class PrimeGenerator implements Runnable {

    private final List<BigInteger> primes = new ArrayList<BigInteger>();
    private volatile boolean cancelled;

    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!cancelled) {
            p = p.nextProbablePrime();
            synchronized (this) {
                primes.add(p);
            }
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public synchronized List<BigInteger> get() {
        return new ArrayList<BigInteger>(primes);
    }

    public static void main(String[] args) throws InterruptedException {
        PrimeGenerator generator = new PrimeGenerator();
        new Thread(generator).start();
        try {
            Thread.sleep(10000);
        } finally {
            generator.cancel();
        }
        System.out.println(generator.get());
    }

}
```

## 中断

