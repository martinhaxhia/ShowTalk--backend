package com.viatech.showTalk;

import org.springframework.boot.SpringApplication;

public class TestShowTalkApplication {

	public static void main(String[] args) {
		SpringApplication.from(ShowTalkApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
