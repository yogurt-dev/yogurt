package com.github.jyoghurt.qqEmail.common.util;

/**
 * Created by zhangjl on 2015/10/27.
 */
public class QQEmailConstants {
    /*charset*/
    public  static String charset="utf-8";
    /*获取token的URL*/
    public static String getTokenURL = "https://exmail.qq.com/cgi-bin/token";
    /*根据版本同步用户更新列表的URL*/
    public static String getUserListURL = "http://openapi.exmail.qq.com:12211/openapi/user/list";
    /*根据用户账户名得到用户具体信息*/
    public static String getUserInfoURL="http://openapi.exmail.qq.com:12211/openapi/user/get";
    /*同步成员帐号资料*/
    public static String getSyncUserURL="http://openapi.exmail.qq.com:12211/openapi/user/sync";
    /*同步部门*/
    public static String getSyncUnitURL="http://openapi.exmail.qq.com:12211/openapi/party/sync";
}
