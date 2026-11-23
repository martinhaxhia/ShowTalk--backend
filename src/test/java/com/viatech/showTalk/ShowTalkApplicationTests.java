package com.viatech.showTalk;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ShowTalkApplicationTests {

	@Test
	void contextLoads() {
	}

}
