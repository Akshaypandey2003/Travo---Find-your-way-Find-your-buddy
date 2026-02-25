package com.user.InternalController;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.user.DTO.UserResponse;
import com.user.DTO.UserSummary;
import com.user.Entity.Connections;
import com.user.Entity.User;
import com.user.Enum.ConnectionStatus;
import com.user.Helper.UserMapper;
import com.user.Repository.ConnectionRepo;
import com.user.Service.ConnectionService;
import com.user.Service.UserService;

@Controller
@RequestMapping("/user/internal")
public class UserInternalController {
    
    private final UserService userService;
    private final ConnectionRepo connectionRepo;

    private final UserMapper userMapper;

    public UserInternalController(UserService userService, ConnectionRepo connectionRepo, UserMapper userMapper)
    {
        this.userService = userService;
        this.connectionRepo = connectionRepo;
        this.userMapper = userMapper;
    }


    @GetMapping("/summary/{userId}")
    public ResponseEntity<UserSummary> getUserSummaryById(String userId) {
        UserResponse userResponse = userService.getUserById(userId);

        User user = userMapper.toEntity(userResponse);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserSummary summary = UserSummary.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .profilePic(user.getProfilePic())
                .build();

        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/get-all-friends/{userId}")
    public ResponseEntity<List<String>> getAllFriends(String userId) {


        List<String> friends = connectionRepo
                                .findByFollowerIdAndStatus(userId, ConnectionStatus.FOLLOWING)
                                .stream()
                                .map(Connections::getFollowingId)
                                .toList();
        if (friends == null || friends.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(friends);
    }
}
