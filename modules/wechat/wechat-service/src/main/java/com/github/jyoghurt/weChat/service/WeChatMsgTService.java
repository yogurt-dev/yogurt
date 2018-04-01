package com.github.jyoghurt.weChat.service;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.weChat.domain.WeChatMsgT;
import com.github.jyoghurt.wechatbasic.common.pojo.MaterialNewsMap;
import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
import com.github.jyoghurt.wechatbasic.enums.WeChatMsgTypeEnum;
import com.github.jyoghurt.wechatbasic.exception.WeChatException;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.result.QueryResult;
import com.github.jyoghurt.core.service.BaseService;

import java.util.List;

/**
 * 微信消息记录表服务层
 */
public interface WeChatMsgTService extends BaseService<WeChatMsgT> {

    public void add(WeChatMsgT weChatMsgT) ;

    public void addMpNews(WeChatMsgT weChatMsgT) ;

    public List<WeChatMsgT> getNewsList() ;

    public void sendWeChat(WeChatMsgT weChatMsgT, String unitId) ;

    public void sendWeChatText(WeChatMsgT weChatMsgT, String unitId) ;

    public void sendMsgByOpenId(String message) ;

    public String uploadMaterialNews(MaterialNewsMap materialNewsMap, WeChatAccountType weChatAccountType,
                                     SecurityUnitT unitT, String ifsend) throws WeChatException;

    public void preView(String towxname, String media_id, WeChatAccountType weChatAccountType, SecurityUnitT unitT) throws  WeChatException;

    public QueryResult<MaterialNewsMap> batchgetMaterialNews(QueryHandle queryHandle, WeChatAccountType
            weChatAccountType, SecurityUnitT unitT) throws  WeChatException;

    public MaterialNewsMap updateMaterialNews(MaterialNewsMap materialNewsMap, WeChatAccountType weChatAccountType,
                                              SecurityUnitT unitT, String ifsend) throws  WeChatException;

    public void delMaterial(String media_id, WeChatAccountType weChatAccountType, SecurityUnitT unitT) throws
            WeChatException;

    public HttpResultEntity<?> getMaterial(String media_id, WeChatAccountType weChatAccountType, WeChatMsgTypeEnum
            weChatMsgTypeEnum, SecurityUnitT unitT) throws  WeChatException;


}
