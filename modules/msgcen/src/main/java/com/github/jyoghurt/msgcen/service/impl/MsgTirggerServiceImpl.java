package com.github.jyoghurt.msgcen.service.impl;

import com.github.jyoghurt.msgcen.dao.MsgTirggerMapper;
import com.github.jyoghurt.msgcen.domain.MsgTirggerT;
import com.github.jyoghurt.msgcen.service.MsgTirggerService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("msgTirggerService")
public class MsgTirggerServiceImpl extends ServiceSupport<MsgTirggerT, MsgTirggerMapper> implements MsgTirggerService {
	@Autowired
    private MsgTirggerMapper msgTirggerMapper;

    @Override
	public MsgTirggerMapper getMapper() {
		return msgTirggerMapper;
	}

    @Override
    public void logicDelete(Serializable id){
        getMapper().logicDelete(MsgTirggerT.class, id);
    }

    @Override
    public MsgTirggerT find(Serializable id){
        return getMapper().selectById(MsgTirggerT.class,id);
    }
}
