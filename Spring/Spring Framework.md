# Spring Framework概述

Spring项目通常代表使用Spring Framework技术开发的项目

Spring Framework被分成多个模块：核心的配置模型和core container，DI；web应用MVC；持久层；响应式web flex等。



# Core Technologies

> 官方：IoC is also known as dependency injection (DI)



![container magic](https://docs.spring.io/spring-framework/docs/current/reference/html/images/container-magic.png)·



### Bean扩容点

实现接口，改变Bean的生命周期



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

实现了BeanPostProcessor的类，被容器自动扫描并注入，在普通Bean初始化前后回调函数

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
```



**BeanFactoryPostProcessor**

 the Spring IoC container lets a `BeanFactoryPostProcessor` read the configuration metadata and potentially change it *before* the container instantiates any beans other than `BeanFactoryPostProcessor` instances.

> 最好也同时实现`Ordered`接口，这样可以设置执行顺序
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

实现FactoryBean接口的类，本身是一个bean

可以自定义实例化逻辑，和@Configuration中的@Bean类似



### 依赖注入注解



**@Autowired**

自动注入，默认按照类by Type；可以用在类、属性、构造方法、普通方法上

设置 required = "false"（默认true），这样在无法注入时会直接跳过，不会报错



**同一个Autowired接口有多个实现类，解决方案**

@Primary：表示在多个实现类bean同时存在的情况下，优先选择该bean

@Qualifier：将注入自动注入的方式更改为按名称by Name

@Resource：与Autowired不同，注入方式默认by Name；该注解只支持用在属性上



**@Value**

注入外部配置，默认从`application.properties`和`application.yml` 中读取

```java
//配置文件：mail.username = "515322780"

@Value("${mail.username}")
private String mailUsername
```

> 注意：静态属性无法通过@Value注入，可以在非静态的set方法上@Value，注入静态属性值



**JSR-330注解**

`JSR-330` 是 `Java` 的依赖注入标准，可以代替spring注解

@Inject --> @Autowired ，@Named（不可组合，所以不能用来生成自定义注解）--> @Component

> 不如就用spring的



### 扫描classpath并完成BeanDefinition注册

使用`@Component` , `@Service`，`@Controller`等，将类变为Stereotyped Classes（定性类）



在`@Configuration`类上，添加`@ComponentScan（basepackage = “com.tyler”）`

扫描定性类，生成对应的`BeanDefinition`实例，注册进`ApplicationContext`容器

> 注意：这里的basepackage必须是配置类和被扫描类的公共父包



**BeanDefinition接口**

以下是`AbstractBeanDefinition`的一些属性，这个抽象类实现了`BeanDefinition`接口

| Property              | Explained in...                                              |
| :-------------------- | :----------------------------------------------------------- |
| Class                 | 类，用来确定实例化对象的类                                   |
| Name                  | @Component("name")，作为bean在容器中的唯一标识               |
| Scope                 | @Scope默认单例Singleton，可以改变为Prototype等（通常不需要） |
| Constructor arguments | 构造方法参数，依赖                                           |
| Properties            | 属性，依赖                                                   |
| Autowiring mode       | 依赖注入模式，byName or byType                               |
| Lazy initialization   | @Lazy为true的bean，容器只有在它被需求时才会初始化，默认在容器启动时初始化 |
| Initialization method | @PostConstruct方法                                           |
| Destruction method    | @PreDestroy方法                                              |



### Java-based Container Configuration

使用`@Configuration`和`@Bean`注解，将对象注入Spring IoC容器

优势：可以自定义很多逻辑判断，然后决定返回什么bean实例到容器中



**Lite模式**

在@Component，@ComponentScan，@Import，@ImportResource等注解的类下，声明@Bean方法

`@Bean`方法是一种通用的工厂方法（`factory-method`）机制。

优点：不用CGLIB代理，加快启动速度；因为不代理了，@Bean方法可以是private或者final，当普通类的普通方法

缺点：配置类内部**不能通过方法调用**来处理依赖，否则**每次生成的都是一个新实例而并非IoC容器内的单例**（但可以通过@Autowire解决，我觉得也不是什么大事）



**Full模式**

标注有`@Configuration`注解的类被称为full模式的配置类

优点：调用在同一个配置类下的@Bean方法，保证得到的是容器中的Bean（@Bean方法不能是static、private、final，否则无法被CGLIB代理）

缺点：需要被CGLIB动态代理（@Configuration(proxyBeanMethods = false) 这样可以变为Lite模式）

>  All `@Configuration` classes are subclassed at startup-time with `CGLIB`. In the subclass, the child method checks the container first for any cached (scoped) beans before it calls the parent method and creates a new instance.



**选择**：如果配置类下注册的bean有互相依赖，就使用Full模式；如果没有互相依赖的情况，就使用Lite模式



# IoC

两种IOC容器，帮助我们创建对象

- BeanFactory：最基础的IOC容器，提供DI功能
- ApplicationContext：在BeanFactory的基础上，增加了更多的企业级功能（BFPP，BPP）

IoC容器instantisates,seembles,and manages the bean object ('s lifecycle)

The configuration metadata that are supplied to the container are used create Bean obejct



**ApplicationContext宏观周期**

BeanFactoryPostProcessor：在load definitions之后，instantiate bean之前，更改IoC容器配置

![enter image description here](https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210909225758.png)

**Bean生命周期**

Aware：从IoC上下文（Metadata）中获取值，如String BeanName

BeanPostProcessors：改变bean实例的具体内容

afterProptertiesSet() & 自定义init()：可以打印日志，或者使用已经注入的变量值；官方推荐使用注解方式@PostConstruct

![image-20210909170231362](https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210909225801.png)

<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210909225754.png" alt="enter image description here" style="zoom: 50%;" />	



# Dependency Injection

**什么是依赖注入**

The best definition I've found so far is [one by James Shore](http://jamesshore.com/Blog/Dependency-Injection-Demystified.html):

> "Dependency Injection" is a 25-dollar term for a 5-cent concept. [...] Dependency injection means giving an object its instance variables. [...].

There is [an article by Martin Fowler](http://martinfowler.com/articles/injection.html) that may prove useful, too.

Dependency injection is basically providing the objects that an object needs (its dependencies) instead of having it construct them itself. It's a very useful technique for testing, since it allows dependencies to be mocked or stubbed out.

Dependencies can be injected into objects by many means (such as constructor injection or setter injection). One can even use specialized dependency injection frameworks (e.g. Spring) to do that, but they certainly aren't required. You don't need those frameworks to have dependency injection. Instantiating and passing objects (dependencies) explicitly is just as good an injection as injection by framework.



**依赖注入方式**

By Constructor

By Setter method

都可以使用，只不过注入依赖的时机不同而已



个人更偏向使用Constructor注入

- 依赖更明确

- 灵活决定注入顺序

- 静态变量可以直接使用依赖（静态变量在构造方法后，在setter方法前被初始化）



# AOP

一种程序结构思想

就像class作为OOP中的组件，AOP思想将aspect作为关键组件



cross-cutting concern 关注横切点





# Spring MVC

MVC是一种设计模式

Request --> Controller --> Model --> View --> Response

SpringMVC框架让我们使用最少的代码，完成整个业务请求的处理，遵循MVC模式



<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210909225741.png" alt="mvc context hierarchy" style="zoom:67%;" />	

`DispatcherServlet`：基于Servlet，统一接收&返回请求，实际处理委派给各个组件

WebApplicationContext是ApplicationContext的扩展，将各种web-related beans注入到容器中。

DispatcherServlet使用这个容器，作为它自己的获取bean的容器（从WebApplicationContext中获取对应的bean来处理请求）

> `DispatcherServlet` expects a `WebApplicationContext` (an extension of a plain `ApplicationContext`) for its own configuration. 



**负责处理MVC流程的接口**

框架约定的接口，`DispatcherServlet` 委派实现类来处理MVC业务流程

| Bean type                | Explanation                                                  |
| :----------------------- | :----------------------------------------------------------- |
| `HandlerMapping`         | 将一个请求映射到一个处理程序，随之而来的还有一个[拦截器]列表。通常为RequestMappingHandlerMapping实例（支持@RequestMapping） |
| `HandlerAdapter`         | 解析注解，通过反射，执行处理程序                             |
| HandlerExceptionResolver | 解析异常，将异常映射到一个处理程序                           |
| ViewResolver             | 将返回的字符串视图，解析为真实的视图来渲染返回结果           |
| LocaleResolver           | 国际化（CookieLocaleResolver：将语言信息设置到Cookie中，这样整个系统就可以获得语言信息） |
| ThemeResolver            | ThemeResolver工作原理与LocaleResolver工作原理基本是一样的，它在request中查找theme主题并可以修改request的theme主题。 |
| MultipartResolver        | 解析Multipart（文件上传）请求                                |