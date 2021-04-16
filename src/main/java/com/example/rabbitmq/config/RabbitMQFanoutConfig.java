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