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