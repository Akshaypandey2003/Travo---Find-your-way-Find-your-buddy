package com.blog.Repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.blog.Entity.Like;
import com.blog.Enum.ResourceType;

public interface LikeRepo extends MongoRepository<Like, String> {
    Optional<Like> findByResourceIdAndResourceTypeAndAuthorId(String resourceId, ResourceType resourceType, String authorId);
    int countByResourceIdAndResourceType(String resourceId, ResourceType resourceType);
    void deleteByResourceIdAndResourceType(String resourceId, ResourceType resourceType);
} 