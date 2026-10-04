package com.feed.Entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "user_feed")
@CompoundIndexes({
        @CompoundIndex(name = "event_viewer_unique", def = "{'eventId': 1, 'viewerId': 1}", unique = true),
        @CompoundIndex(name = "viewer_created_idx", def = "{'viewerId': 1, 'createdAt': -1}")
})
public class FeedItem {

    @Id
    private String id;

    @Indexed
    private String viewerId;

    @Indexed
    private String eventId;

    private String resourceType;

    private String authorId;
    private String authorName;
    private String authorProfilePic;

    @Indexed
    private String resourceId;

    private String caption;

    @Builder.Default
    private List<String> images = new ArrayList<>();

    private String visibility;

    @Builder.Default
    private Instant createdAt = Instant.now();

    private String thumbnailUrl;

    private long likesCount;
    private long commentsCount;
    private long viewsCount;

    @Builder.Default
    private boolean active = true;
}
