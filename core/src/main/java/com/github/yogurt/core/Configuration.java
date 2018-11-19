package com.github.yogurt.core;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * yogurt所需的配置参数
 *
 * @author jtwu
 */
@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "yogurt")
@Data
@Accessors(chain = true)
public class Configuration {
	@Value("${yogurt.userId:userId}")
	private String userId;
	@Value("${yogurt.userName:userName}")
	private String userName;
}
