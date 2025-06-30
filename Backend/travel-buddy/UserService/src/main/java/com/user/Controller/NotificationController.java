package com.user.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController; 

import com.user.Entity.Notification;
import com.user.ServiceImpl.NotificationService;



@RestController
@RequestMapping("auth/user/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @PostMapping("/send-notification")
    public ResponseEntity<Notification> sendNotification(@RequestBody Notification notification) {
        return new ResponseEntity<>(notificationService.sendNotification(notification), HttpStatus.CREATED);
    }
    
    @GetMapping("/get-notifications/{id}")
    public ResponseEntity<Object> getNotificationsByUserId(@PathVariable  String id)
    {
      return new ResponseEntity<>(notificationService.getNotificationsByUserId(id),HttpStatus.OK);
    }

    @GetMapping("/get-notification/{id}")
    public ResponseEntity<Notification> getNotificationsById(@PathVariable  String id)
    {
      return new ResponseEntity<>(notificationService.getNotificationById(id),HttpStatus.OK);
    }
    
    @GetMapping("/get-all-notifications")
    public ResponseEntity<List<Notification>> getAllNotifications()
    {
      return new ResponseEntity<>(notificationService.getlAllNotifications(),HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteNotificationById(@PathVariable String id) {
      notificationService.deleteNotificationById(id);
        return new ResponseEntity<>("Notification Deleted successfully", HttpStatus.OK);
    }
}
 