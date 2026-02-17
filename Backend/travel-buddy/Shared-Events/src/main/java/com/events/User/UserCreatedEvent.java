package com.events.User;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreatedEvent {

    private String userId;

    private String username;

    private String email;

    private long timestamp;

}