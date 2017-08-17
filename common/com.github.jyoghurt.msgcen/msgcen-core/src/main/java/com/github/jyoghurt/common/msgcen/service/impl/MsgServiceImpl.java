package com.github.jyoghurt.common.msgcen.service.impl;

import com.github.jyoghurt.common.msgcen.dao.MsgMapper;
import com.github.jyoghurt.common.msgcen.domain.MsgT;
import com.github.jyoghurt.common.msgcen.service.MsgService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("msgService")
public class MsgServiceImpl extends ServiceSupport<MsgT, MsgMapper> implements MsgService {
    @Autowired
    private MsgMapper msgMapper;

    @Override
    public MsgMapper getMapper() {
        return msgMapper;
    }

    @Override
    public void logicDelete(Serializable id) {
        getMapper().logicDelete(MsgT.class, id);
    }

    @Override
    public MsgT find(Serializable id) {
        return getMapper().selectById(MsgT.class, id);
    }
}
