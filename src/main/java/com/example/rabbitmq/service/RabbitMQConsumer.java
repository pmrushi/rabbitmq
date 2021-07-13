package com.example.rabbitmq.service;

import com.example.rabbitmq.model.Order;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RabbitMQConsumer {

    public static final String RECEIVED_ORDER = "Received Order : ";
    private List<Order> orders = new ArrayList<>();

    @RabbitListener(queues = "${rabbitmq.direct.queue}")
    public void receivedDirectQueueMessage(Order order) {
        orders.add(order);
        System.out.println(RECEIVED_ORDER + order.toString());
    }

    @RabbitListener(queues = "${rabbitmq.fanout.queue1}")
    public void receivedFanoutQueue1Message(Order order) {
        orders.add(order);
        System.out.println(RECEIVED_ORDER + order.toString());
    }

    @RabbitListener(queues = "${rabbitmq.fanout.queue2}")
    public void receivedFanoutQueue2Message(Order order) {
        orders.add(order);
        System.out.println("Received Order : " + order.toString());
    }

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

    @RabbitListener(queues = "${rabbitmq.header.queue}")
    public void receivedHeaderQueueMessage(Order order) {
        orders.add(order);
        System.out.println("Received Order : " + order.toString());
    }

    public List<Order> getOrders() {
        return orders;
    }
}