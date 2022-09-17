### IOC

控制反转，几乎所有框架都拥有的一个特性；用户简单配置，框架控制流程。

在Spring中，IOC体现于DI，它帮助我们自动完成对象的依赖注入和管理。



### Bean生命周期

通过实现各种接口，在Bean的生命周期中添加自定义方法

如BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, BeanPostProcessor, InitializingBean, DisposableBean



### AOP

通过动态代理，将与业务无关的代码（如日志，事务处理等...）置于业务代码执行的前后，减少重复代码，降低模块间的耦合度。

<img src="assets/SpringAOPProcess.jpg" alt="SpringAOPProcess" style="zoom: 80%;" />	

Spring项目运行时发现需要代理类，JDK Proxy生成implement同样接口的代理类并返回。

CGLib则是通过extends，在编译时完成对字节码的生成。



### 事务

使用 `@Transactional`注解进行事务管理，通过AOP实现

TransactionManager通过TransactionDefinition获得事务，通过TransactionStatus记录事务。这一切都由各个框架自己实现。



**事务传播行为**

解决业务中，方法之间调用的事务问题。

1. PROPAGATION_REQUIRED：外部已经有Transaction，则复用；没有则自己start一个。rollback则全部rollback。
2. PROPAGATION_REQUIRED_NEW：开启独立事务，与外部Transaction独立。rollback与外部独立。
3. PROPAGATION_NESTED：继续外部事务，在方法开始前声明`SAVEPOINT identifier`，如果rollback，也是`rollback to SAVEPOINT identifier`，不影响外部Transaction执行
4. PROPAGATION_MANDATORY：要求外部提供Transaction让当前方法复用，否则直接抛出异常

还有3种不常用，也都很好理解。



**事务隔离**

1. ISOLATION_DEFAULT：采用数据库默认隔离级别
2. ...

使后端与数据库的会话，采用指定的事务隔离



**只读事务**

对于仅查询的方法， 也可以使用@Transactional(readOnly = true)

好处

1. 如果不开启事务，数据库会为每个查询开启一个事务；如果一个方法中有多个查询，可能导致数据不一致（有其他线程在两个查询中，插入了新的数据）

2. 会带来一些hibernate和数据库的优化

   https://stackoverflow.com/a/44986258



**事务回滚**

rollbackFor，遇到RuntimeException或者Error时，自定义的异常类被抛出



**自调用**

避免在同一个类中调用@Transactional方法

代理类只会增强含有@Transactional的public方法，如果我们用private方法调用



动态代理类中是包含原类的，当你调用动态代理类的方法时，会在方法调用前后做增强。

但是如果在pojo.method1()中调用pojo.method2()，proxy.method1()会得到增强，而方法中调用的依然是原类的this.pojo.method2()，因此不会被增强。

同样的道理，如果自调用，被调用方法上的@Transactional不会生效。

https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop-understanding-aop-proxies



### 设计模式



工厂模式：Spring本身就是一个大工厂，我们可以通过名字获取到它生成的Bean对象

单例模式：Bean默认单例

代理模式：Spring AOP基于动态代理

模板模式：Spring-Data-JDBC中的JDBC template；通常模板模式需要我们extends然后override，而Spring常用传入Callback类的方式。

> 给jdbcTemplate.query(String sql)传入sql，它内部声明一个新的Callback类，根据sql重写了各种方法然后new出来，传给下一个函数作为模板的填充。

观察者模式：EventListener和EventPublisher，Spring的事件监听/发布机制

适配器模式：Spring根据请求，将Request封装成不同的Handler（如ControllerHandler，ServletHandler，静态资源HttpRequestHandler），将他们适配到第一个support() returns true的HandlerAdaptor进行不同的下一步处理。