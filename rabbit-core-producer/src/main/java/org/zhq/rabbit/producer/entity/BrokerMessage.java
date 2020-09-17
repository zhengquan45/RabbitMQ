package org.zhq.rabbit.producer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import org.zhq.rabbit.api.Message;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@TableName("broker_message")
@Data
public class BrokerMessage implements Serializable {
    @TableId
    private String messageId;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Message message;
    private Integer tryCount = 0;
    private String status;
    private LocalDateTime nextRetry;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
