package com.github.jyoghurt.common.payment.log4j.filter;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * user:dell
 * date: 2016/9/6.
 */
public class EmailFilter extends Filter {
    @Override
    public int decide(LoggingEvent loggingEvent) {
        if ("sdk.biz.err".equals(loggingEvent.getLoggerName())) {
            return Filter.DENY;
        }
        return 0;
    }
}
