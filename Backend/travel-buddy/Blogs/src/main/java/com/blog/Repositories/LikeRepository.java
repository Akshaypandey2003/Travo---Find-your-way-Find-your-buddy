package com.blog.Repositories;

import java.util.Optional;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.blog.Entity.Like;
import com.blog.Enum.ResourceType;

public interface LikeRepository extends MongoRepository<Like, String> {
    Optional<Like> findByResourceIdAndResourceTypeAndUserId(String resourceId, ResourceType resourceType, String userId);
    List<Like> findByResourceTypeAndResourceIdInAndUserId(ResourceType resourceType, List<String> resourceIds, String userId);
    int countByResourceIdAndResourceType(String resourceId, ResourceType resourceType);
    void deleteByResourceIdAndResourceType(String resourceId, ResourceType resourceType);
} 
