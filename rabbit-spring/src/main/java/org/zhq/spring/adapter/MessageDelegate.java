package org.zhq.spring.adapter;

import org.zhq.spring.entity.Order;
import org.zhq.spring.entity.Packaged;

import java.util.Map;

public class MessageDelegate {

    public void handleMessage(byte[] body) {
        String message = new String(body);
        System.out.println("MessageDelegate#handleMessage consumer:" + message);
    }

//    public void consumeMessage(byte[] body) {
//        String message = new String(body);
//        System.out.println("MessageDelegate#consumeMessage consumer:" + message);
//    }

    public void consumeMessage(String msg) {
        System.out.println("MessageDelegate#consumeMessage(String) consumer:" + msg);
    }

    public void method1(String message) {
        System.out.println("MessageDelegate#method1 consumer:" + message);
    }

    public void method2(String message) {
        System.out.println("MessageDelegate#method2 consumer:" + message);
    }

    public void consumeJsonMessage(Map map){
        System.out.println("MessageDelegate#consumeJsonMessage consumer:"+map.toString());
    }

    public void consumeJavaTypeMessage(Order order){
        System.out.println("MessageDelegate#consumeJavaTypeMessage consumer:"+order.toString());
    }

    public void consumeJavaTypeMessage(Packaged packaged){
        System.out.println("MessageDelegate#consumeJavaTypeMessage consumer:"+packaged.toString());
    }

}
