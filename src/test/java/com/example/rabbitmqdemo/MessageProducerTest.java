package com.example.rabbitmqdemo;

import com.example.rabbitmqdemo.service.MessageProducer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class MessageProducerTest {

    @Test
    void sendsPersistentMessageThroughDemoExchangeWithCorrelationData() {
        // 使用 Mock 隔离 RabbitMQ 服务，验证生产者提交给 RabbitTemplate 的参数。
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        MessageProducer producer = new MessageProducer(rabbitTemplate);
        ArgumentCaptor<MessagePostProcessor> postProcessorCaptor = forClass(MessagePostProcessor.class);

        producer.send("hello");

        // 验证发送目标、原始消息、持久化处理器及消息关联数据均正确。
        verify(rabbitTemplate).convertAndSend(
                eq("demo.exchange"),
                eq("demo"),
                eq("hello"),
                postProcessorCaptor.capture(),
                any(CorrelationData.class)
        );

        // 执行被捕获的处理器，验证它将 RabbitMQ 消息标记为持久消息。
        Message rabbitMessage = new Message("hello".getBytes(), new MessageProperties());
        Message processedMessage = postProcessorCaptor.getValue().postProcessMessage(rabbitMessage);

        org.junit.jupiter.api.Assertions.assertEquals(
                MessageDeliveryMode.PERSISTENT,
                processedMessage.getMessageProperties().getDeliveryMode()
        );
        verifyNoMoreInteractions(rabbitTemplate);
    }
}
