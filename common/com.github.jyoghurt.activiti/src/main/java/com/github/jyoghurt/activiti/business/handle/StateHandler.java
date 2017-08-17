package com.github.jyoghurt.activiti.business.handle;

/**
 * user:dell
 * date: 2016/9/19.
 */

import com.github.jyoghurt.activiti.business.constants.ActivitiConstants;
import com.github.jyoghurt.activiti.business.dao.MainFormMapper;
import com.github.jyoghurt.activiti.business.vo.MainFormUpdateVo;
import com.github.jyoghurt.core.exception.UtilException;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 状态处理
 * 前处理  处理主表流程状态
 */
public class StateHandler implements TaskListener {
    private static Logger logger = LoggerFactory.getLogger(StateHandler.class);
    /**
     * 用户
     */
    private FixedValue state;

    public FixedValue getState() {
        return state;
    }

    public void setState(FixedValue state) {
        this.state = state;
    }

    @Autowired
    private MainFormMapper mainFormMapper;

    @Override
    public void notify(DelegateTask delegateTask) {
        //获取当前步骤状态名称
        String stepState = null == state ? null : state.getValue(null).toString();
        if (StringUtils.isEmpty(stepState)) {
            logger.error("状态处理中未配置当前步骤状态,流程定义Id:{},步骤名称:{}",
                    delegateTask.getProcessDefinitionId(), delegateTask.getName());
            return;
        }
        //获取主表className
        String mainFormClassName = null == delegateTask.getVariable(ActivitiConstants.mainFormClassName) ?
                null : delegateTask.getVariable(ActivitiConstants.mainFormClassName).toString();
        //获取mainFormId
        String mainFormId = null == delegateTask.getVariable(ActivitiConstants.BaseFormId) ?
                null : delegateTask.getVariable(ActivitiConstants.BaseFormId).toString();
        //获取mainFormName
        String mainFormIdName = null == delegateTask.getVariable(ActivitiConstants.mainFormIdName) ?
                null : delegateTask.getVariable(ActivitiConstants.mainFormIdName).toString();
        if (StringUtils.isEmpty(mainFormClassName)) {
            logger.error("未找到主表实体名称");
            return;
        }
        if (StringUtils.isEmpty(mainFormId)) {
            logger.error("未找到主表Id");
            return;
        }
        if (StringUtils.isEmpty(mainFormIdName)) {
            logger.error("未找到主表Id名称");
            return;
        }
        try {
            //获取主表实体对应的表名
            String tableName = JPAHandler.findTableName(mainFormClassName);
            MainFormUpdateVo mainFormUpdateVo = new MainFormUpdateVo();
            mainFormUpdateVo.setMainFormId(mainFormId);
            mainFormUpdateVo.setMainFormTableName(tableName);
            mainFormUpdateVo.setUpdateColumnName("state");
            mainFormUpdateVo.setUpdateColumnValue(stepState);
            mainFormUpdateVo.setMainFormIdName(mainFormIdName);
            mainFormMapper= (MainFormMapper)SpringContextUtils.getBean("mainFormMapper");
            mainFormMapper.updateMainFormState(mainFormUpdateVo);
        } catch (UtilException e) {
            logger.error("根据业务实体名称未找到映射表名称:{}", mainFormClassName, e);
            return;
        }
    }
}
