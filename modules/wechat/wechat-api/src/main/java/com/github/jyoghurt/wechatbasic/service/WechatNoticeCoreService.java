package com.github.jyoghurt.wechatbasic.service;


import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.wechatbasic.common.req.InputMessage;
import com.github.jyoghurt.wechatbasic.common.resp.OutputMessage;
import com.github.jyoghurt.wechatbasic.common.util.MessageUtil;
import com.github.jyoghurt.wechatbasic.common.util.SerializeXmlUtil;
import com.github.jyoghurt.wechatbasic.service.response.service.WeChatAutoResponseMsgService;
import com.github.jyoghurt.wechatbasic.service.statistics.domain.WechatStatisticsT;
import com.github.jyoghurt.wechatbasic.service.statistics.service.WechatStatisticsService;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class WechatNoticeCoreService {
    @Autowired
    private WechatStatisticsService wechatStatisticsService;
    @Autowired
    private WeChatAutoResponseMsgService weChatAutoResponseMsgService;

    private Logger logger = LoggerFactory.getLogger(WechatNoticeCoreService.class);

    //注册service
    public String processRequest(HttpServletRequest request) {
        try {
            InputMessage inputMsg = parseRequest(request);
            return msgHandle(inputMsg);
        } catch (Exception e) {
            logger.error("解析微信公众账号请求错误", e);
        }
        return MessageUtil.RESP_MESSAGE_TYPE_SUCCESS;
    }

    private InputMessage parseRequest(HttpServletRequest request) {
        // 处理接收消息
        ServletInputStream in = null;
        try {
            in = request.getInputStream();
            // 将POST流转换为XStream对象
            XStream xs = SerializeXmlUtil.createXstream();
            xs.processAnnotations(InputMessage.class);
            xs.processAnnotations(OutputMessage.class);
            // 将指定节点下的xml节点数据映射为对象
            xs.alias("xml", InputMessage.class);
            // 将流转换为字符串
            StringBuilder xmlMsg = new StringBuilder();
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1; ) {
                xmlMsg.append(new String(b, 0, n, "UTF-8"));
            }
            logger.info("**************************接收微信的xml************************:", xmlMsg.toString());
            // 将xml内容转换为InputMessage对象
            InputMessage inputMsg = (InputMessage) xs.fromXML(xmlMsg.toString());
            return inputMsg;
        } catch (IOException e) {
            throw new BaseErrorException("解析微信公众号xml信息IO异常");
        }
    }

    private String msgHandle(InputMessage inputMsg) {
        String msgType = inputMsg.getMsgType();
        // 记录事件推送
        if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
            eventMsgHandle(inputMsg);
        }
        return weChatAutoResponseMsgService.matchAutoResponse(inputMsg);
    }

    private void eventMsgHandle(InputMessage inputMsg) {
        wechatStatisticsService.save(new WechatStatisticsT()
                .setEvent(inputMsg.getEvent())
                .setEventKey(inputMsg.getEventKey())
                .setOpenId(inputMsg.getFromUserName())
                .setTicket(inputMsg.getTicket()));
    }
}