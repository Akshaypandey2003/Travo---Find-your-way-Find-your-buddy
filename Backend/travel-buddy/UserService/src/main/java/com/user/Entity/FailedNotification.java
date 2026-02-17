package com.user.Entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Document("failed_notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailedNotification {

    @Id
    private String id;

    private String topic;

    private String key;

    private Object event;

    private int retryCount;

    private long createdAt;
}
