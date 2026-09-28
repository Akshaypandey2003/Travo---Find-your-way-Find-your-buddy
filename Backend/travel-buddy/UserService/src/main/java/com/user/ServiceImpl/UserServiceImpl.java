package com.user.ServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user.Config.JwtProvider;
import com.user.DTO.AuthResponse;
import com.user.DTO.LoginRequest;
import com.user.DTO.MessageResponse;
import com.user.DTO.PageResponse;
import com.user.DTO.RegisterRequest;
import com.user.DTO.ResetPasswordRequest;
import com.user.DTO.UpdateUserRequest;
import com.user.DTO.UserResponse;
import com.user.Entity.CloseFriends;
import com.user.Entity.Connections;
import com.user.Entity.PasswordResetToken;
import com.user.Entity.User;
import com.user.Enum.AccountType;
import com.user.Exceptions.InvalidPasswordException;
import com.user.Exceptions.InvalidResetTokenException;
import com.user.Exceptions.UserConflictException;
import com.user.Exceptions.UserNotFoundException;
import com.user.Helper.AppConstants;
import com.user.Helper.UserMapper;
import com.user.Repository.CloseFriendsRepo;
import com.user.Repository.ConnectionRepo;
import com.user.Repository.PasswordResetTokenRepo;
import com.user.Repository.UserRepo;
import com.user.Service.UserService;

import jakarta.ws.rs.InternalServerErrorException;

@Service
@SuppressWarnings("unused")
public class UserServiceImpl implements UserService {

    private UserRepo userRepo;
    private JwtProvider jwtProvider;
    private PasswordEncoder passwordEncoder;
    private final ConnectionRepo connectionRepo;
    private final CloseFriendsRepo closeFriendsRepo;
    private final UserEventProducer userEventProducer;
    private final UserMapper userMapper;
    private final PasswordResetTokenRepo tokenRepo;
    private final UserNotificationProducer notificationProducer;
    private final RefreshTokenService refreshTokenService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Override
    public List<String> getPublicUserIds(int page, int size) {
        return userRepo.findByAccountType(
                        AccountType.PUBLIC,
                        PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(Sort.Direction.ASC, "userId")))
                .getContent()
                .stream()
                .map(User::getUserId)
                .toList();
    }

    public UserServiceImpl(UserRepo userRepo,
            JwtProvider jwtProvider,
            PasswordEncoder passwordEncoder, UserMapper userMapper, ConnectionRepo connectionRepo,
            CloseFriendsRepo closeFriendsRepo, UserEventProducer userEventProducer, PasswordResetTokenRepo tokenRepo,
            UserNotificationProducer notificationProducer,RefreshTokenService refreshTokenService) {
        this.userRepo = userRepo;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.closeFriendsRepo = closeFriendsRepo;
        this.connectionRepo = connectionRepo;
        this.userMapper = userMapper;
        this.userEventProducer = userEventProducer;
        this.tokenRepo = tokenRepo;
        this.notificationProducer = notificationProducer;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    @Override
    public AuthResponse addUser(RegisterRequest request) {

        // Check if user exists
        if (userRepo.findByEmail(request.getEmail()).isPresent())
            throw new UserConflictException("User already exists");

        // Convert DTO → Entity
        User user = userMapper.toEntity(request);
        user.setAccountType(AccountType.PUBLIC);

        // Encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set profile pic
        if (request.getGender() != null &&
                request.getGender().equalsIgnoreCase("male"))
            user.setProfilePic(AppConstants.DEFAULT_MALE_PIC);
        else
            user.setProfilePic(AppConstants.DEFAULT_FEMALE_PIC);

        // Assign role
        user.setRole(
                request.getEmail().endsWith("@admin.com")
                        ? "ROLE_ADMIN"
                        : "ROLE_USER");

        // Save user
        User savedUser = userRepo.save(user);

        // Generate roles list
        List<String> roles = Arrays.asList(savedUser.getRole());

        // Generate JWT
        String token = jwtProvider.generateToken(
                savedUser.getUserId(),
                savedUser.getEmail(),
                roles);

        // Generate refresh token
        String refreshToken = UUID.randomUUID().toString();

        // Convert to response DTO
        UserResponse userResponse = userMapper.toResponse(savedUser);

        // Service level events
        userEventProducer.publishUserCreated(savedUser.getUserId(), savedUser.getName(), savedUser.getEmail());

        // User notification
        notificationProducer.sendWelcomeNotification(savedUser.getUserId(), savedUser.getName(), savedUser.getEmail());

        return new AuthResponse(
                userResponse,
                token,
                refreshToken,
                System.currentTimeMillis() + 3600000,
                roles,
                new MessageResponse(
                        "User registered successfully",
                        "success"));
    }

    // Retrieve all users
    @Override
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {

        Page<User> page = userRepo.findAll(pageable);

        if (page.isEmpty()) {
            throw new UserNotFoundException("No users found with the given preferences.");
        }

        List<UserResponse> content = page.getContent()
                .stream()
                .map(userMapper::toResponse)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());

    }

    @Override
    public PageResponse<UserResponse> getUserByPreferences(Pageable pageable, List<String> preferences) {

        Page<User> page = userRepo.findByPreferencesInIgnoreCase(pageable, preferences);

        if (page.isEmpty()) {
            throw new UserNotFoundException("No users found with the given preferences.");
        }

        List<UserResponse> content = page.getContent()
                .stream()
                .map(userMapper::toResponse)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }

    // Retrieve a user by ID
    @Override
    @Cacheable(value = "users", key = "#userId")
    public UserResponse getUserById(String userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toResponse(user);
    }

    // Delete a user by ID
    @Transactional
    @Override
    @CacheEvict(value = "users", key = "#userId")
    public void deleteUser(String userId) {

        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        logger.info("Deleting user: {}", userId);

        /*
         * STEP 1: Handle followers
         * These users follow the deleting user
         * Their followingCount must decrement
         */
        List<Connections> followers = connectionRepo.findByFollowingId(userId);

        for (Connections connection : followers) {
            userRepo.decrementFollowingCount(connection.getFollowerId());
        }

        /*
         * STEP 2: Handle following
         * These users are followed by deleting user
         * Their followersCount must decrement
         */
        List<Connections> following = connectionRepo.findByFollowerId(userId);

        for (Connections connection : following) {
            userRepo.decrementFollowersCount(connection.getFollowingId());
        }

        /*
         * STEP 3: Handle close friends where user added others
         * decrement closeFriendsCount of user
         */
        List<CloseFriends> userCloseFriends = closeFriendsRepo.findByUserId(userId);

        for (CloseFriends cf : userCloseFriends) {
            userRepo.decrementCloseFriendsCount(userId);
        }

        /*
         * STEP 4: Handle close friends where user is added by others
         * decrement closeFriendsCount of those users
         */
        List<CloseFriends> addedByOthers = closeFriendsRepo.findByCloseFriendId(userId);

        for (CloseFriends cf : addedByOthers) {
            userRepo.decrementCloseFriendsCount(cf.getUserId());
        }

        /*
         * STEP 5: Delete relationships
         */
        connectionRepo.deleteByFollowerIdOrFollowingId(userId, userId);

        closeFriendsRepo.deleteByUserIdOrCloseFriendId(userId, userId);

        /*
         * STEP 6: Delete user
         */
        userRepo.deleteById(userId);

        userEventProducer.publishUserDeleted(userId);

        logger.info("User deleted successfully: {}", userId);
    }

    @Override
    @Cacheable(value = "users", key = "#email")
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with email: " + email));
    }

    @Transactional
    @Override
    @CacheEvict(value = "users", key = "#userId")
    public UserResponse updateUser(
            String userId,
            UpdateUserRequest request) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Map<String, Object> updatedFields = new HashMap<>();

        if (request.getName() != null) {
            user.setName(request.getName());
            updatedFields.put("name", request.getName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
            updatedFields.put("phone", request.getPhone());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
            updatedFields.put("bio", request.getBio());
        }

        if (request.getCountry() != null) {
            user.setCountry(request.getCountry());
            updatedFields.put("country", request.getCountry());
        }

        if (request.getState() != null) {
            user.setState(request.getState());
            updatedFields.put("state", request.getState());
        }

        if (request.getCity() != null) {
            user.setCity(request.getCity());
            updatedFields.put("city", request.getCity());
        }

        if (request.getProfilePic() != null) {
            user.setProfilePic(request.getProfilePic());
            updatedFields.put("profilePic", request.getProfilePic());
        }

        if (request.getCloudinaryImagePublicId() != null) {
            user.setCloudinaryImagePublicId(
                    request.getCloudinaryImagePublicId());
            updatedFields.put("cloudinaryImagePublicId",
                    request.getCloudinaryImagePublicId());
        }
        if (request.getAccountType() != null) {
            user.setAccountType(request.getAccountType());
            updatedFields.put("accountType", request.getAccountType());
        }
        if( request.getPreferences() != null) {
            user.setPreferences(request.getPreferences());
            updatedFields.put("preferences", request.getPreferences());
        }

        User saved = userRepo.save(user);

        logger.info("User updated: {}", saved.getUserId());

        // publish event only if something changed
        if (!updatedFields.isEmpty()) {
            userEventProducer.publishUserUpdated(userId, updatedFields);
        }

        return userMapper.toResponse(saved);
    }

    public AuthResponse generateToken(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with email: " + request.getEmail()));
        ;

        if (user == null)
            throw new UserNotFoundException("User not found");

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()))
            throw new InvalidPasswordException("Invalid password");

        List<String> roles = Arrays.asList(user.getRole());

        // Generate JWT Token
        String accessToken = jwtProvider.generateToken(user.getUserId(), user.getEmail(), roles);

        String refreshToken = UUID.randomUUID().toString();

        // ✅ SAVE IT
        refreshTokenService.save(refreshToken, user.getUserId());

        UserResponse userResponse = userMapper.toResponse(user);

        return new AuthResponse(
                userResponse,
                accessToken,
                refreshToken,
                System.currentTimeMillis() + 3600000,
                roles,
                new MessageResponse("User logged in successfully", "success"));
    }
    @Override
    public MessageResponse forgotPassword(String email) {

        Optional<User> optionalUser = userRepo.findByEmail(email);

        if (optionalUser.isEmpty()) {

            // DO NOT reveal user doesn't exist
            logger.warn("Password reset requested for non-existing email: {}", email);
            throw new UserNotFoundException("Account does not exists");
        }

        User user = optionalUser.get();

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .userId(user.getUserId())
                .expiryTime(System.currentTimeMillis() + (15 * 60 * 1000))
                .used(false)
                .build();

        tokenRepo.save(resetToken);

        String resetLink = "http://localhost:3000/reset-password?token=" + token;

        notificationProducer.sendPasswordResetNotification(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                resetLink);
        
        return new MessageResponse("Password reset link sent to email", "success");
    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetToken resetToken = tokenRepo.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidResetTokenException(
                        "Invalid or expired token"));
        if (resetToken.isUsed()) {
            throw new InvalidResetTokenException("Token already used");
        }

        if (resetToken.getExpiryTime() < System.currentTimeMillis()) {
            tokenRepo.delete(resetToken);
            throw new InvalidResetTokenException("Token expired");
        }

        User user = userRepo.findById(resetToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepo.save(user);

        tokenRepo.delete(resetToken);
        tokenRepo.deleteByUserId(user.getUserId());

        notificationProducer.sendPasswordResetSuccessNotification(
                user.getUserId(),
                user.getName(),
                user.getEmail());

        logger.info("Password reset successful for userId={}", user.getUserId());
    }

    protected String getUserName(String userId) {
        return userRepo.findById(userId)
                .map(User::getName)
                .orElse("Unknown User");
    }
}
