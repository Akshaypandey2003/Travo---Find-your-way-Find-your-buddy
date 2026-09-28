package com.user.ServiceImpl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user.DTO.CloseFriendResponse;
import com.user.DTO.ConnectionResponse;
import com.user.DTO.ConnectionStatusResponse;
import com.user.DTO.PageResponse;
import com.user.DTO.UserSummary;
import com.user.Entity.CloseFriends;
import com.user.Entity.Connections;
import com.user.Entity.User;
import com.user.Enum.AccountType;
import com.user.Enum.ConnectionStatus;
import com.user.Enum.FollowStatus;
import com.user.Exceptions.ConnectionRequestException;
import com.user.Exceptions.UserNotFoundException;
import com.user.Helper.ConnectionMapper;
import com.user.Repository.CloseFriendsRepo;
import com.user.Repository.ConnectionRepo;
import com.user.Repository.UserRepo;
import com.user.Service.ConnectionService;

import jakarta.ws.rs.InternalServerErrorException;

@Service
@SuppressWarnings("unused")
public class ConnectionServiceImpl implements ConnectionService {

        private static final Logger logger = LoggerFactory.getLogger(ConnectionServiceImpl.class);

        private final ConnectionRepo connectionRepo;
        private final ConnectionMapper connectionMapper;
        private final UserRepo userRepo;
        private final CloseFriendsRepo closeFriendsRepo;
        private final UserNotificationProducer notificationProducer;
        private final ConnectionEventProducer connectionEventProducer;

        public ConnectionServiceImpl(
                        ConnectionRepo connectionRepo,
                        ConnectionMapper connectionMapper, UserRepo userRepo,
                        CloseFriendsRepo closeFriendsRepo, UserNotificationProducer notificationProducer,
                        ConnectionEventProducer connectionEventProducer) {

                this.connectionRepo = connectionRepo;
                this.connectionMapper = connectionMapper;
                this.userRepo = userRepo;
                this.closeFriendsRepo = closeFriendsRepo;
                this.notificationProducer = notificationProducer;
                this.connectionEventProducer = connectionEventProducer;
        }

        // FOLLOW USER
        @Override
        public ConnectionResponse sendFollowRequest(String followerId, String followingId) {

                if (followerId.equals(followingId)) {
                        throw new ConnectionRequestException("Invalid Action !");
                }

                User follower = userRepo.findById(followerId)
                                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

                User following = userRepo.findById(followingId)
                                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

                Optional<Connections> existingOpt = connectionRepo.findByFollowerIdAndFollowingId(followerId,
                                followingId);

                if (existingOpt.isPresent()) {

                        Connections existing = existingOpt.get();

                        if (existing.getStatus() == ConnectionStatus.FOLLOWING) {
                                throw new ConnectionRequestException("Already following");
                        }

                        if (existing.getStatus() == ConnectionStatus.REQUESTED) {
                                throw new ConnectionRequestException("Follow request already sent");
                        }
                }

                // Determine status
                ConnectionStatus status = following.getAccountType() == AccountType.PRIVATE
                                ? ConnectionStatus.REQUESTED
                                : ConnectionStatus.FOLLOWING;

                Connections connection = Connections.builder()
                                .followerId(followerId)
                                .followingId(followingId)
                                .status(status)
                                .build();

                connectionRepo.save(connection);

                // If public account → immediately follow
                if (status == ConnectionStatus.FOLLOWING) {

                        userRepo.incrementFollowingCount(followerId);
                        userRepo.incrementFollowersCount(followingId);
                        notificationProducer.newFollower(followerId, follower.getName(), followingId);
                        connectionEventProducer.publishCreated(followerId, followingId);

                } 
                else
                {
                notificationProducer.friendRequestSend(followerId, follower.getName(), followingId);
                    
                }
                return connectionMapper.toResponse(connection);
        }

        // UNFOLLOW USER
        @Override
        public void unfollowUser(String followerId, String followingId) {

                Connections connection = connectionRepo.findByFollowerIdAndFollowingId(followerId, followingId)
                                .orElseThrow(() -> new ConnectionRequestException("Connection not found"));

                if (connection.getStatus() != ConnectionStatus.FOLLOWING)
                        throw new IllegalStateException("Not following");

                connectionRepo.delete(connection);

                connectionEventProducer.publishRemoved(followerId, followingId);

                userRepo.decrementFollowingCount(followerId);
                userRepo.decrementFollowersCount(followingId);
        }

        // ACCEPT REQUEST
        @Override
        public ConnectionResponse acceptFollowRequest(String receiverId, String followerId) {

                Connections connection = connectionRepo
                                .findByFollowerIdAndFollowingId(followerId, receiverId)
                                .orElseThrow(() -> new ConnectionRequestException("Follow request not found"));

                // Security check → only receiver can accept
                if (!connection.getFollowingId().equals(receiverId)) {
                        throw new ConnectionRequestException("Unauthorized action");
                }

                if (connection.getStatus() != ConnectionStatus.REQUESTED) {
                        throw new IllegalStateException("Invalid request state");
                }

                connection.setStatus(ConnectionStatus.FOLLOWING);

                connectionRepo.save(connection);

                userRepo.incrementFollowingCount(followerId);
                userRepo.incrementFollowersCount(receiverId);

                connectionEventProducer.publishCreated(followerId, receiverId);

                User sender = userRepo.findById(receiverId)
                                .orElseThrow(() -> new UserNotFoundException("Sender not found"));

                notificationProducer.friendRequestAccept(receiverId, sender.getName(),followerId);

                return connectionMapper.toResponse(connection);
        }

        // REJECT REQUEST
        @Override
        public void rejectFollowRequest(String userId, String followerId) {

                Connections connection = connectionRepo.findByFollowerIdAndFollowingId(followerId, userId)
                                .orElseThrow(() -> new ConnectionRequestException("Request not found"));

                connectionRepo.delete(connection);
        }

        @Override
        public PageResponse<UserSummary> getFollowers(
                        String userId,
                        Pageable pageable) {

                Page<Connections> connectionsPage = connectionRepo.findByFollowingIdAndStatus(
                                userId,
                                ConnectionStatus.FOLLOWING,
                                pageable);

                if (connectionsPage.isEmpty()) {
                        return PageResponse.<UserSummary>builder()
                                        .content(List.of())
                                        .page(connectionsPage.getNumber())
                                        .size(connectionsPage.getSize())
                                        .totalElements(connectionsPage.getTotalElements())
                                        .totalPages(connectionsPage.getTotalPages())
                                        .last(connectionsPage.isLast())
                                        .build();
                }

                // Extract IDs
                List<String> followerIds = connectionsPage.getContent()
                                .stream()
                                .map(Connections::getFollowerId)
                                .toList();

                // Fetch users in single query
                List<User> users = userRepo.findByUserIdIn(followerIds);

                // Convert to UserSummary
                List<UserSummary> summaries = users.stream()
                                .map(user -> UserSummary.builder()
                                                .userId(user.getUserId())
                                                .name(user.getName())
                                                .profilePic(user.getProfilePic())
                                                .build())
                                .toList();

                return PageResponse.<UserSummary>builder()
                                .content(summaries)
                                .page(connectionsPage.getNumber())
                                .size(connectionsPage.getSize())
                                .totalElements(connectionsPage.getTotalElements())
                                .totalPages(connectionsPage.getTotalPages())
                                .last(connectionsPage.isLast())
                                .build();
        }

        @Override
        public PageResponse<UserSummary> getFollowing(
                        String userId,
                        Pageable pageable) {

                Page<Connections> connectionsPage = connectionRepo.findByFollowerIdAndStatus(
                                userId,
                                ConnectionStatus.FOLLOWING,
                                pageable);

                if (connectionsPage.isEmpty()) {
                        return PageResponse.<UserSummary>builder()
                                        .content(List.of())
                                        .page(connectionsPage.getNumber())
                                        .size(connectionsPage.getSize())
                                        .totalElements(connectionsPage.getTotalElements())
                                        .totalPages(connectionsPage.getTotalPages())
                                        .last(connectionsPage.isLast())
                                        .build();
                }

                List<String> followingIds = connectionsPage.getContent()
                                .stream()
                                .map(Connections::getFollowingId)
                                .toList();

                List<User> users = userRepo.findByUserIdIn(followingIds);

                List<UserSummary> summaries = users.stream()
                                .map(user -> UserSummary.builder()
                                                .userId(user.getUserId())
                                                .name(user.getName())
                                                .profilePic(user.getProfilePic())
                                                .build())
                                .toList();

                return PageResponse.<UserSummary>builder()
                                .content(summaries)
                                .page(connectionsPage.getNumber())
                                .size(connectionsPage.getSize())
                                .totalElements(connectionsPage.getTotalElements())
                                .totalPages(connectionsPage.getTotalPages())
                                .last(connectionsPage.isLast())
                                .build();
        }

        @Override
        public ConnectionStatusResponse checkFollowStatus(String currentUserId,
                        String targetUserId) {

                if (currentUserId.equals(targetUserId)) {
                        return new ConnectionStatusResponse(targetUserId,
                                        FollowStatus.SELF);
                }

                boolean iFollow = connectionRepo.existsByFollowerIdAndFollowingIdAndStatus(
                                currentUserId,
                                targetUserId,
                                ConnectionStatus.FOLLOWING);

                boolean theyFollow = connectionRepo.existsByFollowerIdAndFollowingIdAndStatus(
                                targetUserId,
                                currentUserId,
                                ConnectionStatus.FOLLOWING);

                boolean requested = connectionRepo.existsByFollowerIdAndFollowingIdAndStatus(
                                currentUserId,
                                targetUserId,
                                ConnectionStatus.REQUESTED);

                FollowStatus status;

                if (requested) {
                        status = FollowStatus.REQUESTED;
                } else if (iFollow && theyFollow) {
                        status = FollowStatus.MUTUAL;
                } else if (iFollow) {
                        status = FollowStatus.FOLLOWING;
                } else if (theyFollow) {
                        status = FollowStatus.FOLLOWED_BY;
                } else {
                        status = FollowStatus.NOT_FOLLOWING;
                }

                return new ConnectionStatusResponse(targetUserId, status);
        }

        @Override
        public PageResponse<UserSummary> getMutualFollowing(String userA, String userB, Pageable pageable) {

                if (userA.equals(userB)) {
                        throw new IllegalArgumentException("Both users cannot be same");
                }

                // 1️⃣ Get ALL following IDs of both users (NOT paginated)
                List<String> followingA = connectionRepo
                                .findByFollowerIdAndStatus(userA, ConnectionStatus.FOLLOWING)
                                .stream()
                                .map(Connections::getFollowingId)
                                .toList();

                List<String> followingB = connectionRepo
                                .findByFollowerIdAndStatus(userB, ConnectionStatus.FOLLOWING)
                                .stream()
                                .map(Connections::getFollowingId)
                                .toList();

                if (followingA.isEmpty() || followingB.isEmpty()) {

                        return PageResponse.<UserSummary>builder()
                                        .content(List.of())
                                        .page(pageable.getPageNumber())
                                        .size(pageable.getPageSize())
                                        .totalElements(0)
                                        .totalPages(0)
                                        .last(true)
                                        .build();
                }

                // 2️⃣ Find intersection
                Set<String> mutualIds = new HashSet<>(followingA);

                mutualIds.retainAll(followingB);

                if (mutualIds.isEmpty()) {

                        return PageResponse.<UserSummary>builder()
                                        .content(List.of())
                                        .page(pageable.getPageNumber())
                                        .size(pageable.getPageSize())
                                        .totalElements(0)
                                        .totalPages(0)
                                        .last(true)
                                        .build();
                }

                // 3️⃣ Convert to list for pagination
                List<String> mutualList = new ArrayList<>(mutualIds);

                int start = (int) pageable.getOffset();
                int end = Math.min(start + pageable.getPageSize(), mutualList.size());

                List<String> paginatedIds = start >= mutualList.size()
                                ? List.of()
                                : mutualList.subList(start, end);

                // 4️⃣ Fetch users
                List<User> users = userRepo.findByUserIdIn(paginatedIds);

                // 5️⃣ Convert to UserSummary
                List<UserSummary> summaries = users.stream()
                                .map(user -> UserSummary.builder()
                                                .userId(user.getUserId())
                                                .name(user.getName())
                                                .profilePic(user.getProfilePic())
                                                .build())
                                .toList();

                // 6️⃣ Return paginated response
                return PageResponse.<UserSummary>builder()
                                .content(summaries)
                                .page(pageable.getPageNumber())
                                .size(pageable.getPageSize())
                                .totalElements(mutualList.size())
                                .totalPages(
                                                (int) Math.ceil(
                                                                (double) mutualList.size() / pageable.getPageSize()))
                                .last(end >= mutualList.size())
                                .build();
        }

        @Override
        public CloseFriendResponse addCloseFriend(
                        String userId,
                        String closeFriendId) {

                if (userId.equals(closeFriendId)) {
                        throw new RuntimeException(
                                        "Cannot add yourself as close friend");
                }

                // verify both users exist
                User user = userRepo.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                User friend = userRepo.findById(closeFriendId)
                                .orElseThrow(() -> new UserNotFoundException("Close friend not found"));

                closeFriendsRepo
                                .findByUserIdAndCloseFriendId(
                                                userId,
                                                closeFriendId)
                                .ifPresent(c -> {
                                        throw new RuntimeException(
                                                        "Already close friend");
                                });

                CloseFriends savedCloseFriend = closeFriendsRepo.save(
                                CloseFriends.builder()
                                                .userId(userId)
                                                .closeFriendId(closeFriendId)
                                                .build());

                // update counter
                userRepo.incrementCloseFriendsCount(userId);

                UserSummary userSummary = UserSummary.builder().userId(friend.getUserId()).name(friend.getName())
                                .profilePic(friend.getProfilePic()).build();

                return CloseFriendResponse.builder()
                                .id(savedCloseFriend.getId())
                                .addedAt(savedCloseFriend.getCreatedAt())
                                .user(userSummary)
                                .build();
        }

        @Override
        public void removeCloseFriend(
                        String userId,
                        String closeFriendId) {

                CloseFriends relation = closeFriendsRepo
                                .findByUserIdAndCloseFriendId(userId, closeFriendId)
                                .orElseThrow(() -> new ConnectionRequestException("Close friend not found"));

                closeFriendsRepo.delete(relation);
                userRepo.decrementCloseFriendsCount(userId);
        }

        @Override
        public List<CloseFriendResponse> getCloseFriends(String userId) {

                // 1️⃣ Fetch all close friend relations
                List<CloseFriends> relations = closeFriendsRepo.findByUserId(userId);

                if (relations.isEmpty()) {
                        return List.of();
                }

                // 2️⃣ Extract friend IDs
                List<String> friendIds = relations.stream()
                                .map(CloseFriends::getCloseFriendId)
                                .toList();

                // 3️⃣ Fetch all users in one query
                List<User> users = userRepo.findByUserIdIn(friendIds);

                // 4️⃣ Convert users to Map for fast lookup
                Map<String, User> userMap = users.stream()
                                .collect(Collectors.toMap(User::getUserId, user -> user));

                // 5️⃣ Build final response
                return relations.stream()
                                .map(relation -> {
                                        User friend = userMap.get(relation.getCloseFriendId());

                                        return CloseFriendResponse.builder()
                                                        .id(relation.getId())
                                                        .addedAt(relation.getCreatedAt())
                                                        .user(
                                                                        friend == null ? null
                                                                                        : UserSummary.builder()
                                                                                                        .userId(friend.getUserId())
                                                                                                        .name(friend.getName())
                                                                                                        .profilePic(friend
                                                                                                                        .getProfilePic())
                                                                                                        .build())
                                                        .build();
                                })
                                .toList();
        }

}