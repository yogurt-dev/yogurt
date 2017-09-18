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
 * @Description: 公共策略，各模块公共使用一个流水号，流水号自增 	如1200030200
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-11-11 10:22
 */
@Component("commonalityRule")
public class CommonalityRule extends SerialBaseRule{

    @Override
    public String generateSerialNo(Module module, SerialType serialType) {
        checkContrainerInRedis(commNoContainerKey);
        if (!checkExistSerialNoInMemery(module.getCode(),getContainerFromRedis(commNoContainerKey))) {
            String startNo = queryStartNum(module.getCode(), module.getCode(), step);
            //向redis中放入序列号
            Map<String,List<Integer>> map = getContainerFromRedis(commNoContainerKey);
            setData(module.getCode(), Integer.parseInt(startNo), map);
            setContainerToRedis(commNoContainerKey,map);
        }
        //从redis中获取序列号
        Map<String,List<Integer>> map = getContainerFromRedis(commNoContainerKey);
        String serialNo = getNoFromData(module.getCode(), map).toString();
        setContainerToRedis(commNoContainerKey,map);
        return serialNo;
    }


    /**
     * 查询模块业务主键开始值
     *
     * @param moduleName
     * @return
     */
    private String queryStartNum(String moduleName, String serialType, Integer step) throws
            SerialException {
        try {
            return getSerialInfoService().queryCommSerialInfoByModuleName(moduleName, step, serialType);
        } catch (BaseErrorException e) {
            throw new SerialException("1", "获取业务主键异常，请查询SerailInfoT表中，是否配置有module为COMM的配置项" + e.getMessage());
        }
    }

}
