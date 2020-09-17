package org.zhq.rabbit.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message implements Serializable {

    /**
     * 消息id
     */
    private String messageId;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 消息的路由规则
     */
    private String routingKey = "";
    /**
     * 消息的附加属性
     */
    private Map<String,Object> attributes = new HashMap<>();
    /**
     * 消息的延迟参数配置
     */
    private long delayMills;
    /**
     * 消息的类型(默认是确认消息模式)
     */
    private String messageType = MessageType.CONFIRM;

}
