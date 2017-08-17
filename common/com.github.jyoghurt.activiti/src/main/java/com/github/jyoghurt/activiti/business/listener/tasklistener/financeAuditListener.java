package com.github.jyoghurt.activiti.business.listener.tasklistener;

import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by dell on 2016/1/3.
 */
public class financeAuditListener implements TaskListener {
    private static Logger logger = LoggerFactory.getLogger(financeAuditListener.class);
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
     * 规则
     */
    private FixedValue rule;

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

    public FixedValue getRule() {
        return rule;
    }

    public void setRule(FixedValue rule) {
        this.rule = rule;
    }

    public FixedValue getOwner() {
        return owner;
    }

    public void setOwner(FixedValue owner) {
        this.owner = owner;
    }

    public void notify(DelegateTask var1) {
        String assignee = owner == null ? null : owner.getValue(null) == null ? null : owner.getValue(null).toString();
        String role = roleId == null ? null : roleId.getValue(null) == null ? null : roleId.getValue(null).toString();
        String unit = unitId == null ? null : unitId.getValue(null) == null ? null : unitId.getValue(null).toString();
        String ruleKey = rule == null ? null : rule.getValue(null) == null ? null : rule.getValue(null).toString();
        if (ruleKey != null) {
            if (var1.getVariable(ruleKey) == null) {
                logger.error("taskId为：" + var1.getId() + "未配置人员规则：" + ruleKey);
            }
            var1.setAssignee(var1.getVariable(ruleKey).toString());
        }
        if (assignee != null) {
            var1.setAssignee(assignee);
        }
        if (role != null || unit != null) {
            try {
                SecurityUserTService securityUserTService = (SecurityUserTService) SpringContextUtils.getBean("securityUserTService");
                List<SecurityUserT> userList = securityUserTService.queryUserByRole(unit, role);
                for (SecurityUserT securityUserT : userList) {
                    var1.addCandidateUser(securityUserT.getUserId());
                }
            } catch (BaseErrorException e) {
                logger.error("taskId为" + var1.getId() + "添加待办人错误", e);
            }
        }
    }

}
