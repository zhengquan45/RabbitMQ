package org.zhq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zhq.rabbit.api.Message;
import org.zhq.rabbit.producer.broker.ProducerClient;

@SpringBootApplication
@RestController
public class SampleApplication {
    private final ProducerClient producerClient;

    public SampleApplication(ProducerClient producerClient) {
        this.producerClient = producerClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
//    rapidSend(Message message);
//
//    void confirmSend(Message message);
//
//    void reliantSend(Message message);
    @GetMapping("/rapidSend")
    public void rapidSend(){

    }
}
