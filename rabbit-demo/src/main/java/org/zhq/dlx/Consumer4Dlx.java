package org.zhq.dlx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;

public class Consumer4Dlx {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        String exchangeName = "test_dlx_exchange";
        String routingKey = "dlx.#";
        String queueName = "test_dlx_queue";
        //正常队列的设置
        channel.exchangeDeclare(exchangeName, "topic", true,false,null);
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","dlx.exchange");
        channel.queueDeclare(queueName, true, false, false, arguments);
        channel.queueBind(queueName,exchangeName,routingKey);

        //死信队列的设置
        channel.exchangeDeclare("dlx.exchange", "topic", true,false,null);
        channel.queueDeclare("dlx.queue",true,false,false,null);
        channel.queueBind("dlx.queue","dlx.exchange","#");
        channel.basicConsume(queueName, true, new MyConsumer(channel));

    }
}
