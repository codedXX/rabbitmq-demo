package com.example.rabbitmqdemo.service;

import com.example.rabbitmqdemo.configration.RabbitMqConfig;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MessageProducer {

    // RabbitTemplate 是生产者发送 RabbitMQ 消息的核心对象。
    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(String message) {
        // 为本次消息生成唯一 ID，ConfirmCallback 可以通过它识别是哪条消息。
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        // 把消息发送到 demo.exchange，并使用 demo 路由键。
        // exchange、routingKey、message、correlationData 的顺序分别是：
        // 交换机、路由键、消息内容、消息关联数据。
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_KEY,
                message,
                correlationData
        );
    }
}
