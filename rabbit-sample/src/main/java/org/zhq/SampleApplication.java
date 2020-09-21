package org.zhq;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zhq.rabbit.api.Message;
import org.zhq.rabbit.api.MessageType;
import org.zhq.rabbit.producer.broker.ProducerClient;
import org.zhq.rabbit.task.annotation.EnableElasticJob;

import java.util.UUID;

@EnableElasticJob
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
        Message message = Message.builder().messageId(IdWorker.getIdStr())
                .messageType(MessageType.RAPID)
                .topic("rapid-exchange")
                .routingKey("rapid.add")
                .build();
        producerClient.send(message);
    }

    @GetMapping("/confirmSend")
    public void confirmSend() {
        Message message = Message.builder().messageId(IdWorker.getIdStr())
                .messageType(MessageType.CONFIRM)
                .topic("confirm-exchange")
                .routingKey("confirm.add")
                .build();
        producerClient.send(message);
    }


    @GetMapping("/reliantSend")
    public void reliantSend() {
        Message message = Message.builder().messageId(IdWorker.getIdStr())
                .messageType(MessageType.RELIANT)
                .topic("reliant-exchange")
                .routingKey("reliant.add")
                .build();
        producerClient.send(message);
    }

}