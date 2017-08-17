package com.github.jyoghurt.security.log4j.filter;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * user:zjl
 * date: 2016/12/1.
 */
public class Log4jFilter extends Filter {
    @Override
    public int decide(LoggingEvent loggingEvent) {
        try {
            if (null == loggingEvent.getThrowableInformation()) {
                return Filter.NEUTRAL;
            }
            if (null == loggingEvent.getThrowableInformation().getThrowable()) {
                return Filter.NEUTRAL;
            }
            //管道破裂异常过滤
            if ("Broken pipe".equals(loggingEvent.getThrowableInformation().getThrowable().getMessage())) {
                return Filter.DENY;
            }
            if ("ACCIDENT".equals(loggingEvent.getLoggerName())) {
                return Filter.DENY;
            }
            return Filter.NEUTRAL;
        } catch (Exception e) {
            return Filter.NEUTRAL;
        }
    }
}
