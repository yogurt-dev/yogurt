package com.github.jyoghurt.vertx.handle;

import io.vertx.core.Vertx;

/**
 * Created by jtwu on 2017/5/17.
 */
public class VertxHandler {
    public static final Vertx vertx = Vertx.vertx();

    // 静态方法返回该类的实例
    public static Vertx getInstance() {
        return vertx;
    }

    //发送消息 点对点  若存在多点 仅有一个点能收到
    public static void send(String address, Object message) {
        getInstance().eventBus().send(address + "_Client", message);
    }

    public static void publish(String address, Object message) {
        getInstance().eventBus().publish(address + "_Client", message);
    }
}
