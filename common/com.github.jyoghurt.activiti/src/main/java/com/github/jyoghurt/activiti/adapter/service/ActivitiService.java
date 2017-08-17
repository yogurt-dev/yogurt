package com.github.jyoghurt.activiti.adapter.service;

import com.github.jyoghurt.activiti.business.module.FormProperties;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import java.io.UnsupportedEncodingException;

/**
 * Created by dell on 2015/12/30.
 */
public interface ActivitiService {
     ActivityImpl findActivitiImpl(String taskId, String activityId)  ;
     /**
      * 根据当前任务的表单数据获取当前任务的表单属性
      *
      * @param taskFormData 当前任务的表单数据
      * @return FormProperties   表单属性
      */
     FormProperties findFormProperties(TaskFormData taskFormData) throws UnsupportedEncodingException;
     FormProperties findFormProperties(StartFormData startFormData) throws UnsupportedEncodingException;
}
