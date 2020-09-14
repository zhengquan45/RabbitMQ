package org.zhq.rabbit.api;

public final class MessageType {
    /**
     * 迅速消息:不需要保障消息的可靠性,也不需要做confirm确认
     */
    public final static String RAPID = "0";
    /**
     * 确认消息:不需要保障消息的可靠性,需要做confirm确认
     */
    public final static String CONFIRM = "1";

    /**
     * 可靠消息:100%保障消息的可靠性,不允许有任何消息的丢失
     * 保证数据库和所发消息原子性(最终一致性)
     */
    public final static String RELIANT = "2";

}
