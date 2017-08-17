package com.github.jyoghurt.security.securityUserT.handler;

import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import pub.utils.HttpRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Project: 微信登录
 * @Package: com.df.security.securityUserT.handler
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2017-02-13 12:36
 */
public class WechatHandler {


    public static final String ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
    public static final String APP_ID = "wx175d8f548f9da669";
    public static final String SECRET = "a1362bd9683813b380dd1f132c0ed252";


    /**
     * 获取微信access_token
     *
     * @param code
     * @param state
     * @return
     */
    public static JSONObject fetchAccesstoken(String code, String state) {
        String result = HttpRequestUtils.sendGet(ACCESS_TOKEN, StringUtils.join("appid=",APP_ID,"&secret=",SECRET,
                "&code=",code,"&grant_type=authorization_code"));
        JSONObject responseFromWechat = JSONObject.fromObject(result);
        return responseFromWechat;
    }

    /**
     * 交互成功后，页面跳转
     */
    public static void gotoPage(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String gotoPage = request.getContextPath() + url;
        response.getWriter().print(StringUtils.join("<script language=javascript>window.location.href = '",gotoPage,"'</script>"));
        response.flushBuffer();
    }

    /**
     * 交互成功后，页面跳转，并提示
     *
     * @param request
     * @param response
     * @param url
     * @param message
     */
    public static void gotoPageAndAlert(HttpServletRequest request, HttpServletResponse response, String url, String
            message) throws IOException {
        String gotoPage = request.getContextPath() + url;
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print(StringUtils.join("<script language=javascript>alert('",message,"');window.location.href ='",gotoPage, "'</script>"));
        response.flushBuffer();
    }

}
