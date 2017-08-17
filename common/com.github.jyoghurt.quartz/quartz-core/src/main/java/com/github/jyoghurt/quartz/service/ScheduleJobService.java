package com.github.jyoghurt.quartz.service;


import com.github.jyoghurt.quartz.domain.ScheduleJob;
import com.github.jyoghurt.quartz.exception.QuartzException;
import com.github.jyoghurt.core.service.BaseService;

/**
 * 周期任务服务层
 */
public interface ScheduleJobService extends BaseService<ScheduleJob> {
    void scheduleStart() throws QuartzException;

    void syncScheduleJobs() throws QuartzException;

    void syncScheduleJob(ScheduleJob scheduleJob) throws QuartzException;

    void removeScheduleJob(String jobGroup, String jobName) throws QuartzException;

    void removeScheduleJob(String scheduleJobId) throws QuartzException;
}
