# SpringSecurity

- WebSecurityConfigurerAdapter：自定义Sercurity策略
- AuthenticationManagerBuilder：自定义认证策略
- @EnableWebSecurity：开启WebSecurity模式

Spring Security的两个主要目标是“认证”和“授权”（访问控制）

这个概念是通用的，而不只是在Spring Security中存在



**链式编程**

```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/pageOne").hasRole("vip1")
                .antMatchers("/pageTwo").hasRole("vip2");

        //没有权限会到登录页面
        http.formLogin().loginPage("/toLogin");

        //把这个安全码得disable掉，就可以自定义login页面了！
        //否则自定义的login页面，每次也得传这个安全码值
        http.csrf().disable();

        //开启注销功能,清除cookie和session,添加退出登录后的地址
        http.logout().deleteCookies("remove").invalidateHttpSession(true).logoutSuccessUrl("/");

        //记住账号密码 就是往cookie中丢一个值
        http.rememberMe().rememberMeParameter("remember");
    }

    //自定义认证策略
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .withUser("tyler").password(new BCryptPasswordEncoder().encode("123")).roles("vip1","vip2")
                .and()
                .withUser("test").password(new BCryptPasswordEncoder().encode("123")).roles("vip1");
    }
    //There is no PasswordEncoder mapped for the id "null" 密码没被加密，明文密码不安全！我们必须加密
}
```



### Shiro

简单理解：

`ShiroFilterFactoryBean`是一个`FactoryBean`，它在getObject中定义了一个Filter

这个Filter拦截所有路径，可以看为一个DispatcherServlet的Filter版本，其中有一个map来映射请求路径和权限要求。

每次请求进来，判断是否需要认证，还是直接通过。

容器启动的时候，调用了`ShiroFilterFactoryBean`的`getObject()`方法，将Filter实例化到容器

> 实现原理：一个类同时实现FactoryBean和PostBeanProcessor，且getObject()返回的是一个Filter/Servlet/Listener，就可以直接被实例化！



原理猜测：

PostBeanProcessor更早实例化，将beanname和getObject()返回的Filter实现类关联起来，这样的话，等Filter开始注册时，这个bean就会被扫描到（beanname对应filter实现类）

如果只用FactoryBean的话，关联的Type还是FactoryBean的实现类，所以Filter开始扫描注册的时候，匹配会失败！

因此先猜测：FactoryBean在被扫描为BeanDefinition的时候，是很普通的，关联的Class和beanname一致。

直到它实例化后，调用getBean("MyFactoryBean")，才会判断是否为FactoryBean的实现类，并将其注入容器，然后改变该beanname关联的对象



真实原理：

先后初始化的问题

Filter的集中注册，是在将所有Filter初始化之后进行的，会去Singleton单例池中寻找和beanName一样的类，并拿回来进行比对

它先拿到MyFactoryBean对象，再调用它的getObjectType()方法，在拿到真实类，在判断是否为Filter

但是前提是它拿的到MyFactoryBean对象！这个和容器的初始化顺序有关！

先把root容器，也就是WebApplicationContext弄好！然后开始扫Servlet/Listener/Filter，所以之前在root容器中initialization之前实例化的那些类，都能被找到。

而一个普通的Component，应该要等到后面了！

![image-20210512005651111](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210512005651111.png)

问题来了，那么直接implement Filter的类是怎么被注入的呢？

很简单，如果在匹配Filter实现类的时候，

先从单例池中找，没找到的话

去Attempt to predict the bean type，说是predict其实就是用类本身

用官方的话说：If we don't have a bean type, fallback to the predicted type

然后去看这个类本身是不是一个Filter实现类，发现是！就先放入集合，统计好了一个个实例化！

> 所以shiro是图省事了，既当BeanPostProcessor，又通过FactoryBean自定义Filter的实例化过程；但是这个逻辑真的恶心人，不知道其他框架是否也这么对待spring整合