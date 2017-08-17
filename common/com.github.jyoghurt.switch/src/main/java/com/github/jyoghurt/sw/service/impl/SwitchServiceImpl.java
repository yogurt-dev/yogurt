package com.github.jyoghurt.sw.service.impl;

import com.github.jyoghurt.sw.dao.SwitchMapper;
import com.github.jyoghurt.sw.domain.SwitchT;
import com.github.jyoghurt.sw.service.SwitchService;
import com.github.jyoghurt.sw.utils.SwitchUtils;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

@Service("switchService")
public class SwitchServiceImpl extends ServiceSupport<SwitchT, SwitchMapper> implements SwitchService {
    @Autowired
    private SwitchMapper switchMapper;

    @Override
    public SwitchMapper getMapper() {
        return switchMapper;
    }

    @Override
    public void logicDelete(Serializable id) {
        getMapper().logicDelete(SwitchT.class, id);
    }

    @Override
    public SwitchT find(Serializable id) {
        return getMapper().selectById(SwitchT.class, id);
    }

    @Cacheable(value = "switch", key = "#root.targetClass + #root.methodName+#switchGroupKey")
    public boolean switchIsOpenBySwitchGroupKey(String switchGroupKey) {
        List<SwitchT> list = findAll(new SwitchT().setSwitchGroupKey(switchGroupKey), new QueryHandle().addWhereSql("switchStatus>=#{data.switchStatus1} and availableTime<=now() ")
                .addExpandData("switchStatus1", SwitchUtils.getEnvCode()));
        return !CollectionUtils.isEmpty(list);
    }

    @Override
    @CacheEvict(value = "switch", allEntries = true)
    public void cacheEvict() {

    }
}
