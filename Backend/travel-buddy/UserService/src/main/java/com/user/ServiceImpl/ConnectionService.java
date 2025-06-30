package com.user.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.user.DTO.NotificationMessage;
import com.user.Entity.Connections;
import com.user.Entity.Notification;
import com.user.Repository.ConnectionRepo;
import com.user.Repository.UserRepo;
import com.user.Service.UserService;
import com.user.Entity.User;
import com.user.Entity.Notification.NotificationType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConnectionService {

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    @Autowired
    private ConnectionRepo ConnectionsRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    // Send a friend request
    public Connections sendFriendRequest(String senderId, String receiverId) {
        try {
            if (ConnectionsRepo.findByRequestFromAndRequestTo(senderId, receiverId) != null) {
                throw new RuntimeException("Friend request already sent!");
            }
            // creating connection object 
            Connections connections = new Connections();
            connections.setRequestFrom(senderId);
            connections.setRequestTo(receiverId);
            connections.setStatus(false);

            //adding users to following/followers lists
            Connections con = ConnectionsRepo.save(connections);
            User user1 = userService.getUserById(senderId);
            User user2 = userService.getUserById(receiverId);
            
            ArrayList<String> following = user1.getFollowing();
            if(following==null)
            {
                following = new ArrayList<>();
            }
            if(!following.contains(receiverId))
                following.add(receiverId);
            user1.setFollowing(following);
          

            userService.addUser(user1);
            
            ArrayList<String> followers = user2.getFollowers();
            if(followers==null)
            {
                followers = new ArrayList<>();
                
            }
            if(!followers.contains(senderId))
            followers.add(senderId);
                user2.setFollowers(followers);

            userService.addUser(user2);

            //Creating notification object
            Notification notification = new Notification();
            notification.setNotificationFrom(senderId);
            notification.setNotificationTo(receiverId);
            notification.setType(NotificationType.FRIEND_REQUEST);
            notification.setMessage("Wants to follow you.");
            notification.setSenderName(user1.getName());
            notification.setSenderProfilePic(user1.getProfilePic());

            // Map<String,Object>map = new HashMap<>();
            // map.put(senderId,"wants to follow you");
            // notification.setData(map);
            
            Notification savedNotification = notificationService.sendNotification(notification);
            
            NotificationMessage wsMessage = new NotificationMessage();
            wsMessage.setNotificationFrom(senderId);
            wsMessage.setNotificationTo(receiverId);
            wsMessage.setMessage("Wants to follow you.");
            wsMessage.setType(savedNotification.getType()+"");
            wsMessage.setSenderName(user1.getName());
            wsMessage.setSenderProfilePic(user1.getProfilePic());
            wsMessage.setNotificationId(savedNotification.getNotificationId());
            webSocketNotificationService.sendNotification(wsMessage);

            return con;
        } catch (Exception e) {
            throw new RuntimeException("Error while sending request: " + e.getMessage());
        }
    }

    // Accept friend request
    public  Notification acceptFriendRequest(String senderId, String receiverId,String notificationId) 
    {
        Connections connections = ConnectionsRepo.findByRequestFromAndRequestTo(senderId, receiverId);
        if (connections == null) {
            throw new RuntimeException("No friend request found!");
        }
        connections.setStatus(true);

        User user1 = userService.getUserById(senderId);
        User user2 = userService.getUserById(receiverId);

        ArrayList<String> senderFollowers = user1.getFollowers();
        ArrayList<String> receiverFollowing = user2.getFollowing();
        if(senderFollowers==null)
        {
            senderFollowers = new ArrayList<>();
            
        }
        if(!senderFollowers.contains(receiverId))
        senderFollowers.add(receiverId);
        user1.setFollowers(senderFollowers);

        if(receiverFollowing==null)
        {
            receiverFollowing = new ArrayList<>();
            
        }
        if(!receiverFollowing.contains(senderId))
        {
            receiverFollowing.add(senderId);
        }
        user2.setFollowing(receiverFollowing);
        
        userRepo.save(user1);
        userRepo.save(user2);
        
        
        Notification notification = notificationService.deleteNotificationById(notificationId);
        notification.setSenderName(user1.getName());
        ConnectionsRepo.save(connections);
        
        return notification;
    }

    // Reject friend request
    public void rejectFriendRequest(String senderId, String receiverId) {
        Connections Connections = ConnectionsRepo.findByRequestFromAndRequestTo(senderId, receiverId);
        if (Connections != null) {
            ConnectionsRepo.delete(Connections);
        }
    }

    // Get all received friend requests
    public List<Connections> getReceivedRequests(String userId) {
        return ConnectionsRepo.findByRequestToAndStatus(userId, false);
    }

    // Get all sent friend requests
    public List<Connections> getSentRequests(String userId) {
        return ConnectionsRepo.findByRequestFromAndStatus(userId, false);
    }

    public List<Connections> getAll() {
        return ConnectionsRepo.findAll();
    }
}
