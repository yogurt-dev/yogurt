package com.github.jyoghurt.weChat.controller;


import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import com.github.jyoghurt.security.annotations.IgnoreAuthentication;
import com.github.jyoghurt.wechatbasic.common.pojo.*;
import com.github.jyoghurt.wechatbasic.common.util.AdvancedUtil;
import com.github.jyoghurt.wechatbasic.common.util.AppletUtil;
import com.github.jyoghurt.wechatbasic.common.util.SignUtil;
import com.github.jyoghurt.wechatbasic.common.util.WeixinUtil;
import com.github.jyoghurt.wechatbasic.enums.WeChatMenusTypeEnum;
import com.github.jyoghurt.wechatbasic.service.WechatNoticeCoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Lvyu on 2015/9/23.
 */
@RestController
@RequestMapping("/weChat")
public class WeChatController extends BaseController {
    @Autowired
    private WechatNoticeCoreService wechatNoticeCoreService;

    @LogContent("微信接口")
    @IgnoreAuthentication
    @RequestMapping(value = "/index")
    public void index() throws IOException {
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");
        if ("GET".equals(request.getMethod())) {
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (SignUtil.checkSignature(signature, timestamp, nonce)) {
                PrintWriter writer = response.getWriter();
                writer.write(echostr);
            }
        } else {
            // 调用核心业务类接收消息、处理消息
            String respMessage = wechatNoticeCoreService.processRequest(request);
            PrintWriter writer = response.getWriter();
            writer.write(respMessage);
        }
    }

    /**
     * nRHhvrmciPzCO3dQ-fodDk4AKxfY33-aoSls57udve4
     * 列出配货单
     */
    @LogContent("自定义菜单")
    @RequestMapping(value = "/createMenu/{mediaId}", method = RequestMethod.GET)
    public HttpResultEntity<?> createMenu(@PathVariable String mediaId) {
        String appletAppId = "wx6d3b59eec2a43a48";
        // TODO: 2017/12/13  按照自定义菜单配的路径修改
        String pagePath = "pages/index/index";
        // TODO: 2017/12/13  按照自定义菜单配的路径修改
        String url = "http://www.xiaoye.mobi";
        List<Button> levelOneMenu = new ArrayList<>();
        //我的校园
        CommonButton mySchoolButton = new CommonButton();
        mySchoolButton.setName("进入小野");
        mySchoolButton.setAppid(appletAppId);
        mySchoolButton.setPagepath(pagePath);
        mySchoolButton.setUrl(url);
        mySchoolButton.setType(WeChatMenusTypeEnum.miniprogram);
        levelOneMenu.add(mySchoolButton);
        //活动季
        CommonButton activitiBtn = new CommonButton();
        activitiBtn.setName("活动季");
        activitiBtn.setMedia_id(mediaId);
        activitiBtn.setType(WeChatMenusTypeEnum.media_id);
        levelOneMenu.add(activitiBtn);
        Menu menu = new Menu();
        menu.setButton(levelOneMenu);
        WeixinUtil.createMenu(menu, WeixinUtil.getAccessToken().getToken());
        return getSuccessResult();
    }

    /**
     * 生成带编号场景的二维码
     */
    @LogContent("生成带编号场景的二维码")
    @RequestMapping(value = "/getQRCode/{code}", method = RequestMethod.GET)
    public HttpResultEntity<?> getQRCode(@PathVariable Integer code) {
        String codeUrl = AdvancedUtil.getQRCode(AdvancedUtil.createPermanentQRCode(WeixinUtil.getAccessToken().getToken(), code), "D:/upload/qrCode");
        return getSuccessResult(codeUrl);
    }

    @LogContent("获取素材列表")
    @RequestMapping(value = "/getMaterialList", method = RequestMethod.GET)
    public HttpResultEntity<?> getMaterialList() {
        BathgetMaterialParam bathgetMaterialParam = new BathgetMaterialParam();
        bathgetMaterialParam.setCount(100);
        bathgetMaterialParam.setOffset(0);
        //获取媒体下载路径;
        MaterialNewsMapList materialNewsMapList = AdvancedUtil.batchgetMaterialNews(WeixinUtil.getAccessToken().getToken(),
                bathgetMaterialParam);
        return getSuccessResult(materialNewsMapList);
    }

    @LogContent("获取用户信息")
    @RequestMapping(value = "/getUserInfo/{code}", method = RequestMethod.GET)
    public HttpResultEntity<?> getUserInfo(@PathVariable String code) {
        String appId = SpringContextUtils.getProperty("wechatAppId");
        String appsecret = SpringContextUtils.getProperty("wechatAppSecret");
        WeixinOauth2Token weixinOauth2Token = AdvancedUtil.getOauth2OpenId(appId, appsecret, code);
        WeixinUserInfo weixinUserInfo = AdvancedUtil.getUserInfo(weixinOauth2Token.getAccessToken(), weixinOauth2Token.getOpenId());
        return getSuccessResult(weixinUserInfo);
    }

    @LogContent("获取用户信息")
    @RequestMapping(value = "/getSNSUserInfo/{code}", method = RequestMethod.GET)
    public HttpResultEntity<?> getSNSUserInfo(@PathVariable String code) {
        String appId = SpringContextUtils.getProperty("wechatAppId");
        String appsecret = SpringContextUtils.getProperty("wechatAppSecret");
        WeixinOauth2Token weixinOauth2Token = AdvancedUtil.getOauth2OpenId(appId, appsecret, code);
        SNSUserInfo userInfo = AdvancedUtil.getSNSUserInfo(weixinOauth2Token.getAccessToken(),
                weixinOauth2Token.getOpenId());
        return getSuccessResult(userInfo);
    }

    @LogContent("清空token")
    @RequestMapping(value = "/cleanToken", method = RequestMethod.GET)
    public HttpResultEntity<?> cleanToken() {
        WeixinUtil.cleanToken();
        return getSuccessResult();
    }

    @LogContent("createPermanentQRCode")
    @RequestMapping(value = "/createPermanentQRCode", method = RequestMethod.POST)
    public HttpResultEntity<?> createPermanentQRCode(@RequestBody CreatePermanentQRCodeParam createPermanentQRCodeParam) throws UnsupportedEncodingException {
        return getSuccessResult(AppletUtil.createPermanentQRCode(createPermanentQRCodeParam));
    }

    @LogContent("getJsapiTicket")
    @RequestMapping(value = "/getJsapiTicket", method = RequestMethod.GET)
    public HttpResultEntity<?> getJsapiTicket() {
        String ticket = AdvancedUtil.getJsapiTicket(WeixinUtil.getAccessToken().getToken());
        return super.getSuccessResult();
    }

    public static void main(String[] args) {

        //1、获取AccessToken
        String accessToken = "4_lS5sL9PWdkzq_WXZd0TbxkaDzJ2qFKV0ut-5lobR6t7lzJWF7xsI7G0aQhR6g3zzhRnKfE7T-RseGrG0Ff9A3159dPy3lGzw0l9ghNgkFANSQl_JyQvZWoftiBzINivvA4a4AG_1RbU3s9SHYEJcACAFLQ";

        //2、获取Ticket
        String jsapi_ticket = AdvancedUtil.getJsapiTicket("4_lS5sL9PWdkzq_WXZd0TbxkaDzJ2qFKV0ut-5lobR6t7lzJWF7xsI7G0aQhR6g3zzhRnKfE7T-RseGrG0Ff9A3159dPy3lGzw0l9ghNgkFANSQl_JyQvZWoftiBzINivvA4a4AG_1RbU3s9SHYEJcACAFLQ");

        //3、时间戳和随机字符串
        String noncestr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);//随机字符串
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);//时间戳

        System.out.println("accessToken:" + accessToken + "\njsapi_ticket:" + jsapi_ticket + "\n时间戳：" + timestamp + "\n随机字符串：" + noncestr);

        //4、获取url
        String url = "http://www.luiyang.com/add.html";
            /*根据JSSDK上面的规则进行计算，这里比较简单，我就手动写啦
            String[] ArrTmp = {"jsapi_ticket","timestamp","nonce","url"};
            Arrays.sort(ArrTmp);
            StringBuffer sf = new StringBuffer();
            for(int i=0;i<ArrTmp.length;i++){
                sf.append(ArrTmp[i]);
            }
            */

        //5、将参数排序并拼接字符串
        String str = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + noncestr + "&timestamp=" + timestamp + "&url=" + url;

        //6、将字符串进行sha1加密
        String signature = AdvancedUtil.SHA1(str);
        System.out.println("参数：" + str + "\n签名：" + signature);

    }

}