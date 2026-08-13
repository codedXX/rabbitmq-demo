package com.example.rabbitmqdemo.controller;

import com.example.rabbitmqdemo.service.MessageProducer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    private final MessageProducer messageProducer;

    public MessageController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @PostMapping(value = "/messages", consumes = MediaType.TEXT_PLAIN_VALUE)
    public String send(@RequestBody String message) {
        messageProducer.send(message);
        return "sent: " + message;
    }
}
