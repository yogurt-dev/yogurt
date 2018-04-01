package com.github.jyoghurt.common.payment.business.alipay.common.utils;

import com.alipay.api.AlipayResponse;
import com.github.jyoghurt.http.util.HttpClientUtils;
import com.github.jyoghurt.common.payment.business.alipay.common.constants.AliPayConstants;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.config.AliPayConfigs;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.core.exception.BaseErrorException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * user: dell
 * date: 2016/4/13.
 */
public class AlipayCommonUtil {
    private static Logger logger = LoggerFactory.getLogger(AlipayCommonUtil.class);
    private static final String ERROR_MSG_TMPL = "初始化支付宝失败,{0},详细信息:{1}";

    public static void logResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(), response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    /**
     * 初始化SDK依赖的几个关键配置
     */
    private static AliPayConfigs initSDKConfiguration(String openApiDomain, String pid, String md5Key, String appid,
                                                      String privateKey, String publicKey, String alipayPublicKey) {
        AliPayConfigs aliPayConfigs = new AliPayConfigs();
        return aliPayConfigs.initAliPayConfigs(openApiDomain, pid, md5Key, appid, privateKey, publicKey,
                alipayPublicKey);
    }

    public static Boolean checkCallBack(String notifyId, AliPayConfigs aliPayConfigs) {
        try {
            String requestUrl = AliPayConstants.checkCallBackApi.replace("pid", aliPayConfigs.getPid()).replace("notifyId", notifyId);
            String response = HttpClientUtils.parseResponse(HttpClientUtils.get(requestUrl));
            return AliPayConstants.checkPase.equals(response);
        } catch (Exception e) {
            logger.error("支付宝支付验证异常",e);
            return false;
        }
    }

    /**
     * 初始化支付宝参数
     *
     * @param paymentRecordsT 支付记录
     * @return 支付宝参数
     */
    public static AliPayConfigs initAliPayConfigs(PaymentRecordsT paymentRecordsT) {
        Map<String, Object> vars = paymentRecordsT.getDataAreaMap();
        if (null == vars.get("aliPid")) {
            throw new BaseErrorException(ERROR_MSG_TMPL, "相关数据区中缺少参数aliPid", paymentRecordsT.toString());
        }
        if (null == vars.get("aliAppid")) {
            throw new BaseErrorException(ERROR_MSG_TMPL, "相关数据区中缺少参数aliAppid", paymentRecordsT.toString());
        }
        if (null == vars.get("aliMd5Key")) {
            throw new BaseErrorException(ERROR_MSG_TMPL, "相关数据区中缺少参数aliMd5Key", paymentRecordsT.toString());
        }
        if (null == vars.get("aliPrivateKey")) {
            throw new BaseErrorException(ERROR_MSG_TMPL, "相关数据区中缺少参数aliPrivateKey", paymentRecordsT.toString());
        }
        if (null == vars.get("aliPublicKey")) {
            throw new BaseErrorException(ERROR_MSG_TMPL, "相关数据区中缺少参数aliPublicKey", paymentRecordsT.toString());
        }
        if (null == vars.get("aliPayPublicKey")) {
            throw new BaseErrorException(ERROR_MSG_TMPL, "相关数据区中缺少参数aliPayPublicKey", paymentRecordsT.toString());
        }
        //初始化配置
        return AlipayCommonUtil.initSDKConfiguration("https://openapi.alipay.com/gateway.do",
                vars.get("aliPid").toString(), vars.get("aliMd5Key").toString(), vars.get("aliAppid").toString(),
                vars.get("aliPrivateKey").toString(), vars.get("aliPublicKey").toString(), vars.get("aliPayPublicKey").toString());
    }
}
