package com.example.rabbitmq.controller;

import com.example.rabbitmq.model.Order;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/orders/header/")
public class RabbitMQHeaderWebController {

    @Value("${rabbitmq.header.exchange}")
    String exchange;

    @Value("${rabbitmq.header.matchKey}")
    String matchKey;

    @Value("${rabbitmq.header.matchValue}")
    String matchValue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping(value = "/producer")
    public Order producer(@RequestBody Order order) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader(matchKey, matchValue);
        MessageConverter messageConverter = new Jackson2JsonMessageConverter();
        Message message = messageConverter.toMessage(order, messageProperties);
        rabbitTemplate.convertAndSend(exchange, "", message);
        return order;
    }
}