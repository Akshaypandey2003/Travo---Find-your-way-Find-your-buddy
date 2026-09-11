package com.feed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableCaching
@EnableMongoRepositories(basePackages = "com.feed.Repository")
public class UserFeedServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserFeedServiceApplication.class, args);
    }
}
