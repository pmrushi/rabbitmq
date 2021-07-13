package com.example.rabbitmq.controller;

import com.example.rabbitmq.model.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitMQDirectWebControllerTest {

    @Mock
    RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMQDirectWebController rabbitMQDirectWebController;

    @Test
    void producer() {
        Order order = new Order();
        doNothing().when(rabbitTemplate).convertAndSend(null, null, order);
        Assertions.assertEquals(rabbitMQDirectWebController.producer(order), order);
        verify(rabbitTemplate).convertAndSend(null, null, order);
    }
}