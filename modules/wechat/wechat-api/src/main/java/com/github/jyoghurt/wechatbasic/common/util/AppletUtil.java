package com.github.jyoghurt.wechatbasic.common.util;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/12/29
 * Time: 14:18
 * To change this template use File | Settings | File Templates.
 */

import com.alibaba.fastjson.JSON;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import com.github.jyoghurt.http.handler.HttpClientHandler;
import com.github.jyoghurt.image.domain.ImageConfig;
import com.github.jyoghurt.image.service.ImgUploadHelper;
import com.github.jyoghurt.wechatbasic.common.pojo.AccessToken;
import com.github.jyoghurt.wechatbasic.common.pojo.CreatePermanentQRCodeParam;
import com.github.jyoghurt.wechatbasic.exception.WeChatException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

/**
 * 微信小程序接口
 */
public class AppletUtil {
    private static Logger log = LoggerFactory.getLogger(AppletUtil.class);

    /**
     * 创建永久带参二维码
     *
     * @param accessToken 接口访问凭证
     * @param sceneId     场景ID
     * @return ticket
     */
    /**
     * 创建永久带参二维码
     * 参数为字符串
     *
     * @param accessToken 接口访问凭证
     * @return ticket
     */
    public static String createPermanentQRCode(CreatePermanentQRCodeParam param) {
        try {
            // 拼接请求地址
            CloseableHttpResponse httpResponse = getCloseableHttpResponse( param);
//            System.out.println("***************"+httpResponse);
            try {
                ImageConfig imageConfig = new ImageConfig();
                imageConfig.setModuleName("appletQR");
                InputStream str = httpResponse.getEntity().getContent();
                return ImgUploadHelper.upload(SpringContextUtils.getProperty("uploadPath"),
                        SpringContextUtils.getProperty("downloadPath"), imageConfig, UUID.randomUUID() + ".png", str);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("小程序生成带参数的二维码异常", e);
            }
        } catch (WeChatException e) {
            log.error("微信创建永久带参二维码异常", e);
        }
        return null;
    }


    public static CloseableHttpResponse getCloseableHttpResponse(CreatePermanentQRCodeParam param) {
        AccessToken token = WeixinUtil.getAccessToken(SpringContextUtils.getProperty("appletAppId"), SpringContextUtils.getProperty("appletSecret"));
        String requestUrl = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", token.getToken());
        // 创建永久带参二维码
        Map<String, Object> map = new HashedMap();
        map.put("scene", param.getScene());
        map.put("page", param.getPage());
        map.put("width", param.getWidth());
        map.put("auto_color", param.getAuto_color());
        map.put("line_color", param.getLine_color());
//            "{\"r\":\"80\",\"g\":\"142\",\"b\":\"248\"}");
        return new HttpClientHandler().sendPostAndOpen(requestUrl, JSON.toJSONString(map));
    }
}
