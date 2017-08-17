package com.github.jyoghurt.security.securityUserT.schedule;

import com.github.jyoghurt.quartz.support.JobSupport;
import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.security.securityUserT.schedule
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-12-19 10:37
 */
@Component
public class UserSchedule extends JobSupport {

    @Resource
    private SecurityUserTService securityUserTService;

    @Override
    public void executeJob(JobExecutionContext context) {
        securityUserTService.cleanVerification();
    }
}
