package org.zhq.rabbit.producer.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import lombok.extern.slf4j.Slf4j;
import org.zhq.rabbit.constant.BrokerMessageStatus;
import org.zhq.rabbit.producer.broker.RabbitBroker;
import org.zhq.rabbit.producer.entity.BrokerMessage;
import org.zhq.rabbit.producer.service.MessageStoreService;
import org.zhq.rabbit.task.annotation.ElasticJobConfig;

import java.util.List;

@ElasticJobConfig(jobName = "org.zhq.rabbit.producer.task.RetryMessageDataflowJob",
        cron = "0/10 * * * * ?",
        description = "可靠性投递消息补偿任务",
        overwrite = true,
        streamingProcess = true
)
@Slf4j
public class RetryMessageDataflowJob implements DataflowJob<BrokerMessage> {
    private final MessageStoreService messageStoreService;
    private final RabbitBroker rabbitBroker;
    private final static int MAX_RETRY_COUNT = 3;

    public RetryMessageDataflowJob(MessageStoreService messageStoreService, RabbitBroker rabbitBroker) {
        this.messageStoreService = messageStoreService;
        this.rabbitBroker = rabbitBroker;
    }

    @Override
    public List<BrokerMessage> fetchData(ShardingContext shardingContext) {
        List<BrokerMessage> list = messageStoreService.fetchTimeoutMessageForRetry(BrokerMessageStatus.SENDING);
        log.info("fetch {} sending brokerMessage",list.size());
        return list;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<BrokerMessage> list) {
        log.info("process {} sending brokerMessage",list.size());
        list.forEach(brokerMessage -> {
            Integer tryCount = brokerMessage.getTryCount();
            if(tryCount >= MAX_RETRY_COUNT){
                log.warn("brokerMessage try 3 times.can't send message");
                messageStoreService.fail(brokerMessage.getMessageId());
            }else{
                rabbitBroker.confirmSend(brokerMessage.getMessage());
                messageStoreService.updateForTryCount(brokerMessage.getMessageId());
            }
        });
    }
}
