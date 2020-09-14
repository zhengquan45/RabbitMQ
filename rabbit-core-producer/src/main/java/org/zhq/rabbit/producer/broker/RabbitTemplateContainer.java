package org.zhq.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.zhq.rabbit.api.Message;
import org.zhq.rabbit.api.MessageType;
import org.zhq.rabbit.api.exception.MessageRuntimeException;
import org.zhq.rabbit.common.convert.GenericMessageConverter;
import org.zhq.rabbit.common.convert.RabbitMessageConverter;
import org.zhq.rabbit.common.serializer.Serializer;
import org.zhq.rabbit.common.serializer.SerializerFactory;
import org.zhq.rabbit.common.serializer.impl.JsonSerializeFactory;
import org.zhq.rabbit.producer.service.MessageStoreService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback {

    private final Map<String, RabbitTemplate> rabbitTemplateMap;
    private final ConnectionFactory connectionFactory;
    private Splitter splitter = Splitter.on("#");
    private SerializerFactory serializerFactory = JsonSerializeFactory.INSTANCE;
    private final MessageStoreService messageStoreService;

    @Autowired
    public RabbitTemplateContainer(ConnectionFactory connectionFactory, MessageStoreService messageStoreService) {
        this.messageStoreService = messageStoreService;
        this.rabbitTemplateMap = new HashMap<>();
        this.connectionFactory = connectionFactory;
    }

    public RabbitTemplate getTemplate(Message message) throws MessageRuntimeException {
        Preconditions.checkNotNull(message);
        String topic = message.getTopic();
        RabbitTemplate rabbitTemplate = rabbitTemplateMap.get(topic);
        if (rabbitTemplate == null) {
            log.info("#RabbitTemplateContainer.getTemplate#topic:{} rabbitTemplate is not exist", topic);
            rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.setExchange(topic);
            rabbitTemplate.setRetryTemplate(new RetryTemplate());
            rabbitTemplate.setRoutingKey(message.getRoutingKey());
            //序列化
            Serializer serializer = serializerFactory.create();
            GenericMessageConverter gmc = new GenericMessageConverter(serializer);
            RabbitMessageConverter rmc = new RabbitMessageConverter(gmc);
            rabbitTemplate.setMessageConverter(rmc);
            if (!message.getMessageType().equals(MessageType.RAPID)) {
                rabbitTemplate.setConfirmCallback(this);
            }
            rabbitTemplateMap.putIfAbsent(topic, rabbitTemplate);
        }
        return rabbitTemplate;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        List<String> strings = splitter.splitToList(correlationData.getId());
        String messageId = strings.get(0);
        long timestamp = Long.parseLong(strings.get(1));
        if (ack) {
            messageStoreService.success(messageId);
            log.info("#RabbitTemplateContainer.confirm# send message is ok,messageId:{},timestamp:{}", messageId, timestamp);
        } else {
            messageStoreService.fail(messageId);
            log.error("#RabbitTemplateContainer.confirm# send message isn't ok,messageId:{},timestamp:{}", messageId, timestamp);
        }
    }
}
