package com.user.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.user.Entity.Connections;
import com.user.Enum.ConnectionStatus;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepo extends MongoRepository<Connections, String> {

        Optional<Connections> findByFollowerIdAndFollowingId(
                        String followerId,
                        String followingId);

        Page<Connections> findByFollowingIdAndStatus(
                        String followingId,
                        ConnectionStatus status, Pageable pageable);

        Page<Connections> findByFollowerIdAndStatus(
                        String followerId,
                        ConnectionStatus status, Pageable pageable);

        List<Connections> findByFollowingIdAndStatus(
                        String followingId,
                        ConnectionStatus status);

        List<Connections> findByFollowerIdAndStatus(
                        String followerId,
                        ConnectionStatus status);

        long countByFollowerIdAndStatus(
                        String followerId,
                        com.user.Enum.ConnectionStatus status);

        long countByFollowingIdAndStatus(
                        String followingId,
                        ConnectionStatus status);

        void deleteByFollowerIdOrFollowingId(String followerId, String followingId);

        List<Connections> findByFollowerId(String followerId);

        List<Connections> findByFollowingId(String followingId);

        boolean existsByFollowerIdAndFollowingIdAndStatus(
                        String followerId,
                        String followingId,
                        ConnectionStatus status);

}
