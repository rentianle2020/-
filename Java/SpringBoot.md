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

   返回值构造方法中，传参ExceptionEntity实例，和HttpStatus（相应状态码）



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

