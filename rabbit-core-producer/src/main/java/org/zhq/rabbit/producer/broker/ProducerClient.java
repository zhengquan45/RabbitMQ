package org.zhq.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zhq.rabbit.api.Message;
import org.zhq.rabbit.api.MessageProducer;
import org.zhq.rabbit.api.MessageType;
import org.zhq.rabbit.api.SendCallback;
import org.zhq.rabbit.api.exception.MessageRuntimeException;

import java.util.List;

@Component
public class ProducerClient implements MessageProducer {
    private final RabbitBroker rabbitBroker;

    @Autowired
    public ProducerClient(RabbitBroker rabbitBroker) {
        this.rabbitBroker = rabbitBroker;
    }

    @Override
    public void send(Message message) throws MessageRuntimeException {
        Preconditions.checkNotNull(message);
        String messageType = message.getMessageType();
        switch (messageType) {
            case MessageType.RAPID:
                rabbitBroker.rapidSend(message);
                break;
            case MessageType.CONFIRM:
                rabbitBroker.confirmSend(message);
                break;
            case MessageType.RELIANT:
                rabbitBroker.reliantSend(message);
                break;
            default:
                throw new MessageRuntimeException("不支持的消息类型");
        }
    }

    @Override
    public void send(List<Message> messages) throws MessageRuntimeException {

    }

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRuntimeException {

    }
}
