package com.blog.Repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.blog.Entity.Blog;


public interface BlogRepo extends MongoRepository<Blog,String> {
    
    Page<Blog> findByCategoryIgnoreCase(String category, Pageable pageable);
    Page<Blog> findByAuthorId(String authorId, Pageable pageable);
    Page<Blog> findByTitleIgnoreCase(String title, Pageable pageable);
    Page<Blog> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String keyword,
            String contentKeyword,
            Pageable pageable);
    List<Blog> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant startDate, Instant endDate);
}
