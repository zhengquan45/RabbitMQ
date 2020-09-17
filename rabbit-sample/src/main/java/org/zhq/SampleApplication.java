package org.zhq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zhq.rabbit.api.Message;
import org.zhq.rabbit.api.MessageType;
import org.zhq.rabbit.producer.broker.ProducerClient;

import java.util.UUID;

@SpringBootApplication
@RestController
@Slf4j
public class SampleApplication {
    private final ProducerClient producerClient;

    @Autowired
    public SampleApplication(ProducerClient producerClient) {
        this.producerClient = producerClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @GetMapping("/rapidSend")
    public void rapidSend() {
        Message message = Message.builder().messageId(UUID.randomUUID().toString())
                .messageType(MessageType.RAPID)
                .topic("rapid-exchange")
                .routingKey("rapid.add")
                .build();
        producerClient.send(message);
    }

    @GetMapping("/confirmSend")
    public void confirmSend() {
        Message message = Message.builder().messageId(UUID.randomUUID().toString())
                .messageType(MessageType.CONFIRM)
                .topic("confirm-exchange")
                .routingKey("confirm.111")
                .build();
        producerClient.send(message);
    }


    @GetMapping("/reliantSend")
    public void reliantSend() {
        Message message = Message.builder().messageId(UUID.randomUUID().toString())
                .messageType(MessageType.RELIANT)
                .topic("reliant-exchange")
                .routingKey("reliant.bbbb")
                .build();
        producerClient.send(message);
    }

}