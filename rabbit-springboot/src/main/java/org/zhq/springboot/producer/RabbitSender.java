package org.zhq.springboot.producer;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static org.springframework.amqp.rabbit.core.RabbitTemplate.*;
import static org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;

@Component
public class RabbitSender {
    private final RabbitTemplate rabbitTemplate;
    final ConfirmCallback confirmCallback = (correlationData, ack, cause) -> {
        System.out.println("correlationData:" + correlationData);
        if (!ack) {
            System.out.println("cause:" + cause);
        }
    };

    final ReturnCallback returnCallback = (message, replyCode, replyText, exchange, routingKey) -> {
        System.out.println("message:" + message);
        System.out.println("replyCode:" + replyCode);
        System.out.println("replyText:" + replyText);
        System.out.println("exchange:" + exchange);
        System.out.println("routingKey:" + routingKey);
    };

    @Autowired
    public RabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(Object message, Map<String, Object> properties) {
        MessageHeaders mhs = new MessageHeaders(properties);
        Message msg = MessageBuilder.createMessage(message, mhs);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("exchange-01", "321.add", msg,correlationData);
    }
}
