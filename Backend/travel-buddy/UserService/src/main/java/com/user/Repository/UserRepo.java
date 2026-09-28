package com.user.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import com.user.Entity.User;
import com.user.Enum.AccountType;

public interface UserRepo extends MongoRepository<User, String> {

    Page<User> findByPreferencesInIgnoreCase(Pageable page,List<String> preferences);

    Optional<User> findByEmail(String email);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'followersCount': 1 } }")
    void incrementFollowersCount(String userId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'followingsCount': 1 } }")
    void incrementFollowingCount(String userId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'followersCount': -1 } }")
    void decrementFollowersCount(String userId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'followingsCount': -1 } }")
    void decrementFollowingCount(String userId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'closeFriendsCount': 1 } }")
    void incrementCloseFriendsCount(String userId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'closeFriendsCount': -1 } }")
    void decrementCloseFriendsCount(String userId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'tripsCount': 1 } }")
    void incrementTripsCount(String userId);

    @Query("{ '_id': ?0, 'tripsCount': { '$gt': 0 } }")
    @Update("{ '$inc': { 'tripsCount': -1 } }")
    void decrementTripsCount(String userId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'blogsCount': 1 } }")
    void incrementBlogsCount(String userId);

    @Query("{ '_id': ?0, 'blogsCount': { '$gt': 0 } }")
    @Update("{ '$inc': { 'blogsCount': -1 } }")
    void decrementBlogsCount(String userId);

    List<User> findByUserIdIn(List<String> userIds);

    Page<User> findByAccountType(AccountType accountType, Pageable pageable);
}
