package com.github.jyoghurt.qqEmail.common.util;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.jyoghurt.qqEmail.Exception.EmailException;
import com.github.jyoghurt.qqEmail.domain.SyncUnit;
import com.github.jyoghurt.qqEmail.domain.SyncUser;
import com.github.jyoghurt.qqEmail.domain.UserInfoVo;
import com.github.jyoghurt.qqEmail.domain.UserUpdateListVo;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QQEmailUtil {
    /**
     * 根据实体中的属性值是否为空拼接url
     *
     * @param model 书体
     * @return urlParam URL参数
     * @
     */
    public static String analysisModelToURL(Object model) throws EmailException {
        try {
            String urlParam = "";
         /*获取model成员变量*/
            Field[] field = model.getClass().getDeclaredFields();
            for (Field presentField : field) {
             /*获取改成员变量get方法*/
                Method method = model.getClass().getMethod("get" + presentField.getName());
             /*反射获取value*/
                String value = (String) method.invoke(model);
                urlParam += value == null ? "" : presentField.getName() + "=" + value + "&";
            }
            urlParam = urlParam.endsWith("&") ? urlParam.substring(0, urlParam.length() - 1) : urlParam;
            return urlParam;
        } catch (Exception e) {
            throw new EmailException("-1", "根据实体拼接url失败！");
        }
    }

    /**
     * 根据用户名及key获取token
     *
     * @param client_id     管理员账号
     * @param client_secret 接口key
     * @return JSONObject tokenObj
     * @
     */
    public static JSONObject getToken(String client_id, String client_secret) throws EmailException {
        if(StringUtils.isEmpty(client_id)||StringUtils.isEmpty(client_secret)){
            throw new EmailException("1","未配置client_id或密码");
        }
        String param = "grant_type=client_credentials";
        param += "&client_id=" + client_id;
        param += "&client_secret=" + client_secret;
        return JSON.parseObject(HttpUtil.sendPostUrl(QQEmailConstants.getTokenURL, param, QQEmailConstants.charset));
    }

    /**
     * 根据token及版本号同步用户列表
     *
     * @param ver   版本号
     * @param token access_token
     * @return UserUpdateListVo
     * @
     */
    public static UserUpdateListVo getUserListByVer(String ver, String token) throws EmailException {
        Gson gson = new Gson();
        /*封装请求信息*/
        String param = "ver=" + ver;
        /*请求头部信息封装token*/
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + token);
        return gson.fromJson(HttpUtil.sendPostUrl(QQEmailConstants.getUserListURL, param, QQEmailConstants.charset, headerMap), UserUpdateListVo.class);
    }

    /**
     * 根据用户账号同步用户信息
     *
     * @param alias 用户邮箱
     * @param token access_token
     * @return UserInfoVo
     */
    public static UserInfoVo getUserInfoByAlias(String alias, String token) throws EmailException {
        Gson gson = new Gson();
          /*封装请求信息*/
        String param = "alias=" + alias;
        /*请求头部信息封装token*/
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + token);
        return gson.fromJson(HttpUtil.sendPostUrl(QQEmailConstants.getUserInfoURL, param, QQEmailConstants.charset,
                headerMap), UserInfoVo.class);
    }

    /**
     * 根据用户账号同步用户信息
     *
     * @param userInfoVo 用户资料
     * @param token      access_token
     * @return UserInfoVo
     * @
     */
    public static UserInfoVo getUserInfoByAlias(UserInfoVo userInfoVo, String token) throws EmailException {
        Gson gson = new Gson();
          /*封装请求信息*/
        String param = "alias=" + userInfoVo.getAlias();
        /*请求头部信息封装token*/
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + token);
        return gson.fromJson(HttpUtil.sendPostUrl(QQEmailConstants.getUserInfoURL, param, QQEmailConstants.charset,
                headerMap), UserInfoVo.class).setAction(userInfoVo.getAction());
    }

    /**
     * 根据用户账号同步用户信息
     *
     * @param userInfoVoList 用户资料集合
     * @param token          access_token
     * @return List<UserInfoVo>
     * @
     */
    public static List<UserInfoVo> getUserInfoByAlias(List<UserInfoVo> userInfoVoList, String token) throws EmailException {
        Gson gson = new Gson();
        /*请求头部信息封装token*/
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + token);
        /*返回用户详情列表*/
        List<UserInfoVo> resultList = new ArrayList<>();
        for (UserInfoVo userInfo : userInfoVoList) {
            /*封装请求信息*/
            String param = "alias=" + userInfo.getAlias();
            resultList.add(gson.fromJson(HttpUtil.sendPostUrl(QQEmailConstants.getUserInfoURL, param, QQEmailConstants
                            .charset,
                    headerMap), UserInfoVo.class).setAction(userInfo.getAction()));
        }
        return resultList;
    }

    /**
     * 将本地成员信息同步到腾讯企业邮箱
     *
     * @param syncUser 实体
     * @param token    access_token
     * @return String  同步成员返回码
     * @
     */
    public static String syncUser(SyncUser syncUser, String token) throws EmailException {
       /*请求头部信息封装token*/
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + token);
        /*解析实体拼接参数*/
        return HttpUtil.sendPostUrl(QQEmailConstants.getSyncUserURL, analysisModelToURL(syncUser), QQEmailConstants.charset,
                headerMap);
    }

    /**
     * 将本地部门同步到腾讯企业邮箱
     *
     * @param syncUnit 实体
     * @param token    access_token
     * @return Party 部门列表
     * @
     */
    public static String syncUnit(SyncUnit syncUnit, String token) throws EmailException {
        Gson gson = new Gson();
          /*请求头部信息封装token*/
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + token);
        return HttpUtil.sendPostUrl(QQEmailConstants.getSyncUnitURL, analysisModelToURL(syncUnit), QQEmailConstants.charset, headerMap);
    }

    public static void main(String arges[]) throws EmailException {
        getToken("mayidongli","a0a0efd54ed07c16842eac1c274d64ac");
        SyncUnit syncUnit=new SyncUnit();
        syncUnit.setAction("1");
        syncUnit.setDstpath("/鞍山别克4S店/sss");
        syncUnit(syncUnit, getToken("mayidongli", "a0a0efd54ed07c16842eac1c274d64ac").get("access_token").toString());
    }
}
