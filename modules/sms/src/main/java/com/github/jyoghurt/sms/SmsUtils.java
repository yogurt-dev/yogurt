package com.github.jyoghurt.sms;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.github.jyoghurt.dataDict.service.DataDictUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: utils
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2015-12-01 10:35
 */
public class SmsUtils {
    private static Logger logger = LoggerFactory.getLogger(SmsUtils.class);
    private static SmsConfig smsConfig;

    /**
     * 发送短信
     *
     * @param to         发送人列表，英文逗号分隔
     * @param templateId 模板编码
     * @param datas      替换数据
     */
    public static void send(String to, String templateId, String[] datas) throws SmsException {
        HashMap<String, Object> result;
        //初始化SDK
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
        if (null == smsConfig) {
            smsConfig = (SmsConfig) DataDictUtils.getDataDictObject(SmsConfig.class);
        }

        //******************************注释*********************************************
        //*初始化服务器地址和端口                                                       *
        //*沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
        //*生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883");       *
        //*******************************************************************************

        restAPI.init(smsConfig.getServerIP(), smsConfig.getServerPort());


        //******************************注释*********************************************
        //*初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN     *
        //*ACOUNT SID和AUTH TOKEN在登陆官网后，在“应用-管理控制台”中查看开发者主账号获取*
        //*参数顺序：第一个参数是ACOUNT SID，第二个参数是AUTH TOKEN。                   *
        //*******************************************************************************
        restAPI.setAccount(smsConfig.getAccountSid(), smsConfig.getAccountToken());


        //******************************注释*********************************************
        //*初始化应用ID                                                                 *
        //*测试开发可使用“测试Demo”的APP ID，正式上线需要使用自己创建的应用的App ID     *
        //*应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID*
        //*******************************************************************************
        restAPI.setAppId(smsConfig.getAppId());


        //******************************注释****************************************************************
        //*调用发送模板短信的接口发送短信                                                                  *
        //*参数顺序说明：                                                                                  *
        //*第一个参数:是要发送的手机号码，可以用逗号分隔，一次最多支持100个手机号                          *
        //*第二个参数:是模板ID，在平台上创建的短信模板的ID值；测试的时候可以使用系统的默认模板，id为1。    *
        //*系统默认模板的内容为“【云通讯】您使用的是云通讯短信模板，您的验证码是{1}，请于{2}分钟内正确输入”*
        //*第三个参数是要替换的内容数组。																														       *
        //**************************************************************************************************

        //**************************************举例说明***********************************************************************
        //*假设您用测试Demo的APP ID，则需使用默认模板ID 1，发送手机号是13800000000，传入参数为6532和5，则调用方式为           *
        //*result = restAPI.sendTemplateSMS("13800000000","1" ,new String[]{"6532","5"});																		  *
        //*则13800000000手机号收到的短信内容是：【云通讯】您使用的是云通讯短信模板，您的验证码是6532，请于5分钟内正确输入     *
        //*********************************************************************************************************************

        result = restAPI.sendTemplateSMS(to, templateId, datas);

        logger.info("SDKTestGetSubAccounts result=" + result);
        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            logger.info(" send sms result is " + result.get("data"));
            return;
        }
        throw new SmsException(SmsException.ERROR_91100,(String) result.get("statusCode"), (String) result.get("statusMsg"));
    }
}
