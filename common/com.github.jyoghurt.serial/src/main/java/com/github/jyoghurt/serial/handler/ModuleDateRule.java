package com.github.jyoghurt.serial.handler;


import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.serial.enums.Module;
import com.github.jyoghurt.serial.enums.SerialType;
import com.github.jyoghurt.serial.exception.SerialException;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @Project:
 * @Package: com.df.community.base.serial.handler
 * @Description: 模块日期策略  如 SHOP-C-2016-10-31-0201 每天从1 开始
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-11-11 10:12
 */
@Component("moduleDateRule")
public class ModuleDateRule extends SerialBaseRule{

    private static SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");

    @Override
    public String generateSerialNo(Module module, SerialType serialType) {
        String moduleName = module.getCode(),now = sdf.format(new Date(System.currentTimeMillis()));
        checkContrainerInRedis(moduleDateNoContainer);
        //检查内存中是否有可用数据
        if (!checkExistSerialNoInMemery(moduleName,now,getContainerFromRedis(moduleDateNoContainer))) {
            //更新数据库，与内存同步。内存中的序列号=数据库中的记录号
            String startNo = queryStartNum(moduleName, serialType, step);
            //将序列存储到redis中
            Map<String,List<Integer>> map = getContainerFromRedis(moduleDateNoContainer);
            setData(moduleName + now, Integer.parseInt(startNo), map);
            setContainerToRedis(moduleDateNoContainer,map);
        }

        //从redis中获取序列号
        Map<String,List<Integer>> map = getContainerFromRedis(moduleDateNoContainer);
        String serialNo = serialNoFormat(moduleName, now, leftZeroPadding(getNoFromData(moduleName +
                now, map)));
        setContainerToRedis(moduleDateNoContainer,map);
        return  serialNo;
    }




    /**
     * 查询模块业务主键开始值
     *
     * @param moduleName
     * @return
     */
    private String queryStartNum(String moduleName, SerialType serialType, Integer step) throws SerialException {
        try {
            return getSerialInfoService().querySerialInfoByModuleName(moduleName, step, serialType);
        } catch (BaseErrorException e) {
            throw new SerialException("1", "获取业务主键异常：" + e.getMessage());
        }
    }

    /**
     * 生成业务流水号
     *
     * @param modelName
     * @param date
     * @param serailNo
     * @return
     */
    private String serialNoFormat(String modelName, String date, String serailNo) {
        return modelName.concat("-").concat(date).concat("-").concat(serailNo);
    }
}
