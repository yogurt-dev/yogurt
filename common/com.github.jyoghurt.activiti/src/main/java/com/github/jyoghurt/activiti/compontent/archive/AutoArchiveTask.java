package com.github.jyoghurt.activiti.compontent.archive;

import com.github.jyoghurt.activiti.business.constants.ActivitiConstants;
import com.github.jyoghurt.activiti.business.flowEntity.FlowMainFormEntity;
import com.github.jyoghurt.activiti.business.service.WorkFlowService;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by dell on 2016/1/14.
 */
public class AutoArchiveTask implements JavaDelegate {
    private static Logger logger = LoggerFactory.getLogger(AutoArchiveTask.class);

    @Override
    public void execute(DelegateExecution execution)   {
        logger.info("********************正在归档********************");
        WorkFlowService workFlowService = (WorkFlowService) SpringContextUtils.getBean("administrationFlowService");//todo  相关数据区
        FlowMainFormEntity flowMainFormEntity = (FlowMainFormEntity) workFlowService.find(execution.getVariable(ActivitiConstants.BaseFormId).toString());
        flowMainFormEntity.setState(ActivitiConstants.ArchiveCH);
        flowMainFormEntity.setArchiveTime(new Date());
        workFlowService.update(flowMainFormEntity);
        execution.setVariable(ActivitiConstants.Archive,ActivitiConstants.ArchiveCH);
        logger.info("********************归档结束********************");
    }

    private static String toLowerCaseFirstOne(String var) {
        if (Character.isLowerCase(var.charAt(0)))
            return var;
        else
            return (new StringBuilder()).append(Character.toLowerCase(var.charAt(0))).append(var.substring(1)).toString();
    }
}
