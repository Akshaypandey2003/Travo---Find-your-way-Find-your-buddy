package com.events.Feed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCreatedEvent {

    private String eventId;
    private String resourceType;
    private String authorId;
    private String authorName;
    private String authorProfilePic;
    private String resourceId;
    private String caption;
    @Builder.Default
    private List<String> images = new ArrayList<>();
     @Builder.Default
    private List<String> cloudinaryPublicIds = new ArrayList<>();
    private String visibility;
    private long likesCount;
    private long commentsCount;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private long viewCount;
    
}
