# Messaging with RabbitMQ
This guide walks you through the process of setting up a RabbitMQ AMQP server that publishes and subscribes to messages and creating a Spring Boot application to interact with that RabbitMQ server.

Before you can build your messaging application, you need to set up a RabbitMQ server to handle
receiving and sending messages.

You can  use https://docs.docker.com/compose/[Docker Compose] to quickly launch a RabbitMQ server, if you have Docker running locally. 
There is a `docker-compose.yml` in the root of the `complete` project. 
It is very simple, as the following listing shows:
```
 rabbitmq:
    image: rabbitmq:3-management
    hostname: rabbitmq
    ports:
      - 5672:5672
      - 15672:15672
```
With this file in the current directory, you can run below command to get RabbitMQ server running in a container.
```
docker-compose up
```
Login to RabbitMQ server using below url
```
http://localhost:15672/

By default
Username: guest
Password: guest

```
## Starting with Spring Initializr
If you use Gradle, visit the Spring Initializr (https://start.spring.io/) to generate a new project with the required dependency (Spring for RabbitMQ).

'org.springframework.boot:spring-boot-starter-amqp' dependency used for RabbitMQ

The following shows the `build.gradle` file that is created when you choose Gradle:  
```
plugins {
	id 'org.springframework.boot' version '2.4.4'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-amqp'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testImplementation 'org.springframework.amqp:spring-rabbit-test'
}

test {
	useJUnitPlatform()
}

```

### Register the Listener and Send a Message

Spring AMQP's `RabbitTemplate` provides everything you need to send and receive messages
with RabbitMQ. However, you need to:

- Configure a message listener container.
- Declare the queue, the exchange, and the binding between them.
- Configure a component to send some messages to test the listener.

Spring Boot automatically creates a connection factory and a RabbitTemplate,
reducing the amount of code you have to write.

``
Exchanges & Queue no need to create Manually, when we run application it will create automaticaaly.
``

## Exchange Types
RabbitMQ has the following types of Exchanges :

* Direct Exchange
* Fanout Exchange
* Topic Exchange
* Header Exchange

### Direct Exchange
Based on the routing key a message sent to the queue having the same routing key specified in the binding rule. 
The routing key of exchange, and the binding queue have to be an exact match. 
A message sent to exactly one queue.

## RabbitMQ Direct Exchange configuration 
src/main/java/com/example/rabbitmq/config/RabbitMQDirectConfig.java

```
package com.example.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQDirectConfig {

    @Value("${rabbitmq.direct.queue}")
    String queueName;

    @Value("${rabbitmq.direct.exchange}")
    String exchange;

    @Value("${rabbitmq.direct.routingKey}")
    private String routingKey;

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitNewTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
```
The `queue()` method creates an AMQP queue. 
The `exchange()` method creates exchange. 
The `binding()` method binds these two together, defining the behavior that
occurs when `RabbitTemplate` publishes to an exchange. 
Spring AMQP requires that the `Queue`, the `Exchange`, and the `Binding` be
declared as top-level Spring beans in order to be set up properly.

### Send a Direct Exchange Message using REST API
src/main/java/com/example/rabbitmq/controller/RabbitMQDirectWebController.java

```
@RestController
@RequestMapping(value = "/api/v1/orders/direct")
public class RabbitMQDirectWebController {

    @Value("${rabbitmq.direct.exchange}")
    String exchange;

    @Value("${rabbitmq.direct.routingKey}")
    private String routingKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping(value = "/producer")
    public Order producer(@RequestBody Order order) {
        rabbitTemplate.convertAndSend(exchange, routingKey, order);
        return order;
    }
}
```
### Create a RabbitMQ Message Receiver
src/main/java/com/example/rabbitmq/service/RabbitMQConsumer.java
```
    @RabbitListener(queues = "${rabbitmq.direct.queue}")
    public void receivedDirectQueueMessage(Order order) {
        orders.add(order);
        System.out.println("Received Order : " + order.toString());
    }

```
The RabbitMQ Listener(RabbitListener class) listens to RabbitMQ Queue for any incoming messages. 
For the basic configuration we specify the Queue/Topic Name (the name of the queue/topic where the message should be consumed)

## Fanout Exchange
The message routed to all the available bounded queues. 
The routing key if provided is completely ignored. So this is a kind of publish-subscribe design pattern.

### RabbitMQ Fanout Exchange configuration
src/main/java/com/example/rabbitmq/config/RabbitMQFanoutConfig.java

```
package com.example.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQFanoutConfig {

    @Value("${rabbitmq.fanout.queue1}")
    String queueName1;

    @Value("${rabbitmq.fanout.queue2}")
    String queueName2;

    @Value("${rabbitmq.fanout.exchange}")
    String exchange;

    @Bean
    Queue orderFanoutQueue1() {
        return new Queue(queueName1, false);
    }

    @Bean
    Queue orderFanoutQueue2() {
        return new Queue(queueName2, false);
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange(exchange);
    }

    @Bean
    Binding fanoutBinding1(Queue orderFanoutQueue1, FanoutExchange exchange) {
        return BindingBuilder.bind(orderFanoutQueue1).to(exchange);
    }

    @Bean
    Binding fanoutBinding2(Queue orderFanoutQueue2, FanoutExchange exchange) {
        return BindingBuilder.bind(orderFanoutQueue2).to(exchange);
    }
}
```

### Send a Fanout Exchange Message using REST API
src/main/java/com/example/rabbitmq/controller/RabbitMQFanoutWebController.java

```
@RestController
@RequestMapping(value = "/api/v1/orders/fanout/")
public class RabbitMQFanoutWebController {

    @Value("${rabbitmq.fanout.exchange}")
    String exchange;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping(value = "/producer")
    public Order producer(@RequestBody Order order) {
        rabbitTemplate.convertAndSend(exchange, "", order);
        return order;
    }
}
```
### Create a RabbitMQ Message Receiver
src/main/java/com/example/rabbitmq/service/RabbitMQConsumer.java
```
    @RabbitListener(queues = "${rabbitmq.fanout.queue1}")
    public void receivedFanoutQueue1Message(Order order) {
        orders.add(order);
        System.out.println("Received Order : " + order.toString());
    }

    @RabbitListener(queues = "${rabbitmq.fanout.queue2}")
    public void receivedFanoutQueue2Message(Order order) {
        orders.add(order);
        System.out.println("Received Order : " + order.toString());
    }

```

## Topic Exchange
Here again the routing key made use of, but unlike in direct exchange type, here the routing key of the exchange, and the bound queues should not necessarily be an exact match. 
Using regular expressions like wildcard we can send the exchange to multiple bound queues.

### RabbitMQ Topic Exchange configuration
src/main/java/com/example/rabbitmq/config/RabbitMQTopicConfig.java
```
package com.example.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopicConfig {

    @Value("${rabbitmq.topic.queue}")
    String queueName;

    @Value("${rabbitmq.topic.allqueue}")
    String allQueueName;

    @Value("${rabbitmq.topic.exchange}")
    String exchange;

    @Value("${rabbitmq.topic.routingKey}")
    private String routingKey;

    @Value("${rabbitmq.topic.routingAllKey}")
    private String routingAllKey;

    @Bean
    Queue topicQueue() {
        return new Queue(queueName, false);
    }

    @Bean
    Queue allQueue() {
        return new Queue(allQueueName, false);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    Binding topicBinding(Queue topicQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicQueue).to(topicExchange).with(routingKey);
    }

    @Bean
    Binding allBinding(Queue allQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(allQueue).to(topicExchange).with(routingAllKey);
    }
}
```
### Send a Topic Exchange Message using REST API
src/main/java/com/example/rabbitmq/controller/RabbitMQTopicWebController.java
```
@RestController
@RequestMapping(value = "/api/v1/orders/topic/")
public class RabbitMQTopicWebController {

    @Value("${rabbitmq.topic.exchange}")
    String exchange;

    @Value("${rabbitmq.topic.routingKey}")
    private String routingKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping(value = "/producer")
    public Order producer(@RequestBody Order order) {
        rabbitTemplate.convertAndSend(exchange, routingKey, order);
        return order;
    }
}
```
### Create a RabbitMQ Message Receiver
src/main/java/com/example/rabbitmq/service/RabbitMQConsumer.java
```
    @RabbitListener(queues = "${rabbitmq.topic.queue}")
    public void receivedTopicQueueMessage(Order order) {
        orders.add(order);
        System.out.println("Received Order : " + order.toString());
    }

    @RabbitListener(queues = "${rabbitmq.topic.allqueue}")
    public void receivedTopicAllQueueMessage(Order order) {
        orders.add(order);
        System.out.println("Received Order : " + order.toString());
    }
```

## Header Exchange
In this type of exchange the routing queue selected based on the criteria specified in the headers instead of the routing key. 
This is similar to topic exchange type, but here we can specify complex criteria for selecting routing queues.
### RabbitMQ Header Exchange configuration
src/main/java/com/example/rabbitmq/config/RabbitMQTopicConfig.java
```
package com.example.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQHeaderConfig {

    @Value("${rabbitmq.header.queue}")
    String queueName;

    @Value("${rabbitmq.header.exchange}")
    String exchange;

    @Value("${rabbitmq.header.matchKey}")
    String matchKey;

    @Value("${rabbitmq.header.matchValue}")
    String matchValue;

    @Bean
    Queue orderHeaderQueue() {
        return new Queue(queueName, false);
    }

    @Bean
    HeadersExchange headerExchange() {
        return new HeadersExchange(exchange);
    }

    @Bean
    Binding headerBinding(Queue orderHeaderQueue, HeadersExchange headerExchange) {
        return BindingBuilder.bind(orderHeaderQueue).to(headerExchange).where(matchKey).matches(matchValue);
    }
}
```

### Send a Header Exchange Message using REST API
src/main/java/com/example/rabbitmq/controller/RabbitMQTopicWebController.java
```
@RestController
@RequestMapping(value = "/api/v1/orders/header/")
public class RabbitMQHeaderWebController {

    @Value("${rabbitmq.header.exchange}")
    String exchange;

    @Value("${rabbitmq.header.matchKey}")
    String matchKey;

    @Value("${rabbitmq.header.matchValue}")
    String matchValue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping(value = "/producer")
    public Order producer(@RequestBody Order order) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader(matchKey, matchValue);
        MessageConverter messageConverter = new Jackson2JsonMessageConverter();
        Message message = messageConverter.toMessage(order, messageProperties);
        rabbitTemplate.convertAndSend(exchange, "", message);
        return order;
    }
}
```
### Create a RabbitMQ Message Receiver
src/main/java/com/example/rabbitmq/service/RabbitMQConsumer.java
```
    @RabbitListener(queues = "${rabbitmq.header.queue}")
    public void receivedHeaderQueueMessage(Order order) {
        orders.add(order);
        System.out.println("Received Order : " + order.toString());
    }
```

### Run the application 
If you use Gradle, you can run the application by using below command
```
./gradlew bootRun
gradlew.bat bootRun (for Windows)
```
Alternatively, you can build the JAR file by using below command 
```
./gradlew build
gradlew.bat build (for Windows)
```
Then run the JAR file as below
```
java -jar build/libs/rabbitmq-0.0.1-SNAPSHOT.jar
```

### Rest Endpoints to test different exchange types of messages

Direct exchange - POST - http://localhost:9001/api/v1/orders/direct/producer

Fanout exchange - POST - http://localhost:9001/api/v1/orders/fanout/producer

Topic exchange - POST - http://localhost:9001/api/v1/orders/topic/producer

Header exchange - POST - http://localhost:9001/api/v1/orders/header/producer

Body

```
{
    "orderId": "order",
    "itemName": "pen",
    "quantity": 2
}
```

Received Orders

GET - http://localhost:9002/api/v1/orders/consumer

# Reference links:
https://spring.io/guides/gs/messaging-rabbitmq/

https://www.rabbitmq.com/tutorials/amqp-concepts.html

https://www.javainuse.com/messaging/rabbitmq/exchange





