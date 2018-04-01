package com.github.jyoghurt.wechatbasic.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangjl on 2015/9/29.
 */
public class WeiXinConstants {

    public static final String SESSION_USERNAME = "username";
    public static final String SESSION_TUTORIALS = "tutorials";
    public static final String SESSION_ADMIN = "entity";
    public static final String UN_AUDIT_MESSAGE = "尊敬的%s,您提交的教程%s,因%s不能通过审核，请您按要求修改。";
    public static final String AUDIT_MESSAGE = "尊敬的%s,您提交的教程%s,已通过审核。";
    public static final String SESSION_USER = "user";
    public static final String WEBSOCKET_USERNAME = "websocket_username";
    public static final String MPNEWS = "mpnews";
    public static final String SEND = "send";
    public static final String SUCCESSCODE="0";
    /*微信异常映射*/
    public static final Map<String, String> weChatErrorMap = new HashMap<>();

    static {
        weChatErrorMap.put("40001", "验证token失败");
        weChatErrorMap.put("40002", "不合法的凭证类型");
        weChatErrorMap.put("40003", "不合法的OpenID");
        weChatErrorMap.put("40004", "不合法的媒体文件类型");
        weChatErrorMap.put("40005", "不合法的文件类型");
        weChatErrorMap.put("40006", "不合法的文件大小");
        weChatErrorMap.put("40007", "该素材已从素材库中删除");
        weChatErrorMap.put("40008", "不合法的消息类型");
        weChatErrorMap.put("40009", "不合法的图片文件大小");
        weChatErrorMap.put("40010", "不合法的语音文件大小");
        weChatErrorMap.put("40011", "不合法的视频文件大小");
        weChatErrorMap.put("40012", "不合法的缩略图文件大小");
        weChatErrorMap.put("40013", "不合法的APPID");
        weChatErrorMap.put("40014", "不合法的access_token");
        weChatErrorMap.put("40015", "不合法的菜单类型");
        weChatErrorMap.put("40016", "不合法的按钮个数");
        weChatErrorMap.put("40018", "不合法的按钮名字长度");
        weChatErrorMap.put("40019", "不合法的按钮KEY长度");
        weChatErrorMap.put("40020", "不合法的按钮URL长度");
        weChatErrorMap.put("40021", "不合法的菜单版本号");
        weChatErrorMap.put("40022", "不合法的子菜单级数");
        weChatErrorMap.put("40023", "不合法的子菜单按钮个数");
        weChatErrorMap.put("40024", "不合法的子菜单按钮类型");
        weChatErrorMap.put("40025", "不合法的子菜单按钮名字长度");
        weChatErrorMap.put("40026", "不合法的子菜单按钮KEY长度");
        weChatErrorMap.put("40027", "不合法的子菜单按钮URL长度");
        weChatErrorMap.put("40028", "不合法的自定义菜单使用用户");
        weChatErrorMap.put("41001", "缺少access_token参数");
        weChatErrorMap.put("41002", "缺少appid参数");
        weChatErrorMap.put("41003", "缺少refresh_token参数");
        weChatErrorMap.put("41004", "缺少secret参数");
        weChatErrorMap.put("41005", "缺少多媒体文件数据");
        weChatErrorMap.put("41006", "缺少media_id参数");
        weChatErrorMap.put("41007", "缺少子菜单数据");
        weChatErrorMap.put("42001", "access_token超时");
        weChatErrorMap.put("43001", "需要GET请求");
        weChatErrorMap.put("43002", "需要POST请求");
        weChatErrorMap.put("43003", "需要HTTPS请求");
        weChatErrorMap.put("44001", "多媒体文件为空");
        weChatErrorMap.put("44002", "POST的数据包为空");
        weChatErrorMap.put("44003", "图文消息内容为空");
        weChatErrorMap.put("45001", "多媒体文件大小超过限制");
        weChatErrorMap.put("45002", "消息内容超过限制");
        weChatErrorMap.put("45003", "标题字段超过限制");
        weChatErrorMap.put("45004", "描述字段超过限制");
        weChatErrorMap.put("45005", "链接字段超过限制");
        weChatErrorMap.put("45006", "图片链接字段超过限制");
        weChatErrorMap.put("45007", "语音播放时间超过限制");
        weChatErrorMap.put("45008", "图文消息超过限制");
        weChatErrorMap.put("45009", "接口调用超过限制");
        weChatErrorMap.put("45010", "创建菜单个数超过限制");
        weChatErrorMap.put("45028", "群发次数超过限制");
        weChatErrorMap.put("46001", "不存在媒体数据");
        weChatErrorMap.put("46002", "不存在的菜单版本");
        weChatErrorMap.put("46003", "不存在的菜单数据");
        weChatErrorMap.put("47001", "解析JSON/XML内容错误");
    }
}