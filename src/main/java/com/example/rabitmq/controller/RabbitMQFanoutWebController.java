package com.example.rabitmq.controller;

import com.example.rabitmq.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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