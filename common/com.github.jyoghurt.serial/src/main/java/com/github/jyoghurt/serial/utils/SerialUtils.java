package com.github.jyoghurt.serial.utils;


import com.github.jyoghurt.core.utils.SpringContextUtils;
import com.github.jyoghurt.serial.enums.Module;
import com.github.jyoghurt.serial.enums.SerialType;
import com.github.jyoghurt.serial.exception.SerialException;
import com.github.jyoghurt.serial.handler.CommonalityRule;
import com.github.jyoghurt.serial.handler.ModuleDateRule;
import com.github.jyoghurt.serial.handler.ModulePrivateRule;
import com.github.jyoghurt.serial.handler.SerialHanlder;
import com.github.jyoghurt.serial.service.SerialInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Project: 驴鱼社区
 * @Package: com.df.community.base.serial.utils
 * @Description: 生成业务流水
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-03-15 14:27 创建
 * @date: 2016-11-11 重构，采用策略模式
 */
public class SerialUtils {

    private static SerialHanlder serialHanlder;
    private static SerialInfoService serialInfoService;
    private static CommonalityRule commonalityRule;
    private static ModuleDateRule moduleDateRule;
    private static ModulePrivateRule modulePrivateRule;

    private static Logger logger = LoggerFactory.getLogger(SerialUtils.class);

    static {
        serialHanlder = (SerialHanlder) SpringContextUtils.getBean("serialHanlder");

        serialInfoService = (SerialInfoService) SpringContextUtils.getBean("serialInfoService");

        commonalityRule = (CommonalityRule) SpringContextUtils.getBean("commonalityRule");
        moduleDateRule = (ModuleDateRule) SpringContextUtils.getBean("moduleDateRule");
        modulePrivateRule = (ModulePrivateRule) SpringContextUtils.getBean("modulePrivateRule");

        commonalityRule.setSerialInfoService(serialInfoService);
        moduleDateRule.setSerialInfoService(serialInfoService);
        modulePrivateRule.setSerialInfoService(serialInfoService);
    }

    /**
     * 获取业务流水号
     *
     * @param module     模块名称
     * @param serialType 获取流水号类型
     */
    public synchronized static String fetchSerialNumber(Module module, SerialType serialType) throws SerialException {
        switch(serialType){
            case ADVANCED:
                serialHanlder.setSerialBaseRule(moduleDateRule);
                break;
            case NORMAL:
                serialHanlder.setSerialBaseRule(commonalityRule);
                break;
            default:
                serialHanlder.setSerialBaseRule(modulePrivateRule);
                break;
        }
        String serialNo = serialHanlder.fetchSerialNumber(module,serialType);
        logger.debug("★★★★★★★生成序列号 begin★★★★★★★");
        logger.debug("生成模块："+module.getCode());
        logger.debug("生成方式："+serialType.getCode());
        logger.debug("生成结果："+serialNo);
        logger.debug("★★★★★★★生成序列号 end★★★★★★★");
        return serialNo;
    }
}
