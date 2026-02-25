package com.blog.Repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.blog.Entity.Comment;


@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

  Page<Comment> findByBlogIdOrderByCreatedAtDesc(String blogId, Pageable pageable);

  List<Comment> findByBlogIdAndParentCommentIdOrderByCreatedAtDesc(String blogId, String parentCommentId);
  List<Comment> findByParentCommentIdOrderByCreatedAtAsc(String parentCommentId);
  
  void deleteByBlogId(String blogId);
  
  public List<Comment> findByBlogIdAndParentCommentIdAndRepliedToUserIdOrderByCreatedAtDesc(
    String blogId, String parentCommentId, String repliedToUserId);

  List<Comment> findByBlogIdAndParentCommentIdAndRepliedToUserIdAndAuthorIdOrderByCreatedAtDesc(
      String blogId, String parentCommentId, String repliedToUserId, String authorId);

  Page<Comment> findByBlogIdAndParentCommentIdIsNullOrderByCreatedAtDesc(String blogId, Pageable pageable);

  Page<Comment> findByParentCommentIdOrderByCreatedAtAsc(
        String parentCommentId,
        Pageable pageable
);

}
