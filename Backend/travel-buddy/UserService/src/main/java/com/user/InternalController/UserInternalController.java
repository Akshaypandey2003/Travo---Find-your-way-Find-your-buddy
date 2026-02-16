package com.user.InternalController;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.user.DTO.UserResponse;
import com.user.DTO.UserSummary;
import com.user.Entity.User;
import com.user.Helper.UserMapper;
import com.user.Service.UserService;

@Controller
@RequestMapping("/user/internal")
public class UserInternalController {
    
    private final UserService userService;

    private final UserMapper userMapper;

    public UserInternalController(UserService userService, UserMapper userMapper)
    {
        this.userService = userService;
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
}
