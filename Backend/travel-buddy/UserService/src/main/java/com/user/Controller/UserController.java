package com.user.Controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.DTO.PageResponse;
import com.user.DTO.UpdateUserRequest;
import com.user.DTO.UserResponse;
import com.user.Service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(
                userService.updateUser(userId, request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(Pageable pageable) {

        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {

        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @PostMapping("/get-user-by-preferences")
    public ResponseEntity<PageResponse<UserResponse>> getUserByPreferences(
            @RequestBody @Valid List<String> preferences,
            Pageable pageable) {
        return ResponseEntity.ok(userService.getUserByPreferences(pageable, preferences));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String userId) {

        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    public UserService getUserService() {
        return userService;
    }
}
