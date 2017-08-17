package com.github.jyoghurt.vertx.support;

import com.github.jyoghurt.vertx.handle.VertxHandler;

/**
 * user:DELL
 * date:2017/7/25.
 */
public abstract class VertxSocketSupport {
    /**
     * 初始化服务端接收消息地址
     *
     * @return 服务端地址
     */
    public abstract String initAddress();

    /**
     * 接收消息
     *
     * @param message 消息
     */
    public abstract void handleMessage(Object message);

    /**
     * 默认发送方法
     *
     * @param message 消息内容
     */
    public void send(Object message) {
        VertxHandler.getInstance().eventBus().send(initAddress() + "_Client", message);
    }
}
