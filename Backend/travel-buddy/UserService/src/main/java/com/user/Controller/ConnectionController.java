package com.user.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.Entity.Connections;
import com.user.ServiceImpl.ConnectionService;

@RestController
@RequestMapping("/connection")
public class ConnectionController 
{
     @Autowired
    private ConnectionService connectionService;

    @PostMapping("/send/{senderId}/{receiverId}")
    public ResponseEntity<Connections> sendRequest(@PathVariable String senderId, @PathVariable String receiverId)
    {
        return new ResponseEntity<>(connectionService.sendFriendRequest(senderId, receiverId),HttpStatus.OK);
    }

    @PostMapping("/accept/{notificationId}/{senderId}/{receiverId}")
    public ResponseEntity<?> acceptRequest(@PathVariable String notificationId, @PathVariable String senderId, @PathVariable String receiverId) {

        connectionService.acceptFriendRequest(senderId, receiverId,notificationId);
        return ResponseEntity.ok("Friend Request Accepted Successfully!!");
    }

    @GetMapping("/received/{userId}")
    public ResponseEntity<List<Connections>> getReceivedRequests(@PathVariable String userId) {
        return ResponseEntity.ok(connectionService.getReceivedRequests(userId));
    }
    @DeleteMapping("/delete/{connectionId}")
    public ResponseEntity<?> deleteConnection(@PathVariable String connectionId) {
        connectionService.rejectFriendRequest(connectionId);
 
        return ResponseEntity.ok("Friend Request Rejected Successfully!!");
    }

    @GetMapping("/sent/{userId}")
    public ResponseEntity<List<Connections>> getSentRequests(@PathVariable String userId) {
        return ResponseEntity.ok(connectionService.getSentRequests(userId));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Connections>> getAllConnectionRequests() {
        return ResponseEntity.ok(connectionService.getAll());
    }


}
