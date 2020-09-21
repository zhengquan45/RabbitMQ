package org.zhq.rabbit.producer.service;

import org.zhq.rabbit.constant.BrokerMessageStatus;
import org.zhq.rabbit.producer.entity.BrokerMessage;

import java.util.List;

public interface MessageStoreService {
    void save(BrokerMessage brokerMessage);

    void success(String messageId);

    void fail(String messageId);

    List<BrokerMessage> fetchTimeoutMessageForRetry(BrokerMessageStatus brokerMessageStatus);

    void updateForTryCount(String messageId);
}
