package com.github.jyoghurt.weChat.controller;


import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.security.annotations.IgnoreAuthentication;
import com.github.jyoghurt.wechatbasic.common.pojo.*;
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
    @RequestMapping(value = "/createMenu/{mediaId}", method = RequestMethod.GET)
    public HttpResultEntity<?> createMenu(@PathVariable String mediaId) {
        String appletAppId = "wx6d3b59eec2a43a48";
        // TODO: 2017/12/13  按照自定义菜单配的路径修改
        String pagePath = "pages/school/index";
        // TODO: 2017/12/13  按照自定义菜单配的路径修改
        String url = "http://appleterror.lvyushequ.com/";
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

}
