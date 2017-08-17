package com.github.jyoghurt.quartz.listener;

import com.github.jyoghurt.quartz.exception.QuartzException;
import com.github.jyoghurt.quartz.service.ScheduleJobService;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * user:dell
 * date: 2016/7/5.
 */
public class InitScheduleListener implements ServletContextListener {
    private static Logger logger = LoggerFactory.getLogger(InitScheduleListener.class);
    /**
     * 周期任务服务类
     */
    private ScheduleJobService scheduleJobService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            logger.info("============================================");
            logger.info(InitScheduleListener.class.getSimpleName());
            logger.info("============================================");
            scheduleJobService = SpringContextUtils.getBean(ScheduleJobService.class);
            scheduleJobService.syncScheduleJobs();
            logger.info("============================================");
            logger.info(InitScheduleListener.class.getSimpleName() + "Over");
            logger.info("============================================");
            logger.info("============================================");
            logger.info("启动调度器");
            logger.info("============================================");
            scheduleJobService.scheduleStart();
            logger.info("============================================");
            logger.info("启动调度器成功");
            logger.info("============================================");
        } catch (QuartzException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
