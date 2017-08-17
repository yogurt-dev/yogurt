package com.github.jyoghurt.activiti.business.listener.localListener;

import com.github.jyoghurt.activiti.business.constants.ActivitiConstants;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiActivityEventImpl;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class FormPropertiesCopyListener implements ActivitiEventListener {
    private static Logger logger = LoggerFactory.getLogger(FormPropertiesCopyListener.class);
    @Override
    public void onEvent(ActivitiEvent activitiEvent) {
        if (ActivitiEventType.ACTIVITY_COMPLETED.equals(activitiEvent.getType())) {
            //根据执行id获得相关数据区
            Map<String, Object> vars = activitiEvent.getEngineServices().getRuntimeService().getVariables(activitiEvent.getExecutionId());
            //流程启动 将开始环节的formdata存于相关数据区
            if(ActivitiConstants.startEvent.equals(((ActivitiActivityEventImpl) activitiEvent).getActivityType())){
                StartFormData startFormData= activitiEvent.getEngineServices().getFormService().getStartFormData(activitiEvent.getProcessDefinitionId());
                //改变工单状态
                for (FormProperty formProperty : startFormData.getFormProperties()) {
                    if (ActivitiConstants.state.equals(formProperty.getId())) {
                        try {
                            vars.put(ActivitiConstants.state,new String(formProperty.getName().getBytes("gbk"),"utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            logger.error("变更工单状态异常");
                        }
                    }
                }
                activitiEvent.getEngineServices().getRuntimeService().setVariables(activitiEvent.getExecutionId(), vars);
            }

            Task task = activitiEvent.getEngineServices().getTaskService().createTaskQuery()
                    .executionId(activitiEvent.getExecutionId())
                    .singleResult();
            if (task != null && vars.get(task.getTaskDefinitionKey()) == null) {//若此任务中的formproperties未存于相关数据区，则保存
                TaskFormData taskFormData = activitiEvent.getEngineServices().getFormService().getTaskFormData(task.getId());
                for (FormProperty formProperty : taskFormData.getFormProperties()) {
                    if (ActivitiConstants.state.equals(formProperty.getId())) {
                        try {
                            vars.put(ActivitiConstants.state,new String(formProperty.getName().getBytes("gbk"),"utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            logger.error("变更工单状态异常");
                        }
                    }
                }
                activitiEvent.getEngineServices().getRuntimeService().setVariables(activitiEvent.getExecutionId(), vars);
            }
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
