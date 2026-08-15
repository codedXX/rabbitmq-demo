package com.example.rabbitmqdemo;

import com.example.rabbitmqdemo.configration.RabbitMqConfig;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
            exchange = @Exchange(name = RabbitMqConfig.EXCHANGE_NAME, type = ExchangeTypes.DIRECT, durable = "true"),
            // 只有路由键为 demo 的消息才会路由到 demo.queue。
            key = RabbitMqConfig.ROUTING_KEY))
    public void receive(String message) {
        // 消费者从队列中取出消息后执行这里的业务逻辑。
        log.info("received message: {}", message);
    }


    //第二个参数 Message message 是 RabbitMQ 的原始消息对象
    @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueueMessage(String msg, Message message, Channel channel) throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag(); /// 手动 ACK/NACK 必需
        log.info("spring 消费者接收到消息：{}", msg);

        if ("fail".equals(msg)) {
            // NACK：拒绝消息。
            // 第一个参数 deliveryTag：要拒绝的消息标识。
            // 第二个参数 multiple=false：只拒绝当前消息，不批量拒绝之前未确认的消息。
            // 第三个参数 requeue=false：不重新放回原队列；未配置死信队列时消息会被丢弃。
            channel.basicNack(deliveryTag, false, false);
            return;
        }

        // ACK：确认消息已成功处理，Broker 将它从队列删除。
        // 第二个参数 multiple=false：只确认当前 deliveryTag 对应的这一条消息。
        // 若设为 true，会确认当前 Channel 中所有 deliveryTag 小于等于当前值的未确认消息。
//        channel.basicAck(deliveryTag, false);
        log.info("消息处理完成");
    }
}
