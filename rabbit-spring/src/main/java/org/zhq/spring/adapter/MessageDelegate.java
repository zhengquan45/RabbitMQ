package org.zhq.spring.adapter;

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
//        String message = new String(body);
        System.out.println("MessageDelegate#method1 consumer:" + message);
    }

    public void method2(String message) {
//        String message = new String(body);
        System.out.println("MessageDelegate#method2 consumer:" + message);
    }
}
