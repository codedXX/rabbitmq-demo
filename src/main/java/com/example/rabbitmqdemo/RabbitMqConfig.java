package com.example.rabbitmqdemo;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String QUEUE_NAME = "demo.queue";

    @Bean
    public Queue demoQueue() {
        return new Queue(QUEUE_NAME, true);
    }
}
