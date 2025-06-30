package com.user.ServiceImpl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.user.DTO.NotificationMessage;
import com.user.Entity.Notification;
import com.user.Entity.User;
import com.user.Exceptions.ResourceNotFoundException;
import com.user.Repository.NotificationRepo;
import com.user.Repository.UserRepo;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepo notificationRepo;
 
    @Autowired
    private UserRepo userRepo;

    @Autowired
    WebSocketNotificationService webSocketNotificationService;
   
    public Notification sendNotification(Notification notification)
    {
        User user = userRepo.findById(notification.getNotificationFrom()).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + notification.getNotificationTo()));
        notification.setSenderName(user.getName());
        notification.setSenderProfilePic(user.getProfilePic());


        Notification savedNotification = notificationRepo.save(notification);

        NotificationMessage wsMessage = new NotificationMessage();
            wsMessage.setNotificationFrom(savedNotification.getNotificationFrom());
            wsMessage.setNotificationTo(savedNotification.getNotificationTo());
            wsMessage.setTripId(savedNotification.getTripId());
            wsMessage.setMessage(savedNotification.getMessage());
            wsMessage.setType(savedNotification.getType().toString());
            wsMessage.setSenderName(user.getName());
            wsMessage.setSenderProfilePic(user.getProfilePic());
            wsMessage.setNotificationId(savedNotification.getNotificationId());
            webSocketNotificationService.sendNotification(wsMessage);

        return savedNotification;
    }
    public Object getNotificationsByUserId(String id)
    {
           List<Notification> notifications = notificationRepo.findByNotificationToOrderByCreatedAtDesc(id);
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
}
