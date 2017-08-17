package com.github.jyoghurt.sms.aliyun;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.github.jyoghurt.dataDict.service.DataDictUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云短信工具类
 */
public class AliyunSmsUtils {
    private static Logger logger = LoggerFactory.getLogger(AliyunSmsUtils.class);


    //产品名称:云通信短信API产品,开发者无需替换
    private static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    private static final String domain = "dysmsapi.aliyuncs.com";

    private static final String accessKeyId;
    private static final String accessKeySecret;
    static {
        accessKeyId = DataDictUtils.getDataDictValueName("aliyunConfig", "aliyunConfig_accessKeyId", false);
        accessKeySecret = DataDictUtils.getDataDictValueName("aliyunConfig", "aliyunConfig_accessKeySecret", false);
    }


    /**
     * 发送短信
     *
     * @param phoneNumbers 手机号，英文逗号分隔
     * @param signName 短信签名
     * @param templateCode 模板编码
     * @param params      模板参数
     */
    public static SendSmsResponse send(String phoneNumbers, String signName, String templateCode, String... params) throws SmsException {
        Map<String, String> paramMap = new HashMap<>();
        if (params != null) {
            for (int i=0; i<params.length; i++) {
                paramMap.put("p" + (i + 1), params[i]);
            }
        }
        return send(phoneNumbers, signName, templateCode, paramMap);
    }

    /**
     * 发送短信
     *
     * @param phoneNumbers 手机号，英文逗号分隔
     * @param templateCode 模板编码
     * @param paramMap      模板变量
     */
    public static SendSmsResponse send(String phoneNumbers, String signName, String templateCode, Map<String, String> paramMap) throws SmsException {
        try {
            //可自助调整超时时间
    //        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
    //        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //必填:待发送手机号
            request.setPhoneNumbers(phoneNumbers);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(signName);
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            //可选:模板中的变量替换JSON串
            request.setTemplateParam(JSON.toJSONString(paramMap));

            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");

            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//            request.setOutId("yourOutId");

            //hint 此处可能会抛出异常，注意catch
            SendSmsResponse response = acsClient.getAcsResponse(request);
            if (!"OK".equals(response.getCode())) {
                throw new SmsException(response.getCode(), response.getMessage());
            }
            return response;
        } catch (ClientException e) {
            throw new SmsException(e.getErrCode(), e.getErrMsg());
        }
    }
}
