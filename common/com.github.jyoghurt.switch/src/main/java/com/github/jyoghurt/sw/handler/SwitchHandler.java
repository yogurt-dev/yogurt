package com.github.jyoghurt.sw.handler;

import com.github.jyoghurt.sw.service.SwitchService;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SwitchHandler {

    private static Logger logger = LoggerFactory.getLogger(SwitchHandler.class);

    private static SwitchService switchService;

    private static SwitchService getSwitchService() {
        if (null == switchService) {
            switchService = (SwitchService) SpringContextUtils.getBean("switchService");
        }
        return switchService;
    }

    /**
     * 根据key判断是否开放功能，true是开，false是关.
     *
     * @param switchGroupKey switchGroupKey
     * @return boolean true是开，false是关
     */
    public static boolean switchIsOpenBySwitchGroupKey(String switchGroupKey) {
        return getSwitchService().switchIsOpenBySwitchGroupKey(switchGroupKey);
    }
}
