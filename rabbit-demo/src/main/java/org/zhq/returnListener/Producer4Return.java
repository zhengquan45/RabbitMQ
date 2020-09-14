package org.zhq.returnListener;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Producer4Return {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        String exchangeName = "test_return_exchange";
        String routingKeyCrr = "return.save";
        String routingKeyErr = "abc.save";
        String msg = "hello rabbitmq";
        channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
            System.out.println("============handle return==============");
            System.out.println("replyCode:" + replyCode);
            System.out.println("replyText:" + replyText);
            System.out.println("exchange:" + exchange);
            System.out.println("routingKey:" + routingKey);
            System.out.println("properties:" + properties);
            System.out.println("body:" + new String(body));
        });
//        channel.basicPublish(exchangeName, routingKeyCrr, true,null, msg.getBytes());
        channel.basicPublish(exchangeName, routingKeyErr,true, null, msg.getBytes());
    }
}
