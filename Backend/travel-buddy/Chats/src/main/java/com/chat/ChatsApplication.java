package com.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ChatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatsApplication.class, args);
	}

}
