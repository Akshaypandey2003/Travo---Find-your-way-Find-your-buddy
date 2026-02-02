package com.user.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.DTO.AuthResponse;
import com.user.Entity.User;
import com.user.Service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        
        System.out.println("User received for registration is: "+user);
        User existingUser = userService.getUserByEmail(user.getEmail());
        if (existingUser != null) 
        {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Conflict status if user already exists
        }
        
        AuthResponse response = userService.addUser(user);

        System.out.println("User saved is: "+response);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        try {
        AuthResponse response = userService.generateToken(user.getUserId(),user.getEmail(), user.getPassword());

        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(response);

    } catch (RuntimeException ex) {
        if ("INVALID_PASSWORD".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    }

}
