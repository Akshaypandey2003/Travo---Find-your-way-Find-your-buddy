package com.user.ServiceImpl;

import com.user.Config.JwtProvider;
import com.user.DTO.AuthResponse;
import com.user.DTO.Author;
import com.user.DTO.MessageResponse;
import com.user.DTO.NotificationMessage;
import com.user.Entity.Notification;
import com.user.Entity.Notification.NotificationType;
import com.user.Entity.User;
import com.user.Exceptions.UserNotFoundException;
import com.user.Helper.AppConstants;
import com.user.Repository.UserRepo;
import com.user.Service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    @Autowired
    private JwtProvider jwtProvider;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    // Add a new user
    public AuthResponse addUser(User user) {
        try {
            if (user.getProfilePic() == null) {
                if (user.getGender().equalsIgnoreCase("male"))
                    user.setProfilePic(AppConstants.DEFAULT_MALE_PIC);
                else
                    user.setProfilePic(AppConstants.DEFAULT_FEMALE_PIC);
            }

            User savedUser = userRepo.save(user);
             // Assign role based on email
            List<String> roles = user.getEmail().endsWith("@admin.com") ? Arrays.asList("ROLE_ADMIN")
                    : Arrays.asList("ROLE_USER");

         // Generate JWT Token
         String token = jwtProvider.generateToken(user.getEmail(), roles);

         AuthResponse authResponse = new AuthResponse(
                    savedUser,
                    token,
                    "refresh_token",
                    System.currentTimeMillis() + 3600000,
                    roles,
                    new MessageResponse("User registered successfully", "success"));
        
        return authResponse;
        } catch (Exception e) {
            throw new RuntimeException("Error while saving user: " + e.getMessage());
        }
    }



    // Retrieve all users
    public List<User> getAllUser(Pageable pageable) {
        try {
            List<User> users = userRepo.findAll(pageable).getContent();
            if (users.isEmpty()) {
                throw new UserNotFoundException("No users found in the system.");
            }
            return users;
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching users: " + e.getMessage());
        }
    }

    // Retrieve a user by ID
    public User getUserById(String userId) {
        try {
            return userRepo.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving user: " + e.getMessage());
        }
    }



    public Author getAuthorById(String userId) {
        try {
            User user =  userRepo.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

            return new Author(user.getUserId(),user.getName(),user.getProfilePic());
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving user: " + e.getMessage());
        }
    }

    // Delete a user by ID
    public String deleteUser(String userId) {
        try {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));
            userRepo.delete(user);
            return "User with ID " + userId + " has been deleted successfully.";
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting user: " + e.getMessage());
        }
    }

    @Override
    public List<User> getUserByPreferences(List<String> preferences) {
        List<User> users = userRepo.findByPreferencesInIgnoreCase(preferences);

        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found with the given preferences.");
        }

        return users;
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepo.findByEmail(email);
        return user;
    }

    @Override
    public User updateUser(User user) {
        try {
            return userRepo.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error while saving user: " + e.getMessage());
        }
    }

    public ResponseEntity<Object> updateLikes(String userId, String senderId) {
        try {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

            userRepo.save(user);
            User user2 = userRepo.findById(senderId)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + senderId + " not found"));
            // Creating notification object
            Notification notification = new Notification();
            notification.setNotificationFrom(senderId);
            notification.setNotificationTo(userId);
            notification.setType(NotificationType.LIKE);
            notification.setMessage("Liked your profile.");
            notification.setSenderName(user2.getName());
            notification.setSenderProfilePic(user2.getProfilePic());

            // Map<String,Object>map = new HashMap<>();
            // map.put(senderId,"wants to follow you");
            // notification.setData(map);

            NotificationMessage wsMessage = new NotificationMessage();
            wsMessage.setNotificationFrom(senderId);
            wsMessage.setNotificationTo(userId);
            wsMessage.setMessage("Liked your profile");
            wsMessage.setType(notification.getType()+"");
            wsMessage.setSenderName(user2.getName());
            wsMessage.setSenderProfilePic(user2.getProfilePic());

            if (user.getLikes() == null) {
                user.setLikes(new ArrayList<>());
                user.getLikes().add(senderId);
                Notification savedNotification = notificationService.sendNotification(notification);
                wsMessage.setNotificationId(savedNotification.getNotificationId());
                webSocketNotificationService.sendNotification(wsMessage);
                logger.info("User liked the profile at line 146.");
            } else if (user.getLikes().contains(senderId)) {

                user.getLikes().remove(senderId);
                logger.info("User unliked the profile.");
            } else {
                user.getLikes().add(senderId);
                Notification savedNotification = notificationService.sendNotification(notification);
                wsMessage.setNotificationId(savedNotification.getNotificationId());
                webSocketNotificationService.sendNotification(wsMessage);
                logger.info("User liked the profile at line 156.");
            }

            userRepo.save(user);

            return ResponseEntity.ok("User likes updated successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Error while updating likes: " + e.getMessage());
        }
    }

    @Override
    public String addCloseFriend(String userId, String friendId) {
        try {
            User user = userRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

            if (!user.getCloseFriends().contains(friendId)) {
                user.getCloseFriends().add(friendId);
            }
            userRepo.save(user);

            return "Close friend added successfully";

        } catch (Exception e) {
            throw new RuntimeException("Error while adding close friends: " + e.getMessage());
        }
    }

    @Override
    public String removeCloseFriend(String userId, String friendId) {
        try {
            User user = userRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

            if (user.getCloseFriends().contains(friendId)) {
                user.getCloseFriends().remove(friendId);
            }
            
            userRepo.save(user);

            return "Close friend removed successfully";
        } catch (Exception e) {
            throw new RuntimeException("Error while removing close friends: " + e.getMessage());
        }
    }

    
    public AuthResponse generateToken(User user) 
    {
        List<String> roles = Arrays.asList("ROLE_USER");

        // Generate JWT Token
        String token = jwtProvider.generateToken(user.getEmail(),roles);

            return new AuthResponse(
                    user,
                    token,
                    "refresh_token",
                    System.currentTimeMillis() + 3600000,
                    roles,
                    new MessageResponse("User logged in successfully", "success"));
    }

    public AuthResponse generateToken(String email, String password) {

        User user = userRepo.findByEmail(email);
        if (user == null) {
            return null;
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("INVALID_PASSWORD");
        }

        List<String> roles = List.of("ROLE_USER");

        String token = jwtProvider.generateToken(user.getEmail(), roles);

        return new AuthResponse(
                user,
                token,
                "refresh_token",
                System.currentTimeMillis() + 3600000,
                roles,
                new MessageResponse("User logged in successfully", "success")
        );
    }

}
