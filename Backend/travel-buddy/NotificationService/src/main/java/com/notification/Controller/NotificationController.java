package com.notification.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.notification.Entity.Notification;
import com.notification.Service.NotificationService;


@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;
    
    @GetMapping("/get-notification-by-user/{id}")
    public ResponseEntity<Object> getNotificationsByUserId(@PathVariable  String id, @RequestParam String read)
    {
      return new ResponseEntity<>(notificationService.getNotificationsByUserId(id, read),HttpStatus.OK);
    }

    @GetMapping("/get-notification/{id}")
    public ResponseEntity<Notification> getNotificationsById(@PathVariable  String id)
    {
      return new ResponseEntity<>(notificationService.getNotificationById(id),HttpStatus.OK);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteNotificationById(@PathVariable String id) {
      notificationService.deleteNotificationById(id);
        return new ResponseEntity<>("Notification Deleted successfully", HttpStatus.OK);
    }
}
 