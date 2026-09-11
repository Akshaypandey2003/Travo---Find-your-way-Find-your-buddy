package com.feed.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.feed.Entity.FeedItem;

public interface FeedItemRepository extends MongoRepository<FeedItem, String> {

    Page<FeedItem> findByViewerIdAndActiveTrueOrderByCreatedAtDesc(String viewerId, Pageable pageable);

    Optional<FeedItem> findByEventIdAndViewerId(String eventId, String viewerId);

    List<FeedItem> findByResourceIdAndResourceTypeAndActiveTrue(String resourceId, String resourceType);
}
