### Why go?

RPC package非常好用，c++实现起来非常复杂。

type-safe静态类型语言，不容易出现类型转换问题。

Threads和GC的组合是很重要的，手动寻找最后使用reference的thread非常复杂。



### Threads

program -> multiple threads in one address space -> seperate program counter, registers and stack



**Why use threads？**

I/O concurrency: One program waiting another can proceed；ex.RPC call in distributed system

Parallelism: run computation in multiple cores at same time; increase performance

Convenience: run in background periodically, dont want to start a new main func; ex. master thread check if worker thread alive periodically



单线程event driven（while true，wait for input -> select a handler to handle event in main thread），更小的开销，更慢的处理速度

多线程thread driven（request come in, get a thread from pool to handle the request, main thread continue receiving new requests），更多的开销，更快的处理速度



**Process**

One program has one single address space, contains multiple threads

不同的Processes完全隔离开，需要进程间通信才能获取对方的数据

而同一个Process下的Threads可以有共享数据

时钟中断，OS根据schedule algorithm选择一个thread进行context switch



**lock**

go know nothing about变量和锁的关联，需要程序员自己为两者创建联系



**Coordination**

多数情况thread只是拿锁拿数据，不在意别的thread，但有的情况我们希望wait for other threads

- channels
- sync.Cond
- WaitGroup



**Deadlock**

T1 hold lockA and acquire lockB, T2 hold lockB and acquire lockA

T1 wait T2 && T2 wait T1