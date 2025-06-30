package com.user.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.user.Entity.User;

public interface UserRepo extends MongoRepository<User,String> {
 
    List<User> findByPreferencesInIgnoreCase(List<String> preferences);
    User findByEmail(String email);
}