package com.user.Service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.user.DTO.CloseFriendResponse;
import com.user.DTO.ConnectionResponse;
import com.user.DTO.ConnectionStatusResponse;
import com.user.DTO.PageResponse;
import com.user.DTO.UserSummary;

public interface ConnectionService {

    ConnectionResponse sendFollowRequest(String followerId, String followingId);

    void unfollowUser(String followerId, String followingId);

    PageResponse<UserSummary> getFollowers(String userId,Pageable pageable);

    PageResponse<UserSummary> getFollowing(
                        String userId,
                        Pageable pageable);

    PageResponse<UserSummary> getMutualFollowing(String userA, String userB, Pageable pageable);
    ConnectionResponse acceptFollowRequest(String userId, String followerId);

    ConnectionStatusResponse
     checkFollowStatus(String currentUserId,
                        String targetUserId);

    void rejectFollowRequest(String userId, String followerId);

    // List<String> getFollowingIds(String userId);

    // List<String> getFollowersIds(String userId);

    CloseFriendResponse addCloseFriend(
            String userId,
            String closeFriendId);

    void removeCloseFriend(
            String userId,
            String closeFriendId);

    List<CloseFriendResponse> getCloseFriends(String userId);



   


}