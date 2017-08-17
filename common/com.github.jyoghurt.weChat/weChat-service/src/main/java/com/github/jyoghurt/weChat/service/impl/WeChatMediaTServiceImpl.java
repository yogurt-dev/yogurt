package com.github.jyoghurt.weChat.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.github.jyoghurt.image.domain.ImageConfig;
import com.github.jyoghurt.image.exception.ImgException;
import com.github.jyoghurt.image.service.UploadImgService;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.weChat.dao.WeChatMediaTMapper;
import com.github.jyoghurt.weChat.domain.WeChatMediaT;
import com.github.jyoghurt.weChat.service.WeChatMediaTService;
import com.github.jyoghurt.wechatbasic.common.pojo.AccessToken;
import com.github.jyoghurt.wechatbasic.common.pojo.WeixinMedia;
import com.github.jyoghurt.wechatbasic.common.util.AdvancedUtil;
import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.result.HttpResultHandle;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.Serializable;

import static com.github.jyoghurt.wechatbasic.common.util.WeixinUtil.getAccessToken;

@Service("weChatMediaTService")
public class WeChatMediaTServiceImpl extends ServiceSupport<WeChatMediaT, WeChatMediaTMapper> implements WeChatMediaTService {
    @Autowired
    private WeChatMediaTMapper weChatMediaTMapper;
    @Autowired
    private UploadImgService uploadImgService;
    @Value("${uploadPath}")
    private String uploadPath;
    @Value("${downloadPath}")
    private String downloadPath;

    @Override
    public WeChatMediaTMapper getMapper() {
        return weChatMediaTMapper;
    }

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(WeChatMediaT.class, id);
    }

    @Override
    public WeChatMediaT find(Serializable id)  {
        return getMapper().selectById(WeChatMediaT.class, id);
    }

    @Override
    public HttpResultEntity uploadImg(MultipartFile file, String moduleName, WeChatAccountType
            weChatAccountType, SecurityUnitT unitT)   {
        HttpResultEntity httpResultEntity = null;
        try {
            httpResultEntity = uploadImgService.uploadImg(file, new ImageConfig().setModuleName(moduleName));
        } catch (ImgException e) {
            e.printStackTrace();
        }
        //若上传报错则返回报错
        if (!"1".equals(httpResultEntity.getErrorCode())) {
            return HttpResultHandle.getErrorResult("上传文件失败!");
        }
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
        WeixinMedia weixinMedia = new WeixinMedia();
        String filePath = uploadPath + httpResultEntity.getResult().toString().replace(downloadPath, "");
        weixinMedia = AdvancedUtil.uploadMaterial(token.getToken(), filePath);
        //获取媒体下载路径
        //下载路径  从素材库下载
        String downLoadFilePath = StringUtils.join(uploadPath, "/", appId, "/");
        makeDir(downLoadFilePath);
        AdvancedUtil.getMaterial(token.getToken(), weixinMedia.getMedia_id(), downLoadFilePath, ".jpg");
        //显示路径
        String showFilePath = StringUtils.join(downloadPath, "/", appId, "/", weixinMedia.getMedia_id(), ".jpg");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("thumb_media_id", weixinMedia.getMedia_id());
        jsonObj.put("downloadPath", showFilePath);
        return HttpResultHandle.getSuccessResult(jsonObj);
    }


    public Boolean makeDir(String filePath) {
        File file = new File(filePath);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
            logger.info("//创建" + filePath);
            return false;
        }
        return true;
    }

    public Boolean haveFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists() && !file.isDirectory()) {
            return false;
        }
        return true;
    }
}
