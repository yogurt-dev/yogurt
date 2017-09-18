package com.github.jyoghurt.serial.service;


import com.github.jyoghurt.core.service.BaseService;
import com.github.jyoghurt.serial.domain.SerialInfoT;
import com.github.jyoghurt.serial.enums.SerialType;
import com.github.jyoghurt.serial.exception.SerialException;

/**
 * 业务主键序列号服务层
 */
public interface SerialInfoService extends BaseService<SerialInfoT> {
    public Integer querySerialInfoByModuleName() ;

    /**
     * 查询 业务模块的流水号记录
     * @param module
     * @param step
     * @param serialType
     * @return

     * @throws SerialException
     */
    String querySerialInfoByModuleName(String module, Integer step, SerialType serialType) throws SerialException;

    /**
     * 查询 业务模块的流水号记录
     * @param module
     * @param step
     * @param serialType
     * @return

     * @throws SerialException
     */
    String queryNormalSerialInfoByModuleName(String module, Integer step, SerialType serialType) throws
            SerialException;

    /**
     * 查询 通用业务模块流水号记录
     * @param module
     * @param step
     * @param serialType
     * @return

     */
    String queryCommSerialInfoByModuleName(String module, Integer step, String serialType) ;

}
