package com.user.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.user.Entity.User;
import com.user.Service.ImageService;
import com.user.Service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @PostMapping("/add-user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        
        System.out.println("USer received for registration is: "+user);
        User existingUser = userService.getUserByEmail(user.getEmail());
        if (existingUser != null) 
        {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Conflict status if user already exists
        }
        User savedUser = userService.addUser(user);
        System.out.println("User saved is: "+savedUser);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    //--------------- file uploading is being handled in frontend itself ------------------
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId,
            @RequestBody User updatedUser) {
        // String jwtUserId = jwt.getClaim("sub");

        // if (!jwtUserId.equals(userId)) {
        // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        // }

        User existingUser = userService.getUserById(userId);
        if (existingUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (updatedUser.getProfilePic() != null && updatedUser.getCloudinaryImagePublicId() != null) {

            // Delete the old image from Cloudinary if it exists
            if (existingUser.getCloudinaryImagePublicId() != null) {
                imageService.removeImage(existingUser.getCloudinaryImagePublicId());
            }
            existingUser.setProfilePic(updatedUser.getProfilePic());
            existingUser.setCloudinaryImagePublicId(updatedUser.getCloudinaryImagePublicId());
        }

        // Update only non-null fields
        if (updatedUser.getName() != null)
            existingUser.setName(updatedUser.getName());
        if (updatedUser.getEmail() != null)
            existingUser.setEmail(updatedUser.getEmail());
        if (updatedUser.getPhone() != null)
            existingUser.setPhone(updatedUser.getPhone());
        if (updatedUser.getBio() != null)
            existingUser.setBio(updatedUser.getBio());
        if (updatedUser.getCountry() != null)
            existingUser.setCountry(updatedUser.getCountry());
        if (updatedUser.getState() != null)
            existingUser.setState(updatedUser.getState());
        if (updatedUser.getCity() != null)
            existingUser.setCity(updatedUser.getCity());
        if (updatedUser.getGender() != null)
            existingUser.setGender(updatedUser.getGender());
        if (updatedUser.getPreferences() != null)
            existingUser.setPreferences(updatedUser.getPreferences());
        // if (updatedUser.getPassword() != null) {
        // String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
        // existingUser.setPassword(encodedPassword);
        // }

        User savedUser = userService.updateUser(existingUser); // Save changes
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }
    @PutMapping("/updateLike/{userId}/{senderId}")
    public ResponseEntity<Object> updateLikes(@PathVariable String userId, @PathVariable String senderId)
    {
        return new ResponseEntity<>(userService.updateLikes(userId, senderId), HttpStatus.OK);
    }
    @PostMapping("/validate-user")
    public ResponseEntity<User> validateUser(@RequestBody User user) {

        User existingUser = userService.getUserByEmail(user.getEmail());

        if (existingUser == null )
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else if(!passwordEncoder.matches(user.getPassword(), existingUser.getPassword()))
        {
          return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(existingUser);
    }

    @GetMapping("/get-user")
    public ResponseEntity<List<User>> getAllUser( @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(userService.getAllUser(pageable), HttpStatus.OK);
    }

    @GetMapping("/get-user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @GetMapping("/get-user-by-preferences")
    public ResponseEntity<List<User>> getUserByPreferences(@RequestBody List<String> preferences) {
        return new ResponseEntity<>(userService.getUserByPreferences(preferences), HttpStatus.OK);
    }
    
    @PostMapping("/addCloseFriend/{userId}/{friendId}")
    public ResponseEntity<Object> addCloseFriend(@PathVariable String userId, @PathVariable String friendId) {
        return new ResponseEntity<>(Map.of("message: ",userService.addCloseFriend(userId,friendId)), HttpStatus.OK);
    }
    @DeleteMapping("/removeCloseFriend/{userId}/{friendId}")
    public ResponseEntity<Object> removeCloseFriend(@PathVariable String userId, @PathVariable String friendId) {
        return new ResponseEntity<>(Map.of("message: ",userService.removeCloseFriend(userId,friendId)), HttpStatus.OK);
    }

}
