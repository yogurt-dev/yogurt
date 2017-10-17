package com.df.quartz.service.impl;


import com.df.quartz.dao.ScheduleJobMapper;
import com.df.quartz.domain.ScheduleJob;
import com.df.quartz.exception.QuartzException;
import com.df.quartz.service.ScheduleJobService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.apache.commons.collections.map.HashedMap;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("scheduleJobService")
public class ScheduleJobServiceImpl extends ServiceSupport<ScheduleJob, ScheduleJobMapper> implements ScheduleJobService {
    private static Logger logger = LoggerFactory.getLogger(ScheduleJobServiceImpl.class);
    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    @Override
    public ScheduleJobMapper getMapper() {
        return scheduleJobMapper;
    }

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public void logicDelete(Serializable id) {
        getMapper().logicDelete(ScheduleJob.class, id);
    }

    @Override
    public ScheduleJob find(Serializable id) {
        return getMapper().selectById(ScheduleJob.class, id);
    }


    @Override
    public void scheduleStart() throws QuartzException {
        schedulerFactoryBean.start();
    }

    public void syncScheduleJobs() throws QuartzException {
        //找到所有需要同步的任务
        List<ScheduleJob> scheduleJobs = this.findAll(new ScheduleJob().setDeleteFlag(false));
        for (ScheduleJob scheduleJob : scheduleJobs) {
            syncScheduleJob(scheduleJob);
        }
        //匹配应该删除的周期任务
        matchRemoveJob(scheduleJobs);
    }

    /**
     * 同步周期任务
     *
     * @param scheduleJob 周期任务对象
     * @throws QuartzException
     */
    public void syncScheduleJob(ScheduleJob scheduleJob) throws QuartzException {
        try {
            //job为空或者job状态为禁用的不参与同步
            if (scheduleJob == null || scheduleJob.getJobState()) {
                forbiddenScheduleJob(scheduleJob);
                return;
            }
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            //判断是否获取到调度器实例
            //初始化触发器
            TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // 不存在，创建一个
            if (null == trigger) {
                createNewScheduleJob(scheduleJob, scheduler);
                return;
            }
            //若触发器存在则更新
            updateScheduleJob(scheduleJob, scheduler, triggerKey, trigger);
        } catch (Exception e) {
            assert scheduleJob != null;
            logger.error("同步调度异常,详细内容:{}", scheduleJob.toString(), e);
            throw new QuartzException(QuartzException.SYNCSCHEDULEJOB_EXCEPTION, e);
        }
    }

    /**
     * 禁用触发器
     *
     * @param scheduleJob 周期任务对象
     * @throws QuartzException
     */
    private void forbiddenScheduleJob(ScheduleJob scheduleJob) throws QuartzException {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            //判断是否获取到调度器实例
            //初始化触发器
            TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (null == trigger) {
                return;
            }
            removeScheduleJob(scheduleJob);
        } catch (SchedulerException e) {
            logger.error("禁用周期任务异常,详细内容:{}", scheduleJob.toString(), e);
            throw new QuartzException(QuartzException.SYNCSCHEDULEJOB_EXCEPTION, e);
        }
    }

    /**
     * 构造新触发器
     *
     * @param scheduleJob 周期任务对象
     * @param scheduler   调度器对象
     * @throws QuartzException
     */
    private void createNewScheduleJob(ScheduleJob scheduleJob, Scheduler scheduler) throws QuartzException {
        try {
            Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(scheduleJob.getClassName());
            JobDetail jobDetail = JobBuilder
                    .newJob(clazz)
                    .requestRecovery(false)
                    .withIdentity(scheduleJob.getJobName(), scheduleJob.getJobGroup())
                    .build();
            jobDetail.getJobDataMap().put("scheduleJob", scheduleJob);
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(scheduleJob.getJobName(), scheduleJob.getJobGroup()).withSchedule(scheduleBuilder).build();
            logger.info("==========================================");
            logger.info("创建任务,jobName:{}", scheduleJob.getJobName());
            logger.info("==========================================");
            scheduler.scheduleJob(jobDetail, trigger);
            logger.info("==========================================");
            logger.info("创建任务完毕,jobName:{}", scheduleJob.getJobName());
            logger.info("==========================================");
        } catch (ClassNotFoundException e) {
            throw new QuartzException(QuartzException.SCHEDULEJOB_CLASS_NOT_FOUND_EXCEPTION, e);
        } catch (Exception e) {
            throw new QuartzException(QuartzException.CREATE_SCHEDULER_EXCEPTION, e);
        }
    }

    /**
     * 更新触发器
     *
     * @param scheduleJob 周期任务对象
     * @param scheduler   调度器对象
     * @param triggerKey  触发器key
     * @param trigger     触发器实例
     * @throws QuartzException
     */
    private void updateScheduleJob(ScheduleJob scheduleJob, Scheduler scheduler, TriggerKey triggerKey, CronTrigger trigger) throws QuartzException {
        try {
            //如果job类型为非补偿回滚则清除quartz的历史记录 重新加入job
            if (!scheduleJob.getRollBackTheWrong()) {
                reActivationScheduleJob(scheduleJob, triggerKey);
                return;
            }
            logger.info("========================================");
            logger.info("更新任务,jobName:{}", triggerKey.getName());
            logger.info("========================================");
            //比对周期时间 若发生变化则更新否则不更新
            if (scheduleJob.getCronExpression().equals(trigger.getCronExpression())) {
                return;
            }
            //比对后确实有不同则
            // Trigger已存在，那么更新相应的定时设置
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
            logger.info("==========================================");
            logger.info("更新任务完毕,jobName:{}", triggerKey.getName());
            logger.info("==========================================");
        } catch (Exception e) {
            throw new QuartzException(QuartzException.UPDATE_SCHEDULER_EXCEPTION, e);
        }
    }

    /**
     * 重新激活触发器
     * 删除 重建  对触发器进行重置
     *
     * @param scheduleJob 周期任务对象
     * @param triggerKey  触发器key
     * @throws QuartzException
     */
    private void reActivationScheduleJob(ScheduleJob scheduleJob, TriggerKey triggerKey) throws QuartzException {
        logger.info("========================================");
        logger.info("更新<失败回滚>任务类型,jobName:{}", triggerKey.getName());
        logger.info("========================================");
        this.removeScheduleJob(scheduleJob);
        this.syncScheduleJob(scheduleJob);
        logger.info("========================================");
        logger.info("更新<失败回滚>任务类型成功,jobName:{}", triggerKey.getName());
        logger.info("========================================");
    }

    private void matchRemoveJob(List<ScheduleJob> scheduleJobs) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Map<String, Boolean> scheduleJobsMap = new HashedMap();
        for (ScheduleJob scheduleJob : scheduleJobs) {
            scheduleJobsMap.put(scheduleJob.getJobName(), true);
        }
        try {
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyGroup());
            for (JobKey jobKey : jobKeys) {
                if (null!=scheduleJobsMap.get(jobKey.getName())) {
                    continue;
                }
                removeScheduleJob(jobKey.getGroup(), jobKey.getName());
            }
        } catch (SchedulerException e) {
            logger.error("获取周期任务全部触发器异常");
        }
    }

    private void removeScheduleJob(ScheduleJob scheduleJob) throws QuartzException {
        removeScheduleJob(scheduleJob.getJobGroup(), scheduleJob.getJobName());
    }

    @Override
    public void removeScheduleJob(String jobGroup, String jobName) throws QuartzException {
        //初始化触发器
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = new JobKey(jobName, jobGroup);
        // 按新的trigger重新设置job执行
        try {
            logger.info("==========================================");
            logger.info("删除任务,jobName:{}", jobName);
            logger.info("==========================================");
            scheduler.deleteJob(jobKey);
            logger.info("==========================================");
            logger.info("删除任务完成,jobName:{}", jobName);
            logger.info("==========================================");
        } catch (SchedulerException e) {
            throw new QuartzException(QuartzException.REMOVE_SCHEDULEJOB_EXCEPTION, e);
        }
    }

    public void removeScheduleJob(String scheduleJobId) throws QuartzException {
        ScheduleJob scheduleJob = this.find(scheduleJobId);
        this.removeScheduleJob(scheduleJob);
    }
}
