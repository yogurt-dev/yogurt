package com.github.jyoghurt.serviceLog.service;


import com.github.jyoghurt.core.service.BaseService;
import com.github.jyoghurt.serviceLog.domain.ServiceLogT;
import com.github.jyoghurt.serviceLog.enums.ServiceTypeEnum;

/**
 * 订单接口服务层
 */
public interface ServiceLogService extends BaseService<ServiceLogT> {
    /**
     * 记录接口日志
     *
     * @param serviceType    接口类型  ServiceTypeEnum枚举
     * @param serviceName    接口名称
     * @param serviceContent 模块名称
     * @param callType       请求类型  fasle 0：调用|true 1：接收

     */
    void recordServiceLog(ServiceTypeEnum serviceType, String serviceName, String moduleName, String serviceContent, Boolean callType) ;

}
