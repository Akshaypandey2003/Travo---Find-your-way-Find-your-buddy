package com.user.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.user.Entity.CloseFriends;

public interface CloseFriendsRepo
        extends MongoRepository<CloseFriends, String> {

    Optional<CloseFriends>
    findByUserIdAndCloseFriendId(
            String userId,
            String closeFriendId
    );

    List<CloseFriends>
    findByUserId(String userId);

    long countByUserId(String userId);

    void deleteByUserIdAndCloseFriendId(
            String userId,
            String closeFriendId
    );

    void deleteByUserIdOrCloseFriendId(String userId, String closeFriendId);


    List<CloseFriends> findByCloseFriendId(String closeFriendId);

}