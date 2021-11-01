# SpringBoot



### Structuring Your Applications



**N Tier Architecture**

REST LAYER 接收返回信息

Service LAYER 业务逻辑

DAO LAYER 访问数据库



CommandLineRunner类作为启动时使用的类



### JSON

Spring没有重复造轮子，而是使用了Jackson依赖来处理JSON字符串

原理：反射机制，调用所有get()方法（无论对象是否有该属性），然后组装成JSON字符串

- 将get方法注释掉之后，该属性就不会被转换成JSON
- 如果对象一个get()都没有，会报错
- 添加额外的get()方法来返回JSON属性

扩展：

@JsonIgnore：让属性不会被转换为JSON字符串，也可以通过@JsonProperty的access属性只写/只读

@JsonProperty("alias")：改变属性在JSON字符串中的key名

以上两个注解可以放在属性、方法上，如果用lombok@Data了，就放在属性上就好



### Restful APIs

GET：request data

POST：create new resource 非幂等性，每次会造成不同的结果

PUT：create new update old resource 保证幂等性，发送多少次都是一样的结果

DELETE：delete resource



**API命名和版本**

不要在root uri定义接口，better naming convention 更好的命名惯例

api/v1/customer

api/v2/customer

v1和v2为了向下兼容，升级接口到v2后也能让v1照常访问

best practice when u deprecate（过时）version 1 and goes to version 2

将v1 @Deprecated



另外：

根据团队的命名惯例，可以叫做api/v1/customers，然后getCustomers方法就直接访问，getCustomer方法需要加上@PathVariable("customerId")

或者api/v1/customer，getCustomers方法路径加上/all



> *Immutable List* 指的是不可变、线程安全的集合



### Validation

```
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

在实体类属性上加上

- @NotBlank：不能为空或null

- @NotNull：可以为空，不能为null

然后在请求参数前加上@Valid，因为Spring会先判断是否参数有这个注解，再调用ValidAdaptor去检测

具体调用方法`validateIfApplicable()`



**更多注解请见**

```
package javax.validation.constraints;
```



### Exception Handling

在配置中开启，即可在请求错误时将message、errors、stacktrace以kv形式返回

```yml
server:
  error:
    include-message: always
    include-binding-errors: always
    include-stacktrace: on_param #请求体中stacktrace:true才会返回，太冗余了
```



**HTTP状态码**

https://developer.mozilla.org/en-US/docs/Web/HTTP/Status



**异常自定义流程**

1. 自定义异常类

   继承`RuntimeException`：在运行时抛出异常，通常在程序错误时被使用，如IllegalArgumentException

   继承Exception：在编译时检查异常，函数内catch或向调用者throws，通常在资源错误时被使用，如IOException

2. 定义一个ExceptionEntity类用来封装meesage、HttpStatus、ZoneDateTime等信息到响应体中

3. 定义一个注解了`@ControllerAdvice`的ExceptionHandler类

   使用`@ExceptionHandler(value = NotFoundException)`在各个方法上标注Handle的异常类

   参数为其Handle的Exception，通过ExceptionEntity包装错误信息和时间，返回值为ResponseEntity对象

   返回值构造方法中，传参ExceptionEntity实例，和HttpStatus（响应状态码）



### Spring Data

H2 Database：用Java语言实现，超级轻量级数据库(2MB jar file size)，通常使用InMemoery模式来测试数据



引入spring-boot-database-start和数据库，完成yml配置

```yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: tr
    password: 
  jpa:
    show-sql: true #显示Hibernate执行的sql
    database-platform: org.hibernate.dialect.H2Dialect
  h2:
    console:
      enabled: true
      path: /h2
```



创建Dao层接口，自动被扫描、实现、注入容器

接下来就可以使用他的实现类在Service层完成业务逻辑了！

```java
@Repository
public interface CustomerRepository
        extends JpaRepository<Customer,Long> { //封装类和主键类型

}
```



### Open Feign Rest Client

Feign makes writing java http clients easier

一个超级好用的REST客户端，微服务中互相调用接口使用



Free fake API for testing：http://jsonplaceholder.typicode.com/



SpringCloud为分布式项目提供解决方案（各种组件）

将SpringCloud依赖放到父模块的dependency manangement中，子模块引用时可以统一版本号和作用域



### Logging

SpringBoot-starter默认依赖Logback

```java
private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);
```

在配置文件中可以配置Log

> File --> Keymap --> Main Menu --> Code --> Code Completion --> SmartType和输入法切换冲突，切换成Shift + Tab，超级好用！



### SpringBoot Actuator and Metrics

生成可视化指标，帮助监管的生产级应用

```java
<dependency>
   <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

引入依赖后自动开放两个uri，health和info，我们访问http://localhost:8080/actuator即可看到



通过配置文件自定义应用的info

```yml
info:
  app:
    name: Test SpringBoot
    version: 1.0.0
  Author:
    name: Tyler
    age: 22
```



配置Actuator的终端URI信息

```yml
management:
  endpoints:
    web:
      exposure:
        include: 'health,info,prometheus' #对外开启这3个终端URI
  endpoint:
    health:
      show-details: always
```



**Micrometer**

引入prometheus依赖，Spring Boot Actuator发现依赖自动配置，在配置中开启endpoint即可

```java
<dependency>
   <groupId>io.micrometer</groupId>
   <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

> Micrometer provides a simple facade over the instrumentation clients for the most popular monitoring systems, allowing you to instrument your JVM-based application code without vendor lock-in. Think SLF4J, but for metrics.



### Application Properties and Profiles



**向属性注入值**

- 使用@Value("info.app.name")

- @Autowire Environment然后从中getProperty("info.app.version")

- 推荐：使用@ConfigurationProperties(prefiex = "info")

  支持relaxed binding：maps the Environment property to the bean property name even it is not an exact match. For example, dash-separated environment properties (app-name bound to appName) or capitalized properties as in PORT (bound to port).

  支持自动转换：根据metadata中的key对应类中的属性名

  type-safe：注入时会自动进行类型转换，而不是像前两种方法只能注入String类型数据；同时还支持使用Vliadation包中的注解对注入的值进行检查



**Profiles**

在运行jar包时加上参数，Springboot就能自动选择对应尾缀的application-xxx.yml配置文件

![image-20210913212532068](https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210913212543.png)	

application-dev.yaml：开发环境

application-staging.yaml：测试环境

application-prod.yaml：生产环境

​	

### Testing

Integration Test：程序启动时，测试所有的函数和接口

Diffblue自动生成，或者Shift + Ctrl + T自动生成



### Lombok

Project Lombok is a java library that automatically plugs into your editor and build tools, spicing up your java.
Never write another getter or equals method again, with one annotation your class has a fully featured builder, Automate your logging variables, and much more.



**@Data**

对于final的属性，@Data会为其生成对应的构造函数，而不会去生成set()函数，因为final属性修饰的属性不能被set()

如果所有都是final，就是一个AllArgsConstructor，如果都不是final，就是一个NoArgsConstructor



**record**

Java15中引入了record，public record pojo(String xxx, Integer xxx){}，自动生成private final属性，构造函数，getter，equals，hashcode，toString



**原理**

https://stackoverflow.com/questions/6107197/how-does-lombok-work

http://notatube.blogspot.com/2010/12/project-lombok-creating-custom.html



## Useful class

ResponseEntity

RestTemplate



### **URL & URI**

早期时将URI分为**URL地址定位和URN名字定位**

现在我们常常使用URL来代指所有网络地址`http:...`，所以听到URI时，经常给人造成困惑！

URL和URI与相对和绝对路径没有任何关系！

> https://www.w3.org/TR/uri-clarification/
>
> People who are well-versed in URI matters tend to use "URL" and "URI" in ways that seem to be interchangable. Among these experts, this isn't a problem. But among the Internet community at large, it is. People are not convinced that URI and URL mean the same thing, in documents where they (apparently) do. 



<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210913212552.png" alt="URI/URL Venn Diagram" style="zoom:50%;" />	

