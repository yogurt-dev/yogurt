package com.github.jyoghurt.activiti.business.module;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dell on 2016/1/11.
 */
public class ProcessInstanceMsg {
    private String processDefinitionKey;
    private Map<String, Object> relevanceData=new HashMap<>();
    private String activitiId;

    public String getActivitiId() {
        return activitiId;
    }

    public void setActivitiId(String activitiId) {
        this.activitiId = activitiId;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public Map<String, Object> getRelevanceData() {
        return relevanceData;
    }

    public void setRelevanceData(Map<String, Object> relevanceData) {
        this.relevanceData = relevanceData;
    }

}
