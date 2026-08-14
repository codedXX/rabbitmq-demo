package com.example.rabbitmqdemo.configration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    // 输出生产者确认结果和消息退回结果。
    private static final Logger log = LoggerFactory.getLogger(RabbitMqConfig.class);

    // 队列名称；消费者会监听这个队列。
    public static final String QUEUE_NAME = "demo.queue";

    // 交换机名称；生产者把消息发送到这个交换机。
    public static final String EXCHANGE_NAME = "demo.exchange";

    // Direct 交换机使用的路由键；必须和消费者绑定的 key 一致。
    public static final String ROUTING_KEY = "demo";

    @Bean
    public RabbitTemplateCustomizer rabbitTemplateCustomizer() {
        return rabbitTemplate -> {
            // 开启 mandatory：消息无法从交换机路由到队列时，触发 ReturnsCallback。
            rabbitTemplate.setMandatory(true);

            // ConfirmCallback：确认消息是否成功到达交换机。
            rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                // CorrelationData 用来把 RabbitMQ 的确认结果关联回原始消息。
                String correlationId = correlationData == null ? "unknown" : correlationData.getId();
                if (ack) {
                    // ack=true：交换机已经接收消息。
                    log.info("publisher confirm success, correlationId={}", correlationId);
                } else {
                    // ack=false：交换机没有确认消息，cause 中包含失败原因。
                    log.error("publisher confirm failed, correlationId={}, cause={}",
                            correlationId, cause);
                }
            });

            // ReturnsCallback：交换机接收了消息，但无法把消息路由到任何队列时触发。
            rabbitTemplate.setReturnsCallback(returned -> log.error(
                    "publisher return, exchange={}, routingKey={}, replyCode={}, replyText={}",
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyCode(),
                    returned.getReplyText()
            ));
        };
    }
}
