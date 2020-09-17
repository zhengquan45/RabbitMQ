package org.zhq.spring;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.zhq.spring.adapter.MessageDelegate;
import org.zhq.spring.converter.TextMessageConverter;
import org.zhq.spring.entity.Order;
import org.zhq.spring.entity.Packaged;

import java.util.HashMap;
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
//        simpleMessageListenerContainer.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
//            System.out.println("consumer:" + new String(message.getBody()));
//        });
        //适配器
        //可以指定默认的监听方法
        //可以指定转换器
        //适配器1
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        adapter.setMessageConverter(new TextMessageConverter());
//        simpleMessageListenerContainer.setMessageListener(adapter);
        //适配器2
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        HashMap<String, String> queueOrTagToMethodName = new HashMap<>();
//        queueOrTagToMethodName.put("queue01","method1");
//        queueOrTagToMethodName.put("queue02","method2");
//        adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
//        simpleMessageListenerContainer.setMessageListener(adapter);
        //支持json格式的转换器
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeJsonMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        simpleMessageListenerContainer.setMessageListener(adapter);

        //发送端指定转化的类型
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeJavaTypeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
//        javaTypeMapper.setTrustedPackages("*");
//        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        simpleMessageListenerContainer.setMessageListener(adapter);

        //接受端指定转化类型
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeJavaTypeMessage");
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        HashMap<String, Class<?>> idClassMap = new HashMap<>();
        idClassMap.put("order", Order.class);
        idClassMap.put("packaged", Packaged.class);
        javaTypeMapper.setIdClassMapping(idClassMap);
        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        adapter.setMessageConverter(jackson2JsonMessageConverter);
        simpleMessageListenerContainer.setMessageListener(adapter);
        //复杂转化器转化流媒体

        return simpleMessageListenerContainer;
    }
}
