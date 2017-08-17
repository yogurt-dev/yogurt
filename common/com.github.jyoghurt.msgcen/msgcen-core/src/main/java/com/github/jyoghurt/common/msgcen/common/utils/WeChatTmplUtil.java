package com.github.jyoghurt.common.msgcen.common.utils;


import com.github.jyoghurt.common.msgcen.common.enums.LvEnvironmentConfig;
import com.github.jyoghurt.common.msgcen.common.enums.WechatTmplConfig;
import com.github.jyoghurt.dataDict.domain.DataDictValue;
import com.github.jyoghurt.dataDict.service.DataDictUtils;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * user:dell
 * date: 2016/7/18.
 */

public class WeChatTmplUtil {
    private static Logger logger = LoggerFactory.getLogger(WeChatTmplUtil.class);

    public static String getTmplDictValueCode(WechatTmplConfig wechatTmplConfig) {
        LvEnvironmentConfig lvEnvironmentConfig = null;
        String env = SpringContextUtils.getProperty("environmentName");
        try {
            lvEnvironmentConfig = LvEnvironmentConfig.valueOf(env);
        } catch (Exception e) {
            logger.error("驴鱼环境配置枚举转换异常,转换枚举LvEnvironmentConfig，转换参数:{}", env, e);
        }
        if (null == wechatTmplConfig) {
            logger.error("模板配置参数为空");
        }
        if (LvEnvironmentConfig.autotest == lvEnvironmentConfig) {
            lvEnvironmentConfig = LvEnvironmentConfig.develop_continous;
        }
        return StringUtils.join(lvEnvironmentConfig.name(), "_", wechatTmplConfig.name());
    }

    public static String getTmplId(WechatTmplConfig wechatTmplConfig) {
        DataDictValue dataDictValue = DataDictUtils.getDataDictValue("WechatTmplConfig", getTmplDictValueCode(wechatTmplConfig));
        return dataDictValue.getDictValueName();
    }
}
