package com.github.yogurt.sample;

import com.github.yogurt.core.Configuration;
import org.jooq.conf.Settings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author jtwu
 */
@EnableConfigurationProperties({Configuration.class})
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		new Settings().withRenderSchema(false);
		SpringApplication.run(DemoApplication.class, args);
	}
}
