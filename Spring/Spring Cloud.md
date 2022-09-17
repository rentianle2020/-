服务发现Service Discovery：Netflix Eureka, Zookeeper

- with dynamic nature of cloud native application, 如果使用写死的URLs访问，会带来问题
- 服务发现允许微服务可以轻松的发现它需要调用服务的当前地址

网关API Gateway：Netflix Zuul, Srping Cloud Gateway

- route API requests to the correct service
- Zuul leverages 服务发现 & 负载均衡

**communication via HTTP request or via messaging**

服务调用&负载均衡 Routing and Load Balancing：Open Feign, Netflix Ribbon

异步信息 Messaging: RabbitMQ, Kafka

熔断降级Circuit Breakers：Netflix Hystrix

- Failure is inevitable, but user dont need to know
- circuit breakers help an application in the face of failure
- requests failed for certain times, circuit breakers tripped，返回一些cached info，减轻失败服务的压力

统一配置Configuration：Config

- 统一配置所有微服务，微服务找配置中心去要对应的config

链路追踪Tracing：Spring Cloud Sleuth and Zipkin

- One request may contains larger number of requets to various microservices
- Tracing requests through the application when debugging issue

认证：Spring Security（OAuth2/SSO）

消息总线：Bus



### Eureka

注册发现中心

> 与Zookeeper的区别？
>
> Zookeeper遵循CP，为了一致性要停机一段时间；而Eureka遵循AP，强调高可用



**Server**

```java
@SpringBootApplication
@EnableEurekaClient
public class EurekaClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }
}
```



```yaml
server:
  port: 8761
```



**Client**

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```



```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```



1. 注册中心中有容器保存服务的注册信息
2. 应用下线后注册中心怎么处理这个服务？建立心跳机制，隔一段时间发送一次请求来确认服务存活
   1. 主动下线
   2. 被动下线
3. 服务添加服务列表的本地缓存，定期向注册中心确认存活的服务。脏读问题？
4. 如果突然大量服务不响应，服务中心也不会将他们踢出，反而认为自己网络出现问题，保证AP



**集群**

使用Replicas来主从复制



**服务发现**

通过服务名称，找到服务实例，然后进行接口调用







### 分布式一致性协议





### paxos



### raft

