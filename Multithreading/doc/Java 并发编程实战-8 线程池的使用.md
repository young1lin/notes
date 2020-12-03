# 线程饥饿死锁

在线程池中，如果任务依赖于其他任务，那么可能产生死锁。

我怎么记得是2N+1 呢，一半读，一半写。

N+1个线程数大小通常最优的利用率。

Nt = Nc * Uc * (1+W/C)

Nt 是 N 个线程数。N thread num。

以此类推。

# 配置 ThreadPoolExecutor

