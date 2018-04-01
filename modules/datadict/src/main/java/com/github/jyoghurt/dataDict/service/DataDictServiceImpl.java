package com.github.jyoghurt.dataDict.service;

import com.github.jyoghurt.dataDict.convert.DataDictConvert;
import com.github.jyoghurt.dataDict.domain.DataDictValue;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.handle.QueryHandle;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jtwu on 2016/1/4.
 */
@Service("dataDictService")
class DataDictServiceImpl implements DataDictService {
    @Resource
    private DataDictValueService dataDictValueService;
    @Resource
    private DataDictItemService dataDictItemService;

    public DataDictValue geDataDictValueById(Long valueId) {
        return dataDictValueService.find(valueId);
    }

    /**
     * 根据字典项编码获取字典值列表
     *
     * @param dictItemCode 字典项编码
     * @return 字典值列表
     */
    @Override
    @Cacheable(value = "dataDict", key = "#root.targetClass + #root.methodName+#dictItemCode")
    public List<DataDictValue> geDataDictValuesByItemCode(String dictItemCode) {
        return geDataDictValues(dictItemCode, null);
    }


    @Override
    @Cacheable(value = "dataDict", key = "#root.targetClass + #root.methodName +#dictItemCode +#parentDictValueCode +#dictValueDesc")
    public List<DataDictValue> getDataDictValues(String dictItemCode, String parentDictValueCode, String dictValueDesc) {
        return dataDictValueService.findAll(new DataDictValue().setDictItemCode(dictItemCode).setDeleteFlag(false)
                .setDictValueDesc(dictValueDesc).setParentDictValueCode(parentDictValueCode), new QueryHandle().addOrderBy("sortNum", "asc"));
    }

    /**
     * 根据字典项编码获取字典值列表
     *
     * @param dictItemCode  字典项编码
     * @param dictValueDesc 字典值描述
     * @return 字典值列表
     */
    @Override
    @Cacheable(value = "dataDict", key = "#root.targetClass + #root.methodName+#dictItemCode+#dictValueDesc")
    public List<DataDictValue> geDataDictValues(String dictItemCode, String dictValueDesc) {
        return getDataDictValues(dictItemCode, null, dictValueDesc);
    }

    /**
     * 根据字典项编码和字典值编码获取字典信息
     *
     * @param dictItemCode  字典项编码
     * @param dictValueCode 字典值编码
     * @return 字典值
     * {@inheritDoc}
     */
    @Cacheable(value = "dataDict", key = "#root.targetClass + #root.methodName+ #dictItemCode +#dictValueCode")
    public DataDictValue getDataDictValue(String dictItemCode, String dictValueCode) {
        List<DataDictValue> list = dataDictValueService.findAll(new DataDictValue().setDictItemCode(dictItemCode)
                .setDictValueCode(dictValueCode).setDeleteFlag(false), new QueryHandle().setRows(1));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 根据字典项编码和字典值编码获取字典信息
     *
     * @param enumValue 枚举值
     * @return 字典值
     * {@inheritDoc}
     */
    @Cacheable(value = "dataDict", key = "#root.targetClass + #root.methodName+ #enumValue.name()")
    public DataDictValue getDataDictValue(Enum enumValue) {
        List<DataDictValue> list = dataDictValueService.findAll(new DataDictValue().setDictItemCode(enumValue.getClass().getSimpleName())
                .setDictValueCode(enumValue.name()).setDeleteFlag(false), new QueryHandle().setRows(1));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 获取字典项的所有字典值
     *
     * @param dictItemCode 字典项编码
     * @return 字典值
     * {@inheritDoc}
     */
    @Cacheable(value = "dataDict", key = "#root.targetClass + #root.methodName + #dictItemCode")
    public List<DataDictValue> getDataDictValues(String dictItemCode) {
        return dataDictValueService.findAll(new DataDictValue().setDictItemCode(dictItemCode).setDeleteFlag(false),new QueryHandle().addOrderBy("sortNum","ASC"));
    }

    /**
     * 把类名当做枚举项code，查询旗下所有枚举值，并根据枚举值的code与类属性的对应关系，自动填充对象
     * eg：SmsConfig
     * public class SmsConfig {
     * private String serverPort;
     * private String accountSid;
     * private String appId;
     * private String accountToken;
     * private String serverIP;
     * ...
     * }
     * <p>
     * 该方法会自动将枚举项SmsConfig下的枚举值的value填充到对应的属性上
     *
     * @param entityClass 需要返回的枚举类
     * @return 枚举项对象
     */
    @Cacheable(value = "dataDict", key = "#root.targetClass + #root.methodName+ #entityClass.getSimpleName()")
    public Object getDataDictObject(Class entityClass) {
        List<DataDictValue> list = getDataDictValues(entityClass.getSimpleName());
        try {
            Object object = entityClass.newInstance();
            for (DataDictValue value : list) {
                try {
                    Field field = entityClass.getDeclaredField(value.getDictValueCode());
                    field.setAccessible(true);  //设置私有属性范围
                    field.set(object, value.getDictValueName());
                } catch (NoSuchFieldException e) {
                    continue;
                }
            }
            return object;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BaseErrorException("查询字典值失败！", e);
        }
    }

    @Override
    @CacheEvict(value = "dataDict", allEntries = true)
    public void cacheEvict() {
    }

    @Override
    @Cacheable(value = "dataDict", key = "#root.targetClass + #root.methodName+#dictItemCode+#dictValueCode+#extend")
    public String getDataDictValueName(String dictItemCode, String dictValueCode, Boolean extend) {
        DataDictValue dataDictValue = getDataDictValue(dictItemCode, dictValueCode);
        if (extend && StringUtils.isNotEmpty(dataDictValue.getParentDictItemCode())) {
            return getDataDictValueName(dataDictValue.getParentDictItemCode(), dataDictValue.getParentDictValueCode(), extend) + " " + dataDictValue.getDictValueName();
        }
        return dataDictValue.getDictValueName();
    }

    @Override
    //@Cacheable(value = "dataDict", key = "#root.targetClass + #root.methodName+#dictItemCode+#dictValueCode")
    public List<DataDictValue> getDataDictAndSubValues(String dictItemCode, String dictValueCode) {
        List<DataDictValue> allDataDictValuesList = dataDictValueService.findAll(new DataDictValue().setDeleteFlag(false));
        //过滤自己
        //filterDataDictValuesByDictItemCode(allDataDictValuesList, dictItemCode, dictValueCode);
        //递归过滤子节点
        if (!StringUtils.isEmpty(dictValueCode)) {
            filterDataDictValuesByParentDictItemCode(allDataDictValuesList, dictItemCode, dictValueCode);
        } else {
            filterDataDictValuesByDictItemCode(allDataDictValuesList, dictItemCode, dictValueCode);
        }
        List<DataDictValue> resultList = new ArrayList<>();
        for (DataDictValue dataDictValue : allDataDictValuesList) {
            if (!dataDictValue.isMatch()) {
                continue;
            }
            this.processParentTreeNodeStr(dataDictValue);
            resultList.add(dataDictValue);
        }
        resultList = getChildrenDataDictAndSubValues(allDataDictValuesList, resultList);
        return resultList;
    }

    private List<DataDictValue> getChildrenDataDictAndSubValues(List<DataDictValue> allDataDictValuesList, List<DataDictValue> parentDataDictValuesList) {
        if (CollectionUtils.isEmpty(parentDataDictValuesList)) {
            return parentDataDictValuesList;
        }
        HashMap<String, DataDictValue> allDataDictValuesMap = DataDictConvert.convert(allDataDictValuesList);
        for (DataDictValue dataDictValue : parentDataDictValuesList) {
            String key = DataDictConvert.getKey(dataDictValue);
            if ("__-__".equals(key)) {
                continue;
            }
            if (allDataDictValuesMap.containsKey(key)) {
                dataDictValue.setParent(true);
            }
        }
        return parentDataDictValuesList;
    }

    private void processParentTreeNodeStr(DataDictValue dataDictValue) {
        if (dataDictValue.getParentDictItemCode() == null) {
            dataDictValue.setParentDictItemCode("");
        }
        if (dataDictValue.getParentDictValueCode() == null) {
            dataDictValue.setParentDictValueCode("");
        }
        dataDictValue.setTreeNodeStr(dataDictValue.getDictItemCode() + "#_#" + dataDictValue.getDictValueCode());
        dataDictValue.setParentTreeNodeStr(dataDictValue.getParentDictItemCode() + "#_#" + dataDictValue.getParentDictValueCode());
    }

    private void filterDataDictValuesByDictItemCode(List<DataDictValue> allDataDictValuesList, String dictItemCode, String dictValueCode) {
        for (DataDictValue dataDictValue : allDataDictValuesList) {
            if (dataDictValue.isMatch()) {
                continue;
            }
            if (StringUtils.isEmpty(dictItemCode)) {
                continue;
            }
            if (StringUtils.isEmpty(dataDictValue.getDictItemCode())) {
                continue;
            }
            if (StringUtils.isEmpty(dictValueCode)) {
                if (dataDictValue.getDictItemCode().equals(dictItemCode)) {
                    dataDictValue.setMatch(true);
                }
            } else {
                if (StringUtils.isEmpty(dataDictValue.getDictValueCode())) {
                    continue;
                }
                if (dataDictValue.getDictItemCode().equals(dictItemCode) && dataDictValue.getDictValueCode().equals(dictValueCode)) {
                    dataDictValue.setMatch(true);
                }
            }
        }
    }

    private static int i = 0;

    //过滤子节点
    private List<DataDictValue> filterDataDictValuesByParentDictItemCode(List<DataDictValue> allDataDictValuesList, String dictItemCode, String dictValueCode) {
        List<DataDictValue> subList = new ArrayList<>();
        for (DataDictValue dataDictValue : allDataDictValuesList) {
            i++;
            if (dataDictValue.isMatch()) {
                continue;
            }
            if (StringUtils.isEmpty(dictItemCode)) {
                continue;
            }
            if (StringUtils.isEmpty(dataDictValue.getParentDictItemCode())) {
                continue;
            }
            if (StringUtils.isEmpty(dictValueCode)) {
                if (dataDictValue.getParentDictItemCode().equals(dictItemCode) && !dataDictValue.isMatch()) {
                    dataDictValue.setMatch(true);
                    subList.add(dataDictValue);
                }
            } else {
                if (StringUtils.isEmpty(dataDictValue.getParentDictValueCode())) {
                    continue;
                }
                if (dataDictValue.getParentDictItemCode().equals(dictItemCode) && dataDictValue.getParentDictValueCode().equals(dictValueCode) && !dataDictValue.isMatch()) {
                    dataDictValue.setMatch(true);
                    subList.add(dataDictValue);
                }
            }
        }
//        if (!flag) {
//            filterDataDictValuesByParentDictItemCode(allDataDictValuesList,);
//        }
//        Set<DataDictValue> filterRepeatSubList = new HashSet<>(subList);
//        for (DataDictValue dataDictValue : filterRepeatSubList) {
//            filterDataDictValuesByParentDictItemCode(allDataDictValuesList, dataDictValue.getDictItemCode(), dataDictValue.getDictValueCode());
//        }
        return allDataDictValuesList;
    }

}