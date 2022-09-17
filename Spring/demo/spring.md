### Autoconfigurations

https://www.marcobehler.com/guides/spring-boot-autoconfiguration#_introduction

### RESTful Web Service

Spring依赖于Jackson2，帮助我们将返回的对象转换为JSON格式。

```java
@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}
}
```

### Scheduling Tasks

在方法上添加@Schedule(fixedRate / fixedDelay)，表示将该方法设置为定时任务

并在启动类上添加@EnableScheduling注释，表示开启定时任务

```java
@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
		log.info("The time is now {}", dateFormat.format(new Date()));
	}
}
```

可以使用`awaitility`类库，完成对定时任务的测试

```java
@Test
public void reportCurrentTime() {
    //在至多10秒内，执行至少2次reportCurrentTime()
	await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> {
		verify(tasks, atLeast(2)).reportCurrentTime();
	});
}
```

### Consuming REST

使用RestTemplate向REST接口发送请求，返回值会自动封装到对象中

可以使用@JsonIgnoreProperties, @JsonProperty等注解自定义JSON转换格式。

```java
@Bean
public RestTemplate restTemplate(RestTemplateBuilder builder) {
	return builder.build();
}

@Bean
public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
	return args -> {
		Post post = restTemplate.getForObject(
			"https://jsonplaceholder.typicode.com/posts/1", Post.class);
		log.info(post.toString());
	};
}
```

### Maven

在POM文件中声明各种MAVEN配置如groupId，artifactId，version...

https://spring.io/guides/gs/maven/

compile -> test -> package(into jar) -> install(save to local)

> Maven uses a plugin called "surefire" to run unit tests. The default configuration of this plugin compiles and runs all classes in `src/test/java` with a name matching `*Test`.

### Upload File

前端上传{"file", file}，后端使用@RequestParam MultipartFile接收

接下来操作持久化file即可

```java
@PostMapping("/")
public String handleFileUpload(@RequestParam("file") MultipartFile file,
		RedirectAttributes redirectAttributes) {

	storageService.store(file);
	redirectAttributes.addFlashAttribute("message",
			"You successfully uploaded " + file.getOriginalFilename() + "!");

	return "redirect:/";
}
```

指定文件上传大小，防止DDOS

```yaml
spring.servlet.multipart.max-file-size=128KB
spring.servlet.multipart.max-request-size=128KB
```

### Redis Messageing

依赖`spring-boot-starter-data-redis`

依次创建

- Receiver：自定义接收代理
- MessageListenerAdapter(Receiver) ：使用Receiver作为代理
- RedisMessageListenerContainer(RedisConnectionFactory, MessageListenerAdapter)
  ：subscribe对应的channel，并将该channel与listener映射

```java
@Bean
RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
										MessageListenerAdapter listenerAdapter) {

	RedisMessageListenerContainer container = new RedisMessageListenerContainer();
	container.setConnectionFactory(connectionFactory);
	container.addMessageListener(listenerAdapter, new PatternTopic("chat"));

	return container;
}

@Bean
MessageListenerAdapter listenerAdapter(Receiver receiver) {
	return new MessageListenerAdapter(receiver, "receiveMessage");
}

@Bean
Receiver receiver() {
	return new Receiver();
}
```

