package com.github.jyoghurt.image.service.impl;

import com.github.jyoghurt.image.dao.ImgMapper;
import com.github.jyoghurt.image.domain.ImgT;
import com.github.jyoghurt.image.service.ImgService;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.handle.OperatorHandle;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

@Service("imgService")
public class ImgServiceImpl extends ServiceSupport<ImgT, ImgMapper> implements ImgService {
	@Autowired
    private ImgMapper imgMapper;

    @Override
	public ImgMapper getMapper() {
		return imgMapper;
	}

    @Override
    public void logicDelete(Serializable id)  {
        getMapper().logicDelete(ImgT.class, id);
    }

    @Override
    public ImgT find(Serializable id)  {
        return getMapper().selectById(ImgT.class,id);
    }

    @Override
    public void updateWashClothImg(String imgids,String affixids, String clothId)  {
        if(StringUtils.isNotEmpty(imgids)){
            String[] ids = imgids.split(",");
            for(String id:ids){
                this.getMapper().updateForSelective(new ImgT().setImgId(id).setNaturalkey(clothId));
            }
        }

        if(StringUtils.isNotEmpty(affixids)){
            String[] ids = affixids.split(",");
            for(String id:ids){
                this.getMapper().updateForSelective(new ImgT().setImgId(id).setNaturalkey(clothId));
            }
        }
    }

    @Override
    public void updateImgByBusinessId(String businessId, List<String> imgIds)  {
        //将业务原有关联的图片的业务主键，清空
        this.getMapper().updateNaturalKeyByBusinessId("",businessId);
        //将对应的imgIds的主键ID，设置为新的主键
        if(CollectionUtils.isNotEmpty(imgIds)){
            for(String imgId:imgIds){
                this.getMapper().updateNaturalKeyByImgId(businessId,imgId);
            }
        }
    }
    @Override
    public void updateImgByBusinessIdAndType(String businessId, String type, List<String> imgIdList)  {
        //将业务原有关联的图片的业务主键，清空
        this.getMapper().updateNaturalKeyByBusinessIdAndType("", businessId, type);
        //将对应的imgIds的主键ID，设置为新的主键
        if(CollectionUtils.isNotEmpty(imgIdList)){
            for(String imgId:imgIdList){
                this.getMapper().updateNaturalKeyByImgId(businessId,imgId);
            }
        }

    }


    /**
     * 设置业务对象图片
     *
     * @param businessObj 业务对象或者业务对象集合
     * @param businessIdName 业务对象的主键字段
     * @param imgFieldName  业务对象的图片字段，图片字段可以是对象或集合
     */
    @Override
    public void setImg(Object businessObj, String businessIdName, String imgFieldName){
        try {
            Class clazz = businessObj.getClass();
            Class businessClass = null;
            Field idField = null;
            Field imgField = null;
            List<String> businessIdList = new ArrayList<>();
            if (Collection.class.isAssignableFrom(clazz)) { // 集合对象
                Collection businessCollection =  (Collection) businessObj;
                if (CollectionUtils.isNotEmpty(businessCollection)) {
                    List businessList = new ArrayList<>();
                    businessList.addAll(businessCollection);
                    businessClass = businessList.get(0).getClass();
                } else {
                    return;
                }
            } else { // 普通对象
                businessClass = clazz;
            }
            idField = businessClass.getDeclaredField(businessIdName);
            idField.setAccessible(true);
            imgField = businessClass.getDeclaredField(imgFieldName);
            imgField.setAccessible(true);
            Map<String, Object> objectMap = new HashMap<>();

            if (Collection.class.isAssignableFrom(clazz)) {
                Collection businessCollection =  (Collection) businessObj;
                for (Object object : businessCollection) {
                    String id = (String)idField.get(object);
                    businessIdList.add(id);
                    objectMap.put(id, object);
                    initImgCollection(object, imgField);
                }
            } else {
                String id = (String)idField.get(businessObj);
                businessIdList.add(id);
                objectMap.put(id, businessObj);
                initImgCollection(businessObj, imgField);

            }

            List<ImgT> allImgTList = findAll(new ImgT().setDeleteFlag(false), new QueryHandle().addOperatorHandle("naturalkey", OperatorHandle.operatorType.IN, businessIdList.toArray()));
            for (ImgT imgT : allImgTList) {
                Object object = objectMap.get(imgT.getNaturalkey());
                if (Collection.class.isAssignableFrom(imgField.getType())) {
                    ((Collection)imgField.get(object)).add(imgT);
                } else {
                    imgField.set(object, imgT);
                }
            }
        } catch (NoSuchFieldException e) {
            throw new BaseErrorException("设置图片时，字段名有问题:" + businessIdName, e);
        } catch (IllegalAccessException e) {
            throw new BaseErrorException("设置图片错误", e);
        }
    }

    private void initImgCollection(Object object, Field imgField) throws IllegalAccessException {
        if (List.class.isAssignableFrom(imgField.getType())) {
            imgField.set(object, new ArrayList<>());
        } else if (Set.class.isAssignableFrom(imgField.getType())) {
            imgField.set(object, new HashSet<>());
        } else if (!imgField.getType().equals(ImgT.class)){
            throw new BaseErrorException("设置图片错误,图片字段类型不正确");
        }
    }

}
