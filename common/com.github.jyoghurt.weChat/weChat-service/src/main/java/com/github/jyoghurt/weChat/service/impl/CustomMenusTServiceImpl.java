package com.github.jyoghurt.weChat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.weChat.dao.CustomMenusTMapper;
import com.github.jyoghurt.weChat.domain.WeChatCustomMenusT;
import com.github.jyoghurt.weChat.domain.WeChatCustomMenusTVo;
import com.github.jyoghurt.weChat.service.CustomMenusTService;
import com.github.jyoghurt.wechatbasic.common.pojo.*;
import com.github.jyoghurt.wechatbasic.common.util.WeixinUtil;
import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
import com.github.jyoghurt.wechatbasic.enums.WeChatMenusTypeEnum;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("customMenusTService")
public class CustomMenusTServiceImpl extends ServiceSupport<WeChatCustomMenusT, CustomMenusTMapper> implements CustomMenusTService {
    private int key = 0;
    @Autowired
    private CustomMenusTMapper customMenusTMapper;

    @Override
    public CustomMenusTMapper getMapper() {
        return customMenusTMapper;
    }

    @Override
    public void delete(Serializable id) {
        getMapper().delete(WeChatCustomMenusT.class, id);
    }

    @Override
    public WeChatCustomMenusT find(Serializable id) {
        return getMapper().selectById(WeChatCustomMenusT.class, id);
    }

    @Override
    public List<WeChatCustomMenusTVo> getMenus(String appId) {
        /*先查出所有根节点*/
        List<WeChatCustomMenusTVo> weChatCustomMenusTVoList = customMenusTMapper.findByParentIdAsc("-1", appId);
        for (WeChatCustomMenusTVo weChatCustomMenusTVo : weChatCustomMenusTVoList) {
            weChatCustomMenusTVo.setCustomMenusTList(customMenusTMapper.findByParentIdDesc(weChatCustomMenusTVo.getId().toString(),
                    appId));
        }
        return weChatCustomMenusTVoList;
    }

    @Override
    public List<WeChatCustomMenusTVo> saveCustomMenusTList(WeChatCustomMenusTVo customMenusList, SecurityUserT securityUserT,
                                                           String appId) {
        List<WeChatCustomMenusTVo> saveList = customMenusList.getCustomMenusTList();
        changeToEntityList(saveList, securityUserT);
        /*先查出所有根节点*/
        List<WeChatCustomMenusTVo> weChatCustomMenusTVoList = new ArrayList<WeChatCustomMenusTVo>();
        weChatCustomMenusTVoList = this.getMenus(appId);

        return weChatCustomMenusTVoList;
    }

    @Override
    public List<WeChatCustomMenusTVo> deletCustomMenusTList(WeChatCustomMenusTVo customMenusList, String appId) {
        List<WeChatCustomMenusTVo> deletList = customMenusList.getCustomMenusTList();
        for (WeChatCustomMenusTVo deletVo : deletList) {
            getMapper().delete(WeChatCustomMenusT.class, deletVo.getId());
            if (deletVo.getCustomMenusTList() != null) {
                for (WeChatCustomMenusTVo childMenus : deletVo.getCustomMenusTList()) {
                    getMapper().delete(WeChatCustomMenusT.class, childMenus.getId());
                }
            }
        }
        List<WeChatCustomMenusTVo> weChatCustomMenusTVoList = new ArrayList<WeChatCustomMenusTVo>();
        weChatCustomMenusTVoList = this.getMenus(appId);
        return weChatCustomMenusTVoList;
    }

    @Override
    public JSONObject saveAndSendList(WeChatCustomMenusTVo customMenusList, SecurityUserT securityUserT,
                                      String appId, String appsecret) {
        JSONObject returnObj = new JSONObject();
        List<WeChatCustomMenusTVo> returnList = this.saveCustomMenusTList(customMenusList, securityUserT, appId);
        //获取taken
        AccessToken token = WeixinUtil.getAccessToken(appId, appsecret);
        returnObj.put("data", returnList);
        returnObj.put("chatMsg", WeixinUtil.createMenu(createMenuToSend(customMenusList), token.getToken()));
        return returnObj;
    }

    @Override
    public JSONObject getApp(SecurityUnitT securityUnitT, String appType) {
        JSONObject returnObj = new JSONObject();
        String appId = "";
        String appSecret = "";
        if ((WeChatAccountType.TYPE_SUBSCRIPTION).toString().equals(appType)) {  //订阅号
            appId = securityUnitT.getAppId();
            appSecret = securityUnitT.getSecretKey();
        } else if ((WeChatAccountType.TYPE_SERVICE).toString().equals(appType)) {  //服务号
            appId = securityUnitT.getAppIdF();
            appSecret = securityUnitT.getSecretKeyF();
        } else if ((WeChatAccountType.TYPE_ENTERPRISE).toString().equals(appType)) {  //企业号
            appId = securityUnitT.getAppIdQ();
            appSecret = securityUnitT.getSecretKeyQ();
        }
        returnObj.put("appId", appId);
        returnObj.put("appsecret", appSecret);
        return returnObj;
    }

    //组织menu发送数据
    public Menu createMenuToSend(WeChatCustomMenusTVo customMenusList) {
        Menu menu = new Menu();
        List<Button> buttonList = new ArrayList<Button>();
        List<WeChatCustomMenusTVo> List = customMenusList.getCustomMenusTList();
        //遍历第一层List
        for (WeChatCustomMenusTVo weChatCustomMenusTVo : List) {
            if (WeChatMenusTypeEnum.view.equals(weChatCustomMenusTVo.getClickType()) || WeChatMenusTypeEnum.media_id.equals
                    (weChatCustomMenusTVo.getClickType())) {
                CommonButton bottomBtn = getCommonButton(weChatCustomMenusTVo);
                buttonList.add(bottomBtn);
            }
            if (WeChatMenusTypeEnum.click.equals(weChatCustomMenusTVo.getClickType())) {
                ComplexButton bottomBtn = new ComplexButton();
                bottomBtn.setName(weChatCustomMenusTVo.getMenuName());
                List<CommonButton> subButtonList = new ArrayList<CommonButton>();
                //反向遍历二级菜单
                for (WeChatCustomMenusTVo subVo : weChatCustomMenusTVo.getCustomMenusTList()) {
                    CommonButton subBtn = getCommonButton(subVo);
                    subButtonList.add(subBtn);
                }
                bottomBtn.setSub_button((CommonButton[]) subButtonList.toArray(new CommonButton[subButtonList.size()]));
                buttonList.add(bottomBtn);
            }
        }
        menu.setButton((Button[]) buttonList.toArray(new Button[buttonList.size()]));
        return menu;
    }

    //递归CustomMenusTVo集合封装Button
    public CommonButton getCommonButton(WeChatCustomMenusTVo weChatCustomMenusTVo) {
        CommonButton bottomBtn = new CommonButton();
        switch (weChatCustomMenusTVo.getClickType()) {
            case WeChatMenusTypeEnum.media_id: {
                bottomBtn.setMedia_id(weChatCustomMenusTVo.getContent());
                break;
            }
            case WeChatMenusTypeEnum.view: {
                bottomBtn.setUrl(weChatCustomMenusTVo.getContent());
                break;
            }
            case WeChatMenusTypeEnum.click: {
                bottomBtn.setUrl("");
                break;
            }
        }
        bottomBtn.setName(weChatCustomMenusTVo.getMenuName());
        bottomBtn.setType(weChatCustomMenusTVo.getClickType());
        bottomBtn.setKey(String.valueOf(key));
        key++;
        return bottomBtn;
    }

    /**/
    //将VoList数据转成实体list
    public List<WeChatCustomMenusT> changeToEntityList(List<WeChatCustomMenusTVo> voList, SecurityUserT securityUserT) {
        List<WeChatCustomMenusT> weChatCustomMenusTList = new ArrayList<WeChatCustomMenusT>();
        for (WeChatCustomMenusTVo customMenusVo : voList) {
            WeChatCustomMenusT weChatCustomMenusT = changeToEntity(customMenusVo);
            weChatCustomMenusTList.add(weChatCustomMenusT);
            if (weChatCustomMenusT.getId() == null) {
                weChatCustomMenusT.setFounderId(securityUserT.getUserId());
                weChatCustomMenusT.setFounderName(securityUserT.getUserName());
                weChatCustomMenusT.setCreateDateTime(new Date());
                weChatCustomMenusT.setModifyDateTime(new Date());
                customMenusTMapper.save(weChatCustomMenusT);
            } else {
                customMenusTMapper.update(weChatCustomMenusT);
            }
            if (customMenusVo.getCustomMenusTList() != null) {
                for (WeChatCustomMenusTVo childWeChatCustomMenusTVo : customMenusVo.getCustomMenusTList()) {
                    WeChatCustomMenusT childWeChatCustomMenusT = changeToEntity(childWeChatCustomMenusTVo);
                    childWeChatCustomMenusT.setParentId(weChatCustomMenusT.getId());
                    if (childWeChatCustomMenusT.getId() == null) {
                        childWeChatCustomMenusT.setFounderId(securityUserT.getUserId());
                        childWeChatCustomMenusT.setFounderName(securityUserT.getUserName());
                        childWeChatCustomMenusT.setCreateDateTime(new Date());
                        childWeChatCustomMenusT.setModifyDateTime(new Date());
                        customMenusTMapper.save(childWeChatCustomMenusT);
                    } else {
                        customMenusTMapper.update(childWeChatCustomMenusT);
                    }
                }

            }

        }
        return weChatCustomMenusTList;
    }

    //将Vo数据转成实体
    public WeChatCustomMenusT changeToEntity(WeChatCustomMenusTVo weChatCustomMenusTVo) {
        WeChatCustomMenusT weChatCustomMenusT = new WeChatCustomMenusT();
        weChatCustomMenusT.setAppId
                (weChatCustomMenusTVo.getAppId())
                .setClickType
                        (weChatCustomMenusTVo.getClickType()).setId
                (weChatCustomMenusTVo.getId()).setContent(weChatCustomMenusTVo.getContent()).setMenuName(weChatCustomMenusTVo
                .getMenuName()).setParentId(weChatCustomMenusTVo.getParentId()).setCreateDateTime(weChatCustomMenusTVo
                .getCreateDateTime()).setFounderId(weChatCustomMenusTVo.getFounderId()).setFounderName(weChatCustomMenusTVo
                .getFounderName())
                .setModifyDateTime(new Date());
        return weChatCustomMenusT;
    }
}
