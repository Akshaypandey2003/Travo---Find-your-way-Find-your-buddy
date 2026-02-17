package com.events.User;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDeletedEvent {

    private String userId;

    private long timestamp;

}