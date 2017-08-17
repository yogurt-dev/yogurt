package com.github.jyoghurt.serviceLog.service.impl;


import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import com.github.jyoghurt.serviceLog.dao.ServiceLogMapper;
import com.github.jyoghurt.serviceLog.domain.ServiceLogT;
import com.github.jyoghurt.serviceLog.enums.ServiceTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.jyoghurt.serviceLog.service.ServiceLogService;

import java.io.Serializable;

@Service("serviceLogService")
public class ServiceLogServiceImpl extends ServiceSupport<ServiceLogT, ServiceLogMapper> implements ServiceLogService {
    private static Logger logger = LoggerFactory.getLogger(ServiceLogServiceImpl.class);
    @Autowired
    private ServiceLogMapper serviceLogMapper;

    @Override
    public ServiceLogMapper getMapper() {
        return serviceLogMapper;
    }

    @Override
    public void logicDelete(Serializable id)  {
        getMapper().logicDelete(ServiceLogT.class, id);
    }

    @Override
    public ServiceLogT find(Serializable id)  {
        return getMapper().selectById(ServiceLogT.class, id);
    }

    public void recordServiceLog(ServiceTypeEnum serviceType, String serviceName, String moduleName, String serviceContent, Boolean callType)  {
        if (null == serviceType || null == callType || StringUtils.isEmpty(serviceName) || StringUtils.isEmpty(moduleName)) {
            logger.error("记录接口缺少参数");
            throw new BaseErrorException();
        }
        ServiceLogT serviceLogT = new ServiceLogT();
        serviceLogT.setServiceType(serviceType).setServiceName(serviceName).setModuleName(moduleName)
                .setServiceContent(serviceContent).setCallType(callType);
        this.save(serviceLogT);
    }
}
