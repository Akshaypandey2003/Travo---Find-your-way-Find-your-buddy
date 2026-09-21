package com.blog.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    NewTopic blogEventsTopic() {
        return new NewTopic("blog-events", 1, (short) 1);
    }

    @Bean
    NewTopic postEventsTopic() {
        return new NewTopic("post-events", 1, (short) 1);
    }
}