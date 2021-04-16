package com.example.rabbitmq.controller;

import com.example.rabbitmq.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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