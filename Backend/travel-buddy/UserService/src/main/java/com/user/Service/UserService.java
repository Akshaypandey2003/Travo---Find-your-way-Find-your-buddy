package com.user.Service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.user.Entity.User;

public interface UserService 
{
    public User addUser(User user);
    public User updateUser(User user);
    public String deleteUser(String userId);
    public List<User> getAllUser(Pageable pageable);
    public User getUserById(String userId);
    public User getUserByEmail(String email);
    public List<User> getUserByPreferences(List<String> preferences);
    public ResponseEntity<Object> updateLikes(String userId, String senderId);
    public String addCloseFriend(String userId, String friendId);
    public String removeCloseFriend(String userId, String friendId);
} 
    
   