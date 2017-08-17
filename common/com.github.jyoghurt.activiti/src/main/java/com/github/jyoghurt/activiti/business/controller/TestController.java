package com.github.jyoghurt.activiti.business.controller;

import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2016/1/11.
 */
@RestController
@LogContent(module = "测试activiti")
@RequestMapping("/test")
public class TestController extends BaseController {
    @Autowired
    private ProcessEngine processEngine;

    @LogContent("syncUser")
    @RequestMapping(value = "/testSyncUser", method = RequestMethod.GET)
    public HttpResultEntity build(String userId)   {
        User user = processEngine.getIdentityService().newUser(userId);
        processEngine.getIdentityService().saveUser(user);
        return getSuccessResult();
    }

    @LogContent("syncUser")
    @RequestMapping(value = "/editVarsUtf8", method = RequestMethod.GET)
    public HttpResultEntity editVarsUtf8(String userId)   {
        TaskQuery taskQuery = processEngine.getTaskService()
                .createTaskQuery().processDefinitionKey("Administration").includeProcessVariables();
        List<Task> tasks = taskQuery.list();
        for (Task task : tasks) {
            Object o = task.getProcessVariables().get(task.getTaskDefinitionKey());
            if (null == o) {
                continue;
            }
            convertVars(task.getTaskDefinitionKey(),(Map) o);
            processEngine.getTaskService().setVariable(task.getId(), task.getTaskDefinitionKey(),o);
        }
        return getSuccessResult();
    }

    private void convertVars(String taskKey,Map map) {
        if (null != map.get("compontentTitle")) {
            switch (taskKey) {
                case "step2":
                    map.put("compontentTitle", "待财务审核");
                    break;
                case "step3":
                    map.put("compontentTitle", "待客服审核");
                    break;
                case "step4":
                    map.put("compontentTitle", "待业务审核");
                    break;
                case "step5":
                    map.put("compontentTitle", "待补充车牌");
                    break;
                case "step6":
                    map.put("compontentTitle", "待修改-财务驳回");
                    break;
                case "step7":
                    map.put("compontentTitle", "待修改-客服驳回");
                    break;
                case "step8":
                    map.put("compontentTitle", "待修改-业务驳回");
                    break;
                case "step9":
                    map.put("compontentTitle", "待车牌审核");
                    break;
                case "step10":
                    map.put("compontentTitle", "待修改-车牌驳回");
                    break;
                case "step12":
                    map.put("compontentTitle", "待驴鱼财务审核");
                    break;
                case "step13":
                    map.put("compontentTitle", "待修改-驴鱼驳回");
                    break;
            }
        }
    }
}
