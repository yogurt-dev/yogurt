package com.github.jyoghurt.wechatbasic.common.util;

import com.alibaba.fastjson.JSON;
import com.github.jyoghurt.wechatbasic.common.pojo.*;
import com.github.jyoghurt.wechatbasic.common.resp.*;
import com.github.jyoghurt.wechatbasic.common.templet.ParentTpl;
import com.github.jyoghurt.wechatbasic.enums.WeChatMsgTypeEnum;
import com.github.jyoghurt.wechatbasic.exception.WeChatException;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.redis.RedisHandler;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import com.google.gson.Gson;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 高级接口工具类
 *
 * @author yutao
 * @date 2013-11-9
 */
public class AdvancedUtil {
    private static Logger log = LoggerFactory.getLogger(AdvancedUtil.class);

    /**
     * 组装文本客服消息
     *
     * @param openId  消息发送对象
     * @param content 文本消息内容
     * @return
     */
    public static String makeTextCustomMessage(String openId, String content) {
        // 对消息内容中的双引号进行转义
        content = content.replace("\"", "\\\"");
        String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
        return String.format(jsonMsg, openId, content);
    }

    /**
     * 组装图片客服消息
     *
     * @param openId  消息发送对象
     * @param mediaId 媒体文件id
     * @return
     */
    public static String makeImageCustomMessage(String openId, String mediaId) {
        String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"image\",\"image\":{\"media_id\":\"%s\"}}";
        return String.format(jsonMsg, openId, mediaId);
    }

    /**
     * 组装语音客服消息
     *
     * @param openId  消息发送对象
     * @param mediaId 媒体文件id
     * @return
     */
    public static String makeVoiceCustomMessage(String openId, String mediaId) {
        String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"voice\",\"voice\":{\"media_id\":\"%s\"}}";
        return String.format(jsonMsg, openId, mediaId);
    }

    /**
     * 组装视频客服消息
     *
     * @param openId       消息发送对象
     * @param mediaId      媒体文件id
     * @param thumbMediaId 视频消息缩略图的媒体id
     * @return
     */
    public static String makeVideoCustomMessage(String openId, String mediaId, String thumbMediaId) {
        String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"video\",\"video\":{\"media_id\":\"%s\",\"thumb_media_id\":\"%s\"}}";
        return String.format(jsonMsg, openId, mediaId, thumbMediaId);
    }

    /**
     * 组装音乐客服消息
     *
     * @param openId 消息发送对象
     * @param music  音乐对象
     * @return
     */
    public static String makeMusicCustomMessage(String openId, Music music) {
        String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"music\",\"music\":%s}";
        jsonMsg = String.format(jsonMsg, openId, JSONObject.fromObject(music).toString());
        // 将jsonMsg中的thumbmediaid替换为thumb_media_id
        jsonMsg = jsonMsg.replace("thumbmediaid", "thumb_media_id");
        return jsonMsg;
    }

    /**
     * 组装图文客服消息
     *
     * @param openId      消息发送对象
     * @param articleList 图文消息列表
     * @return
     */
    public static String makeNewsCustomMessage(String openId, List<Articles> articleList) {
        String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"news\",\"news\":{\"articles\":%s}}";
        jsonMsg = String.format(jsonMsg, openId, JSONArray.fromObject(articleList).toString().replaceAll("\"", "\\\""));
        // 将jsonMsg中的picUrl替换为picurl
        jsonMsg = jsonMsg.replace("picUrl", "picurl");
        return jsonMsg;
    }

    /**
     * 发送客服消息
     *
     * @param accessToken 接口访问凭证
     * @param jsonMsg     json格式的客服消息（包括touser、msgtype和消息内容）
     * @return true | false
     */
    public static boolean sendCustomMessage(String accessToken, String jsonMsg) {
        boolean result = false;
        try {
            log.info("消息内容：{}", jsonMsg);
            // 拼接请求地址
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
            requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
            // 发送客服消息
            JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", jsonMsg);
            if (null != jsonObject) {
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                if (0 == errorCode) {
                    result = true;
                    log.info("客服消息发送成功 errcode:{} errmsg:{}", errorCode, errorMsg);
                } else {
                    log.error("客服消息发送失败 errcode:{} errmsg:{}", errorCode, errorMsg);
                }
            }
            return result;
        } catch (WeChatException e) {
            log.error("微信发送客服消息异常,发送内容:{}", jsonMsg, e);
        }
        return result;
    }

    /**
     * 获取网页授权凭证
     * snsapi_base
     *
     * @param appId
     * @param redirectUri
     */
    public static void getOauth2(String appId, String redirectUri) {
        try {
            // 拼接请求地址
            String requestUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
            requestUrl = requestUrl.replace("APPID", appId);
            requestUrl = requestUrl.replace("REDIRECT_URI", redirectUri);
            // 获取网页授权凭证
            JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
        } catch (WeChatException e) {
            log.error("微信获取网页授权凭证异常", e);
            e.printStackTrace();
        }
    }

    /**
     * 获取网页授权凭证
     *
     * @param appId     公众账号的唯一标识
     * @param appSecret 公众账号的密钥
     * @param code
     * @return WeixinAouth2Token
     */
    public static WeixinOauth2Token getOauth2AccessToken(String appId, String appSecret, String code) {
        WeixinOauth2Token wat = null;
        JSONObject jsonObject = new JSONObject();
        try {
            // 拼接请求地址
            String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
            requestUrl = requestUrl.replace("APPID", appId);
            requestUrl = requestUrl.replace("SECRET", appSecret);
            requestUrl = requestUrl.replace("CODE", code);
            // 获取网页授权凭证
            jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
            if (null != jsonObject) {
                wat = new WeixinOauth2Token();
                wat.setAccessToken(jsonObject.getString("access_token"));
                wat.setExpiresIn(jsonObject.getInt("expires_in"));
                wat.setRefreshToken(jsonObject.getString("refresh_token"));
                wat.setOpenId(jsonObject.getString("openid"));
                wat.setScope(jsonObject.getString("scope"));
            }
            return wat;
        } catch (WeChatException e) {
            log.error("微信获取网页授权凭证异常", e);
        }
        return wat;
    }

    /**
     * 获取网页授权凭证
     *
     * @param appId     公众账号的唯一标识
     * @param appSecret 公众账号的密钥
     * @param code
     * @return openId
     */
    public static String getOauth2OpenId(String appId, String appSecret, String code) {
        log.info("========================================");
        log.info("从redis缓存中读取code,查看是否存在重复跳转现象" + code);
        log.info("========================================");
        Object token = null;
        RedisHandler redisHandler = (RedisHandler) SpringContextUtils.getBean("redisHandler");
        try {
            token = redisHandler.getRedisTemplate().opsForValue().get(appId + "_" + code);
        } catch (Exception e) {
            log.info("========================================");
            log.info("从redis获取缓存序列化失败");
            log.info("========================================");
        }
        if (null != token) {
            log.error("确定微信端存在同一code重复跳转获取token现象，现从redis读取token,不再进行请求" + code);
            return token.toString();
        }
        WeixinOauth2Token weixinOauth2Token = getOauth2AccessToken(appId, appSecret, code);
        if (null == weixinOauth2Token) {
            throw new BaseErrorException("微信根据code请求openId失败");
        }
        String openId = weixinOauth2Token.getOpenId();
        //将token置于redis缓存
        log.info("============================================");
        log.info("将token置于redis缓存");
        log.info("============================================");
        redisHandler.getRedisTemplate().opsForValue().set(appId + "_" + code, openId);
        redisHandler.getRedisTemplate().expire(appId + "_" + code, 60, TimeUnit.SECONDS);
        log.info("============================================");
        return openId;
    }

    /**
     * 刷新网页授权凭证
     *
     * @param appId        公众账号的唯一标识
     * @param refreshToken
     * @return WeixinAouth2Token
     */
    public static WeixinOauth2Token refreshOauth2AccessToken(String appId, String refreshToken) {
        WeixinOauth2Token wat = null;
        JSONObject jsonObject = new JSONObject();
        try {
            // 拼接请求地址
            String requestUrl = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
            requestUrl = requestUrl.replace("APPID", appId);
            requestUrl = requestUrl.replace("REFRESH_TOKEN", refreshToken);
            // 刷新网页授权凭证
            jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
            if (null != jsonObject) {
                wat = new WeixinOauth2Token();
                wat.setAccessToken(jsonObject.getString("access_token"));
                wat.setExpiresIn(jsonObject.getInt("expires_in"));
                wat.setRefreshToken(jsonObject.getString("refresh_token"));
                wat.setOpenId(jsonObject.getString("openid"));
                wat.setScope(jsonObject.getString("scope"));
            }
            return wat;
        } catch (WeChatException e) {
            log.error("微信刷新网页授权凭证异常", e);
        }
        return wat;
    }


    /**
     * 通过网页授权获取用户信息
     *
     * @param accessToken 网页授权接口调用凭证
     * @param openId      用户标识
     * @return SNSUserInfo
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    public static SNSUserInfo getSNSUserInfo(String accessToken, String openId) {
        SNSUserInfo snsUserInfo = null;
        JSONObject jsonObject = new JSONObject();
        try {
            // 拼接请求地址
            String requestUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
            requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
            // 通过网页授权获取用户信息
            jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
            if (null != jsonObject) {
                snsUserInfo = new SNSUserInfo();
                // 用户的标识
                snsUserInfo.setOpenId(jsonObject.getString("openid"));
                // 昵称
                snsUserInfo.setNickname(jsonObject.getString("nickname"));
                // 性别（1是男性，2是女性，0是未知）
                snsUserInfo.setSex(jsonObject.getInt("sex"));
                // 用户所在国家
                snsUserInfo.setCountry(jsonObject.getString("country"));
                // 用户所在省份
                snsUserInfo.setProvince(jsonObject.getString("province"));
                // 用户所在城市
                snsUserInfo.setCity(jsonObject.getString("city"));
                // 用户头像
                snsUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
                // 用户特权信息
                snsUserInfo.setPrivilegeList(JSONArray.toList(jsonObject.getJSONArray("privilege"), List.class));
            }
            return snsUserInfo;
        } catch (WeChatException e) {
            log.error("微信通过网页授权获取用户信息异常", e);
        }
        return snsUserInfo;
    }

    /**
     * 通过AccessToken
     *
     * @return SNSUserInfo
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    public static String getAccessToken() {
        SNSUserInfo snsUserInfo = null;
        JSONObject jsonObject = new JSONObject();
        try {
            // 拼接请求地址
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
            requestUrl = requestUrl.replace("APPID", "wx66776eafa3939846").replace("APPSECRET", "6146de557f4773639569f1c277f1a56a");
            // 通过网页授权获取用户信息
            jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
            if (null != jsonObject) {
                return jsonObject.getString("access_token");
            }
            return null;
        } catch (WeChatException e) {
            log.error("微信SNSUserInfo异常", e);
        }
        return null;
    }


    /**
     * 获取jsapi_ticket
     *
     * @param accessToken 网页授权接口调用凭证
     * @return SNSUserInfo
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    public static String getJsapiTicket(String accessToken) {
        SNSUserInfo snsUserInfo = null;
        JSONObject jsonObject = new JSONObject();
        try {
            // 拼接请求地址
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
            requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
            // 通过网页授权获取用户信息
            jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
            System.out.println(jsonObject);
            if (null != jsonObject) {
                // 用户的标识
                return jsonObject.getString("ticket");
            }
            return null;
        } catch (WeChatException e) {
            log.error("微信获取jsapi_ticket异常", e);
        }
        return null;
    }


    /**
     * 创建临时带参二维码
     *
     * @param accessToken   接口访问凭证
     * @param expireSeconds 二维码有效时间，单位为秒，最大不超过1800
     * @param sceneId       场景ID
     * @return WeixinQRCode
     */
    public static WeixinQRCode createTemporaryQRCode(String accessToken, int expireSeconds, int sceneId) {
        WeixinQRCode weixinQRCode = null;
        JSONObject jsonObject = new JSONObject();
        // 拼接请求地址
        try {
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
            requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
            // 需要提交的json数据
            String jsonMsg = "{\"expire_seconds\": %d, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": %d}}}";
            // 创建临时带参二维码
            jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonMsg, expireSeconds, sceneId));

            if (null != jsonObject) {
                weixinQRCode = new WeixinQRCode();
                weixinQRCode.setTicket(jsonObject.getString("ticket"));
                weixinQRCode.setExpireSeconds(jsonObject.getInt("expire_seconds"));
                log.info("创建临时带参二维码成功 ticket:{} expire_seconds:{}", weixinQRCode.getTicket(), weixinQRCode.getExpireSeconds());
            }
            return weixinQRCode;
        } catch (WeChatException e) {
            log.error("微信创建临时带参二维码异常", e);
        }
        return weixinQRCode;
    }

    /**
     * 创建永久带参二维码
     *
     * @param accessToken 接口访问凭证
     * @param sceneId     场景ID
     * @return ticket
     */
    public static String createPermanentQRCode(String accessToken, int sceneId) {
        String ticket = null;
        JSONObject jsonObject = new JSONObject();
        try {
            // 拼接请求地址
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
            requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
            // 需要提交的json数据
            String jsonMsg = "{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": %d}}}";
            // 创建永久带参二维码
            jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonMsg, sceneId));

            if (null != jsonObject) {
                ticket = jsonObject.getString("ticket");
                log.info("创建永久带参二维码成功 ticket:{}", ticket);
            }
            return ticket;
        } catch (WeChatException e) {
            log.error("微信创建永久带参二维码异常", e);
        }
        return ticket;
    }

    /**
     * 创建永久带参二维码
     * 参数为字符串
     *
     * @param accessToken 接口访问凭证
     * @param scene_str   场景ID
     * @return ticket
     */
    public static String createPermanentQRCode(String accessToken, String scene_str) {
        String ticket = null;
        JSONObject jsonObject = new JSONObject();
        try {
            // 拼接请求地址
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
            requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
            // 需要提交的json数据
            String jsonMsg = "{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": %d}}}";
            // 创建永久带参二维码
            jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonMsg, scene_str));
            if (null != jsonObject) {
                ticket = jsonObject.getString("ticket");
                log.info("创建永久带参二维码成功 ticket:{}", ticket);
            }
            return ticket;
        } catch (WeChatException e) {
            log.error("微信创建永久带参二维码异常", e);
        }
        return ticket;
    }

    /**
     * 根据ticket换取二维码
     *
     * @param ticket   二维码ticket
     * @param savePath 保存路径
     */
    public static String getQRCode(String ticket, String savePath) {
        String filePath = null;
        // 拼接请求地址
        String requestUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";
        requestUrl = requestUrl.replace("TICKET", CommonUtil.urlEncodeUTF8(ticket));
        try {
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");

            if (!savePath.endsWith("/")) {
                savePath += "/";
            }
            // 将ticket作为文件名
            filePath = savePath + ticket + ".jpg";

            // 将微信服务器返回的输入流写入文件
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            byte[] buf = new byte[8096];
            int size = 0;
            while ((size = bis.read(buf)) != -1)
                fos.write(buf, 0, size);
            fos.close();
            bis.close();

            conn.disconnect();
            log.info("根据ticket换取二维码成功，filePath=" + filePath);
        } catch (Exception e) {
            filePath = null;
            log.error("根据ticket换取二维码失败：{}", e);
        }
        return filePath;
    }

    /**
     * 获取用户信息
     *
     * @param accessToken 接口访问凭证
     * @param openId      用户标识
     * @return WeixinUserInfo
     */
    public static WeixinUserInfo getUserInfo(String accessToken, String openId) {
        WeixinUserInfo weixinUserInfo = null;
        JSONObject jsonObject = new JSONObject();
        try {
            // 拼接请求地址
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";
            requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
            // 获取用户信息
            jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
            if (null != jsonObject) {
                weixinUserInfo = new WeixinUserInfo();
                // 用户的标识
                weixinUserInfo.setOpenId(jsonObject.getString("openid"));
                // 关注状态（1是关注，0是未关注），未关注时获取不到其余信息
                weixinUserInfo.setSubscribe(jsonObject.getInt("subscribe"));
                // 用户关注时间
                weixinUserInfo.setSubscribeTime(jsonObject.getString("subscribe_time"));
                // 昵称
                weixinUserInfo.setNickname(jsonObject.getString("nickname"));
                // 用户的性别（1是男性，2是女性，0是未知）
                weixinUserInfo.setSex(jsonObject.getInt("sex"));
                // 用户所在国家
                weixinUserInfo.setCountry(jsonObject.getString("country"));
                // 用户所在省份
                weixinUserInfo.setProvince(jsonObject.getString("province"));
                // 用户所在城市
                weixinUserInfo.setCity(jsonObject.getString("city"));
                // 用户的语言，简体中文为zh_CN
                weixinUserInfo.setLanguage(jsonObject.getString("language"));
                // 用户头像
                weixinUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
            }
            return weixinUserInfo;
        } catch (WeChatException e) {
            log.error("微信获取用户信息异常", e);
        }
        return weixinUserInfo;

    }

    /**
     * 获取关注者列表
     *
     * @param accessToken 调用接口凭证
     * @param nextOpenId  第一个拉取的openId，不填默认从头开始拉取
     * @return WeixinUserList
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    public static WeixinUserList getUserList(String accessToken, String nextOpenId) throws WeChatException {
        WeixinUserList weixinUserList = null;
        if (null == nextOpenId)
            nextOpenId = "";
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("NEXT_OPENID", nextOpenId);
        // 获取关注者列表
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
        // 如果请求成功
        if (null != jsonObject) {
            try {
                weixinUserList = new WeixinUserList();
                weixinUserList.setTotal(jsonObject.getInt("total"));
                weixinUserList.setCount(jsonObject.getInt("count"));
                weixinUserList.setNextOpenId(jsonObject.getString("next_openid"));
                JSONObject dataObject = (JSONObject) jsonObject.get("data");
                weixinUserList.setOpenIdList(JSONArray.toList(dataObject.getJSONArray("openid"), List.class));
            } catch (JSONException e) {
                weixinUserList = null;
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("获取关注者列表失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return weixinUserList;
    }

    /**
     * 查询分组
     *
     * @param accessToken 调用接口凭证
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    public static List<WeixinGroup> getGroups(String accessToken) throws WeChatException {
        List<WeixinGroup> weixinGroupList = null;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/groups/get?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        // 查询分组
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject) {
            try {
                weixinGroupList = JSONArray.toList(jsonObject.getJSONArray("groups"), WeixinGroup.class);
            } catch (JSONException e) {
                weixinGroupList = null;
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("查询分组失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return weixinGroupList;
    }

    /**
     * 创建分组
     *
     * @param accessToken 接口访问凭证
     * @param groupName   分组名称
     * @return
     */
    public static WeixinGroup createGroup(String accessToken, String groupName) throws WeChatException {
        WeixinGroup weixinGroup = null;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/groups/create?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        // 需要提交的json数据
        String jsonData = "{\"group\" : {\"name\" : \"%s\"}}";
        // 创建分组
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonData, groupName));

        if (null != jsonObject) {
            try {
                weixinGroup = new WeixinGroup();
                weixinGroup.setId(jsonObject.getJSONObject("group").getInt("id"));
                weixinGroup.setName(jsonObject.getJSONObject("group").getString("name"));
            } catch (JSONException e) {
                weixinGroup = null;
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("创建分组失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return weixinGroup;
    }

    /**
     * 修改分组名
     *
     * @param accessToken 接口访问凭证
     * @param groupId     分组id
     * @param groupName   修改后的分组名
     * @return true | false
     */
    public static boolean updateGroup(String accessToken, int groupId, String groupName) throws WeChatException {
        boolean result = false;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/groups/update?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        // 需要提交的json数据
        String jsonData = "{\"group\": {\"id\": %d, \"name\": \"%s\"}}";
        // 修改分组名
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonData, groupId, groupName));

        if (null != jsonObject) {
            int errorCode = jsonObject.getInt("errcode");
            String errorMsg = jsonObject.getString("errmsg");
            if (0 == errorCode) {
                result = true;
                log.info("修改分组名成功 errcode:{} errmsg:{}", errorCode, errorMsg);
            } else {
                log.error("修改分组名失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return result;
    }

    /**
     * 移动用户分组
     *
     * @param accessToken 接口访问凭证
     * @param openId      用户标识
     * @param groupId     分组id
     * @return true | false
     */
    public static boolean updateMemberGroup(String accessToken, String openId, int groupId) throws WeChatException {
        boolean result = false;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/groups/members/update?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        // 需要提交的json数据
        String jsonData = "{\"openid\":\"%s\",\"to_groupid\":%d}";
        // 移动用户分组
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonData, openId, groupId));
        if (null != jsonObject) {
            int errorCode = jsonObject.getInt("errcode");
            String errorMsg = jsonObject.getString("errmsg");
            if (0 == errorCode) {
                result = true;
                log.info("移动用户分组成功 errcode:{} errmsg:{}", errorCode, errorMsg);
            } else {
                log.error("移动用户分组失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return result;
    }

    /*==============================================================消息发送==========================================================================*/
    public static JSONObject sendAll(String accessToken, String message, WeChatMsgTypeEnum msgType) throws WeChatException {
        return sendAll(accessToken, message, msgType, null);
    }

    public static JSONObject sendAll(String accessToken, String message, WeChatMsgTypeEnum msgType, String group_id) throws
            WeChatException {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        /*根据消息类型及是否存在分组构造消息发送对象*/
        AllToMessage allToMessage = new AllToMessage();
        Filter filter = new Filter();
        /*若存在分组则按照分组发送*/
        filter.setIs_to_all(true);
        if (group_id != null && !"".equals(group_id)) {
            filter.setIs_to_all(false);
            filter.setGroup_id(group_id);
        }
        allToMessage.setFilter(filter);
        allToMessage.setMsgtype(msgType);
        /*图文消息发送方式*/
        if (WeChatMsgTypeEnum.mpnews.equals(msgType)) {
            Mpnews mpnews = new Mpnews();
            mpnews.setMedia_id(message);
            allToMessage.setMpnews(mpnews);
        }
        /*文本消息发送方式*/
        if (WeChatMsgTypeEnum.text.equals(msgType)) {
            Text text = new Text();
            text.setContent(message);
            allToMessage.setText(text);
        }
        return JSONObject.fromObject(CommonUtil.httpsRequest(requestUrl, "POST", JSONObject.fromObject(allToMessage)
                .toString()));
    }
    /*===================================================================================自定义菜单===========================================*/
/*==============================================================媒体文件上传下载==========================================================================*/

    /**
     * 上传媒体文件
     *
     * @param accessToken  接口访问凭证
     * @param type         媒体文件类型（image、voice、video和thumb）
     * @param mediaFileUrl 媒体文件的url
     */
    public static WeixinMedia uploadMedia(String accessToken, String type, String mediaFileUrl, String uploadMediaUrl) throws WeChatException {
        WeixinMedia weixinMedia = new WeixinMedia();
        uploadMediaUrl = uploadMediaUrl.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);
        JSONObject jsonObject = CommonUtil.httpUploadMedia(uploadMediaUrl, mediaFileUrl);
        if ("thumb".equals(type)) {
            weixinMedia.setMedia_id(jsonObject.getString("thumb_media_id"));
            return weixinMedia;
        }
        weixinMedia.setMedia_id(jsonObject.getString("media_id"));
        return weixinMedia;
    }

    /**
     * 下载媒体文件
     *
     * @param accessToken 接口访问凭证
     * @param mediaId     媒体文件标识
     * @param savePath    文件在服务器上的存储路径
     * @return
     */
    public static String getMedia(String accessToken, String mediaId, String savePath) throws WeChatException {
        // 拼接请求地址
        String requestUrl = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("MEDIA_ID", mediaId);
        if (!savePath.endsWith("/")) {
            savePath += "/";
        }
        //拼接下载路径
        String downloadPath = savePath + mediaId;
        return CommonUtil.downLoadMedia(requestUrl, downloadPath);
    }


    /*上传普通媒体*/
    public static WeixinMedia uploadMedia(String accessToken, String type, String mediaFileUrl) throws WeChatException {
        return uploadMedia(accessToken, type, mediaFileUrl, "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE");
    }
    /*==============================================================永久素材库上传下载==========================================================================*/

    /**
     * 根据url上传永久媒体至素材库
     *
     * @param accessToken  accessToken
     * @param type         素材类型
     * @param mediaFileUrl 媒体素材url（http://）
     * @return WeixinMedia媒体对象
     */
    public static WeixinMedia uploadMaterial(String accessToken, String type, String mediaFileUrl) throws WeChatException {
        return uploadMedia(accessToken, type, mediaFileUrl, "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=TYPE");
    }

    /*上传永久媒体至素材库*/
    public static WeixinMedia uploadMaterial(String accessToken, String filePath) {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=image";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        Gson gson = new Gson();
        return gson.fromJson(JSON.toJSONString(CommonUtil.uploadBylocal(requestUrl, filePath)), WeixinMedia.class);
    }

    /*上传永久图文消息至素材库*/
    public static WeixinMedia uploadMaterialNews(String accessToken, NewsList newsList) throws WeChatException {
        WeixinMedia weixinMedia = new WeixinMedia();
        try {
            // 拼接请求地址
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=ACCESS_TOKEN";
            requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
            // 需要提交的json数据
            String jsonData = JSONObject.fromObject(newsList).toString();
            // 修改分组名
            JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", jsonData);
            weixinMedia.setMedia_id(jsonObject.get("media_id").toString());
            return weixinMedia;
        } catch (WeChatException e) {
            log.error("微信上传永久图文消息至素材库异常", e);
            throw new WeChatException(e.getMessage());
        }
    }

    /*根据文件路径上传图文消息内的图片获取URL*/
    public static String uploadImg(String accessToken, String filePath) throws WeChatException {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        return CommonUtil.uploadBylocal(requestUrl, filePath).get("url").toString();
    }

    /*根据文件url上传图文消息内的图片获取URL*/
    public static String uploadImgByInterLink(String accessToken, String filePath) throws WeChatException {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        JSONObject jsonObject = CommonUtil.httpUploadMedia(requestUrl, filePath);
        return jsonObject.get("url").toString();
    }

    /*获取永久媒体素材*/
    public static String getMaterial(String accessToken, String mediaId, String savePath, String fileExt) {
        if (!savePath.endsWith("/")) {
            savePath += "/";
        }
        //拼接下载路径
        String downloadPath = savePath + mediaId;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        // 需要提交的json数据
        String jsonData = "{\"media_id\":\"%s\"}";
        String outputStr = String.format(jsonData, mediaId);
        return CommonUtil.downLoadMaterial(requestUrl, outputStr, downloadPath, fileExt);
    }

    /*获取永久图文素材*/
    public static MaterialNewsContent getMaterialNews(String accessToken, String mediaId) throws WeChatException {
        Gson gson = new Gson();
        MaterialNewsContent materialNewsContent = new MaterialNewsContent();
        JSONObject jsonObject = new JSONObject();
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        // 需要提交的json数据
        String jsonData = "{\"media_id\":\"%s\"}";
        // 移动用户分组
        jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonData, mediaId));
        if (null != jsonObject) {
            materialNewsContent = gson.fromJson(JSONObject.fromObject(jsonObject).toString(), MaterialNewsContent.class);
        }
        return materialNewsContent;
    }

    /*删除永久素材*/
    public static JSONObject delMaterial(String accessToken, String mediaId) throws WeChatException {
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        // 需要提交的json数据
        String jsonData = "{\"media_id\":\"%s\"}";
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonData, mediaId));
        return jsonObject;
    }

    /*修改永久图文素材*/
    public static JSONObject updateMaterialNews(String accessToken, UpdateMaterialNewsMap updateMaterialNewsMap) throws WeChatException {
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/material/update_news?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        return CommonUtil.httpsRequest(requestUrl, "POST", JSONObject.fromObject(updateMaterialNewsMap)
                .toString());
    }

    /*获取永久素材总数*/
    public static MaterialCount getMaterialCount(String accessToken) throws WeChatException {
        Gson gson = new Gson();
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        // 获取素材总数
        return gson.fromJson(JSONObject.fromObject(CommonUtil.httpsRequest(requestUrl, "GET", null)).toString(), MaterialCount.class);
    }

    /*获取永久素材列表*/
    public static JSONObject batchgetMaterialBase(String accessToken, BathgetMaterialParam bathgetMaterialParam) throws WeChatException {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        return CommonUtil.httpsRequest(requestUrl, "POST", JSONObject.fromObject(bathgetMaterialParam).toString());
    }

    /*获取永久图文素材列表*/
    public static MaterialNewsMapList batchgetMaterialNews(String accessToken, BathgetMaterialParam bathgetMaterialParam) throws WeChatException {
        Gson gson = new Gson();
        bathgetMaterialParam.setType("news");
        return gson.fromJson(JSONObject.fromObject(batchgetMaterialBase(accessToken, bathgetMaterialParam)).toString(),
                MaterialNewsMapList.class);
    }

    /*获取永久除图文外其他素材列表*/
    public static MaterialList batchgetMaterial(String accessToken, BathgetMaterialParam bathgetMaterialParam) throws WeChatException {
        Gson gson = new Gson();
        return gson.fromJson(JSONObject.fromObject(batchgetMaterialBase(accessToken, bathgetMaterialParam)).toString(),
                MaterialList.class);
    }

    /*预览*/
    public static void preView(String accessToken, PreViewParam preViewParam) throws WeChatException {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        CommonUtil.httpsRequest(requestUrl, "POST", JSONObject.fromObject(preViewParam).toString());
    }

    public static JSONObject sendTemple(String accessToken, ParentTpl parentTpl) throws WeChatException {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        return CommonUtil.httpsRequest(requestUrl, "POST", JSON.toJSONString(parentTpl));
    }

    public static JSONObject sendTemple(String accessToken, JSONObject parentTpl) throws WeChatException {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        return CommonUtil.httpsRequest(requestUrl, "POST", JSON.toJSONString(parentTpl));
    }

    public static void main(String args[]) {
        createPermanentQRCode(getAccessToken(), "驴鱼登录");
        //"media_id" -> "IMEkemtKyIY4OZstSo00qQw48zhqGiHCf9ZamV8EQPY"
        getOauth2("wx66776eafa3939846", "http://zjl19881215.6655.la/PaymentWechatJsApi.html");
/*        Date d = new Date(1395658920);
        d.getTime();
        String accessToken = getAccessToken();
        WeChatOrderSubmitTpl weChatOrderSubmitTpl = new WeChatOrderSubmitTpl();
        //首行展示
        FirstTpl firstTpl = new FirstTpl();
        //订单Id
        OrderIdTpl orderIdTpl = new OrderIdTpl();
        //待支付金额
        PaymentAmountTpl paymentAmountTpl = new PaymentAmountTpl();
        //扩展字段key
        BackupFieldNameTpl backupFieldNameTpl = new BackupFieldNameTpl();
        backupFieldNameTpl.setValue("商品名称:");
        //扩展字段value
        BackupFieldDataTpl backupFieldDataTpl = new BackupFieldDataTpl();
        backupFieldDataTpl.setValue("大力");
        orderIdTpl.setValue("驴鱼订单-2001231231123");
        firstTpl.setValue("123");
        paymentAmountTpl.setValue("33.24");
        weChatOrderSubmitTpl.setFirst(firstTpl);
        weChatOrderSubmitTpl.setOrderID(orderIdTpl);
        weChatOrderSubmitTpl.setOrderMoneySum(paymentAmountTpl);
        weChatOrderSubmitTpl.setBackupFieldName(backupFieldNameTpl);
        weChatOrderSubmitTpl.setBackupFieldData(backupFieldDataTpl);
        ParentTpl parentTpl = new ParentTpl();
        parentTpl.setData(weChatOrderSubmitTpl);
        parentTpl.setTemplate_id("d5UQFqf38z2vYn7bF4IhY10Fej18QuMDADxjKzpDA9E");
        parentTpl.setTouser("oiDO7wm8imcXPq0xLtQWHe4SxV8s");
        sendTemple(accessToken, parentTpl);*/
/*        BathgetMaterialParam bathgetMaterialParam = new BathgetMaterialParam();
        bathgetMaterialParam.setType("news");
        bathgetMaterialParam.setOffset(0);
        bathgetMaterialParam.setCount(20);
        batchgetMaterialNews(accessToken, bathgetMaterialParam);*/
  /*      sendCustomMessage(accessToken, makeNewsCustomMessage("o9MbCvptRpUhT81EyJB1H0YsLAW0", getMaterialNews
                (accessToken, "IMEkemtKyIY4OZstSo00qedqwRense0BXdfXJInTVRE").getNews_item()));*/
        /*测试永久素材库*/
        //测试预览
/*        PreViewParam preViewParam = new PreViewParam();
        Mpnews mpnews = new Mpnews();
        mpnews.setMedia_id("fvLXaeONc6vHQLWGRUzdhyNtr6H2FZ4xH7cjP3Ut17Q");
        preViewParam.setTowxname("jianlin1215");
        preViewParam.setMsgtype("mpnews");
        preViewParam.setMpnews(mpnews);
        preView(accessToken, preViewParam);
        System.out.print("fvLXaeONc6vHQLWGRUzdhyNtr6H2FZ4xH7cjP3Ut17Q");*/
        //测试获取
     /*   getMaterialNews(accessToken, "fvLXaeONc6vHQLWGRUzdhyNtr6H2FZ4xH7cjP3Ut17Q");
        System.out.print("success");*/
        //测试修改
        //测试删除
   /*     delMaterial(accessToken, "fvLXaeONc6vHQLWGRUzdhyNtr6H2FZ4xH7cjP3Ut17Q");
        System.out.print("success");*/
/*        // 获取接口访问凭证
        String accessToken = CommonUtil.getToken("APPID", "APPSECRET").getAccessToken();

        *//**
         * 发送客服消息（文本消息）
         *//*
        // 组装文本客服消息
        String jsonTextMsg = makeTextCustomMessage("oEdzejiHCDqafJbz4WNJtWTMbDcE", "点击查看<a href=\"http://blog.csdn.net/lyq8479\">柳峰的博客</a>");
        // 发送客服消息
        sendCustomMessage(accessToken, jsonTextMsg);

        *//**
         * 发送客服消息（图文消息）
         *//*
        Article article1 = new Article();
        article1.setTitle("微信上也能斗地主");
        article1.setDescription("");
        article1.setPicUrl("http://www.egouji.com/xiaoq/game/doudizhu_big.png");
        article1.setUrl("http://resource.duopao.com/duopao/games/small_games/weixingame/Doudizhu/doudizhu.htm");
        Article article2 = new Article();
        article2.setTitle("傲气雄鹰\n80后不得不玩的经典游戏");
        article2.setDescription("");
        article2.setPicUrl("http://www.egouji.com/xiaoq/game/aoqixiongying.png");
        article2.setUrl("http://resource.duopao.com/duopao/games/small_games/weixingame/Plane/aoqixiongying.html");
        List<Article> list = new ArrayList<Article>();
        list.add(article1);
        list.add(article2);
        // 组装图文客服消息
        String jsonNewsMsg = makeNewsCustomMessage("oEdzejiHCDqafJbz4WNJtWTMbDcE", list);
        // 发送客服消息
        sendCustomMessage(accessToken, jsonNewsMsg);

        *//**
         * 创建临时二维码
         *//*
        WeixinQRCode weixinQRCode = createTemporaryQRCode(accessToken, 900, 111111);
        // 临时二维码的ticket
        System.out.println(weixinQRCode.getTicket());
        // 临时二维码的有效时间
        System.out.println(weixinQRCode.getExpireSeconds());

        *//**
         * 根据ticket换取二维码
         *//*
        String ticket = "gQEg7zoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL2lIVVJ3VmJsTzFsQ0ZuQ0Y1bG5WAAIEW35+UgMEAAAAAA==";
        String savePath = "G:/download";
        // 根据ticket换取二维码
        getQRCode(ticket, savePath);

        *//**
         * 获取用户信息
         *//*
        WeixinUserInfo user = getUserInfo(accessToken, "oEdzejiHCDqafJbz4WNJtWTMbDcE");
        System.out.println("OpenID：" + user.getOpenId());
        System.out.println("关注状态：" + user.getSubscribe());
        System.out.println("关注时间：" + user.getSubscribeTime());
        System.out.println("昵称：" + user.getNickname());
        System.out.println("性别：" + user.getSex());
        System.out.println("国家：" + user.getCountry());
        System.out.println("省份：" + user.getProvince());
        System.out.println("城市：" + user.getCity());
        System.out.println("语言：" + user.getLanguage());
        System.out.println("头像：" + user.getHeadImgUrl());

        *//**
         * 获取关注者列表
         *//*
        WeixinUserList weixinUserList = getUserList(accessToken, "");
        System.out.println("总关注用户数：" + weixinUserList.getTotal());
        System.out.println("本次获取用户数：" + weixinUserList.getCount());
        System.out.println("OpenID列表：" + weixinUserList.getOpenIdList().toString());
        System.out.println("next_openid：" + weixinUserList.getNextOpenId());

        *//**
         * 查询分组
         *//*
        List<WeixinGroup> groupList = getGroups(accessToken);
        // 循环输出各分组信息
        for (WeixinGroup group : groupList) {
            System.out.println(String.format("ID：%d 名称：%s 用户数：%d", group.getId(), group.getName(), group.getCount()));
        }

        *//**
         * 创建分组
         *//*
        WeixinGroup group = createGroup(accessToken, "公司员工");
        System.out.println(String.format("成功创建分组：%s id：%d", group.getName(), group.getId()));

        *//**
         * 修改分组名
         *//*
        updateGroup(accessToken, 100, "同事");

        *//**
         * 移动用户分组
         *//*
        updateMemberGroup(accessToken, "oEdzejiHCDqafJbz4WNJtWTMbDcE", 100);*/

        /**
         * 上传多媒体文件
         */
/*        WeixinMedia weixinMedia = uploadMedia("ubKXJTw689JSAaAx2URQXXBhX7aayOFUDf2B2Xn7ifmHxkof_dLuPIZdFL9PONGa915LWSFiKnb71B8sjIcM9IhyTH8olxqcXnWzTsnte_kRJEgACAIAV",
                "image", "http://localhost:8080/mim/upload/wx/20151110/20151110215455_44.jpg");
        System.out.println(weixinMedia.getMediaId());
        System.out.println(weixinMedia.getType());
        System.out.println(weixinMedia.getCreatedAt());*/

        /**
         * 下载多媒体文件
         */
/*        getMedia("ubKXJTw689JSAaAx2URQXXBhX7aayOFUDf2B2Xn7ifmHxkof_dLuPIZdFL9PONGa915LWSFiKnb71B8sjIcM9IhyTH8olxqcXnWzTsnte_kRJEgACAIAV",
                "yb3ba2FPH1qTnJvWrQRr-HwTbcs4juPv2em70xT86lo", "E:/wechatbasic");*/
 /*       getMaterial("ubKXJTw689JSAaAx2URQXXBhX7aayOFUDf2B2Xn7ifmHxkof_dLuPIZdFL9PONGa915LWSFiKnb71B8sjIcM9IhyTH8olxqcXnWzTsnte_kRJEgACAIAV",
                "yb3ba2FPH1qTnJvWrQRr-KBIJCOUCpKpTUSSlhtivxs", "E:/wechatbasic", ".jpg");*/
    /*    getMaterialNews
                ("YPO1renJ_-OlmCLa2asFpO6K3eJ2hdzzPVBJQG6xHWkmobekDvu3Z72IJGk1KA4UO-Ie6n11L4o8ZEBAVdOw7sjEy3uI2Y9xBjGfYEZPgQQDDUeAFAWWN"
                        , "yb3ba2FPH1qTnJvWrQRr-KBIJCOUCpKpTUSSlhtivxs");*/
      /*  "http://zjl19881215.6655.la/mim/upload/wx/20151110/20151110215455_44.jpg"*/
        // 拼装请求地址
  /*      String uploadMediaUrl = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=TYPE";*/
        /*返回的永久图文素材*///"url" -> "http://mp.weixin.qq.com/s?__biz=MzAwNDcwMDIxNg==&mid=400922169&idx=1&sn=22f7d2322d31a311d6df8021b522aa79#rd"
        //{"touser":"o9MbCvptRpUhT81EyJB1H0YsLAW0","msgtype":"news",
        // "news":{"articles":[{"author":"","content":"<p>21<\/p>\n","content_source_url":"","digest":"","show_cover_pic":"1","thumb_media_id":"1108501790125","title":"55","url":"http://mp.weixin.qq.com/s?__biz=MzIwMDQzNTk1Ng==&mid=400227776&idx=1&sn=74a6515c5f093375532d68697e86c5dc#rd"}]}}
        System.out.println(123);
    }
}
