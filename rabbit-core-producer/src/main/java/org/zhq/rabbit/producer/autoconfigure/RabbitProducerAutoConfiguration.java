package org.zhq.rabbit.producer.autoconfigure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"org.zhq.rabbit.producer.*"})
@MapperScan("org.zhq.rabbit.producer.mapper")
public class RabbitProducerAutoConfiguration {

}
