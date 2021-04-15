package com.example.rabitmq.controller;

import com.example.rabitmq.model.Order;
import com.example.rabitmq.service.RabbitMQConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/orders/")
public class RabbitMQConsumerWebController {

    @Autowired
    private RabbitMQConsumer rabbitMQConsumer;

    @GetMapping(value = "/consumer")
    public List<Order> getOrders() {
        return rabbitMQConsumer.getOrders();
    }
}