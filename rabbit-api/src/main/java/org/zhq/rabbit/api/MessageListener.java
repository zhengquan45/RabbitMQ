package org.zhq.rabbit.api;

public interface MessageListener {

    void onMessage(Message message);
}
