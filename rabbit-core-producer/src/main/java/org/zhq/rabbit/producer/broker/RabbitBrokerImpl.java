package org.zhq.rabbit.producer.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zhq.rabbit.api.Message;
import org.zhq.rabbit.api.MessageType;
import org.zhq.rabbit.constant.BrokerMessageConst;
import org.zhq.rabbit.constant.BrokerMessageStatus;
import org.zhq.rabbit.producer.entity.BrokerMessage;
import org.zhq.rabbit.producer.service.MessageStoreService;

import java.time.LocalDateTime;

/**
 * 每一个topic对应一个rabbitTemplate
 * 1、提高发送效率
 * 2、可以根据不同的需求指定不同的rabbitTemplate 每一个topic都有自己的routingKey规则
 */
@Slf4j
public class RabbitBrokerImpl implements RabbitBroker {
    private final RabbitTemplateContainer rabbitTemplateContainer;
    private final MessageStoreService storeService;

    public RabbitBrokerImpl(RabbitTemplateContainer rabbitTemplateContainer, MessageStoreService storeService) {
        this.rabbitTemplateContainer = rabbitTemplateContainer;
        this.storeService = storeService;
    }

    @Override
    public void rapidSend(Message message) {
        message.setMessageType(MessageType.RAPID);
        sendKernel(message);
    }

    @Override
    public void confirmSend(Message message) {
        message.setMessageType(MessageType.CONFIRM);
        sendKernel(message);
    }

    @Override
    public void reliantSend(Message message) {
        message.setMessageType(MessageType.RELIANT);
        //1 数据库记录发送日志
        saveBrokerMessage(message);
        //2 发送日志
        sendKernel(message);
    }

    private void saveBrokerMessage(Message message) {
        LocalDateTime now = LocalDateTime.now();
        BrokerMessage brokerMessage = new BrokerMessage();
        brokerMessage.setMessageId(message.getMessageId());
        brokerMessage.setStatus(BrokerMessageStatus.SENDING.getCode());
        //tryCount已默认为0无需设置
        brokerMessage.setNextRetry(now.minusMinutes(BrokerMessageConst.TIMEOUT));
        brokerMessage.setCreateTime(now);
        brokerMessage.setUpdateTime(now);
        brokerMessage.setMessage(message);
        storeService.save(brokerMessage);
    }

    private void sendKernel(Message message) {
        AsyncBaseQueue.submit(() -> {
            CorrelationData correlationData =
                    new CorrelationData(String.format("%s#%s#%s",
                            message.getMessageId(),
                            System.currentTimeMillis(),
                            message.getMessageType()));
            String routingKey = message.getRoutingKey();
            String topic = message.getTopic();
            RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getTemplate(message);
            rabbitTemplate.convertAndSend(topic, routingKey, message, correlationData);
            log.info("#RabbitBrokerImpl.sendKernel#send to rabbitmq,messageId:{},messageType:{}", message.getMessageId(),message.getMessageType());
        });
    }
}
