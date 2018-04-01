package com.github.jyoghurt.weChat.controller;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.weChat.domain.WeChatWebMouldT;
import com.github.jyoghurt.weChat.domain.WeChatWebsiteMenuT;
import com.github.jyoghurt.weChat.domain.WeChatWebsiteT;
import com.github.jyoghurt.weChat.service.WeChatWebMouldTService;
import com.github.jyoghurt.weChat.service.WeChatWebsiteMenuTService;
import com.github.jyoghurt.weChat.service.WeChatWebsiteTService;
import com.github.jyoghurt.wechatbasic.common.util.FreeMarkerUtil;
import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.OperatorHandle;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import pub.utils.SessionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebsiteT控制器
 */
@RestController
@RequestMapping("/weChatWebsiteT")
public class WeChatWebsiteTController extends BaseController {


    /**
     * WebsiteT服务类
     */
    @Resource
    private WeChatWebsiteTService weChatWebsiteTService;
    @Resource
    private WeChatWebsiteMenuTService weChatWebsiteMenuTService;
    @Resource
    private WeChatWebMouldTService weChatWebMouldTService;
    @Autowired
    private SecurityUnitTService securityUnitTService;
    @Value("${downloadPath}")
    private String downloadPath;

    /**
     * 列出WebsiteT
     */
    @LogContent("查询WebsiteT")
    @RequestMapping(value = "/list/{accountType}", method = RequestMethod.GET)
    public HttpResultEntity<?> list(WeChatWebsiteT weChatWebsiteT, QueryHandle queryHandle, @PathVariable String accountType)
              {
        SecurityUserT usert = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(usert.getBelongCompany().getUnitId());
        // 第三方用户唯一凭证
        String appId = "";
        // 第三方用户唯一凭证密钥
        String appSecret = "";

        if ((WeChatAccountType.TYPE_SUBSCRIPTION).toString().equals(accountType)) {  //订阅号
            appId = unitT.getAppId();
            appSecret = unitT.getSecretKey();
        } else if ((WeChatAccountType.TYPE_SERVICE).toString().equals(accountType)) {  //服务号
            appId = unitT.getAppIdF();
            appSecret = unitT.getSecretKeyF();
        } else if ((WeChatAccountType.TYPE_ENTERPRISE).toString().equals(accountType)) {  //企业号
            appId = unitT.getAppIdQ();
            appSecret = unitT.getSecretKeyQ();
        }
        weChatWebsiteT.setAppId(appId);
        return getSuccessResult(weChatWebsiteTService.getData(weChatWebsiteT, queryHandle.configPage().addOrderBy("createDateTime",
                "desc").addOperatorHandle("webName", OperatorHandle.operatorType.LIKE)));

    }
//
//
//	/**
//	 * 添加WebsiteT
//	 */
//	@LogContent("添加WebsiteT")
//	@RequestMapping(method=RequestMethod.POST)
//	public HttpResultEntity<?> add(@RequestBody WeChatWebsiteT weChatWebsiteT)   {
//		weChatWebsiteTService.save(weChatWebsiteT);
//        return getSuccessResult();
//	}
//

    /**
     * 编辑WebsiteT
     */
    @LogContent("编辑WebsiteT")
    @RequestMapping(method = RequestMethod.PUT)
    public HttpResultEntity<?> edit(@RequestBody WeChatWebsiteT weChatWebsiteT)   {
        weChatWebsiteTService.updateForSelective(weChatWebsiteT);
        return getSuccessResult();
    }

    /**
     * 删除单个WebsiteT-
     */
    @LogContent("删除WebsiteT")
    @RequestMapping(value = "/{webId}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> delete(@PathVariable String webId)   {
        weChatWebsiteTService.delete(webId);
        weChatWebsiteMenuTService.deleteByWebId(webId);
        return getSuccessResult();
    }

//    /**
//     * 查询单个WebsiteT
//	 */
//	 @LogContent("查询单个WebsiteT")
//	 @RequestMapping(value = "/{webId}",method=RequestMethod.GET)
//	 public HttpResultEntity<?> get(@PathVariable String webId)   {
//		 return getSuccessResult(weChatWebsiteTService.find(webId));
//	 }

    /**
     * 查询单个WebsiteT
     */
    @LogContent("查询单个WebsiteT")
    @RequestMapping(value = "/getMainWeb/{webId}", method = RequestMethod.GET)
    public HttpResultEntity<?> get(@PathVariable String webId)   {
        return getSuccessResult(weChatWebsiteTService.findByEebId(webId));
    }

    /**
     * 新增网站
     */
        /*
* 保存微信消息接口
* */
    @LogContent("添加网站")
    @RequestMapping(value = "/addWeb/{accountType}", method = RequestMethod.POST)
    public HttpResultEntity<?> addWeb(@RequestBody WeChatWebsiteT weChatWebsiteT, @PathVariable String accountType)   {
        SecurityUserT usert = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(usert.getBelongCompany().getUnitId());
        // 第三方用户唯一凭证
        String appId = "";
        // 第三方用户唯一凭证密钥
        String appSecret = "";
//todo
        if ((WeChatAccountType.TYPE_SUBSCRIPTION).toString().equals(accountType)) {  //订阅号
            appId = unitT.getAppId();
            appSecret = unitT.getSecretKey();
        } else if ((WeChatAccountType.TYPE_SERVICE).toString().equals(accountType)) {  //服务号
            appId = unitT.getAppIdF();
            appSecret = unitT.getSecretKeyF();
        } else if ((WeChatAccountType.TYPE_ENTERPRISE).toString().equals(accountType)) {  //企业号
            appId = unitT.getAppIdQ();
            appSecret = unitT.getSecretKeyQ();
        }
        weChatWebsiteT.setAppId(appId);
       weChatWebsiteTService.addWeb(weChatWebsiteT);

        return getSuccessResult(weChatWebsiteT.getWebId());
    }

    /**
     * 生成HTml
     */
    @LogContent("生成微官网HTML")
    @RequestMapping(value = "/creatHtml/{webId}", method = RequestMethod.POST)
    public HttpResultEntity<?> creatHtml(@PathVariable String webId)   {
        WeChatWebsiteT weChatWebsiteT = weChatWebsiteTService.findByEebId(webId);
        String creatPath = "temp/" + weChatWebsiteT.getAppId() + "/" + weChatWebsiteT.getWebId();//appid
        if (weChatWebsiteT.getState().equals("1")) {
            copyFolder(weChatWebsiteT, creatPath, "1");
        } else {
            copyFolder(weChatWebsiteT, creatPath, "0");
        }
        return getSuccessResult(creatPath + "/main.html");
    }

    public static void tohtml(String RootDir, String FileName, Map<String, List<WeChatWebsiteMenuT>> rootContent, String mobanPath, String
            mobanName, String creatPath) {
        FreeMarkerUtil freeMarkerUtil = FreeMarkerUtil.getInstance();
        boolean bOKNewsList = freeMarkerUtil.geneHtmlFile(RootDir, mobanPath, mobanName, rootContent, creatPath, FileName);

    }

    public static void tohtml2(String RootDir, String FileName, Map<String, String> rootContent, String
            mobanPath, String
                                       mobanName, String creatPath) {
        FreeMarkerUtil freeMarkerUtil = FreeMarkerUtil.getInstance();
        boolean bOKNewsList = freeMarkerUtil.geneHtmlFile(RootDir, mobanPath, mobanName, rootContent, creatPath, FileName);


    }

    public static boolean toMainHtml(String RootDir, String FileName, Map<String, WeChatWebsiteT> rootContent, String
            mobanPath, String
                                             mobanName, String creatPath) {
        FreeMarkerUtil freeMarkerUtil = FreeMarkerUtil.getInstance();
        boolean bOKNewsList = freeMarkerUtil.geneHtmlFile(RootDir, mobanPath, mobanName, rootContent, creatPath, FileName);
        if (bOKNewsList) {

            return true;

        } else {

            return false;
        }

    }

    /*
    * 生成html
    * */
    @LogContent("发布微官网HTML")
    @RequestMapping(value = "/publishHtml/{webId}", method = RequestMethod.POST)
    public HttpResultEntity<?> publishHtml(@PathVariable String webId)   {
        WeChatWebsiteT weChatWebsiteT = weChatWebsiteTService.findByEebId(webId);
        String creatPath = "temp/" + weChatWebsiteT.getAppId() + "/" + weChatWebsiteT.getWebId();//appid
        copyFolder(weChatWebsiteT, creatPath, "1");
        return getSuccessResult(creatPath + "/main.html");
    }

    /**
     * 生成html公共方法
     */
    public void copyFolder(WeChatWebsiteT weChatWebsiteT, String creatPath, String state)   {

        //获得模板数据
        WeChatWebMouldT weChatWebMouldT = weChatWebMouldTService.find(weChatWebsiteT.getMouldId());


        String FileName = "main.html";
        String RootDir = session.getServletContext().getRealPath("/");
        String mobanPath = weChatWebMouldT.getMouldUrl();//数据库中获取
        String mobanName = "/" + weChatWebMouldT.getMainMoban();//数据库中获取


        List<WeChatWebsiteMenuT> list = weChatWebsiteT.getList();
//TODO
        for (int i = 0; i < list.size(); i++) {
            //一级菜单
            if (!list.get(i).getParentId().equals("0")) {
                continue;
            }
                if (list.get(i).getClickType() == null) {
                    list.get(i).setUrl("#");
                    weChatWebsiteMenuTService.update(list.get(i));

                } else {
                    if (list.get(i).getClickType().equals("3")) {

                        //生成2级页
                        String FileName1 = "onetext" + i + ".html";
                        String mobanPath1 = weChatWebMouldT.getMouldUrl();//数据库中获取
                        String mobanName1 = "/" + weChatWebMouldT.getTextMoban();//数据库中获取
                        Map<String, String> rootContent1 = new HashMap<String, String>();
                        rootContent1.put("text", list.get(i).getText());

                        tohtml2(RootDir, FileName1, rootContent1, mobanPath1, mobanName1, creatPath);
                        list.get(i).setUrl(FileName1);
                        weChatWebsiteMenuTService.update(list.get(i));
                    } else if (list.get(i).getClickType().equals("1")) {

                        List<WeChatWebsiteMenuT> list2 = weChatWebsiteMenuTService.findByParentId(list.get(i).getMenuId());


                        //生成2级点击后的页面
                        for (int j = 0; j < list2.size(); j++) {
                            if (list2.get(j).getClickType() != null&&!list2.get(j).getClickType().equals("2")) {
                                String FileName1 = "twotext" + i + ".html";
                                String mobanPath1 = weChatWebMouldT.getMouldUrl();//数据库中获取
                                String mobanName1 = "/" + weChatWebMouldT.getTextMoban();//数据库中获取
                                Map<String, String> rootContent1 = new HashMap<String, String>();
                                rootContent1.put("text", list2.get(j).getText());

                                tohtml2(RootDir, FileName1, rootContent1, mobanPath1, mobanName1, creatPath);
                                list2.get(j).setUrl(FileName1);
                                weChatWebsiteMenuTService.update(list2.get(j));
                            }

                        }
                        //生成2级页
                        String FileName1 = "second" + i + ".html";
                        String mobanPath1 = weChatWebMouldT.getMouldUrl();//数据库中获取
                        String mobanName1 = "/" + weChatWebMouldT.getSecondMoban();//数据库中获取
                        String creatPath1 = "";//appid
                        Map<String, List<WeChatWebsiteMenuT>> rootContent1 = new HashMap<String, List<WeChatWebsiteMenuT>>();
                        List<WeChatWebsiteMenuT> list3 = weChatWebsiteMenuTService.findByParentId(list.get(i).getMenuId());
                        for(WeChatWebsiteMenuT weChatWebsiteMenuT : list3){
                            weChatWebsiteMenuT.setMenuImg(changeImgPath(weChatWebsiteMenuT.getMenuImg()));
                        }
                        rootContent1.put("list", list3);
                        tohtml(RootDir, FileName1, rootContent1, mobanPath1, mobanName1, creatPath);
                        list.get(i).setUrl(FileName1);
                        weChatWebsiteMenuTService.update(list.get(i));


                    }


            }
        }
        //生成主页
        Map<String, WeChatWebsiteT> rootContent = new HashMap<String, WeChatWebsiteT>();
        WeChatWebsiteT weChatWebsiteT1 = weChatWebsiteTService.findByEebId(weChatWebsiteT.getWebId());
        for (WeChatWebsiteMenuT menuT : weChatWebsiteT1.getList()) {
            menuT.setMenuImg(changeImgPath(menuT.getMenuImg()));
        }
        weChatWebsiteT1.setWebImg(changeImgPath(weChatWebsiteT1.getWebImg()));
        rootContent.put("data", weChatWebsiteT1);
        //生成首页
        Boolean bo = toMainHtml(RootDir, FileName, rootContent, mobanPath, mobanName, creatPath);
        //TODO
        if (bo) {
            weChatWebsiteT1.setState(state);
            if(weChatWebsiteT1.getWebImg().startsWith("../../../")){
                weChatWebsiteT1.setWebImg(weChatWebsiteT1.getWebImg().replace("../../../",""));
            }
            weChatWebsiteTService.update(weChatWebsiteT1);
        }
    }

    private String changeImgPath(String imgPath){
        if (StringUtils.isNotEmpty(imgPath) && !imgPath.startsWith(downloadPath)&&!imgPath.startsWith("../")) {
            return "../../../" + imgPath;
        }
        return imgPath;
    }
}
