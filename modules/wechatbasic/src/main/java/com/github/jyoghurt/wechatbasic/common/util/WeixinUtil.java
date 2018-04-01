package com.github.jyoghurt.wechatbasic.common.util;

import com.github.jyoghurt.dataDict.service.DataDictUtils;
import com.github.jyoghurt.wechatbasic.common.constants.WechatConstants;
import com.github.jyoghurt.wechatbasic.common.pojo.AccessToken;
import com.github.jyoghurt.wechatbasic.common.pojo.Menu;
import com.github.jyoghurt.wechatbasic.common.pojo.NewsMap;
import com.github.jyoghurt.wechatbasic.common.resp.AllToMessage;
import com.github.jyoghurt.wechatbasic.common.resp.MessageByOpenId;
import com.github.jyoghurt.wechatbasic.domain.TokenEntity;
import com.github.jyoghurt.wechatbasic.enums.WeChatAppMsgConfig;
import com.github.jyoghurt.core.redis.RedisHandler;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class WeixinUtil {
    private static Logger log = LoggerFactory.getLogger(WeixinUtil.class);
    // 获取access_token的接口地址（GET） 限200（次/天）
    private final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    private static Map<String, TokenEntity> tokenMap = new HashMap<>();

    public static void cleanToken() {
        RedisHandler redisHandler = (RedisHandler) SpringContextUtils.getBean("redisHandler");
        redisHandler.getRedisTemplate().opsForValue().set(REDIS_HASH_KEY + "_" + SpringContextUtils.getProperty("wechatAppId"), null);
    }

    private static String REDIS_HASH_KEY = "wechatToken";

    /**
     * 获取access_token
     *
     * @return token
     */
    public static AccessToken getAccessToken() {
        String appId = SpringContextUtils.getProperty("wechatAppId");
        String appsecret = SpringContextUtils.getProperty("wechatAppSecret");
        return getAccessToken(appId, appsecret);
    }

    public static AccessToken getAccessToken(String appId, String appsecret) {
        RedisHandler redisHandler = (RedisHandler) SpringContextUtils.getBean("redisHandler");
        AccessToken accessToken = new AccessToken();
        if (redisHandler.getRedisTemplate().opsForValue().get(REDIS_HASH_KEY + "_" + appId) != null) {
            return accessToken.setToken(redisHandler.getRedisTemplate().opsForValue().get(REDIS_HASH_KEY + "_" + appId).toString());
        }
        String requestUrl = access_token_url.replace("APPID", appId).replace("APPSECRET", appsecret);
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
        redisHandler.getRedisTemplate().opsForValue().set(REDIS_HASH_KEY + "_" + appId, jsonObject.getString("access_token"));
        redisHandler.getRedisTemplate().expire(REDIS_HASH_KEY + "_" + appId, 60, TimeUnit.MINUTES);
        accessToken.setToken(jsonObject.getString("access_token"));
        return accessToken;
    }

    public static AccessToken reGetAccessToken() {
        String appId = SpringContextUtils.getProperty("wechatAppId");
        String appsecret = SpringContextUtils.getProperty("wechatAppSecret");
        return reGetToken(appId, appsecret);
    }

    public static AccessToken reGetToken(String appId, String appsecret) {
        RedisHandler redisHandler = (RedisHandler) SpringContextUtils.getBean("redisHandler");
        AccessToken accessToken = new AccessToken();
        String requestUrl = access_token_url.replace("APPID", appId).replace("APPSECRET", appsecret);
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
        redisHandler.getRedisTemplate().opsForValue().set(REDIS_HASH_KEY + "_" + appId, jsonObject.getString("access_token"));
        redisHandler.getRedisTemplate().expire(REDIS_HASH_KEY + "_" + appId, 60, TimeUnit.MINUTES);
        accessToken.setToken(jsonObject.getString("access_token"));
        return accessToken;
    }

    // 菜单创建（POST） 限100（次/天）
    private static String menu_create_url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

    // 上传图文素材（POST） 限100（次/天）
    private static String upload_news_url = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=ACCESS_TOKEN";

    //上传图片接口（POST） 限100（次/天）
    private static String upload_img_url = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=image";
    //群发接口（POST） 限100（次/天）
    private static String send_to_url = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN";
    //客服接口(POST) 无限
    private static String sendByOpenId = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
    //上传图文到素材库
    public static String upload_material_library = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=ACCESS_TOKEN";
    //上传其他到素材库
    public static String upload_material_library_order = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN";

    /**
     * 创建菜单
     *
     * @param menu        菜单实例
     * @param accessToken 有效的access_token
     * @return 0表示成功，其他值表示失败
     */
    public static int createMenu(Menu menu, String accessToken) {
        int result = 0;

        // 拼装创建菜单的url
        String url = menu_create_url.replace("ACCESS_TOKEN", accessToken);
        // 将菜单对象转换成json字符串
        String jsonMenu = JSONObject.fromObject(menu).toString();
        // 调用接口创建菜单
        JSONObject jsonObject = CommonUtil.httpsRequest(url, "POST", jsonMenu);

        if (null != jsonObject) {
            if (0 != jsonObject.getInt("errcode")) {
                result = jsonObject.getInt("errcode");
                log.error("创建菜单失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
            }
        }

        return result;
    }

    //上传图片获得thumb_media_id
    public static String uploadimg(String access_token, String filePath) throws IOException {


        String url = upload_img_url.replace("ACCESS_TOKEN", access_token);

        JSONObject jsonObject = HttpUtil.send(url, filePath);


        return jsonObject.get("media_id").toString();
    }

    //上传图文消息
    public static String createNews(NewsMap newsmap, String accessToken) {
        int result = 0;
        // 拼装创建图文消息的url
        String url = upload_news_url.replace("ACCESS_TOKEN", accessToken);
        // 将图文消息对象转换成json字符串
        String jsonNewsMap = JSONObject.fromObject(newsmap).toString();
        // 调用接口创建图文消息
        JSONObject jsonObject = CommonUtil.httpsRequest(url, "POST", jsonNewsMap);
        System.out.println(jsonObject);
        return jsonObject.get("media_id").toString();

    }
    //通过群组或者全部群发消息

    public static int sendAll(AllToMessage allToMessag, String accessToken) {
        // 拼装创建图文消息的url
        String url = send_to_url.replace("ACCESS_TOKEN", accessToken);
        // 将图文消息对象转换成json字符串
        String jsonNewsMap = JSONObject.fromObject(allToMessag).toString();
        // 调用接口创建图文消息
        JSONObject jsonObject = CommonUtil.httpsRequest(url, "POST", jsonNewsMap);


        return jsonObject.getInt("errcode");
    }

    ;

    /*图文消息上传素材库*/
    public static String uploadMaterialLibrary(NewsMap newsmap, String accessToken) {
        // 拼装创建图文消息的url
        String url = upload_news_url.replace("ACCESS_TOKEN", accessToken);
        // 将图文消息对象转换成json字符串
        String jsonNewsMap = JSONObject.fromObject(newsmap).toString();
        // 调用接口创建图文消息
        JSONObject jsonObject = CommonUtil.httpsRequest(url, "POST", jsonNewsMap);
        System.out.println(jsonObject);
        return jsonObject.get("media_id").toString();
    }

    //根据OPENID单发消息
    public static int sendByOpenId(MessageByOpenId messageByOpenId, String accessToken) {
        // 调用客服接口的url
        String url = sendByOpenId.replace("ACCESS_TOKEN", accessToken);
        // 将客服回复消息对象转换成json字符串
        String jsonNewsMap = JSONObject.fromObject(messageByOpenId).toString();
        JSONObject jsonObject = CommonUtil.httpsRequest(url, "POST", jsonNewsMap);
        return jsonObject.getInt("errcode");
    }

}