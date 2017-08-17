package com.github.jyoghurt.sw.utils;

import com.github.jyoghurt.sw.enums.SwitchEnvEnum;
import com.github.jyoghurt.sw.exception.SwitchException;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


public class SwitchUtils {

    private static Logger logger = LoggerFactory.getLogger(SwitchUtils.class);

    public static Integer getEnvCode() {
        String env = SpringContextUtils.getProperty("environmentName");
        logger.info("switch env:" + env);
        if (StringUtils.isEmpty(env)) {
            throw new SwitchException();
        }
        Integer envCode = SwitchEnvEnum.valueOf(env.toUpperCase()).getCode();
        logger.info("envCode:" + envCode);
        if (envCode == null) {
            throw new SwitchException();
        }
        return envCode;
    }
}
