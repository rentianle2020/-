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

