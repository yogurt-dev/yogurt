package com.github.jyoghurt.serial.handler;


import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.serial.enums.Module;
import com.github.jyoghurt.serial.enums.SerialType;
import com.github.jyoghurt.serial.exception.SerialException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Project:
 * @Package: com.df.community.base.serial.handler
 * @Description: 模块私有流水策略，由各个模块在SerialInfoT中定义一个初始值，然后在本模块维度上自增 如 1 2 3
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-11-11 10:11
 */
@Component("modulePrivateRule")
public class ModulePrivateRule extends SerialBaseRule {

    @Override
    public String generateSerialNo(Module module, SerialType serialType) {
        String moduleName = module.getCode();
        checkContrainerInRedis(modulePrivateNoContainer);
        //判断内存中是否存在业务模块可用的流水号
        if (!checkExistSerialNoInMemery(moduleName,getContainerFromRedis(modulePrivateNoContainer))) {
            String startNo = queryStartNum(moduleName, serialType, step);
            Map<String,List<Integer>> map = getContainerFromRedis(modulePrivateNoContainer);
            setData(moduleName, Integer.parseInt(startNo), map);
            setContainerToRedis(modulePrivateNoContainer,map);
        }

        //从redis中获取序列号
        Map<String,List<Integer>> map = getContainerFromRedis(modulePrivateNoContainer);
        String serialNo = getNoFromData(moduleName, map).toString();
        setContainerToRedis(modulePrivateNoContainer,map);
        return serialNo;
    }

    /**
     * 查询模块业务主键开始值，查询普通分模块流水
     *
     * @param moduleName
     * @param serialType
     * @param step
     * @return
     * @throws SerialException
     */
    private String queryStartNum(String moduleName, SerialType serialType, Integer step) throws
            SerialException {
        try {
            return getSerialInfoService().queryNormalSerialInfoByModuleName(moduleName, step, serialType);
        } catch (BaseErrorException e) {
            throw new SerialException("1", "获取业务主键异常：" + e.getMessage());
        }
    }
}
