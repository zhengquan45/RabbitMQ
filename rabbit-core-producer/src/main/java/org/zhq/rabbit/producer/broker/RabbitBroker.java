package org.zhq.rabbit.producer.broker;


import org.zhq.rabbit.api.Message;

/**
 * 具体发动不同消息的接口
 */
public interface RabbitBroker {

    void rapidSend(Message message);

    void confirmSend(Message message);

    void reliantSend(Message message);
}
