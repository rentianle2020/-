# Spring Framework概述

Spring项目通常代表使用Spring Framework技术开发的项目

Spring Framework被分成多个模块：核心的配置模型和core container，DI；web应用MVC；持久层；响应式web flex等。



**设计哲学**

- 提供不同层面的选择，在不改变代码的前提下，以更改配置的方式来更改需求
- 在各个方面支持各种应用的需求
- 精心选择JDK版本和第三方库版本
- 用心的API设计
- 干净的代码结构，不会在多个包中产生循环依赖



# Core Technologies

> IoC is also known as dependency injection (DI)

Bean在类中只需定义他所依赖的类（初始化时依赖的类，Set的类属性）

当Bean实例化的时候，容器负责将它所依赖的类实例注入进去

本来应该由bean本身来初始化它的依赖对象，现在反而由容器控制bean的初始化（以及其依赖类的初始化），所以也就成为Inverse of Control



 `org.springframework.context.ApplicationContext` 接口代表了Spring IoC容器，我们可以通过XML文件、注解、或Java代码三种配置方式，让容器来初始化、解析、装配bean

![container magic](https://docs.spring.io/spring-framework/docs/current/reference/html/images/container-magic.png)·





**Configuration Metadata 配置**

```java
ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");
```

加载配置，并初始化容器

```java
T getBean(String name, Class<T> requiredType)
```

getBean来通过类在容器中的名字，来获取bean在容器中的实例对象

> 还有其他方法从容器中获取bean实例，但是你永远不应该使用它们！其实，`getBean()`都不应该被调用；在配置bean依赖后（@Autowired），spring会帮我们完成依赖注入



### **Bean Overview**

在容器中，每一个在配置类中定义的bean都对应一个`BeanDefinition`对象

以下是`AbstractBeanDefinition`的一些属性，这个抽象类实现了`BeanDefinition`接口

| Property              | Explained in...                                            |
| :-------------------- | :--------------------------------------------------------- |
| Class                 | @Component的类名，@Bean的返回值；用来确定初始化哪个类      |
| Name                  | 一般只有一个，但也可以有“别名”，作为beam在容器中的唯一标识 |
| Scope                 | 6                                                          |
| Constructor arguments | 3                                                          |
| Properties            | 3                                                          |
| Autowiring mode       | 5                                                          |
| Lazy initialization   | 4                                                          |
| Initialization method | 7                                                          |
| Destruction method    | 7                                                          |

> 除了通过BeanDefinition来告诉我们如何创建一个具体bean对象；我们可以将先用的对象注册到容器中，但是一般情况下不需要。
>
> ApplicationContext.getBeanFactory() --> DefaultListableBeanFactory `registerSingleton(..)` and `registerBeanDefinition(..)` methods



**1、Naming Beans**

一般只有一个，但也可以有“别名”，作为beam在容器中的唯一标识

自动命名时：将类名首字母小写

@Bean注解中也可以添加Alias别名



**2、Instantiating Beans**

BeanDefinition是创建bean对象的配方；当类需要被初始化时，容器会根据这个配方来创建类的实例对象

一般都是通过Constructor初始化；当然也可以通过静态方法或者实例方法，return一个实例对象来初始化

```java
@Configuration
public class Config {

    private static Test test = new Test("aaa");

    @Bean("test")
    public static Test createTest() {
        return test;
    }
}
```



**3、Dependency Injection**

两种依赖注入方式：

- 构造方法注入（初始化**时**注入）
- Set方法注入（初始化**后**注入）

容器创建时，会先检查配置的合法性（注入类是否存在等）；而属性的注入是在创建bean时才完成的



创建bean的时机：

- 当bean为单例singleton-scope时（默认），容器创建时创建类对象
- 其他情况（如多例），只有当该类的实例对象被需求时，才会创建



如果类在初始化的时候产生循环依赖，会报错`BeanCurrentlyInCreationException`

解决方法：

- 改为Set注入
- @Lazy懒加载？好像不解决本质问题



**4、Lazy-initialized Beans**

懒加载为true的bean，会告诉容器，不要在一开始就初始化它，而是在它被需求时

所以，如果不想要在容器初始化时直接将某个singleton bean初始化，可以采用懒加载

但如果一个懒加载类，是一个非懒加载的单例类的依赖，那么也会跟着在容器初始化时，被初始化！



**5、Autowiring Collaborators**

使用XML配置时，可以设置自动注入方式（ByName，ByType，Constructor）

优势：

1. 不用去指定具体的属性或者构造参数
2. 在某类中加了一个属性后，可以不改变配置（XML）

限制和劣势：

1. 基本数据类型不能Autowire
2. 不准确，容易产生Ambiguity

这些问题可以通过后续的Annotation-Based-Configuration得到解决！



> Lookup Method Injection 没有用过，暂时跳过



**6、Bean Scopes**

BeanDefinition作为一个配方，使得Spring可以通过同一个配方，生成多个实例

我们可以控制Bean的范围

| Scope                                                        | Description                                                  |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| [singleton](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-scopes-singleton) | 容器中，只会存放一个对应该BeanDefinition的实例对象，getBean返回同一个实例对象；单例 |
| [prototype](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-scopes-prototype) | 每次request（getBean）时，返回一个新的实例；多例             |

> 暂时忽略request、session、application、websocket，没有使用过
>
> Creating a Custom Scope也先跳过



**7、Bean‘s Nature 生命周期**

可以通过实现`InitializingBean`和`DisposableBean`接口，重写在初始化和销毁时调用的方法



更现代化的程序中，为了不与Spring的接口强耦合，使用`@PostConstruct` and `@PreDestroy`

@PostConstruct注解的函数，在Bean完全初始化完毕后回调

> Constructor(构造方法) -> @Autowired(依赖注入) -> @PostConstruct(注释的方法)

@PreDestroy注解的函数，在包含该Bean的容器销毁前回调

```java
@Configuration
public class Config{

    @PostConstruct
    public void init() {
        System.out.println("Config类初始化完毕");
    }

    @PreDestroy
    public void dispose() {
        System.out.println("Config销毁");
    }
}
```

规范命名：init(), initialize(); destroy(), dispose()



### 可实现的接口

The Spring Framework provides a number of interfaces you can use to customize the nature of a bean. This section groups them as follows:

- Lifecycle Callbacks
- `ApplicationContextAware` and `BeanNameAware`
- Other `Aware` Interfaces



##### Startup and Shutdown Callbacks

通过实现LifeCycle接口，重写3个方法

当容器调用start()方法时，通知所有实现了LifeCycle接口的bean

通过isRunning判断该Bean是否在运行，如果没有的话，就调用它的start()

容器调用close()时，通过isRunning再次判断，如果Bean在运行，则调用stop



**Aware**

Java方式配置的Bean时候，Bean没有意识到自己被注入容器了！

所以如果想要使用ApplicationContext中的服务，就需要new一个

实现Aware接口的子接口，让Bean获得ApplicationContext的服务（拿到BeanName、拿到ApplicationContext容器对象等）

在容器创建bean实例对象时，会调用这些实现Aware接口的bean的重写方法；具体执行时机是在属性注入后，InitializingBean等自定义初始化后的方法调用前。

```java
public class Person implements BeanNameAware {

    String name;

    @Override
    public void setBeanName(String s) {
        name = s;
    }
}
```

> 这种实现接口的方式，将代码和Spring API绑定，不符合IoC的风格（本不应该让bean意识到spring的存在）
>
> 所以，只推荐在基础设施bean中使用（指的应该是Util类等）



### **Container Extension Points 容器扩展点**



**BeanPostProcessor**

If you want to implement some custom logic after the Spring container finishes instantiating, configuring, and initializing a bean, you can plug in one or more custom `BeanPostProcessor` implementations. 

 the post-processor gets a callback from the container both before container initialization methods (such as `InitializingBean.afterPropertiesSet()` or any declared `init` method) are called, and after any bean initialization callbacks. 

An `ApplicationContext` automatically detects any beans that are defined in the configuration metadata that implements the `BeanPostProcessor` interface. The `ApplicationContext` registers these beans as post-processors so that they can be called later, upon bean creation. 



所有BeanPostProcessor的实现列，实在容器初始化时，就被初始化并注入容器了；所以可以影响后面我们注入容器的所有对象

```java
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(bean.getClass().getSimpleName() + "初始化前");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(bean.getClass().getSimpleName() + "初始化后");
        return bean;
    }
}
//MyConfig$$EnhancerBySpringCGLIB$$5db420c9初始化前
//MyConfig$$EnhancerBySpringCGLIB$$5db420c9初始化后
```

> 这里发现了一个有意思的点，@Configuration的类在初始化前就被Cglib动态代理了
>
> 其实也能想到个大概，因为配置类中的方法都是返回Bean，所以要根据方法执行结果，方法名，定义BeanDefinition注入到容器中！



**BeanFactoryPostProcessor**

 the Spring IoC container lets a `BeanFactoryPostProcessor` read the configuration metadata and potentially change it *before* the container instantiates any beans other than `BeanFactoryPostProcessor` instances.

也就是说，在任何bean实例化之前（除了实现了BeanFactoryPostProcessor的类），执行重写的方法

> 如果自定义`BeanFactoryPostProcessor`或`BeanPostProcessor`实现类，最好也同时实现`Ordered`接口，这样可以设置执行顺序
>
> getOrder()方法返回的int越小，越先执行

```java
@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        String[] beanDefinitionNames = configurableListableBeanFactory.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            if(beanDefinitionName.equals("person")){
                Person person = configurableListableBeanFactory.getBean(beanDefinitionName,Person.class);
                person.setName("tyler");
            }
        }
    }
}
```



 **Customizing Instantiation Logic with a `FactoryBean`**

实现FactoryBean接口的类本身是一个bean；

> 注册BeanDefinition和初始化一切正常，当getBean()时，
>
> 首先：isFactoryDereference(@Nullable String name)，判断是否开头为 &，true则将 & 剪掉，直接返回beanInstance（FactoryBean对象）
>
> 如果不是，判断是否该类isInstanceof FactoryBean，不是则直接返回beanInstance（普通对象）
>
> 如果是，则调用getObjectFromFactoryBean，调用该类的getObject方法获取返回值，放到factoryBeanObjectCache容器中方便以后直接取，完成！



自定义初始化逻辑

```java
@Component(value = "person")
public class MyFactoryBean implements FactoryBean {
    @Override
    public Object getObject() throws Exception {
        Person person = new Person();
        person.setName("test");
        return person;
    }

    @Override
    public Class<?> getObjectType() {
        return Person.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
//这样就完成了Person类的自定义初始化，该类在容器中的名字也是person
```



### 通过注解完成依赖注入

注解注入在XML注入前完成，所以XML注入会覆盖之前的注入属性



**@Autowired**

自动注入，先按照类，在按照名；可以用在类、属性、构造方法、普通方法上

可以设置 required = “false‘ （默认true），这样在无法注入时会直接跳过，不会报错！



**微调自动注入**

@Primary

在bean类上注解；如果其他bean需要自动注入该类，并有多个可注入对象，优先选择它

@Qualifier

在@Autowired上加一个@Qualifier，设置value = ”xxx“，xxx为要注入的bean在容器中的名字；

也可以自定义Qualifier，只要在自定义注解上，@Qualifier就行；注解属性可以和枚举连用，增加可读性

`CustomAutowireConfigurer`是一个BeanFactoryPostProceeser，会将我们自定义的@Qualifier的注解类注册进来

@Resource

如果想要优先byName，选择它；该注解只支持用在属性上



**@Value**

用来注入外部配置

 Spring Boot configures by default a `PropertySourcesPlaceholderConfigurer` bean that will get properties from 

Springboot帮我们配置了`PropertySourcesPlaceholderConfigurer`，默认从`application.properties`和`application.yml` 中读取配置

```java
//配置文件：mail.username = "515322780"

@Value("${mail.username}")

private String mailUsername
```

> 注意：静态属性无法通过@Value注入，可以在非静态的set方法上@Value，注入静态属性值



`CommonAnnotationBeanPostProcessor`这个BeanPostProcessor用来识别属性上的@Resource，和方法上的@PostConstruct/@PreDestroy，

> 这三个注解存在于standard Java libraries from JDK 6 to 8



### 通过扫描classpath完成BeanDefinition注册

之前的文档中，我们尝试使用注解完成依赖注入，但BeanDefinition注册依然是在XML中完成的

在spring3.0中，我们可以用Java的方式完成BeanDefinition注册，代替传统的XML方式

@Configuration, @Bean, @Import, @DependsOn



使用元注解`@Component`，或者模式注解(Stereotype Annotations)`@Service`，`@Controller`等，将类变为Stereotyped Classes（定性类）

Spring可以自动扫描这些类，并将对应的`BeanDefinition`实例注册进`ApplicationContext`

开启自动扫描，需要在`@Configuration`类上，添加`@ComponentScan（basepackage = “com.tyler”）`

> 注意：这里的basepackage必须是配置类和被扫描类的公共父包

这里也可以加一些Filter，来过滤扫描，或者添加对自定义注解的扫描（用到再来看）



和配置类一样，@Component类同样可以使用@Bean来生成BeanDefinition

> 如果将方法变为`static`的，可以在不实例化配置类容器的情况下完成对`@Bean`方法的调用，返回值作为实例对象
>
> 这个在注入`BeanPostProcessor` 的时候特别合理，因为它们本应在所有bean实例化前，完成实例化！

```java
@Component
@Lazy
public class Test {

    @Bean
    @Qualifier("person")
    @Scope("singleton")
    public static Person person(){
        return new Person();
    }
}
```



**默认扫描名**

可以在@Component("自定义bean名")

默认使用类名，首字母变为小写

也可以自己实现`BeanNameGenerator`自定义命名规则，添加到@ComponentScan("nameGenerator = MyNameGenerator.class")`



**默认扫描scope**

默认为`singleton`，如果有需要可以手动`@Scope`

可以自定义scope，这边先跳过



spring-context-indexer：加一个依赖就能增快启动时扫描包的速度（编译时生成一个包含所有@Index的文件，扫文件就行）

但同时，要求所有jar包中的组件也@Index

没用过，跳过



**JSR-330注解**

`JSR-330` 是 `Java` 的依赖注入标准，可以代替spring注解

@Inject --> @Autowired ，@Named（不可组合，所以不能用来生成自定义注解）--> @Component

> 个人感觉不如就用spring的
>



### Java-based Container Configuration

通过Java代码中使用注解，完成对容器的配置（其中很多内容，上面已经有使用过了）



**基础注解**

使用`@Configuration`和`@Bean`注解，将对象注入Spring IoC容器



Lite模式：

在@Component，@ComponentScan，@Import，@ImportResource等注解的类下，声明@Bean方法

`@Bean`方法是一种通用的工厂方法（`factory-method`）机制。

优点：不用CGLIB代理，加快启动速度；因为不代理了，@Bean方法可以是private或者final，当普通类的普通方法

缺点：配置类内部**不能通过方法调用**来处理依赖，否则**每次生成的都是一个新实例而并非IoC容器内的单例**（但可以通过@Autowire解决，我觉得也不是什么大事）



Full模式：

标注有`@Configuration`注解的类被称为full模式的配置类

优点：调用在同一个配置类下的@Bean方法，保证得到的是容器中的Bean（@Bean方法不能是static、private、final，否则无法被CGLIB代理）

缺点：需要被CGLIB动态代理（@Configuration(proxyBeanMethods = false) 这样可以变为Lite模式）

>  All `@Configuration` classes are subclassed at startup-time with `CGLIB`. In the subclass, the child method checks the container first for any cached (scoped) beans before it calls the parent method and creates a new instance.



选择：如果配置类下注册的bean有互相依赖，就使用Full模式；如果没有互相依赖的情况，就使用Lite模式



**容器初始化**

通过`ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);`完成容器初始化

也可以手动`ctx.register(Config.class);`

可以通过Config.class去@ComponentScan，也可以手动`ctx.scan("com.tyler")`



**使用@Bean注解**

依赖注入：直接在方法参数中添加依赖，原理和构造器注入类似

```java
@Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }
```

添加回调函数：initMethod & destroyMethod；也可以通过(destroyMethod = "") 将原本的类中的回调方法取消

```java
public class BeanOne {

    public void init() {
        // initialization logic
    }
}

@Configuration
public class AppConfig {

    @Bean(initMethod = "init")
    public BeanOne beanOne() {
        return new BeanOne();
    }
    
//当然，这样直接调用init()也可以
@Bean
    public BeanOne beanOne() {
        BeanOne beanOne = new BeanOne();
        beanOne.init();
        return beanOne;
    }
```

自定义bean的name和alias

```java
@Bean(name = "myThing"）
@Bean({"dataSource", "subsystemA-dataSource", "subsystemB-dataSource"})
```

使用@Description来形容bean

```java
@Bean
    @Description("Provides a basic example of a bean")
    public Thing thing() {
        return new Thing();
    }
```



**@Import**

可以将多个Configuration类，集合成一个；使用@Autowired 接口 来解耦合；通过@Lazy/@DependOn决定初始化顺序



**@Conditional**

有条件的激活@Configuration类，或@Bena方法



### Environment接口

Environment = profile + properties



**Profile**

一个profile就是一组BeanDefinition的逻辑分组。

只有当一个profile处于active状态时，它对应的逻辑上组织在一起的这些BeanDefinition才会被注册到容器中

> Bean definition profiles provide a mechanism in the core container that allows for registration of different beans in different environments. 



它解决的问题

> You could say that you want to register a certain profile of bean definitions in situation A and a different profile in situation B



@Profile注解，可以添加到类或方法上，代表该@Component类或@Bean方法，只有在指定环境激活时，被注册到容器

```java
@Configuration
public class AppConfig {

    @Bean("dataSource")
    @Profile("development") 
    public DataSource standaloneDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:com/bank/config/sql/schema.sql")
            .addScript("classpath:com/bank/config/sql/test-data.sql")
            .build();
    }

    @Bean("dataSource")
    @Profile("production") 
    public DataSource jndiDataSource() throws Exception {
        Context ctx = new InitialContext();
        return (DataSource) ctx.lookup("java:comp/env/jdbc/datasource");
    }
}
```

@Profile需要添加在所有overloaded methods上，如果不一致（有的有注解，有的没有），则选取第一个声明的方法；不能手动指定优先级



激活指定profiles

springboot配置：`spring-profiles-active`



@Profile("default")为默认激活，`spring.profiles.default`可以更改默认激活profile的名字



Properties

`@PropertySource `adding a `PropertySource` to Spring’s `Environment`.

使用SpringBoot之后，基本不用指定propertySource了！

```java
@Configuration
@PropertySource("classpath:/com/myco/app.properties")
public class AppConfig {

    @Autowired
    Environment env;

    @Bean
    public TestBean testBean() {
        TestBean testBean = new TestBean();
        testBean.setName(env.getProperty("testbean.name"));
        return testBean;
    }
}
```



### **LoadTimeWeaver**

将class文件加载到JVM的时候，完成织入；目前不太清楚是什么，怎么用！



### **ApplicationContext额外功能**

ApplicationContext接口，在实现了BeanFactory接口的同时，还实现了一系列功能接口；为了达到框架导向风格（ framework-oriented style）



我们也不用使用硬编码的方式去new一个ApplicationContext

声明式：`ContextLoader` to automatically instantiate an `ApplicationContext` as part of the normal startup process of a Java EE web application.



**MessageSource国际化接口**

不了解！先跳过



**ApplicationEventPublisher事件接口**

是一种观察者模式

如果一个bean实现了`ApplicationListener`接口，每当指定的`ApplicationEvent`被发布时，bean都会被提醒

```java
@Component
public class MyEventListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("容器初始化完毕后，触发监听器");
    }
}
```

这个`ContextRefreshedEvent`的触发时机，是在容器初始化完毕后（所有Singleton都初始化了）

还有很多Events可以监听，先不学；

当然也可以自己Custom事件



现在可以通过 `@EventListener`这个注解，来注册监听器；

可以通过注解，或者参数的方式，告知监听的事件类；更灵活，一个方法可以监听多个事件！

```java
@Component
public class MyEventListener{

    @EventListener({ContextStartedEvent.class,ContextRefreshedEvent.class})
    public void listen(/*ContextStartedEvent event*/){
        System.out.println("注解方式监听容器启动！");
    }
}
```



Asynchronous Listeners异步监听器

使用`@Async`注解即可



Ordering Listeners给监听器排序

使用`@Order`注解即可



**泛型监听器？再说**



预告，2.0章会细讲

An application context is a `ResourceLoader`, which can be used to load `Resource` objects.



10.3. Application Startup Steps 有完整的容器启动流程，可以帮助更好的理解容器



对于web项目，我们不会手动new ApplicationContext；在web.xml中让web容器扫描并初始化 ContextLoaderListener --> ContextLoader --> ApplicationContext



`BeanFactory` or `ApplicationContext`?

很显然要使用ApplicationContext的实现类，因为不仅实现了所有BeanFactory的方法，还实现了其他接口的实用功能。



容器初始化：

1. 生成容器，扫描bean definition
2. BeanFacoryPostProcessor初始化，直接执行方法，更改配置
3. BeanPostProcessor初始化



Bean初始化：

1. 实例化Bean对象（构造方法）
2. 注入Bean属性
3. 实现Aware接口的方法
4. BeanPostProcessor的前置初始化方法postProcessBeforeInitialization
5. 调用Bean自身定义的init方法
6. 如果实现了InitializingBean接口，调用afterPropertiesSet方法
7. 调用BeanPostProcessor的后置初始化方法postProcessAfterInitialization
8. 创建过程完毕



FactoryBean初始化：

如果使用FactoryBean自定义实例化方法

1. 判断是否以&开头，判断是否为BeanFacotory实现类，是则直接返回该类实例
2. 如果没有，判断是否为FactoryBean的实现类，如果是，那么调用它的getObject方法返回实例
3. 调用BeanPostProcessor的后置初始化方法postProcessAfterInitialization





# Spring MVC

`DispatcherServlet`：提供公共算法接收请求，对请求的实际处理委派给各个组件（request mapping，view resolution，exception handling...)

`MyWebApplicationInitializer.onStartup()`方法在web容器加载的时候会自动的被调用，初始化容器和DispatcherServlet

```java
public class MyWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {

        // Load Spring web application configuration
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);

        // Create and register the DispatcherServlet
        DispatcherServlet servlet = new DispatcherServlet(context);
        ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/app/*");
    }
}
```

> 与其让web容器发现并初始化Spring容器和DispatcherServlet
>
> Springboot内置web容器，用Spring Configuration的方式启动容器，注册Filter，Servlet



![mvc context hierarchy](https://docs.spring.io/spring-framework/docs/current/reference/html/images/mvc-context-hierarchy.png)

WebApplicationContext是ApplicationContext的扩展，将各种web-related beans注入到容器中。

DispatcherServlet使用这个容器，作为它自己的获取bean的池子（从WebApplicationContext中获取对应的bean来处理请求）

> `DispatcherServlet` expects a `WebApplicationContext` (an extension of a plain `ApplicationContext`) for its own configuration. 



### 特殊的Bean类型

DispatcherServlet委托“特殊的bean对象”来处理请求，返回响应

特殊的bean对象：实现指定框架约定的接口，Spring管理的对象实例

| Bean type                                                    | Explanation                                                  |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| `HandlerMapping`                                             | 将一个请求映射到一个处理程序，随之而来的还有一个[拦截器]列表。通常为RequestMappingHandlerMapping实例（支持@RequestMapping） |
| `HandlerAdapter`                                             | 解析注解，通过反射，执行处理程序                             |
| HandlerExceptionResolver                                     | 解析异常，将异常映射到一个处理程序                           |
| ViewResolver                                                 | 将返回的字符串视图，解析为真实的视图来渲染返回结果           |
| LocaleResolver                                               | 国际化（CookieLocaleResolver：将语言信息设置到Cookie中，这样整个系统就可以获得语言信息） |
| ThemeResolver                                                | ThemeResolver工作原理与LocaleResolver工作原理基本是一样的，它在request中查找theme主题并可以修改request的theme主题。 |
| MultipartResolver                                            | 解析Multipart（文件上传）请求                                |
| [`FlashMapManager`](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-flash-attributes) | Store and retrieve the “input” and the “output” `FlashMap` that can be used to pass attributes from one request to another, usually across a redirect. See [Flash Attributes](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-flash-attributes). |



### MVC Config

在容器中声明一系列的“特殊类型Bean”，用来处理请求。`DispatcherServlet`从`WebApplicationContext`获取特殊的bean。

两种方式：Java配置类，XML配置



### Processing

The `DispatcherServlet` processes requests as follows:

- 获取WebApplicationContext，将其绑定到请求作用域中，让其他组件可以使用该容器。它在域中的key默认为 `DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE` （注意不是字符串，而是DispatcherServlet类中的一个常量
- 国际化解析器绑定到请求中
- 环境解析器绑定到请求中
- 如果指定了文件上传解析器，监视请求中的文件。如果发现文件，将其包装为`MultipartHttpServletRequest`，提供给接下来步骤的其他组件。
- 搜索到一个适合的处理程序（handler），和这个handler有关的的拦截器触发。prepare a model for rendering
- 如果返回model（渲染页面的数据），view被渲染；没有返回，就不渲染



The `HandlerExceptionResolver` beans declared in the `WebApplicationContext` are used to resolve exceptions thrown during request processing. Those exception resolvers allow customizing the logic to address exceptions. See [Exceptions](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-exceptionhandlers) for more details.

The Spring `DispatcherServlet` also supports the return of the `last-modification-date`, as specified by the Servlet API. The process of determining the last modification date for a specific request is straightforward: The `DispatcherServlet` looks up an appropriate handler mapping and tests whether the handler that is found implements the `LastModified` interface. If so, the value of the `long getLastModified(request)` method of the `LastModified` interface is returned to the client.

You can customize individual `DispatcherServlet` instances by adding Servlet initialization parameters (`init-param` elements) to the Servlet declaration in the `web.xml` file. The following table lists the supported parameters: