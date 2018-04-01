package com.github.jyoghurt.dataDict.service;


import com.github.jyoghurt.dataDict.domain.DataDictValue;

import java.util.List;


/**
 * Created with IntelliJ IDEA. User: metarne Date: 13-4-5 Time: 上午11:44
 * 数据字典服务代理类接口
 */

interface DataDictService {

    /**
     * 根据字典值ID查询字典信息
     *
     * @param valueId 字典值ID
     * @return 字典
     */
    DataDictValue geDataDictValueById(Long valueId) ;

    /**
     * 根据字典项编码获取字典值列表
     *
     * @param dictItemCode 字典项编码
     * @return 字典值列表
     */
    List<DataDictValue> geDataDictValuesByItemCode(String dictItemCode) ;

    /**
     * 查询字典值列表
     *
     * @param dictItemCode        字典项编码
     * @param dictValueDesc       字典值描述
     * @param parentDictValueCode 字典值编码
     * @return 字典值列表
     */
    List<DataDictValue> getDataDictValues(String dictItemCode, String parentDictValueCode, String dictValueDesc) ;

    /**
     * 根据字典项编码获取字典值列表
     *
     * @param dictItemCode  字典项编码
     * @param dictValueDesc 字典值描述
     * @return 字典值列表
     */
    List<DataDictValue> geDataDictValues(String dictItemCode, String dictValueDesc) ;

    /**
     * 根据字典项编码和字典值编码获取字典信息
     *
     * @param dictItemCode  字典项编码
     * @param dictValueCode 字典值编码
     * @return 字典值
      {@inheritDoc}
     */
    DataDictValue getDataDictValue(String dictItemCode, String dictValueCode) ;

    /**
     * 根据字典项编码和字典值编码获取字典信息
     *
     * @param enumValue 枚举值
     * @return 字典值
      {@inheritDoc}
     */
    DataDictValue getDataDictValue(Enum enumValue) ;

    /**
     * 获取字典项的所有字典值
     *
     * @param dictItemCode 字典项编码
     * @return 字典值
      {@inheritDoc}
     */
    List<DataDictValue> getDataDictValues(String dictItemCode) ;

    /**
     * 把类名当做枚举项code，查询旗下所有枚举值，并根据枚举值的code与类属性的对应关系，自动填充对象
     * eg：SmsConfig
     * class SmsConfig {
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
    Object getDataDictObject(Class entityClass) ;
    /**
     * 根据字典编码等信息查询字典值
     *
     * @param dictItemCode  字典项编码
     * @param dictValueCode 字典值编码
     * @return
     */
//     DataDictValue getDataDictValue(String dictItemCode, String dictValueCode) ;

    /**
     * 清除缓存
     *

     */
    void cacheEvict() ;

    /**
     * 递归查询数据字典名字
     *
     * @param dictItemCode  字典项编码
     * @param dictValueCode 字典值编码
     * @param extend        字典名是否包含父字典项名称
     */
    String getDataDictValueName(String dictItemCode, String dictValueCode, Boolean extend) ;


    List<DataDictValue> getDataDictAndSubValues(String dictItemCode, String parentDictValueCode) ;

}
