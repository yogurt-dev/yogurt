package com.github.jyoghurt.activiti.business.listener.localListener;

import com.github.jyoghurt.activiti.business.constants.ActivitiConstants;
import com.github.jyoghurt.vertx.handle.VertxHandler;
import io.vertx.core.json.JsonObject;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;

import java.util.List;

/**
 * user:DELL
 * date:2017/7/25.
 */
public class LocalTaskListener implements ActivitiEventListener {

    @Override
    public void onEvent(ActivitiEvent activitiEvent) {
        //根据ExecutionId获得task
        Task task = activitiEvent.getEngineServices().getTaskService().createTaskQuery().executionId(activitiEvent.getExecutionId()).singleResult();
        List<IdentityLink> links = activitiEvent.getEngineServices().getTaskService().getIdentityLinksForTask(task.getId());
        JsonObject link = new JsonObject();
        for (IdentityLink identityLink : links) {
            link.put(identityLink.getUserId(), identityLink.getType());
        }
        JsonObject message = new JsonObject();
        String[] vars = activitiEvent.getProcessDefinitionId().split(":");
        Object publish = activitiEvent.getEngineServices().getTaskService().getVariable(task.getId(), ActivitiConstants.PUBLISH_KEY);
        message.put("type", vars[0]);
        message.put("method", "SUBTRACT");
        message.put("authority", link);
        if (null != publish) {
            message.put("type", publish.toString());
        }
        VertxHandler.publish("todo", message);
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
