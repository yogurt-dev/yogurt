package com.github.jyoghurt.msgcen.service.impl;

import com.github.jyoghurt.msgcen.dao.MsgTmplMapper;
import com.github.jyoghurt.msgcen.domain.MsgTmplT;
import com.github.jyoghurt.msgcen.service.MsgTmplService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("msgTmplService")
public class MsgTmplServiceImpl extends ServiceSupport<MsgTmplT, MsgTmplMapper> implements MsgTmplService {
    @Autowired
    private MsgTmplMapper msgTmplMapper;

    @Override
    public MsgTmplMapper getMapper() {
        return msgTmplMapper;
    }

    @Override
    public void logicDelete(Serializable id) {
        getMapper().logicDelete(MsgTmplT.class, id);
    }

    @Override
    public MsgTmplT find(Serializable id) {
        return getMapper().selectById(MsgTmplT.class, id);
    }
}
