package org.zhq.rabbit.producer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.zhq.rabbit.constant.BrokerMessageStatus;
import org.zhq.rabbit.producer.entity.BrokerMessage;
import org.zhq.rabbit.producer.mapper.BrokerMessageMapper;

import java.time.LocalDateTime;
import java.util.List;

public class MessageStoreServiceImpl implements MessageStoreService {
    private final BrokerMessageMapper brokerMessageMapper;

    public MessageStoreServiceImpl(BrokerMessageMapper brokerMessageMapper) {
        this.brokerMessageMapper = brokerMessageMapper;
    }

    @Override
    public void save(BrokerMessage brokerMessage) {
        brokerMessageMapper.insertIgnore(brokerMessage);
    }

    @Override
    public void success(String messageId) {
        changeBrokerMessageStatus(messageId, BrokerMessageStatus.SEND_OK);
    }

    @Override
    public void fail(String messageId) {
        changeBrokerMessageStatus(messageId, BrokerMessageStatus.SEND_FAIL);
    }

    @Override
    public List<BrokerMessage> fetchTimeoutMessageForRetry(BrokerMessageStatus brokerMessageStatus) {
        LambdaQueryWrapper<BrokerMessage> queryWrapper = Wrappers.<BrokerMessage>lambdaQuery()
                .eq(BrokerMessage::getStatus, brokerMessageStatus.getCode())
                .gt(BrokerMessage::getNextRetry, LocalDateTime.now());
        return brokerMessageMapper.selectList(queryWrapper);
    }

    @Override
    public void updateForTryCount(String messageId) {
        LambdaUpdateWrapper<BrokerMessage> updateWrapper = Wrappers.<BrokerMessage>lambdaUpdate().setSql("try_count=try_count+1").set(BrokerMessage::getUpdateTime, LocalDateTime.now()).eq(BrokerMessage::getMessageId,messageId);
        brokerMessageMapper.update(null,updateWrapper);
    }

    private void changeBrokerMessageStatus(String messageId, BrokerMessageStatus brokerMessageStatus) {
        BrokerMessage brokerMessage = new BrokerMessage();
        brokerMessage.setStatus(brokerMessageStatus.getCode());
        brokerMessage.setUpdateTime(LocalDateTime.now());
        LambdaUpdateWrapper<BrokerMessage> updateWrapper = Wrappers.<BrokerMessage>lambdaUpdate().eq(BrokerMessage::getMessageId, messageId);
        brokerMessageMapper.update(brokerMessage, updateWrapper);
    }
}
