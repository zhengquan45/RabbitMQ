package org.zhq.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;

public class Producer4Ack {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.confirmSelect();
        String exchangeName = "test_ack_exchange";
        String routingKey = "ack.save";
        for (int i = 0; i < 5; i++) {
            HashMap<String, Object> headers = new HashMap<>();
            headers.put("num", i);
            String msg = "hello rabbitmq"+i;
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                    .deliveryMode(2)
                    .contentEncoding("utf-8")
                    .headers(headers)
                    .build();
            channel.basicPublish(exchangeName, routingKey, properties, msg.getBytes());
        }

    }
}
