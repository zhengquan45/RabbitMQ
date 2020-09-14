package org.zhq.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;

@SpringBootTest(classes = Application.class)
@ExtendWith(SpringExtension.class)
class RabbitSpringApplicationTests {

    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
        rabbitAdmin.declareExchange(new DirectExchange("test.direct", false, false));
        rabbitAdmin.declareExchange(new TopicExchange("test.topic", false, false));
        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout", false, false));

        rabbitAdmin.declareQueue(new Queue("test.direct.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.topic.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.fanout.queue", false));

        rabbitAdmin.declareBinding(new Binding("test.direct.queue",
                Binding.DestinationType.QUEUE,
                "test.direct",
                "direct.#",
                new HashMap<>()));

        //可以直接用这种方式创建exchange 和 queue
        rabbitAdmin.declareBinding(BindingBuilder
                .bind(new Queue("test.topic.queue", false))
                .to(new TopicExchange("test.topic", false, false))
                .with("topic.#"));
        rabbitAdmin.declareBinding(BindingBuilder
                .bind(new Queue("test.fanout.queue", false))
                .to(new FanoutExchange("test.fanout", false, false)));

        rabbitAdmin.purgeQueue("test.topic");
    }

    @Test
    public void sendMessageTest() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        messageProperties.getHeaders().put("desc", "this is a description");
        messageProperties.getHeaders().put("type", "this is a custom type");
        Message message = new Message("this is a text".getBytes(), messageProperties);
        rabbitTemplate.convertAndSend("exchange01","spring.add",message, msg -> {
            msg.getMessageProperties().getHeaders().put("extend","this is a extend");
            return msg;
        });
        Message message2 = new Message("this is a test queue02 to method2".getBytes(), messageProperties);
        rabbitTemplate.convertAndSend("exchange02","rabbit.add",message2, msg -> {
            msg.getMessageProperties().getHeaders().put("extend","this is a extend");
            return msg;
        });
    }

}
