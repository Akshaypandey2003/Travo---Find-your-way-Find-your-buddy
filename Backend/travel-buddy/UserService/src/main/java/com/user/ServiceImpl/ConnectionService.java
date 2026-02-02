package com.user.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.discovery.converters.Auto;
import com.user.DTO.NotificationMessage;
import com.user.Entity.Connections;
import com.user.Entity.User;
import com.user.Repository.ConnectionRepo;
import com.user.Repository.UserRepo;
import com.user.Service.UserService;

@Service
@SuppressWarnings("unused")
public class ConnectionService {

    @Autowired
    private ConnectionRepo ConnectionsRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserNotificationProducer userNotificationProducer;

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

            // adding users to following/followers lists
            Connections con = ConnectionsRepo.save(connections);
            User user1 = userService.getUserById(senderId);
            User user2 = userService.getUserById(receiverId);

            ArrayList<String> following = user1.getFollowing();
            if (following == null) {
                following = new ArrayList<>();
            }
            if (!following.contains(receiverId))
                following.add(receiverId);
            user1.setFollowing(following);

            userService.addUser(user1);

            ArrayList<String> followers = user2.getFollowers();
            if (followers == null) {
                followers = new ArrayList<>();

            }
            if (!followers.contains(senderId))
                followers.add(senderId);
            user2.setFollowers(followers);

            userService.addUser(user2);

            // -------------------------------------- Creating notification object ----------------------------->

            System.out.println("Sending friend request notification event via Kafka");
            userNotificationProducer.friendRequestSend(senderId,receiverId);

            return con;
        } catch (Exception e) {
            throw new RuntimeException("Error while sending request: " + e.getMessage());
        }
    }

    // Accept friend request
    public void acceptFriendRequest(String notificationId,String senderId, String receiverId) {
        Connections connections = ConnectionsRepo.findByRequestFromAndRequestTo(senderId, receiverId);
        if (connections == null) {
            throw new RuntimeException("No friend request found!");
        }
        connections.setStatus(true);

        User user1 = userService.getUserById(senderId);
        User user2 = userService.getUserById(receiverId);

        ArrayList<String> senderFollowers = user1.getFollowers();
        ArrayList<String> receiverFollowing = user2.getFollowing();
        if (senderFollowers == null) {
            senderFollowers = new ArrayList<>();

        }
        if (!senderFollowers.contains(receiverId))
            senderFollowers.add(receiverId);
        user1.setFollowers(senderFollowers);

        if (receiverFollowing == null) {
            receiverFollowing = new ArrayList<>();

        }
        if (!receiverFollowing.contains(senderId)) {
            receiverFollowing.add(senderId);
        }
        user2.setFollowing(receiverFollowing);

        userRepo.save(user1);
        userRepo.save(user2);

        // ----------- Sending notification -------------
         userNotificationProducer.friendRequestAccept(senderId,receiverId);

        ConnectionsRepo.save(connections);
    }

    // Reject friend request
    public void rejectFriendRequest(String connectionId) {
        Connections connection = ConnectionsRepo.findById(connectionId).orElse(null);
        if (connection != null) {
            ConnectionsRepo.delete(connection);
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
