package com.trip.Entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "trip_failed_notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailedNotification {

    @Id
    private String id;

    @NotBlank
    private String topic;

    @NotBlank
    private String key;

    @NotNull
    private Object event;

    @Builder.Default
    private int retryCount = 0;

    @Builder.Default
    private long createdAt = System.currentTimeMillis();

    @Indexed
    private long lastRetryAt;

    private String failureReason;
}
