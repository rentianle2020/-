# Test



spring-boot-starter-test

- org.junit.jupiter： API to create Test + Platform to run test on JVM = Everything we need for JUnit5
- org.junit.vintage：backward compatability to JUnit4（不需要）
- mockito
- assertJ
- hamcrest



## Coverage Test

绿色部分表示测试通过，红色部分表示未测试/测试未通过

**报错**

Idea --> Help --> Edit Custom VM Option --> 在最下面添加 `-Djava.io.tmpdir=C:\Temp` --> 重启Idea



## AssertJ

https://assertj.github.io/doc/



## Mockito

https://javadoc.io/doc/org.mockito/mockito-core/latest/index.html

Unit Testing：如果一个类的Test通过了，其他类对其有依赖，直接Mock一个即可！



## JPA

@DataJpaTest：访问内存数据库

@AutoConfigureTestDatabase(replace = Replace.NONE)：访问生产数据库，不要用内存数据库替换

@Rollback(value = false)：让测试不自动rollback

如果要让@Column中的validation生效

需要添加properties = {"spring.jpa.properties.javax.persistence.validation.mode=none"}



## Unit Testing

@Mock

@Captor

@BeforeEach

given(...).willreturn(...)

then(...).should()



## Itegration Testing

@SpringBootTest

MockService：@ConditionalOnProperty(value="stripe.enabled",havingvalue=“false)



@AutoConfigureMockMvc

@Autowire MockMvc：在IT中，只使用这个对象就可以完成测试了，没必要autowire其他的，所有都通过模拟访问uri来完成



Dont Autowire Controller in IT，Use MockMvc！



https://provesrc.com/how-it-works/



## Test Driven Development

@ParameterizedTest + @CsvSource({"xxx,xxx","yyy,yyy","zzz,zzz"})，可以通过传参赋值来测试多组不同的参数

@DisplayName("asdasdasd")

