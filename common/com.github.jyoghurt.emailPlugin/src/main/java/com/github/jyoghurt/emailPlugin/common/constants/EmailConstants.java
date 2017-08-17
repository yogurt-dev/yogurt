package com.github.jyoghurt.emailPlugin.common.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class EmailConstants {
    private static Logger logger = LoggerFactory.getLogger(EmailConstants.class);
    public static String host;//发送方邮箱地址  temple：smtp.126.com
    public static String senderAccount;//发送方实际用户名
    public static String showName;//发送方显示名称
    public static String passWord;//发送方密码
    public static List<String> targetAddress;//发送地址列表
    public static String subject;//发送标题

}
