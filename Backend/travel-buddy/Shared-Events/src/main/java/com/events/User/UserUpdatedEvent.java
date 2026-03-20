package com.events.User;

import lombok.*;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdatedEvent {

    private String userId;

    // changed fields only
    private Map<String, Object> updatedFields;

    private Instant timestamp;

}