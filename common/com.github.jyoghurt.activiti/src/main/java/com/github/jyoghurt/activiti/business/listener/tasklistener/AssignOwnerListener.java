package com.github.jyoghurt.activiti.business.listener.tasklistener;

import com.github.jyoghurt.activiti.business.constants.ActivitiConstants;
import com.github.jyoghurt.activiti.business.utils.ParseOwnerUtil;
import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import com.github.jyoghurt.vertx.handle.VertxHandler;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import io.vertx.core.json.JsonObject;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2016/1/3.
 */
public class AssignOwnerListener implements TaskListener {
    private static Logger logger = LoggerFactory.getLogger(AssignOwnerListener.class);
    /**
     * 用户
     */
    private FixedValue owner;
    /**
     * 角色Id
     */
    private FixedValue roleId;
    /**
     * 部门Id
     */
    private FixedValue unitId;
    /**
     * 资源
     */
    private FixedValue resource;
    /**
     * 相关数据区变量
     */
    private FixedValue variable;
    /**
     * 不包含相关数据区变量
     */
    private FixedValue excludeVariable;
    /**
     * 满足筛选条件的待办人列表
     */
    private List<String> userIds = new ArrayList<>();

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public FixedValue getResource() {
        return resource;
    }

    public void setResource(FixedValue resource) {
        this.resource = resource;
    }

    public FixedValue getRoleId() {
        return roleId;
    }

    public void setRoleId(FixedValue roleId) {
        this.roleId = roleId;
    }

    public FixedValue getUnitId() {
        return unitId;
    }

    public void setUnitId(FixedValue unitId) {
        this.unitId = unitId;
    }

    public FixedValue getVariable() {
        return variable;
    }

    public void setVariable(FixedValue variable) {
        this.variable = variable;
    }

    public FixedValue getOwner() {
        return owner;
    }

    public void setOwner(FixedValue owner) {
        this.owner = owner;
    }

    public FixedValue getExcludeVariable() {
        return excludeVariable;
    }

    public void setExcludeVariable(FixedValue excludeVariable) {
        this.excludeVariable = excludeVariable;
    }

    public void notify(DelegateTask var1) {

        //相关数据区 若有此配置直接取值 将待办发给此值的人  多用于流程启动者配置
        String variableKey = variable == null ? null : variable.getValue(var1) == null ? null : variable.getValue(var1).toString();
        //角色
        String role = roleId == null ? null : roleId.getValue(var1) == null ? null : roleId.getValue(var1).toString();
        //部门
        String unit = unitId == null ? null : unitId.getValue(var1) == null ? null : unitId.getValue(null).toString();
        //人员包含的资源Id
        String ownerResourceId = null == var1.getVariable("ownerResourceId") ? null : var1.getVariable("ownerResourceId").toString();
        //资源
        String resourceKey = resource == null ? null : resource.getValue(var1) == null ? null : resource.getValue(var1).toString();
        //指定人
        String assignee = owner == null ? null : owner.getValue(var1) == null ? null : owner.getValue(var1).toString();
        //不包含的相关数据区
        String excludeVariableKey = excludeVariable == null ? null : excludeVariable.getValue(var1) == null ? null : excludeVariable.getValue(var1).toString();
        SecurityUserTService securityUserTService = (SecurityUserTService) SpringContextUtils.getBean("securityUserTService");

        //若取相关数据区的值 则与角色部门不相关直接返回
        if (StringUtils.isNotEmpty(variableKey)) {
            if (var1.getVariable(variableKey) == null) {
                logger.error("taskId为：" + var1.getId() + "未配置人员规则：" + variableKey);
            }
            if (StringUtils.isNotEmpty(excludeVariableKey) && var1.getVariable(excludeVariableKey) == null) {
                logger.error("taskId为：" + var1.getId() + "相关数据区无法查出：" + excludeVariableKey);
            }
            if (StringUtils.isNotEmpty(excludeVariableKey) && var1.getVariable(excludeVariableKey).toString().equals(var1.getVariable(variableKey).toString())) {
                return;
            }
            var1.addCandidateUser(var1.getVariable(variableKey).toString());
            List<String> ids = new ArrayList<>();
            ids.add(var1.getVariable(variableKey).toString());
            publishTodo(var1, ids);
            return;
        }
        //若角色或部门筛选 则调用安全接口查出满足条件的人员//
        if ((StringUtils.isNotEmpty(role) || StringUtils.isNotEmpty(unit)) && StringUtils.isEmpty(ownerResourceId)) {
            try {
                this.setUserIds(ParseOwnerUtil.getUserIds(securityUserTService.queryUserByRole(ParseOwnerUtil
                        .getValue(unit, var1), ParseOwnerUtil.getValue(role, var1))));
            } catch (BaseErrorException e) {
                logger.error("taskId为" + var1.getId() + "添加待办人错误", e);
            }
        }
        if (StringUtils.isNotEmpty(role) && StringUtils.isNotEmpty(ownerResourceId)) {
            try {
                this.setUserIds(ParseOwnerUtil.getUserIds(securityUserTService.queryUsersByRoleIdAndResourceId(ParseOwnerUtil.getValue(role, var1),
                        ownerResourceId)));
                var1.removeVariable("ownerResourceId");
            } catch (BaseErrorException e) {
                logger.error("taskId为" + var1.getId() + "添加待办人错误", e);
            }
        }
        //若需要根据资源筛选 则调用安全接口查出满足资源条件的人
        if (StringUtils.isNotEmpty(resourceKey)) {
            switch (ParseOwnerUtil.getOperator(resourceKey)) {
                case AND:
                    setUserIds(securityUserTService.filterUserByResourceId(getUserIds(), ParseOwnerUtil
                            .getValue(resourceKey, var1)));
                    break;
                case OR:
                    getUserIds().addAll(securityUserTService.queryUserByResourceId(ParseOwnerUtil.getValue
                            (resourceKey, var1)));
                    break;
            }
        }
        //是否直接指定了人员
        if (assignee != null) {
            switch (ParseOwnerUtil.getOperator(assignee)) {
                case AND:
                    var1.addCandidateUser(assignee);
                    List<String> ids = new ArrayList<>();
                    ids.add(var1.getVariable(variableKey).toString());
                    publishTodo(var1, ids);
                    return;
                case OR:
                    getUserIds().add(assignee);
                    break;
            }

        }
        if (StringUtils.isEmpty(excludeVariableKey)) {
            var1.addCandidateUsers(getUserIds());
            publishTodo(var1, getUserIds());
            return;
        }
        //排除人员 从相关数据区中获取
        if (var1.getVariable(excludeVariableKey) == null) {
            logger.error("taskId为：" + var1.getId() + "相关数据区无法查出：" + excludeVariableKey);
        }
        for (int i = 0; i < userIds.size(); i++) {
            if (userIds.get(i).equals(var1.getVariable(excludeVariableKey).toString())) {
                userIds.remove(i);
                i--;
            }
        }
        var1.addCandidateUsers(getUserIds());
        publishTodo(var1, getUserIds());
    }

    private void publishTodo(DelegateTask var1, List<String> userIds) {
        JsonObject link = new JsonObject();
        for (String userId : userIds) {
            link.put(userId, userId);
        }
        JsonObject message = new JsonObject();
        String[] vars = var1.getProcessDefinitionId().split(":");
        Object publish = var1.getVariable(ActivitiConstants.PUBLISH_KEY);
        message.put("type", vars[0]);
        message.put("method", "PLUS");
        message.put("authority", link);
        if (null != publish) {
            message.put("type", publish.toString());
        }
        VertxHandler.publish("todo", message);
    }

}
