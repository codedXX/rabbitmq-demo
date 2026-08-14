package com.example.rabbitmqdemo.service;

import com.example.rabbitmqdemo.configration.RabbitMqConfig;
import org.springframework.amqp.core.MessageDeliveryMode;
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

        // 显式设置消息投递模式为 PERSISTENT，使消息在持久化队列中可落盘保存。
        // 此处持久化依赖 RabbitMQ 的持久化交换机和持久化队列配置共同生效。
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_KEY,
                message,
                rabbitMessage -> {
                    // 设置 deliveryMode=2；RabbitMQ 将此消息标记为持久消息。
                    rabbitMessage.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return rabbitMessage;
                },
                correlationData
        );
    }
}
