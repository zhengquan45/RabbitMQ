package org.zhq.spring;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@ComponentScan(basePackages = {"org.zhq.spring"})
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("localhost:5672");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public Queue queue01() {
        return new Queue("queue01", true, false, false, null);
    }

    @Bean
    public Queue queue02() {
        return new Queue("queue02", true, false, false, null);
    }

    @Bean
    public Queue queue03() {
        return new Queue("queue03", true, false, false, null);
    }

    @Bean
    public Exchange exchange01() {
        return new DirectExchange("exchange01", true, false, null);
    }

    @Bean
    public Exchange exchange02() {
        return new TopicExchange("exchange02", true, false, null);
    }

    @Bean
    public Exchange exchange03() {
        return new FanoutExchange("exchange03", true, false, null);
    }

    @Bean
    public Binding binding01() {
        return new Binding("queue01", Binding.DestinationType.QUEUE, "exchange01", "spring.add", null);
    }

    @Bean
    public Binding binding02() {
        return new Binding("queue02", Binding.DestinationType.QUEUE, "exchange02", "rabbit.#", null);
    }

    @Bean
    public Binding binding03() {
        return new Binding("queue03", Binding.DestinationType.QUEUE, "exchange03", "queue.#", null);
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        simpleMessageListenerContainer.setQueues(queue01(), queue02(), queue03());
        simpleMessageListenerContainer.setConcurrentConsumers(1);
        simpleMessageListenerContainer.setMaxConcurrentConsumers(5);
        simpleMessageListenerContainer.setDefaultRequeueRejected(false);
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        simpleMessageListenerContainer.setConsumerTagStrategy(queue -> queue + "-" + UUID.randomUUID().toString());
        simpleMessageListenerContainer.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            System.out.println("consumer:" + new String(message.getBody()));
        });
        return simpleMessageListenerContainer;
    }
}
