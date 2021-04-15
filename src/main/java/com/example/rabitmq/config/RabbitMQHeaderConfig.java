package com.example.rabitmq.config;

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