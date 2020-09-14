package org.zhq.rabbit.producer.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zhq.rabbit.constant.BrokerMessageStatus;
import org.zhq.rabbit.producer.entity.BrokerMessage;
import org.zhq.rabbit.producer.mapper.BrokerMessageMapper;

import java.time.LocalDateTime;

@Service
public class MessageStoreServiceImpl implements MessageStoreService {
    private final BrokerMessageMapper brokerMessageMapper;

    @Autowired
    public MessageStoreServiceImpl(BrokerMessageMapper brokerMessageMapper) {
        this.brokerMessageMapper = brokerMessageMapper;
    }

    @Override
    public void save(BrokerMessage brokerMessage) {
        brokerMessageMapper.insert(brokerMessage);
    }

    @Override
    public void success(String messageId) {
        changeBrokerMessageStatus(messageId, BrokerMessageStatus.SEND_OK);
    }

    @Override
    public void fail(String messageId) {
        changeBrokerMessageStatus(messageId, BrokerMessageStatus.SEND_FAIL);
    }

    private void changeBrokerMessageStatus(String messageId, BrokerMessageStatus brokerMessageStatus) {
        BrokerMessage brokerMessage = new BrokerMessage();
        brokerMessage.setStatus(brokerMessageStatus.getCode());
        brokerMessage.setUpdateTime(LocalDateTime.now());
        LambdaUpdateWrapper<BrokerMessage> updateWrapper = Wrappers.<BrokerMessage>lambdaUpdate().eq(BrokerMessage::getMessageId, messageId);
        brokerMessageMapper.update(brokerMessage, updateWrapper);
    }
}
