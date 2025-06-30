package com.user.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.user.Entity.Notification;

public interface NotificationRepo extends MongoRepository<Notification,String> {
    
    List<Notification> findByNotificationToOrderByCreatedAtDesc(String notificationTo);

}