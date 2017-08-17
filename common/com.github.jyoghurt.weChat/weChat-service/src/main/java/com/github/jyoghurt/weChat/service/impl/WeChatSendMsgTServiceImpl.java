package com.github.jyoghurt.weChat.service.impl;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.weChat.dao.WeChatSendMsgTMapper;
import com.github.jyoghurt.weChat.domain.WeChatSendMsgT;
import com.github.jyoghurt.weChat.service.WeChatSendMsgTService;
import com.github.jyoghurt.wechatbasic.common.pojo.AccessToken;
import com.github.jyoghurt.wechatbasic.common.pojo.Articles;
import com.github.jyoghurt.wechatbasic.common.pojo.MaterialNewsContent;
import com.github.jyoghurt.wechatbasic.common.pojo.MaterialNewsMap;
import com.github.jyoghurt.wechatbasic.common.util.AdvancedUtil;
import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
import com.github.jyoghurt.wechatbasic.enums.WeChatMsgSendEnum;
import com.github.jyoghurt.wechatbasic.enums.WeChatMsgTypeEnum;
import com.github.jyoghurt.wechatbasic.exception.WeChatException;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;

import static com.github.jyoghurt.wechatbasic.common.util.WeixinUtil.getAccessToken;

@Service("weChatSendMsgTService")
public class WeChatSendMsgTServiceImpl extends ServiceSupport<WeChatSendMsgT, WeChatSendMsgTMapper> implements WeChatSendMsgTService {
    @Value("${downloadPath}")
    private String downloadPath;

    @Autowired
    private WeChatSendMsgTMapper weChatSendMsgTMapper;

    @Override
    public WeChatSendMsgTMapper getMapper() {
        return weChatSendMsgTMapper;
    }

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(WeChatSendMsgT.class, id);
    }

    @Override
    public WeChatSendMsgT find(Serializable id)  {
        return getMapper().selectById(WeChatSendMsgT.class, id);
    }

    @Override
    public String sendAll(String content, WeChatAccountType weChatAccountType, WeChatMsgTypeEnum weChatMsgTypeEnum,
                          SecurityUnitT unitT) throws WeChatException {
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
        /*获取token*/
        AccessToken token = getAccessToken(appId, appSecret);
        String accessToken = token.getToken();
        WeChatSendMsgT weChatSendMsgT = new WeChatSendMsgT();
        JSONObject returnObj = new JSONObject();
        switch (weChatMsgTypeEnum) {
            case mpnews: {//若图文消息则content为图文mediaId 需根据mediaId取回图文详细信息
                MaterialNewsMap materialNewsMap=new MaterialNewsMap();
                MaterialNewsContent materialNewsContent= AdvancedUtil.getMaterialNews(accessToken,content);
                String downLoadFilePath = StringUtils.join(downloadPath, "/", appId);
                for(Articles article:materialNewsContent.getNews_item()){
                    article.setDownloadPath(downLoadFilePath + "/" + article.getThumb_media_id() + ".jpg");
                }
                materialNewsMap.setMedia_id(content);
                materialNewsMap.setContent(materialNewsContent);
                materialNewsMap.setUpdate_time(String.valueOf(new Date().getTime()/1000));
                weChatSendMsgT.setContent(JSONObject.fromObject(materialNewsMap).toString());
                returnObj = AdvancedUtil.sendAll(accessToken, materialNewsMap.getMedia_id(), weChatMsgTypeEnum);
                break;
            }
            case text: {
                weChatSendMsgT.setContent(content);
                returnObj = AdvancedUtil.sendAll(accessToken, content, weChatMsgTypeEnum);
                break;
            }
        }
        /*根据群发类型保存群发消息记录*/
        weChatSendMsgT.setMsgId(returnObj.get("msg_id").toString());
        weChatSendMsgT.setMsgType(weChatMsgTypeEnum);
        weChatSendMsgT.setCreateDateTime(new Date());
        weChatSendMsgT.setSend(WeChatMsgSendEnum.sent);
        weChatSendMsgT.setAppId(appId);
        this.getMapper().save(weChatSendMsgT);
        return returnObj.get("msg_id").toString();
    }

}
