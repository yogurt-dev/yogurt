package com.github.jyoghurt.weChat.controller;


import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.security.annotations.IgnoreAuthentication;
import com.github.jyoghurt.wechatbasic.common.pojo.BathgetMaterialParam;
import com.github.jyoghurt.wechatbasic.common.pojo.Button;
import com.github.jyoghurt.wechatbasic.common.pojo.CommonButton;
import com.github.jyoghurt.wechatbasic.common.pojo.Menu;
import com.github.jyoghurt.wechatbasic.common.util.AdvancedUtil;
import com.github.jyoghurt.wechatbasic.common.util.SignUtil;
import com.github.jyoghurt.wechatbasic.common.util.WeixinUtil;
import com.github.jyoghurt.wechatbasic.enums.WeChatMenusTypeEnum;
import com.github.jyoghurt.wechatbasic.service.WechatNoticeCoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
     * 列出配货单
     */
    @LogContent("自定义菜单")
    @RequestMapping(value = "/createMenu", method = RequestMethod.GET)
    public HttpResultEntity<?> createMenu() {
        BathgetMaterialParam bathgetMaterialParam = new BathgetMaterialParam();
        bathgetMaterialParam.setCount(100);
        bathgetMaterialParam.setOffset(0);
        String appletAppId = "wx5b9be6b77c4b201c";
        String pagePath = "pages/school/index";
        String url = "http://appleterror.lvyushequ.com/";
        List<Button> levelOneMenu = new ArrayList<>();
        //校园生活
        CommonButton schoolButton = new CommonButton();
        schoolButton.setName("校园生活");
        List<CommonButton> subSchoolBtns = new ArrayList<>();
        //sub1
        CommonButton schoolSub1Button = new CommonButton();
        schoolSub1Button.setName("快递代取");
        schoolSub1Button.setAppid(appletAppId);
        schoolSub1Button.setPagepath(pagePath);
        schoolSub1Button.setUrl(url);
        schoolSub1Button.setType(WeChatMenusTypeEnum.miniprogram);
        //sub2
        CommonButton schoolSub2Button = new CommonButton();
        schoolSub2Button.setName("校园超市");
        schoolSub2Button.setAppid(appletAppId);
        schoolSub2Button.setPagepath(pagePath);
        schoolSub2Button.setUrl(url);
        schoolSub2Button.setType(WeChatMenusTypeEnum.miniprogram);
        //sub3
        CommonButton schoolSub3Button = new CommonButton();
        schoolSub3Button.setName("耐克定制");
        schoolSub3Button.setMedia_id("oBXUQxDVKs4CxNHk-CPlRUzGuyC8pH70aDDqpFXvO3U");
        schoolSub3Button.setType(WeChatMenusTypeEnum.media_id);
        subSchoolBtns.add(schoolSub1Button);
        subSchoolBtns.add(schoolSub2Button);
        subSchoolBtns.add(schoolSub3Button);
        schoolButton.setSub_button(subSchoolBtns);
        levelOneMenu.add(schoolButton);
        //我的校园
        CommonButton mySchoolButton = new CommonButton();
        mySchoolButton.setName("我的校园");
        mySchoolButton.setAppid(appletAppId);
        mySchoolButton.setPagepath(pagePath);
        mySchoolButton.setUrl(url);
        mySchoolButton.setType(WeChatMenusTypeEnum.miniprogram);
        levelOneMenu.add(mySchoolButton);
        //驴鱼圈子
        CommonButton lyButton = new CommonButton();
        lyButton.setName("驴鱼圈子");
        List<CommonButton> lyBtns = new ArrayList<>();
        //sub1
        CommonButton lySub1Button = new CommonButton();
        lySub1Button.setName("联系我们");
        lySub1Button.setKey("callUs");
        lySub1Button.setType(WeChatMenusTypeEnum.click);
        //sub2
        CommonButton lySub2Button = new CommonButton();
        lySub2Button.setName("招募代理人");
        lySub2Button.setKey("findProxy");
        lySub2Button.setType(WeChatMenusTypeEnum.click);
        //sub3
        CommonButton lySub3Button = new CommonButton();
        lySub3Button.setName("移动端后台");
        lySub3Button.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxc98d1fe9ec478c8a&redirect_uri=http%3A%2F%2F836341fadc484cd889b753f81612d0b4.lvyushequ.com%2Fm_login.html&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
        lySub3Button.setType(WeChatMenusTypeEnum.view);
        lyBtns.add(lySub1Button);
        lyBtns.add(lySub2Button);
        lyBtns.add(lySub3Button);
        lyButton.setSub_button(lyBtns);
        levelOneMenu.add(lyButton);
        Menu menu = new Menu();
        menu.setButton(levelOneMenu);
        WeixinUtil.createMenu(menu, WeixinUtil.getAccessToken().getToken());
        return getSuccessResult();
    }

    /**
     * 列出配货单
     */
    @LogContent("自定义菜单")
    @RequestMapping(value = "/getQRCode/{code}", method = RequestMethod.GET)
    public HttpResultEntity<?> getQRCode(@PathVariable Integer code) {
        String codeUrl = AdvancedUtil.getQRCode(AdvancedUtil.createPermanentQRCode(WeixinUtil.getAccessToken().getToken(), code), "D:/upload/qrCode");
        return getSuccessResult(codeUrl);
    }
}
