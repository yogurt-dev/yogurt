package com.github.yogurt.core.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @Author: jtwu
 * @Date: 2019/6/24 13:41
 */
public class JpaFilter extends Filter<ILoggingEvent> {
	@Override
	public FilterReply decide(ILoggingEvent event) {
		if(!event.getLoggerName().equals("org.hibernate.resource.jdbc.internal.ResourceRegistryStandardImpl")){
			return FilterReply.ACCEPT;
		}
		if(event.getMessage().startsWith("Releasing statement")){
			return FilterReply.ACCEPT;
		}
		return FilterReply.DENY;
	}
}
