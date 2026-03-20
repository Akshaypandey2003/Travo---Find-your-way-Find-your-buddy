package com.user.Controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.DTO.CloseFriendResponse;
import com.user.DTO.ConnectionResponse;
import com.user.DTO.PageResponse;
import com.user.DTO.UserSummary;
import com.user.Service.ConnectionService;

@RestController
@RequestMapping("/api/v1/connections")
public class ConnectionController {

        private final ConnectionService connectionService;

        public ConnectionController(
                        ConnectionService connectionService) {
                this.connectionService = connectionService;
        }

        // Follow
        @PostMapping("/follow/{userId}")
        public ResponseEntity<ConnectionResponse> followUser(
                        @AuthenticationPrincipal String followerId,
                        @PathVariable String userId) {
                return ResponseEntity.ok(
                                connectionService.sendFollowRequest(
                                                followerId,
                                                userId));
        }

        @PostMapping("/accept/{followerId}")
        public ResponseEntity<ConnectionResponse> acceptFollowRequest(
                        @AuthenticationPrincipal String userId,
                        @PathVariable String followerId) {

                return ResponseEntity.ok(
                                connectionService.acceptFollowRequest(
                                                userId,
                                                followerId));
        }

        @DeleteMapping("/reject/{followerId}")
        public ResponseEntity<String> rejectFollowRequest(
                        @AuthenticationPrincipal String userId,
                        @PathVariable String followerId) {

                connectionService.rejectFollowRequest(
                                userId,
                                followerId);

                return ResponseEntity.ok("Follow request rejected");
        }

        // Unfollow
        @DeleteMapping("/unfollow/{userId}")
        public ResponseEntity<String> unfollowUser(
                        @AuthenticationPrincipal String followerId,
                        @PathVariable String userId) {

                connectionService.unfollowUser(
                                followerId,
                                userId);

                return ResponseEntity.ok("Unfollowed successfully");
        }

        // Get Followers
        @GetMapping("/followers")
        public ResponseEntity<PageResponse<UserSummary>> getFollowers(@AuthenticationPrincipal String userId,Pageable pageable) {

                return ResponseEntity.ok(
                                connectionService.getFollowers(userId,pageable));
        }
        // Get Following
        @GetMapping("/followings")
        public ResponseEntity<PageResponse<UserSummary>> getFollowing(@AuthenticationPrincipal String userId,Pageable pageable) {

                return ResponseEntity.ok(
                                connectionService.getFollowing(userId,pageable));
        }

        @PostMapping("/close-friends/{closeFriendId}")
        public ResponseEntity<CloseFriendResponse> addCloseFriend(
                        @AuthenticationPrincipal String userId,
                        @PathVariable String closeFriendId) {

                return ResponseEntity.ok(
                                connectionService.addCloseFriend(
                                                userId,
                                                closeFriendId));
        }

        @DeleteMapping("/close-friends/{closeFriendId}")
        public ResponseEntity<String> removeCloseFriend(
                        @AuthenticationPrincipal String userId,
                        @PathVariable String closeFriendId) {

                connectionService.removeCloseFriend(
                                userId,
                                closeFriendId);

                return ResponseEntity.ok(
                                "Removed from close friends");
        }

        @GetMapping("/close-friends")
        public ResponseEntity<List<CloseFriendResponse>> getCloseFriends(
                        @AuthenticationPrincipal String userId) {

                return ResponseEntity.ok(
                                connectionService.getCloseFriends(userId));
        }
}