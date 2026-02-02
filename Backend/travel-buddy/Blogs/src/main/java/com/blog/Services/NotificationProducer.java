package com.blog.Services;


public interface NotificationProducer {

     public void postBlogNotification(String senderId, String receiverId, String blogId, String blogTitle);
     public void likeBlogNotification(String senderId, String receiverId, String blogId, String blogTitle);
     public void sendCommentNotification(String senderId, String receiverId, String blogId, String blogTitle);
     public void sendCommentLikeNotification(String senderId, String receiverId, String blogId, String blogTitle);

}