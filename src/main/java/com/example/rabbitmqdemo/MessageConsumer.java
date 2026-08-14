package com.example.rabbitmqdemo;

import com.example.rabbitmqdemo.configration.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    // 输出消费者收到的消息。
    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    // @RabbitListener 会创建后台监听容器，应用启动后自动监听队列。
    // bindings 会声明队列、交换机以及交换机到队列的绑定关系。
    @RabbitListener(bindings = @QueueBinding(
            // 显式声明并监听持久化队列；RabbitMQ 重启后队列定义仍会保留。
            value = @Queue(name = RabbitMqConfig.QUEUE_NAME, durable = "true"),
            // 显式声明持久化 Direct 交换机；RabbitMQ 重启后交换机定义仍会保留。
            exchange = @Exchange(
                    name = RabbitMqConfig.EXCHANGE_NAME,
                    type = ExchangeTypes.DIRECT,
                    durable = "true"
            ),
            // 只有路由键为 demo 的消息才会路由到 demo.queue。
            key = RabbitMqConfig.ROUTING_KEY
    ))
    public void receive(String message) {
        // 消费者从队列中取出消息后执行这里的业务逻辑。
        log.info("received message: {}", message);
    }
}
