package com.github.jyoghurt.weChat.service.impl;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.weChat.dao.WeChatAutoResponseMsgTMapper;
import com.github.jyoghurt.weChat.dao.WeChatAutoResponseTMapper;
import com.github.jyoghurt.weChat.domain.WeChatAutoResponseMsgT;
import com.github.jyoghurt.weChat.domain.WeChatAutoResponseT;
import com.github.jyoghurt.weChat.domain.WeChatAutoResponseTVo;
import com.github.jyoghurt.weChat.service.WeChatAutoResponseMsgTService;
import com.github.jyoghurt.weChat.service.WeChatAutoResponseTService;
import com.github.jyoghurt.wechatbasic.common.pojo.Articles;
import com.github.jyoghurt.wechatbasic.common.pojo.MaterialNewsContent;
import com.github.jyoghurt.wechatbasic.common.pojo.MaterialNewsMap;
import com.github.jyoghurt.wechatbasic.common.util.AdvancedUtil;
import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
import com.github.jyoghurt.wechatbasic.enums.WeChatAutoResponseType;
import com.github.jyoghurt.wechatbasic.exception.WeChatException;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

import static com.github.jyoghurt.wechatbasic.common.util.WeixinUtil.getAccessToken;

@Service("weChatAutoResponseTService")
public class WeChatAutoResponseTServiceImpl extends ServiceSupport<WeChatAutoResponseT, WeChatAutoResponseTMapper> implements WeChatAutoResponseTService {
    @Value("${downloadPath}")
    private String downloadPath;
    @Autowired
    private WeChatAutoResponseTMapper weChatAutoResponseTMapper;
    @Autowired
    private WeChatAutoResponseMsgTMapper weChatAutoResponseMsgTMapper;
    @Autowired
    private WeChatAutoResponseMsgTService weChatAutoResponseMsgTService;

    @Override
    public WeChatAutoResponseTMapper getMapper() {
        return weChatAutoResponseTMapper;
    }

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(WeChatAutoResponseT.class, id);
    }

    @Override
    public WeChatAutoResponseT find(Serializable id)  {
        return getMapper().selectById(WeChatAutoResponseT.class, id);
    }

    public WeChatAutoResponseT addAutoResponse(WeChatAutoResponseT weChatAutoResponseT,
                                               WeChatAccountType weChatAccountType, SecurityUnitT unitT, HttpServletRequest request) {
        String appId = "";
        String appSecret = "";
        switch (weChatAccountType) {
            case TYPE_SUBSCRIPTION: {
                appId = unitT.getAppId();
                appSecret = unitT.getSecretKey();
                break;
            }
            case TYPE_SERVICE: {
                appId = unitT.getAppIdF();
                appSecret = unitT.getSecretKeyF();
                break;
            }

            case TYPE_ENTERPRISE: {
                appId = unitT.getAppIdQ();
                appSecret = unitT.getSecretKeyQ();
                break;
            }
        }
        weChatAutoResponseT.setAppId(appId);
        weChatAutoResponseT.setState("0");
        save(weChatAutoResponseT);
        //保存消息列表
        for (WeChatAutoResponseMsgT weChatAutoResponseMsgT : weChatAutoResponseT.getWeChatAutoResponseMsgTList()) {
            switch (weChatAutoResponseMsgT.getMsgType()) {
                case mpnews: {
                    MaterialNewsMap materialNewsMap = new MaterialNewsMap();
                    MaterialNewsContent materialNewsContent = null;
                    try {
                        materialNewsContent = AdvancedUtil.getMaterialNews(getAccessToken(appId,
                                appSecret).getToken(), weChatAutoResponseMsgT.getMediaId());
                    } catch (WeChatException e) {
                        e.printStackTrace();
                    }
                    String downLoadFilePath = StringUtils.join(request.getRequestURL().toString().replace(request.getRequestURI(),"")+"/"+downloadPath, "/", appId);
                    for (Articles article : materialNewsContent.getNews_item()) {
                        article.setDownloadPath(downLoadFilePath + "/" + article.getThumb_media_id() + ".jpg");
                    }
                    materialNewsMap.setMedia_id(weChatAutoResponseMsgT.getMediaId());
                    materialNewsMap.setContent(materialNewsContent);
                    weChatAutoResponseMsgT.setContent(JSONObject.fromObject(materialNewsMap).toString());
                    break;
                }
            }
            weChatAutoResponseMsgT.setAutoResponseId(weChatAutoResponseT.getAutoResponseId());
            weChatAutoResponseMsgTService.save(weChatAutoResponseMsgT);
        }
        return weChatAutoResponseT;
    }

    public WeChatAutoResponseT editAutoResponse(WeChatAutoResponseT weChatAutoResponseT,
                                                WeChatAccountType weChatAccountType, SecurityUnitT unitT, HttpServletRequest request){
        String appId = "";
        String appSecret = "";
        switch (weChatAccountType) {
            case TYPE_SUBSCRIPTION: {
                appId = unitT.getAppId();
                appSecret = unitT.getSecretKey();
                break;
            }
            case TYPE_SERVICE: {
                appId = unitT.getAppIdF();
                appSecret = unitT.getSecretKeyF();
                break;
            }

            case TYPE_ENTERPRISE: {
                appId = unitT.getAppIdQ();
                appSecret = unitT.getSecretKeyQ();
                break;
            }
        }
        weChatAutoResponseT.setAppId(searchAppId(weChatAccountType,unitT));
        weChatAutoResponseT.setState("0");
        update(weChatAutoResponseT);
        for (WeChatAutoResponseMsgT weChatAutoResponseMsgT : weChatAutoResponseT.getWeChatAutoResponseMsgTList()) {
            switch (weChatAutoResponseMsgT.getMsgType()) {
                case mpnews: {
                    MaterialNewsMap materialNewsMap = new MaterialNewsMap();
                    MaterialNewsContent materialNewsContent = null;
                    try {
                        materialNewsContent = AdvancedUtil.getMaterialNews(getAccessToken(appId,
                                appSecret).getToken(), weChatAutoResponseMsgT.getMediaId());
                    } catch (WeChatException e) {
                        e.printStackTrace();
                    }
                    String downLoadFilePath = StringUtils.join(request.getRequestURL().toString().replace(request
                                    .getRequestURI(),"")+"/"+downloadPath, "/",
                            appId);
                    for (Articles article : materialNewsContent.getNews_item()) {
                        article.setDownloadPath(downLoadFilePath + "/" + article.getThumb_media_id() + ".jpg");
                    }
                    materialNewsMap.setMedia_id(weChatAutoResponseMsgT.getMediaId());
                    materialNewsMap.setContent(materialNewsContent);
                    weChatAutoResponseMsgT.setContent(JSONObject.fromObject(materialNewsMap).toString());
                    break;
                }
            }
            weChatAutoResponseMsgT.setAutoResponseId(weChatAutoResponseT.getAutoResponseId());
            weChatAutoResponseMsgTService.update(weChatAutoResponseMsgT);
        }
        return weChatAutoResponseT;
    }

    public WeChatAutoResponseT delAutoResponse(String autoResponseId,
                                               WeChatAccountType weChatAccountType, SecurityUnitT unitT) {

        WeChatAutoResponseT weChatAutoResponseT= weChatAutoResponseTMapper.selectById(WeChatAutoResponseT.class,autoResponseId);
        weChatAutoResponseT.setState("1");
        update(weChatAutoResponseT);
        return weChatAutoResponseT;
    }

    public List<WeChatAutoResponseT> findAutoResponseList(String appId, WeChatAutoResponseType weChatAutoResponseType) {
        return getMapper().findAutoResponseList(appId, weChatAutoResponseType);
    }
    public List<WeChatAutoResponseTVo> findAutoResponseVoList(WeChatAccountType weChatAccountType, WeChatAutoResponseType weChatAutoResponseType, SecurityUnitT unitT){
        String appId = "";
        switch (weChatAccountType) {
            case TYPE_SUBSCRIPTION: {
                appId = unitT.getAppId();
                break;
            }
            case TYPE_SERVICE: {
                appId = unitT.getAppIdF();
                break;
            }

            case TYPE_ENTERPRISE: {
                appId = unitT.getAppIdQ();
                break;
            }
        }
      List<WeChatAutoResponseTVo> weChatAutoResponseTVoList=  getMapper().findAutoResponseVoList(appId, weChatAutoResponseType);
        for(WeChatAutoResponseTVo weChatAutoResponseTVo:weChatAutoResponseTVoList){
            weChatAutoResponseTVo.setWeChatAutoResponseMsgTList(weChatAutoResponseMsgTMapper.findByAutoResponseId(weChatAutoResponseTVo
                    .getAutoResponseId()));
        }
        return weChatAutoResponseTVoList;
    }

    private WeChatAutoResponseT createWeChatAutoResponseT(WeChatAutoResponseTVo weChatAutoResponseTVo){
        WeChatAutoResponseT weChatAutoResponseT=new WeChatAutoResponseT();
        weChatAutoResponseT.setMatchingType(weChatAutoResponseTVo.getMatchingType());
        weChatAutoResponseT.setKeywords(weChatAutoResponseTVo.getKeywords());
        weChatAutoResponseT.setResponseType(weChatAutoResponseTVo.getResponseType());
        weChatAutoResponseT.setAppId(weChatAutoResponseTVo.getAppId());
        weChatAutoResponseT.setAutoResponseId(weChatAutoResponseTVo.getAutoResponseId());
        weChatAutoResponseT.setCreateDateTime(weChatAutoResponseTVo.getCreateDateTime());
        weChatAutoResponseT.setModifyDateTime(weChatAutoResponseTVo.getModifyDateTime());
        weChatAutoResponseT.setFounderId(weChatAutoResponseTVo.getFounderId());
        weChatAutoResponseT.setFounderName(weChatAutoResponseTVo.getFounderName());
        return weChatAutoResponseT;
    }

    private String searchAppId(WeChatAccountType weChatAccountType,SecurityUnitT unitT){
        String appId = "";
        switch (weChatAccountType) {
            case TYPE_SUBSCRIPTION: {
                appId = unitT.getAppId();
                break;
            }
            case TYPE_SERVICE: {
                appId = unitT.getAppIdF();
                break;
            }

            case TYPE_ENTERPRISE: {
                appId = unitT.getAppIdQ();
                break;
            }
        }
        return appId;
    }
}
