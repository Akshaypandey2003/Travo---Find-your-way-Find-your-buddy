package com.user.InternalController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.user.DTO.UserSummary;
import com.user.Entity.User;
import com.user.Service.UserService;

@Controller
@RequestMapping("/user/internal")
public class UserInternalController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/summary/{userId}")
    public ResponseEntity<UserSummary> getUserSummaryById(String userId) {
        User user = userService.getUserById(userId);
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
