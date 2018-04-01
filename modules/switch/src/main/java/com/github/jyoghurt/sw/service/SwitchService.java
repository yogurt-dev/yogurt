package com.github.jyoghurt.sw.service;

import com.github.jyoghurt.sw.domain.SwitchT;
import com.github.jyoghurt.core.service.BaseService;

/**
 * 开关服务层
 */
public interface SwitchService extends BaseService<SwitchT> {

    /**
     * 根据key判断是否开放功能，true是开，false是关.
     *
     * @param switchGroupKey switchGroupKey
     * @return boolean true是开，false是关
     */
    boolean switchIsOpenBySwitchGroupKey(String switchGroupKey);

    /**
     * 清除开关缓存
     */
    void cacheEvict();
}
