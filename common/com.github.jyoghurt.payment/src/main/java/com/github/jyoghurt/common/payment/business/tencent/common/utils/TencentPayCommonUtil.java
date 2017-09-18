package com.github.jyoghurt.common.payment.business.tencent.common.utils;

import com.github.jyoghurt.common.payment.business.tencent.common.config.TencentConfigure;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.utils.SpringContextUtils;

import java.util.Map;


/**
 * user: dell
 * date:2016/3/24.
 */
public class TencentPayCommonUtil {
    private static final String Error_Msg_Tmpl = "初始化微信支付参数异常，异常原因:{0},参数详细信息:{1}";

    /**
     * 初始化SDK依赖的几个关键配置
     *
     * @param key           签名算法需要用到的秘钥
     * @param appID         公众账号ID
     * @param mchID         商户ID
     * @param subMchID      子商户ID，受理模式必填
     * @param certLocalPath HTTP证书在服务器中的路径，用来加载证书用
     */
    private static TencentConfigure initSDKConfiguration(String key, String appID, String mchID, String subMchID, String certLocalPath) {
        TencentConfigure tencentConfigure = new TencentConfigure();
        return tencentConfigure.initTencentConfigure(key, appID, mchID, subMchID, certLocalPath);
    }

    /**
     * 初始化微信SDK
     *
     * @param paymentRecordsT 支付记录
     */
    public static TencentConfigure initTencentPayConfigs(PaymentRecordsT paymentRecordsT) {
        Map<String, Object> vars = paymentRecordsT.getDataAreaMap();
        if (null == vars.get("tencentKey")) {
            throw new BaseErrorException(Error_Msg_Tmpl, "相关数据区中缺少参数tencentKey", paymentRecordsT.toString());
        }
        if (null == vars.get("tencentAppId")&&PaymentGatewayEnum.TENCENT_APPLET != paymentRecordsT.getPaymentMethod()) {
            throw new BaseErrorException(Error_Msg_Tmpl, "相关数据区中缺少参数tencentAppId", paymentRecordsT.toString());
        }
        if (null == vars.get("tencentMchId")&&PaymentGatewayEnum.TENCENT_APPLET != paymentRecordsT.getPaymentMethod()) {
            throw new BaseErrorException(Error_Msg_Tmpl, "相关数据区中缺少参数tencentMchId", paymentRecordsT.toString());
        }
        //小程序
        if (PaymentGatewayEnum.TENCENT_APPLET == paymentRecordsT.getPaymentMethod()) {
            if (null == vars.get("appletAppId")) {
                throw new BaseErrorException(Error_Msg_Tmpl, "相关数据区中缺少参数appletAppId", paymentRecordsT.toString());
            }
            if (null == vars.get("appletMchId")) {
                throw new BaseErrorException(Error_Msg_Tmpl, "相关数据区中缺少参数appletMchId", paymentRecordsT.toString());
            }
        }
        //证书下载路径
        String uploadPath = SpringContextUtils.getProperty("certPath");
        String env = SpringContextUtils.getProperty("environmentName");
        String split = System.getProperty("file.separator");
        String certName = "lvyukeji_cert";
        String downloadPath = uploadPath + split + "certificate" + split + env + split + (PaymentGatewayEnum.TENCENT_APPLET == paymentRecordsT.getPaymentMethod()?"applet_"+certName:certName);
        //初始化配置
        TencentConfigure tencentConfigure = initSDKConfiguration(
                vars.get("tencentKey").toString(),
                PaymentGatewayEnum.TENCENT_APPLET == paymentRecordsT.getPaymentMethod()
                        ? vars.get("appletAppId").toString()
                        : vars.get("tencentAppId").toString(),
                PaymentGatewayEnum.TENCENT_APPLET == paymentRecordsT.getPaymentMethod()
                        ? vars.get("appletMchId").toString()
                        : vars.get("tencentMchId").toString(),
                null == vars.get("tencentSubMchId") || PaymentGatewayEnum.TENCENT_APPLET == paymentRecordsT.getPaymentMethod()
                        ? null
                        : vars.get("tencentSubMchId").toString(),
                downloadPath);
        return tencentConfigure;
    }
}
