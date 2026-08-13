package com.example.rabbitmqdemo;

import com.example.rabbitmqdemo.configration.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

//    @RabbitListener(queues = RabbitMqConfig.QUEUE_NAME)
//    public void receive(String message) {
//        log.info("received message: {}", message);
//    }
@RabbitListener(bindings = @QueueBinding(value=@Queue(name=RabbitMqConfig.QUEUE_NAME),exchange =@Exchange(name="demo.exchange",type="ExchangeTypes.DIRECT")))
public void receive(String message) {
    log.info("received message: {}", message);
}
}
