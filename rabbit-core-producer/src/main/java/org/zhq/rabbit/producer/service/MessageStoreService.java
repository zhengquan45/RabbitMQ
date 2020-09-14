package org.zhq.rabbit.producer.service;

import org.zhq.rabbit.producer.entity.BrokerMessage;

public interface MessageStoreService {
    void save(BrokerMessage brokerMessage);

    void success(String messageId);

    void fail(String messageId);
}
