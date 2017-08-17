package com.github.jyoghurt.quartz.support;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * user:dell
 * date: 2016/7/29.
 */
public abstract class JobSupport implements Job {
    private static Logger logger = LoggerFactory.getLogger(JobSupport.class);

    public void startExecuteLog(JobExecutionContext context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("开始执行周期任务,任务jobName:{},开始执行时间:{}", context.getTrigger().getJobKey().getName(), sdf.format(new Date
                ()));
    }

    public void recordExecuteLog(JobExecutionContext context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("执行周期任务,任务jobName:{},执行结束时间:{}", context.getTrigger().getJobKey().getName(), sdf.format(new Date
                ()));
    }

    public abstract void executeJob(JobExecutionContext context);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        startExecuteLog(context);
        executeJob(context);
        recordExecuteLog(context);
    }
}
