package com.example.rabitmq.service;

import com.example.rabitmq.model.Order;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RabbitMQConsumer {

    private List<Order> orders = new ArrayList<>();

    //@RabbitListener(queues = "${rabbitmq.queue}")
    public void receivedMessage(Order order) {
        orders.add(order);
        System.out.println("Received Order : " + order.toString());
    }

    public List<Order> getOrders() {
        return orders;
    }
}