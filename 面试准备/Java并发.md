### 进程&线程

进程：代表一个新的程序开始运行，统一向OS申请内存等资源

线程：每个进程至少包含一个线程，交给OS调度，享用CPU资源

<img src="assets/Java运行时数据区域JDK1.8.dbbe1f77.png" alt="img" style="zoom:67%;" />	

> 放置于Heap中的Permgen（包含方法区），可能导致内存泄露（垃圾没有被合理回收），报错OutOfMemoryException
>
> JDK1.8用Native Memory中的Metaspace代替，这块区域会动态扩容，减少OutOfMemory的可能



**线程生命周期**

- NEW
- RUNNABLE：正在运行/准备运行
- BLOCKED：争抢锁失败，等待获取monitor lock
- WAITING：手动调用wait()/join()/park()，释放锁并等待notify()/notifyAll()唤醒，唤醒后回到RUNNABLE状态准备运行，如果争抢锁失败，则去到BLOCKED
- TIMED_WAITING：手动调用Thread.sleep/TimeUnit.SECOND.sleep()
- TERMINATED



### 并行&并发

并发：多个任务在同一时间段交替占用CPU时间片

并行：多个任务在同一时间一起执行



### Synchronized

- `synchronized` 关键字加到 `static` 静态方法和 `synchronized(class)` 代码块上都是是给 Class 类上锁。
- `synchronized` 关键字加到实例方法上是给对象实例上锁。



**锁升级**

JDK1.6后，锁主要存在四种状态，依次是：无锁状态、偏向锁状态、轻量级锁状态、重量级锁状态，他们会随着竞争的激烈而逐渐升级。

偏向锁：对于单一线程，使用CAS将对象头中的线程ID设置为自己的即可；CAS失败则升级锁

轻量级锁：使用CAS将对象投中的线程指针指向自己；自选次数超出阈值，则升级锁

重量级锁：线程直接进入阻塞状态，等待被唤醒



**ReentrantLock** 

`synchronized` 是依赖于 JVM 实现的，并没有直接暴露给我们。

`ReentrantLock` 是 JDK 层面实现的（也就是 API 层面，需要 lock() 和 unlock() 方法配合 try/finally 语句块来完成），提供额外功能（公平锁，放弃等待，选择性通知）



### Volatile

**happens before guarantee**：被修饰volatile的field，保证了对它的读写会按照代码顺序执行，其他代码可能会被reordering，并且读写都是在main memory中执行。从而保证了共享数据的可见性和读写顺序性。



### 线程池

复用线程，减少创建/销毁线程的开销，从而提高响应速度，并且让线程具有可管理性。



**创建方式**

ThreadPoolExecutor构造参数

- corePoolSize：最小运行线程数量
- maximumPoolSize：当队列中的任务达到最大容量，最多可以同时运行的线程数量
- workQueue：任务来时，先判断运行中的线程数量是否>corePololSize，如果大于，则先放到队列中
- keepAliveTime：当线程数>corePoolSize且没有新的任务，核心外的线程不会立即销毁，而是等待时间超过keepAliveTime再销毁
- unit：keepAliveTime单位
- threadFactory：线程池创建线程时使用
- handler：饱和策略



《阿里巴巴 Java 开发手册》中强制线程池不允许使用 Executors 去创建，而是通过 ThreadPoolExecutor 的方式。

容易导致OOM，例如：

- FixedThreadPool 和 SingleThreadExecutor 请求队列长度 = Integer.MAX_VALUE

- CachedThreadPool 和 ScheduledThreadPool 允许线程数量 = Integer.MAX_VALUE

而Executors创建这些线程池的方法也是通过调用ThreadPoolExecutor 的构造函数。



**饱和策略**

当运行中的线程数量超过maximumPoolSize，且workQueue已满，这时新任务到来执行的策略

- AbortPolicy：抛异常拒绝处理
- CallerRunsPolicy：调用者（比如main线程）自己执行这个任务
- DiscardPolicy：直接丢掉任务
- DiscardOldestPolicy：丢弃最早的未处理任务（队头）



**执行任务**

1. execute(Runnable)

   <img src="assets/图解线程池实现原理.png" alt="图解线程池实现原理" style="zoom: 80%;" />	

   优先添加coreThread，其次offer给workQueue，再不行就创建new Thread；如果超过maximumPoolSize，则执行reject饱和策略。

   由addWorker(firstTask, isCore)创建Worker线程来执行任务，worker可以拥有firstTask并不断的getTask()，在getTask()中不断workQueue.poll()；直到keepAliveTime时间内没有新的Task，且不为coreThread或设置allowCoreThreadTimeOut，就返回null来结束线程。

2. submit(Callable)返回Future对象，get()获取执行结果



**关闭线程池**

shutdown()等待队列中的任务执行完毕, shuwtdownNow()立即关闭

isShutDown() shutdown方法后返回true，isTerminated() shutdown方法后并所有任务完成后返回true



**线程池大小**

N = CPU核心数

- CPU密集型任务：N + 1，多出来的一个以防线程偶发的缺页终端，充分利用CPU
- IO密集型：2N，任务执行IO时，将CPU分配给其他线程，因此可以配置多一些线程



### Atomic 原子类

volatile存放value；使用native的CAS方法

> Corresponds to C11 atomic_compare_exchange_strong.



### AQS

AbstractQueuedSynchronizer，用来构造锁和同步器的框架。



```java
private volatile int state;//共享变量，使用volatile修饰保证线程可见性，CAS修改其值
```

- 共享变量没有锁定，将线程设置为当前线程，并锁定资源
- 共享已经被锁定，将线程加入队列并阻塞，等待唤醒



**资源共享方式**

1. Exclusive独占：state只有0和1两个状态
   - 公平锁：直接加入队列，按照顺序排队
   - 非公平锁：先CAS试图抢锁，没抢到再加入队列排队
2. Share共享：多个线程可以不断加减state状态



**ReentrantLock可重入锁**

依赖AQS，必须显示调用unlock()释放锁，可以是公平/非公平的，可重入



**Semaphore信号量**

允许多个线程同时acquire/release多个资源；资源小于需求时，先加入队列再挂起线程



**CountDownLatch倒计时器** 

一个线程（多个线程）等待，而其他的 N 个线程在完成“某件事情”

经典用法

1. 初始化为n，await()等待多个线程完成后再继续执行
2. 初始化为1，多个线程await()，主线程countDown()后同时唤醒所有等待线程

计数器的值只能在构造函数中被初始化一次



**CyclicBarrier循环栅栏**

多个线程互相等待，直到到达同一个同步点，再继续一起执行

每次进来一个线程，获取ReentrantLock，查看count，如果数量不够就扔到队列等待signalall()



### 线程任务

Runnable：没有返回值，无法抛异常

Callable：有返回值，可抛异常



### 并发容器

**`ConcurrentHashMap`** ：线程安全HashMap；读不上锁，写也只对当前bucket上锁

**`CopyOnWriteArrayList`** ：线程安全ArrayList；通过上锁完成写写互斥；每次执行写操作/遍历操作时copy数组，修改完成后再赋值给数组引用，不影响读，达到读写不互斥

**`ConcurrentLinkedQueue`** ：线程安全队列；CAS非阻塞队列（获取不到/无法写入元素时，直接返回NULL）

**`BlockingQueue`**：线程安全队列；阻塞，广泛应用在“生产者-消费者”问题中（容器满了，生产者阻塞；容器空了；消费者阻塞）

**`ConcurrentSkipListMap`** ：线程安全跳表