package com.github.jyoghurt.weChat.service;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.weChat.domain.WeChatMediaT;
import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.service.BaseService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 微信本地媒体记录服务层
 *
 */
public interface WeChatMediaTService extends BaseService<WeChatMediaT> {
   public HttpResultEntity uploadImg(MultipartFile file, String moduleName, WeChatAccountType weChatAccountType, SecurityUnitT unitT)
            ;
}
