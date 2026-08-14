package com.example.rabbitmqdemo;

import com.example.rabbitmqdemo.service.MessageProducer;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MessageProducerTest {

    @Test
    void sendsMessageThroughDemoExchangeWithCorrelationData() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        MessageProducer producer = new MessageProducer(rabbitTemplate);

        producer.send("hello");

        verify(rabbitTemplate).convertAndSend(
                eq("demo.exchange"),
                eq("demo"),
                eq("hello"),
                any(CorrelationData.class)
        );
    }
}
