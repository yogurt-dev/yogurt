package com.github.jyoghurt.activiti.business.constants;


/**
 * Created by dell on 2016/1/7.
 */
public class ActivitiConstants {
    /**
     * activiti 基本属性
     */
    /**
     * 表单主键
     */
    public static String FormKey = "FormKey";
    /**
     * 表单属性
     */
    public static String FormProperties = "FormProperties";
    /**
     * activiti流程事件
     */
    /**
     * 流程启动
     */
    public static String startEvent = "startEvent";
    /**
     * 人工活动
     */
    public static String userTask = "userTask";
    /**
     * 走线环节
     */
    public static String parallelGateway = "parallelGateway";
    /**
     * 包含环节
     */
    public static String inclusiveGateway = "inclusiveGateway";
    /**
     * 排他环节
     */
    public static String exclusiveGateway = "exclusiveGateway";
    /**
     * 任务开始（创建实例之后）
     */
    public static String task_created = "TASK_CREATED";
    /**
     * 归档
     */
    public static String Archive = "Archive";

    public static String ArchiveCH = "已归档";
    /**
     * 流程结束
     */
    public static String endEvent = "endEvent";
    /**
     * 相关数据区常用变量
     */
    /**
     * 流程启动者
     */
    public static String startPerson = "startPerson";
    public static String startPersonName = "startPersonName";
    /**
     * 工单编号
     */
    public static String workOrderNumber = "workOrderNumber";
    /**
     * 工单主题
     */
    public static String subject = "subject";
    /**
     * 工单状态
     */
    public static String state = "state";
    /**
     * 业务主键Id
     */
    public static String businessId = "businessId";
    /**
     * 业务实体
     */
    public static String businessClass = "businessClass";
    /**
     * 前台组件Id
     */
    public static String compontentId = "compontentId";
    /**
     * 被审核组件id
     */
    public static String auditedId = "auditedId";
    /**
     * 被审核实体名称
     */
    public static String auditedClass = "auditedClass";
    /**
     * 组件类型
     */
    /**
     * Form型
     */
    /**
     * 包含编辑加展示form组件
     */
    public static String Form = "form";
    /**
     * 编辑form组件
     */
    public static String editForm = "editForm";
    /**
     * 展示form组件
     */
    public static String showForm = "showForm";
    /**
     * 组件标题
     */
    public static String compontentTitle = "compontentTitle";
    /**
     * link型
     */
    public static String Link = "link";
    /**
     * button型
     */
    public static String Button = "button";

    /**
     * 主表id
     */
    public static String BaseFormId = "BaseFormId";
    /**
     * 主表名称
     */
    public static String mainFormClassName = "mainFormClassName";
    /**
     * 主表id名称
     */
    public static String mainFormIdName = "mainFormIdName";
    /**
     * 按钮规则
     */
    public static String Btn = "Btn";
    /**
     * FormPropertis常用变量
     */
    /**
     * linkType
     */
    public static String linkType = "linkType";
    /**
     * 活动定义Id
     */
    public static String activitiId = "activitiId";
    /**
     * spring相关
     */
    /**
     * 审核service实现
     */
    public static String AuditImpl = "WorkflowAuditImpl";
    /**
     * 流程消息key
     */
    public static String PUBLISH_KEY = "publish_key";
}
