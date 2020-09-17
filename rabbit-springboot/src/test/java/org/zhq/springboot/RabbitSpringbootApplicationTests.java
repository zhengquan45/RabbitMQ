package org.zhq.springboot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zhq.springboot.consumer.Order;
import org.zhq.springboot.producer.RabbitSender;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class RabbitSpringbootApplicationTests {
    @Autowired
    private RabbitSender sender;
    @Test
    void sendMessage() {
        Map<String,Object> properties = new HashMap<>();
        sender.send("hello rabbit4springboot",properties);
    }

    @Test
    void sendOrderMessage() {
        Map<String,Object> properties = new HashMap<>();
        Order order = new Order();
        order.setId("001");
        order.setName("this is a order");
        sender.send(order,properties);
    }

}
