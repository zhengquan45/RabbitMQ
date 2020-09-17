package org.zhq.springboot.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.zhq.springboot.yml.YamlPropertySourceFactory;

import java.io.IOException;
import java.util.Map;


@Component
@PropertySource(value = "classpath:queue.yml", factory = YamlPropertySourceFactory.class)
public class RabbitReceiver {

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${springboot.rabbit.queue.name}", durable = "${springboot.rabbit.queue.durable}"),
            exchange = @Exchange(value = "${springboot.rabbit.exchange.name}", type = "${springboot.rabbit.exchange.type}",
                    ignoreDeclarationExceptions = "${springboot.rabbit.exchange.ignoreDeclarationExceptions}",
                    durable = "${springboot.rabbit.exchange.durable}"),
            key = "${springboot.rabbit.routingKey}"
    ))
//    @RabbitHandler
//    public void onMessage(Message message, Channel channel) throws IOException {
//        System.out.println("消费端收到消息体内容:" + message.getPayload());
//        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
//        channel.basicAck(deliveryTag, false);
//    }
    @RabbitHandler
    public void onMessage(@Payload Order order, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        System.out.println("消费端收到消息体订单内容:" + order);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag, false);
    }
}
