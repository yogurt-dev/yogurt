package com.github.jyoghurt.activiti.business.convert;

import com.github.jyoghurt.activiti.business.vo.WorkFlowToDoVo;
import org.activiti.engine.task.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtwu on 2016/1/12.
 */
public class ActivitiConvert {

    public static List<WorkFlowToDoVo> convert(List<Task> tasks) {
        List<WorkFlowToDoVo> voList=new ArrayList<>();
        for(Task task:tasks){
            WorkFlowToDoVo workFlowToDoVo=new WorkFlowToDoVo();
            workFlowToDoVo.setTaskId(task.getId());
            workFlowToDoVo.setAssignee(task.getAssignee());
            workFlowToDoVo.setProcInsId(task.getProcessInstanceId());
            workFlowToDoVo.setProcessVariables(task.getProcessVariables());
            workFlowToDoVo.setTaskLocalVariables(task.getTaskLocalVariables());
            workFlowToDoVo.setCreateTime(task.getCreateTime());
            voList.add(workFlowToDoVo);
        }
        return voList;
    }
}
