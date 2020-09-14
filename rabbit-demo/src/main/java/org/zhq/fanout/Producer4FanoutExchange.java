package org.zhq.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer4FanoutExchange {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        String exchangeName = "test_fanout_exchange";
        String routingKey = "";
        String msg = "hello world rabbitmq 4 direct exchange message...";
        channel.basicPublish(exchangeName,routingKey,null,msg.getBytes());


        channel.close();
        connection.close();
    }
}
