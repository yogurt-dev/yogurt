package com.github.yogurt.sample.test;

import com.github.yogurt.core.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @Author: jtwu
 * @Date: 2018/11/3 17:26
 */

@RunWith(SpringRunner.class)
@Import(Configuration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestControllerTests {
	@Autowired
	private WebTestClient webClient;


	@Test
	public void testDelete() {
		this.webClient.delete().uri("/tests/40").exchange().expectStatus().isOk();
	}
}