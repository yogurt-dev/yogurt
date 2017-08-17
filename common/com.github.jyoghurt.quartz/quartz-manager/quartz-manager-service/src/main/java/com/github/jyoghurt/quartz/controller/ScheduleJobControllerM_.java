package com.github.jyoghurt.quartz.controller;


import com.github.jyoghurt.quartz.domain.ScheduleJob;
import com.github.jyoghurt.quartz.service.ScheduleJobService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * 周期任务控制器
 */
@RestController
@LogContent(module = "周期任务")
@RequestMapping("/scheduleJob")
public class ScheduleJobControllerM_ extends BaseController {


    /**
     * 周期任务服务类
     */
    @Resource
    private ScheduleJobService scheduleJobService;

    /**
     * 列出周期任务
     */
    @LogContent("同步周期任务")
    @RequestMapping(value = "/syncScheduleJobs", method = RequestMethod.GET)
    public HttpResultEntity<?> syncScheduleJobs(ScheduleJob scheduleJob, QueryHandle queryHandle)   {
        scheduleJobService.syncScheduleJobs();
        return getSuccessResult();
    }

    /**
     * 删除周期任务
     */
    @LogContent("删除周期任务")
    @RequestMapping(value = "/removeJob/{jobId}", method = RequestMethod.GET)
    public HttpResultEntity<?> removeJob(@PathVariable String jobId)   {
        scheduleJobService.removeScheduleJob(jobId);
        return getSuccessResult();
    }

    /**
     * 删除周期任务
     */
    @LogContent("删除周期任务")
    @RequestMapping(value = "/removeJob/{jobGroup}/{jobKey}", method = RequestMethod.GET)
    public HttpResultEntity<?> removeJob(@PathVariable String jobGroup, @PathVariable String jobKey)   {
        scheduleJobService.removeScheduleJob(jobGroup,jobKey);
        return getSuccessResult();
    }

    /**
     * 临时添加物流单
     */
    @LogContent("临时添加物流单")
    @RequestMapping(value = "/addLogistical", method = RequestMethod.GET)
    public HttpResultEntity<?> syncScheduleJobs()   {
        //添加5点30的物流单
        ScheduleJob scheduleJob1 = new ScheduleJob();
        scheduleJob1.setJobName("Logistical0530").setJobGroup("Logistical0530").setJobState(false)
                .setCronExpression("0 30 5 * * ?").setDescription("5点30物流单")
                .setClassName("com.df.community.supports.manager.scheduled.SupportsLogisticalDistributeOrderTimerScheduled")
                .setRollBackTheWrong(false);
        ScheduleJob scheduleJob2 = new ScheduleJob();
        scheduleJob2.setJobName("Logistical1130").setJobGroup("Logistical1130").setJobState(false)
                .setCronExpression("0 30 11 * * ?").setDescription("11点30物流单")
                .setClassName("com.df.community.supports.manager.scheduled.SupportsLogisticalDistributeOrderTimerScheduled")
                .setRollBackTheWrong(false);
        ScheduleJob scheduleJob3 = scheduleJobService.find("2").setCronExpression("0 0 16 * * ?");
        scheduleJobService.updateForSelective(scheduleJob3);
        scheduleJobService.save(scheduleJob1);
        scheduleJobService.save(scheduleJob2);
        scheduleJobService.syncScheduleJob(scheduleJob1);
        scheduleJobService.syncScheduleJob(scheduleJob2);
        scheduleJobService.syncScheduleJob(scheduleJob3);
        return getSuccessResult();
    }
}
