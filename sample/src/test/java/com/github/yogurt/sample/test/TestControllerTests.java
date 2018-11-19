package com.github.yogurt.sample.test;

import com.github.yogurt.core.Configuration;
import com.github.yogurt.sample.test.enums.TypeEnum;
import com.github.yogurt.sample.test.po.TestPO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

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
	public void testList() {
		this.webClient.get().uri("/tests").exchange().expectStatus().isOk().expectBodyList(TestPO.class);

	}

	@Test
	public void testSave() {
		this.webClient.post().uri("/tests").body(Mono.just(new TestPO().setName("haha").setTime(LocalDateTime.now())
				.setType(TypeEnum.Y)), TestPO.class).exchange().expectStatus().isOk();
	}

	@Test
	public void testUpdate() {
		this.webClient.put().uri("/tests").body(Mono.just(new TestPO().setId(41L).setName("haha1").setTime(LocalDateTime.now())
				.setType(TypeEnum.N)), TestPO.class).exchange().expectStatus().isOk();

	}


	@Test
	public void testDelete() {
		this.webClient.delete().uri("/tests/40").exchange().expectStatus().isOk();
	}
}