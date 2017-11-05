package com.github.jyoghurt.vertx.controller;

import com.github.jyoghurt.vertx.handle.VertxHandler;
import com.github.jyoghurt.vertx.support.VertxSocketSupport;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * user:DELL
 * date:2017/7/24.
 */
@RestController
@LogContent(module = "vertx注册")
@RequestMapping("/vertxRegister")
public class VertxRegisterController extends BaseController {

    @LogContent("vertx注册")
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public HttpResultEntity<?> register() throws Exception {
        Map<String, VertxSocketSupport> map = SpringContextUtils.getBeans(VertxSocketSupport.class);
//        map.forEach((s, vertxSocketSupport) ->
//                VertxHandler.getInstance().eventBus()
//                        .consumer(vertxSocketSupport.initAddress() + "_Server").handler(
//                        message -> vertxSocketSupport.handleMessage(message.body())
//                ));
        JSONObject obj = new JSONObject();
        obj.put("MANAGER", SpringContextUtils.getProperty("socketManagerAddress"));
        obj.put("SERVER", SpringContextUtils.getProperty("socketServerAddress"));
        return getSuccessResult(obj);
    }
}
