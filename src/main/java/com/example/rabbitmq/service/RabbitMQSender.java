package com.example.rabbitmq.service;

import com.example.rabbitmq.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.direct.exchange}")
    private String exchange;

    @Value("${rabbitmq.direct.routingKey}")
    private String routingKey;

    public void send(Order order) {
        rabbitTemplate.convertAndSend(exchange, routingKey, order);
        System.out.println("Send Order = " + order);
    }
}