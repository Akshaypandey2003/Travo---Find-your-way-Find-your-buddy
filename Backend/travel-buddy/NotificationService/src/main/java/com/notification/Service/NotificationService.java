package com.notification.Service;

import java.util.List;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.notification.Entity.Notification;
import com.notification.Repository.NotificationRepo;

@Service
@SuppressWarnings("null")
public class NotificationService {
    
    @Autowired
    private NotificationRepo notificationRepo;
 
   
    public Notification saveNotification(Notification notification)
    {
        return notificationRepo.save(notification);
    }


    public Object getNotificationsByUserId(String id, String status)
    {
           List<Notification> notifications = notificationRepo.findByNotificationToAndReadOrderByCreatedAtDesc(id, status);

            if (notifications.isEmpty()) {
                return new ResponseEntity<>("Notification not found for this user id: " + id, HttpStatus.NOT_FOUND);
            }
            return notifications;
    }
    public List<Notification> getlAllNotifications()
    {
        return notificationRepo.findAll();
    }

    public  Notification deleteNotificationById(String id) 
    {
        Notification notification = notificationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        Notification existingNotification = notification;
        notificationRepo.delete(notification);

        return existingNotification;
    }

    public Notification getNotificationById(String id) {
        return notificationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
    }

    public boolean deleteNotificationIfExists(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        if (notificationRepo.existsById(id)) {
            notificationRepo.deleteById(id);
            return true;
        }
        return false;
    }
}
