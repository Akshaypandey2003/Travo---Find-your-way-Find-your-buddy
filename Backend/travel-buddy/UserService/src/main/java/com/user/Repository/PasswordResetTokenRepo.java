package com.user.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.user.Entity.PasswordResetToken;

public interface PasswordResetTokenRepo
        extends MongoRepository<PasswordResetToken, String> {

    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUserId(String userId);
}