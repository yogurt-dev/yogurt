package com.github.jyoghurt.vertx.listener;

import com.github.jyoghurt.vertx.handle.VertxHandler;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * user:DELL
 * date:2017/7/24.
 */
public class VertxListener implements ServletContextListener {
    private static Logger logger = LoggerFactory.getLogger(VertxListener.class);
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Router router = Router.router(VertxHandler.getInstance());
        //心跳检测频率2秒一次
        SockJSHandlerOptions socketOptions = new SockJSHandlerOptions().setHeartbeatInterval(10000);
        SockJSHandler sockJSHandler = SockJSHandler.create(VertxHandler.getInstance(), socketOptions);
        BridgeOptions options = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddressRegex(".*"))
                .addOutboundPermitted(new PermittedOptions().setAddressRegex(".*"));
        sockJSHandler.bridge(options, be -> {
            switch (be.type()) {
                case SOCKET_CREATED:
                    System.out.print(be.type().name());
                    break;
                case SOCKET_IDLE:
                    System.out.print(be.type().name());
                    break;
                case SOCKET_PING:
                    System.out.print(be.type().name());
                    break;
                case SOCKET_CLOSED:
                    System.out.print(be.type().name());
                    break;
                case REGISTER:
                    System.out.print(be.type().name());
                    break;
                case UNREGISTER:
                    System.out.print(be.type().name());
                    break;
                case PUBLISH:
                    System.out.print(be.type().name());
                    break;
                case RECEIVE:
                    System.out.print(be.type().name());
                    break;
                case SEND:
                    System.out.print(be.type().name());
                    break;
                default:
                    System.out.print(be.type().name());
                    break;
            }
            be.complete(true);
        });
        VertxHandler.getInstance().createHttpServer().requestHandler(router::accept).listen(Integer.valueOf(SpringContextUtils.getProperty("socketPort")));
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}
