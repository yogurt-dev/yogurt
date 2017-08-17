package com.github.jyoghurt.weChat.controller;

import com.github.jyoghurt.security.annotations.IgnoreAuthentication;
import com.github.jyoghurt.weChat.common.service.CoreService;
import com.github.jyoghurt.wechatbasic.common.util.SignUtil;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Lvyu on 2015/9/23.
 */
@RestController
@RequestMapping("/weChat")
public class WeChatController extends BaseController {
    @LogContent("微信接口")
    @IgnoreAuthentication
    @RequestMapping(value = "/index")
    public void index() throws IOException {
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");
        if("GET".equals(request.getMethod())){
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (SignUtil.checkSignature(signature, timestamp, nonce)) {
                PrintWriter writer = response.getWriter();
                writer.write(echostr);
            }
        }else{
            // 调用核心业务类接收消息、处理消息
            String respMessage = CoreService.processRequest(request);
            PrintWriter writer = response.getWriter();
            writer.write(respMessage);
        }
    }
    
}
