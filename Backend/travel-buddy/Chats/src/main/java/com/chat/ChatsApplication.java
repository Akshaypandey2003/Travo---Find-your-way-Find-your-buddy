package com.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class ChatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatsApplication.class, args);
	}

}
