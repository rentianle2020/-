# Spring Security

Spring Security is a framework that provides

- authentication：Who you are
- authorization：What you can do
- protecction against common attacks.



https://www.youtube.com/watch?v=lxmBJmUhqss

DelegatingFilterProxy就是一个由Tomcat管理的Filter，它内部拿到IOC容器，以FilterChain遍历的方式调用实现了Filter的Bean的doFilter()方法



## 准备开始！

```java
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```



## Authentication

**Form Based Auth**

<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210914193401.png" alt="image-20210914193349397" style="zoom:50%;" />	

Spring Security使用了一个内存数据库，存储并验证SESSIONID（Expire in 30mins of inactivity）

Remeber-me cookie被同事存在浏览器和数据库，用户以后再次来到登陆页面，自动发送remember-me cookie到后台验证，如果auth通过，则返回账号密码（默认两周）



**Basic Auth**

在请求头中添加Authorization: Basic ZGVtbzpwQDU1dzByZA==

简单快速，不能退出，建议在HTTPS网络环境下使用

<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210915214843.png" alt="image-20210915205406184" style="zoom:25%;" />	

here are a few issues with HTTP Basic Auth:

- The password is sent over the wire in base64 encoding (which can be easily converted to plaintext).
- The password is sent repeatedly, for each request. (Larger attack window)
- The password is cached by the webbrowser, at a minimum for the length of the window / process. (Can be silently reused by any other request to the server, e.g. CSRF).
- The password may be stored permanently in the browser, if the user requests. (Same as previous point, in addition might be stolen by another user on a shared machine).

Of those, using SSL only solves the first. And even with that, SSL only protects until the webserver - any internal routing, server logging, etc, will see the plaintext password.



### Authorization

Roles：角色

Authorities/Permissions：权限

PasswordEncoder：密码加密



**Role Base Authentication**

通过给用户授权Role & API验证授权Role的方式，完成对接口的保护



**Permission Base Authentication**

通过Enum管理Role和Permission的关系，每个Role枚举对象包含一个Permission集合

通过给用户授权Permission & API验证授权Permission的方式，完成对接口的保护

> Role和Permission都被封装在GrantedAuthority实现类的集合中，只不过Role的String标识符中有一个"ROLE_"前缀
>
> GrantedAuthority集合被封装在UserDetails实现类的对象中，最终给到Spring Security用来Authorization



**PreAuthoize**

使用注解的方式完成权限验证

开启`@EnableGlobalMethodSecurity(prePostEnabled = true)`

在Controller方法上面注解`@PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_STUDENT')")`



## CSRF

Cross Site Request Forgery

- The attacker's page will trigger an HTTP request to the vulnerable web site.
- If the user is logged in to the vulnerable web site, their **browser will automatically include their session cookie in the request** (assuming [SameSite cookies](https://portswigger.net/web-security/csrf/samesite-cookies) are not being used).
- The vulnerable web site will process the request in the normal way, treat it as having been made by the victim user, and change their email address.

Spring官方建议，只要是用户通过浏览器访问的服务，都把CRSF打开，这样就不会被挟持session cookie进来乱搞了！



### 理解

发回来的cookie，token都是在登录的时候返回；用户在请求中携带这些串，就可以直接获得登陆状态了



## JWT

JSON Web Token

1. Fast
2. Stateless
3. Used across many services



# Laurentiu Spilca

Spring Security主要架构

<img src="C:/Users/%E4%B9%90%E4%B9%90%E5%A4%A7%E5%93%A5%E5%93%A5/Desktop/%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/Spring/assets/image-20210917161000188-1631866202156.png" alt="image-20210917161000188" style="zoom:50%;" />	

UserDetailService通过username拿到UserDetail信息返回

再找PasswordEncoder验证密码的正确性并返回



**UserDetailsManager**

UserDetailsManager扩展了UserDetailService接口，实现这个接口的类额外实现了增删改查用户的功能

3种UserDetailsManager实现方式

- InMemoryUserDetailsManager 在内存中存储用户信息
- JdbcUserDetailsManager JDBC访问数据库，数据库结构需要遵循它的硬编码的sql
- 自己实现，自己封装



**PasswordEncoder**

BCryptPasswordEncoder/ScryptPasswordEncoder/Pbkdf2PasswordEncoder

其他的都不安全了，被标注为Deprecated



![image-20210917163648060](C:/Users/%E4%B9%90%E4%B9%90%E5%A4%A7%E5%93%A5%E5%93%A5/Desktop/%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/Spring/assets/image-20210917163648060.png)



**Authentication Provider**

注入UserDetailService和PasswordEncoder，实现该接口的两个方法

authenticate(Authentication auth)

- 通过验证，return Authentication instance
- 不通过验证，抛异常

supports(Class<?> class)

- 是否支持该Authentication实现类，默认为UsernamePasswordAuthenticationToken

  不同的Authentication实现类就像门禁卡和钥匙的区别一样



**Authentication Filter（filter chian）**

从request中拿到Authentication所需的，传给AuthenticationManager，找到合适的Provider，如果有Provider验证了，且没有报错，会将Authentication对象设置为isAuthenticated，再传回。

Filter通过isAuthenticated()判断是否继续filter chain



**otp**

第一次密码登录，生成OTP（One-Time-Password）

通过邮箱或者短信等形式将OTP给到用户，进行第二次验证

通常是有时限的（使用数据库，schedule任务）



**Security Context**

- 直接在Controller参数中Authentication，自动获取
- SecurityContextHolder.getContext().getAuthentication()

这两种方法的前提，就是我们在Filter验证后，将Authentication set()到Context中



SecurityContextHolder是一个存放Auth的容器

默认是ThreadLocal策略，一旦出现多线程执行，则无法从Holder中获取Auth了

第二种测录是InheritableThreadLocal，如果@Async让Spring创建线程，Spring就能将父线程的变量传给子线程，如果是自己run一个线程，不管用！



改变策略

使用DelegatingSecurityContextRunnable对象，装饰线程对象，执行的时候会将SecurityContext传过去



**CSRF**

让点击链接的人，做一些他们不想做的操作，比如delete一些东西，或者update密码...

利用了cookie:JSESSIONID

开启之后，袭击者就无法知道csrf token，无法随便访问了！

理论上，使用中间人攻击是可以偷到csrf token，但是由于token是时限的，让攻击很难进行



什么时候不需要csrf？OAUTH2情况下，因为实现方式不一样，使用另一种token，不需要同时csrf



**CORS**

<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210923164335.png" alt="image-20210923164318245" style="zoom:50%;" />	

两个不同端口也是算不同源different origin，前端在4200，后端在8080

在CORS发生的情况下，服务器还是会执行请求任务，只是在返回时被浏览器拒绝了！

必须在reponse header中指出Access-Control-Allow-Origin：我的源头，浏览器才接收



解决方法

- 在接口上注解@CrossOrigin("*")
- 在配置类中http.cors()



**OAUTH2**

<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210923175545.png" alt="image-20210923175528450" style="zoom: 50%;" />	



获取token

GrantTypes：

- authorization_code：直接通过账号密码访问Auth服务器，redirect到客户端
- client_credentails：
- refresh_token：可以通过这个token，再次获得新的授权
- password（过时）：在客户端输入账号密码，传到Auth服务器拿Token
- ~~Implicit~~



Auth服务器不仅得认识用户user，还要认识客户端client；不会随便给一个不认识的客户端传输token



**opaque token / UUID token**：不包含任何信息的令牌，99.99%的情况下用JWT



token存放位置

- 默认内存
- **存放在auth端的数据库**



验证方式

- 将R和A放在一个应用，共享了same token store in same server
- **R去找A验证**
- Blackboarding：使用shared database



这种方式，用来验证的账号密码不经过client

为什么要用code换token？直接拿token不可以吗

Implicit Grant Type就是这样，直接拿token

防止callback地址造假，攻击者就直接拿到access token了！

authorization_code情况下，攻击者还要证明他的client : secret

<img src="C:/Users/%E4%B9%90%E4%B9%90%E5%A4%A7%E5%93%A5%E5%93%A5/Desktop/%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/Spring/assets/image-20210924174545187.png" alt="image-20210924174545187" style="zoom:50%;" />	

请求头中：key：Authorization  value：Bearer xxx(access token)

R得去找A问一下token的真实性（调用一个A的查询token endpoint）

若验证通过，就可以给资源了



non-opaque token：JWT（JSON Web Token），通过basic64加密后的JSON字符串

为了让JSON编程一个更短的token，can be decoded into something meaningful



Symmetric key：加密和解密token的key是一样的！sign和validate用的key是一样的，如果被偷了，攻击者就可以自己制造token了！

only provide the minimum privalages



问题：我们不可能和微信的加密key互通有无，否则我们就可以模仿微信进行加密了！所以加密使用的key和validate使用的key不能是一样的

sharing secret with less entity is better

我们只让Auth Server可以sign token



keytool -genkeypair -alias ssia -keyalg RSA -keypass ssia123 -keystore ssia.jks -storepass ssia123

keytool -list -rfc --keystore ssia.jks | openssl x509 -inform pem -pubkey



Auth Server：为什么要key pair即便只用private key来sign token

Resource Server：可以拿着public key，也可以在验证的时候找Auth Server要一下public key



**Keycloak**

一个Auth Server服务器，可以使用web界面来设置各种



**SSO**

我们只是一个Client

我们不需要自己搭建RS或者AS

只需要注册一个Client，想AS要token，找RS要资源即可。