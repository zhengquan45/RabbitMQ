package org.zhq.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RabbitTemplateContainer {

    private final Map<String, RabbitTemplate> rabbitTemplateMap;
    private final ConnectionFactory connectionFactory;
    private final RabbitTemplateConfirmCallback confirmCallback;
    private final RabbitTemplateReturnCallback returnCallback;
    private SerializerFactory serializerFactory = JsonSerializeFactory.INSTANCE;

    @Autowired
    public RabbitTemplateContainer(ConnectionFactory connectionFactory, RabbitTemplateConfirmCallback confirmCallback, RabbitTemplateReturnCallback returnCallback) {
        this.confirmCallback = confirmCallback;
        this.returnCallback = returnCallback;
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
                rabbitTemplate.setConfirmCallback(confirmCallback);
                rabbitTemplate.setReturnCallback(returnCallback);
                //rabbitTemplate这里不是用@Bean导入而是使用new的方式在Component中做池化 导致spring.rabbit.template.mandatory=true无效
                rabbitTemplate.setMandatory(true);
            }
            rabbitTemplateMap.putIfAbsent(topic, rabbitTemplate);
        }
        return rabbitTemplate;
    }


}
