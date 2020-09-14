package org.zhq.rabbit.common.serializer.impl;

import org.zhq.rabbit.api.Message;
import org.zhq.rabbit.common.serializer.Serializer;
import org.zhq.rabbit.common.serializer.SerializerFactory;

public class JsonSerializeFactory implements SerializerFactory {
    public static final JsonSerializeFactory INSTANCE = new JsonSerializeFactory();
    @Override
    public Serializer create() {
        return JsonSerializer.createParametricType(Message.class);
    }
}
