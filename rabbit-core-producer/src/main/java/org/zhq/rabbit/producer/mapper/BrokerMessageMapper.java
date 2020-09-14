package org.zhq.rabbit.producer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.zhq.rabbit.producer.entity.BrokerMessage;
@Repository
public interface BrokerMessageMapper extends BaseMapper<BrokerMessage> {
}
