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
import com.github.jyoghurt.http.util.HttpClientUtils;
import com.github.jyoghurt.image.domain.ImageConfig;
import com.github.jyoghurt.image.service.ImgUploadHelper;
import com.github.jyoghurt.wechatbasic.common.pojo.CreatePermanentQRCodeParam;
import com.github.jyoghurt.wechatbasic.exception.WeChatException;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
     * @param scene_str   场景ID
     * @return ticket
     */
    public static String createPermanentQRCode(String accessToken, String scene_str, String page, int width) {
        try {
            // 拼接请求地址
            String requestUrl = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=ACCESS_TOKEN";
            requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
            // 创建永久带参二维码
            Map<String, Object> map = new HashedMap();
            map.put("scene", scene_str);
            map.put("page", page);
            map.put("width", width);
            CloseableHttpResponse httpResponse = new HttpClientHandler().sendPostAndOpen(requestUrl, JSON.toJSONString(map));
            System.out.println("***************"+httpResponse);
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
}
