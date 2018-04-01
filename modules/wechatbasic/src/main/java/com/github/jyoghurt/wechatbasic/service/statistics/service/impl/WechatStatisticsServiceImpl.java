package com.github.jyoghurt.wechatbasic.service.statistics.service.impl;


import com.github.jyoghurt.core.service.impl.ServiceSupport;
import com.github.jyoghurt.wechatbasic.service.statistics.dao.WechatStatisticsMapper;
import com.github.jyoghurt.wechatbasic.service.statistics.domain.WechatStatisticsT;
import com.github.jyoghurt.wechatbasic.service.statistics.service.WechatStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("wechatStatisticsService")
public class WechatStatisticsServiceImpl extends ServiceSupport<WechatStatisticsT, WechatStatisticsMapper> implements WechatStatisticsService {
	@Autowired
    private WechatStatisticsMapper wechatStatisticsMapper;

    @Override
	public WechatStatisticsMapper getMapper() {
		return wechatStatisticsMapper;
	}

    @Override
    public void logicDelete(Serializable id)  {
        getMapper().logicDelete(WechatStatisticsT.class, id);
    }

    @Override
    public WechatStatisticsT find(Serializable id)  {
        return getMapper().selectById(WechatStatisticsT.class,id);
    }
}
