package org.zhq.rabbit.producer.broker;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zhq.rabbit.api.MessageType;
import org.zhq.rabbit.producer.service.MessageStoreService;

import java.util.List;

@Slf4j
public class RabbitTemplateConfirmCallback implements RabbitTemplate.ConfirmCallback {
    private final MessageStoreService messageStoreService;
    private final Splitter splitter = Splitter.on("#");
    public RabbitTemplateConfirmCallback(MessageStoreService messageStoreService) {
        this.messageStoreService = messageStoreService;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        List<String> strings = splitter.splitToList(correlationData.getId());
        String messageId = strings.get(0);
        long sendTime = Long.parseLong(strings.get(1));
        String messageType = strings.get(2);
        if(ack) {
            //	当Broker 返回ACK成功时, 就是更新一下日志表里对应的消息发送状态为 SEND_OK
            // 	如果当前消息类型为reliant 我们就去数据库查找并进行更新
            if(MessageType.RELIANT.endsWith(messageType)) {
                this.messageStoreService.success(messageId);
            }
            log.info("RabbitTemplateConfirmCallback#confirm send message is OK, confirm messageId: {}, sendTime: {}", messageId, sendTime);
        } else {
            if(MessageType.RELIANT.endsWith(messageType)) {
                this.messageStoreService.fail(messageId);
            }
            log.error("RabbitTemplateConfirmCallback#confirm send message is Fail, confirm messageId: {}, sendTime: {}", messageId, sendTime);
        }
    }
}
