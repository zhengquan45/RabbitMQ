package org.zhq.rabbit.common.convert;

import com.google.common.base.Preconditions;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

public class RabbitMessageConverter implements MessageConverter {

    private GenericMessageConverter delegate;
    private final String defaultExpire = String.valueOf(24 * 60 * 60 * 1000);

    public RabbitMessageConverter(GenericMessageConverter delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public org.springframework.amqp.core.Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        messageProperties.setExpiration(defaultExpire);
        return delegate.toMessage(o,messageProperties);
    }

    @Override
    public Object fromMessage(org.springframework.amqp.core.Message message) throws MessageConversionException {
        return delegate.fromMessage(message);
    }
}
