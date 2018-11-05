package com.github.yogurt.sample;

import org.jooq.conf.Settings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jtwu
 */
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		new Settings().withRenderSchema(false);
		SpringApplication.run(DemoApplication.class, args);
	}
}
