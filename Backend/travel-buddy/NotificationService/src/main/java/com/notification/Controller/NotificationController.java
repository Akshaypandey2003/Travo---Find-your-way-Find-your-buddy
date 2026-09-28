package com.notification.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.notification.Service.NotificationService;


@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<Object> getNotificationsByUserId(@AuthenticationPrincipal String id, @RequestParam boolean read)
    {
      return new ResponseEntity<>(notificationService.getNotificationsByUserId(id, read),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotificationById(@AuthenticationPrincipal String userId, @PathVariable String id) {
      notificationService.deleteNotificationById(id, userId);
        return new ResponseEntity<>("Notification Deleted successfully", HttpStatus.OK);
    }
}
 