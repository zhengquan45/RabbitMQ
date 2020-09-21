package org.zhq.rabbit.producer.autoconfigure;

import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zhq.rabbit.producer.broker.*;
import org.zhq.rabbit.producer.mapper.BrokerMessageMapper;
import org.zhq.rabbit.producer.service.MessageStoreService;
import org.zhq.rabbit.producer.service.MessageStoreServiceImpl;
import org.zhq.rabbit.producer.task.RetryMessageDataflowJob;


@Configuration
@MapperScan("org.zhq.rabbit.producer.mapper")
public class RabbitProducerAutoConfiguration {

    @Bean(name = "retryMessageDataflowJob")
    public DataflowJob retryMessageDataflowJob(MessageStoreService messageStoreService,RabbitBroker rabbitBroker){
        return new RetryMessageDataflowJob(messageStoreService,rabbitBroker);
    }

    @Bean
    public MessageStoreService messageStoreService(BrokerMessageMapper brokerMessageMapper) throws Exception {
        return new MessageStoreServiceImpl(brokerMessageMapper);
    }

    @Bean
    public RabbitTemplateContainer rabbitTemplateContainer(ConnectionFactory connectionFactory,MessageStoreService messageStoreService){
        return new RabbitTemplateContainer(connectionFactory,new RabbitTemplateConfirmCallback(messageStoreService));
    }

    @Bean
    public RabbitBroker rabbitBroker(RabbitTemplateContainer rabbitTemplateContainer,MessageStoreService messageStoreService){
        return new RabbitBrokerImpl(rabbitTemplateContainer,messageStoreService);
    }

    @Bean
    public ProducerClient producerClient(RabbitBroker rabbitBroker){
        return new ProducerClient(rabbitBroker);
    }

}
