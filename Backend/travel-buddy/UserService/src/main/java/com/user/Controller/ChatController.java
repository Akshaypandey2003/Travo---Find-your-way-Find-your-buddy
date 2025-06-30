package com.user.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.Entity.Chat;
import com.user.Entity.Message;
import com.user.ServiceImpl.WebSocketNotificationService;

@RestController
@RequestMapping("/chat")
public class ChatController {
    
     @Autowired
    private WebSocketNotificationService notificationService;

    @PostMapping("/message/send/{sendTo}")
    public ResponseEntity<String> sendMsgNotification(@RequestBody Message message , @PathVariable String sendTo) {
        System.out.println("MEssage received inside user service: "+message +" with user id: "+sendTo);
        notificationService.sendNotification(message,sendTo);
        return ResponseEntity.ok("Message sent ");
    }
    @PostMapping("/new-group/send/{sendTo}")
    public ResponseEntity<String> sendNewChatNotification(@RequestBody Chat chat , @PathVariable String sendTo) {
        System.out.println("MEssage received inside user service: "+chat +" with user id: "+sendTo);
        notificationService.sendNotification(chat,sendTo);
        return ResponseEntity.ok("Message sent ");
    }
}
