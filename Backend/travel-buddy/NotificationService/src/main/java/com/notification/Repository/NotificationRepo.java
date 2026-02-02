package com.notification.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.notification.Entity.Notification;

public interface NotificationRepo extends MongoRepository<Notification, String> {

    public List<Notification> findByNotificationToAndReadOrderByCreatedAtDesc (String id, String status);
}
