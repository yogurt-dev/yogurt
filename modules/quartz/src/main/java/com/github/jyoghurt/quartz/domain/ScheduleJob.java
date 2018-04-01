package com.github.jyoghurt.quartz.domain;

import com.github.jyoghurt.core.domain.BaseEntity;

@javax.persistence.Table(name = "ScheduleJob")
public class ScheduleJob extends BaseEntity<ScheduleJob> {
    /**
     *
     */
    @javax.persistence.Id
    private String jobId;
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务分组
     */
    private String jobGroup;
    /**
     * 任务状态 是否启动任务
     */
    private Boolean jobState;
    /**
     * cron表达式
     */
    private String cronExpression;
    /**
     * 描述
     */
    private String description;
    /**
     * 任务执行时调用哪个类的方法 包名+类名，非spring管理对象
     */
    private String className;
    /**
     * spring 管理对象，使用BeanFactoryUtil获取bean
     */
    private String beanName;
    /**
     * 错误回滚
     */
    private Boolean rollBackTheWrong;

    public String getJobId() {
        return this.jobId;
    }

    public ScheduleJob setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getJobName() {
        return this.jobName;
    }

    public ScheduleJob setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public String getJobGroup() {
        return this.jobGroup;
    }

    public ScheduleJob setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
        return this;
    }

    public Boolean getJobState() {
        return this.jobState;
    }

    public ScheduleJob setJobState(Boolean jobState) {
        this.jobState = jobState;
        return this;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public ScheduleJob setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public ScheduleJob setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getClassName() {
        return this.className;
    }

    public ScheduleJob setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getBeanName() {
        return this.beanName;
    }

    public ScheduleJob setBeanName(String beanName) {
        this.beanName = beanName;
        return this;
    }

    public Boolean getRollBackTheWrong() {
        return rollBackTheWrong;
    }

    public void setRollBackTheWrong(Boolean rollBackTheWrong) {
        this.rollBackTheWrong = rollBackTheWrong;
    }

    @Override
    public String toString() {
        return "ScheduleJob{" +
                "jobId='" + jobId + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobGroup='" + jobGroup + '\'' +
                ", jobState=" + jobState +
                ", cronExpression='" + cronExpression + '\'' +
                ", description='" + description + '\'' +
                ", className='" + className + '\'' +
                ", beanName='" + beanName + '\'' +
                '}';
    }
}
