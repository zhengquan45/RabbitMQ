package org.zhq.rabbit.api;

import org.zhq.rabbit.api.exception.MessageRuntimeException;

import java.util.List;

public interface MessageProducer {

    void send(Message message)throws MessageRuntimeException;
    void send(List<Message> messages)throws MessageRuntimeException;
    void send(Message message,SendCallback sendCallback)throws MessageRuntimeException;
}
