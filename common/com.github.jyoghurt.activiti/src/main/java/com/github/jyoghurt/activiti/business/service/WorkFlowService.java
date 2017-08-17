package com.github.jyoghurt.activiti.business.service;

/**
 * Created by dell on 2016/1/4.
 */

import com.github.jyoghurt.activiti.business.exception.RepetitionSubmitException;
import com.github.jyoghurt.activiti.business.flowEntity.FlowEntity;
import com.github.jyoghurt.activiti.business.flowEntity.FlowMainFormEntity;
import com.github.jyoghurt.activiti.business.vo.WorkFlowTodoSearchVo;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.QueryResult;
import com.github.jyoghurt.core.service.BaseService;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 流程基础service 包含流程发起，提交，跳转，回退等基础方法
 */

public interface WorkFlowService extends BaseService {
    /**
     * 流程启动接口
     *
     * @param flowMainFormEntity 业务实体
     * @param securityUserT      当前操作人实体

     * @throws UnsupportedEncodingException
     */
    void startWorkFlow(FlowMainFormEntity flowMainFormEntity, SecurityUserT securityUserT) throws  UnsupportedEncodingException;

    /**
     * 流程启动接口
     *
     * @param flowMainFormEntity 业务实体
     * @param securityUserT      当前操作人实体
     * @param vars               相关数据区

     * @throws UnsupportedEncodingException
     */
    void startWorkFlow(FlowMainFormEntity flowMainFormEntity, SecurityUserT securityUserT, Map<String, Object> vars) throws  UnsupportedEncodingException;

    /**
     * 提交流程接口
     *
     * @param flowEntity 业务实体
     * @param taskId     代办id
     * @
     */
    void completeTask(FlowEntity flowEntity, String taskId) throws RepetitionSubmitException;

    /**
     * 提交流程接口
     *
     * @param flowEntity 业务实体
     * @param taskId     代办id
     * @param vars       需要保存的相关数据区
     * @
     */
    void completeTask(FlowEntity flowEntity, String taskId, Map<String, Object> vars) throws RepetitionSubmitException;

    /**
     * 查询待办信息
     *
     * @param workFlowTodoSearchVo 查询条件
     * @param queryHandle          分页信息
     * @return QueryResult  分页实体
     * @
     */
    QueryResult findToDo(WorkFlowTodoSearchVo workFlowTodoSearchVo, QueryHandle queryHandle)  ;

}
