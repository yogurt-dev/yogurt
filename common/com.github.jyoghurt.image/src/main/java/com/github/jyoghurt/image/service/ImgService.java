package com.github.jyoghurt.image.service;

import com.github.jyoghurt.image.domain.ImgT;
import com.github.jyoghurt.core.service.BaseService;

import java.util.List;

/**
 * 图片服务层
 *
 */
public interface ImgService extends BaseService<ImgT> {

    /**
     * 更新衣物图片
     * @param imgids
     * @param clothId

     */
    void updateWashClothImg(String imgids, String affixids, String clothId) ;

    /**
     *
     * @param businessId
     * @param imgIds

     */
    void updateImgByBusinessId(String businessId, List<String> imgIds) ;

    /**
     * 根据业务id和类型更新图片信息
     * @param businessId
     * @param type
     * @param imgIdList

     */
    void updateImgByBusinessIdAndType(String businessId, String type, List<String> imgIdList) ;

    void setImg(Object businessObj, String businessIdName, String imgFieldName);
}
