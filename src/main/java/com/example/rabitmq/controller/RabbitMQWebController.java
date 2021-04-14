package com.example.rabitmq.controller;

import com.example.rabitmq.model.Order;
import com.example.rabitmq.service.RabbitMQConsumer;
import com.example.rabitmq.service.RabbitMQSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/orders")
public class RabbitMQWebController {

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private RabbitMQConsumer rabbitMQConsumer;

    @PostMapping(value = "/producer")
    public String producer(@RequestBody Order order) {
        rabbitMQSender.send(order);
        return "Order sent Successfully : " + order.toString();
    }

    @GetMapping(value = "/consumer")
    public List<Order> getOrders() {
        return rabbitMQConsumer.getOrders();
    }
}