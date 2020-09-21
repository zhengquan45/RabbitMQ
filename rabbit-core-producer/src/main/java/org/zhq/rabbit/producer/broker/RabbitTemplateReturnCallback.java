package org.zhq.rabbit.producer.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zhq.rabbit.api.MessageType;
import org.zhq.rabbit.producer.service.MessageStoreService;

import java.io.IOException;

@Slf4j
public class RabbitTemplateReturnCallback implements RabbitTemplate.ReturnCallback {
    private final MessageStoreService messageStoreService;
    private final ObjectMapper objectMapper;

    public RabbitTemplateReturnCallback(MessageStoreService messageStoreService, ObjectMapper objectMapper) {
        this.messageStoreService = messageStoreService;
        this.objectMapper = objectMapper;
    }


    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        byte[] body = message.getBody();
        org.zhq.rabbit.api.Message msg = null;
        try {
            msg = objectMapper.readValue(body, org.zhq.rabbit.api.Message.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(MessageType.RELIANT.endsWith(msg.getMessageType())) {
            messageStoreService.fail(msg.getMessageId());
        }
        log.error("RabbitTemplateReturnCallback#returnedMessage send message is Fail, return messageId: {}", msg.getMessageId());
    }
}
