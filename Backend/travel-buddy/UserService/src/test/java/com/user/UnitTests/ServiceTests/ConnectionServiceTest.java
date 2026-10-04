package com.user.UnitTests.ServiceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import com.user.DTO.*;
import com.user.Entity.*;
import com.user.Enum.*;
import com.user.Exceptions.ConnectionRequestException;
import com.user.Exceptions.UserNotFoundException;
import com.user.Helper.ConnectionMapper;
import com.user.Repository.*;
import com.user.ServiceImpl.ConnectionServiceImpl;
import com.user.ServiceImpl.ConnectionEventProducer;
import com.user.ServiceImpl.UserNotificationProducer;

@ExtendWith(MockitoExtension.class)
public class ConnectionServiceTest {

    @Mock
    private ConnectionRepo connectionRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private CloseFriendsRepo closeFriendsRepo;

    @Mock
    private ConnectionMapper connectionMapper;

    @Mock
    private UserNotificationProducer notificationProducer;

    @Mock
    private ConnectionEventProducer connectionEventProducer;

    @InjectMocks
    private ConnectionServiceImpl connectionService;

    private User publicUser;
    private User privateUser;
    private Connections connection;

    private Pageable pageable;

    @BeforeEach
    void setup() {

        pageable = PageRequest.of(0, 10);

        publicUser = User.builder()
                .userId("user1")
                .name("Akshay")
                .accountType(AccountType.PUBLIC)
                .build();

        privateUser = User.builder()
                .userId("user2")
                .name("John")
                .accountType(AccountType.PRIVATE)
                .build();

        connection = Connections.builder()
                .followerId("user1")
                .followingId("user2")
                .status(ConnectionStatus.REQUESTED)
                .build();
    }

    // =====================================================
    // SEND FOLLOW REQUEST
    // =====================================================

    @Test
    void sendFollowRequest_PublicAccount_Success() {

        when(userRepo.findById("user1")).thenReturn(Optional.of(publicUser));
        when(userRepo.findById("user2")).thenReturn(Optional.of(publicUser));
        when(connectionRepo.findByFollowerIdAndFollowingId("user1", "user2"))
                .thenReturn(Optional.empty());

        when(connectionRepo.save(any())).thenReturn(connection);

        when(connectionMapper.toResponse(any()))
                .thenReturn(ConnectionResponse.builder().build());

        ConnectionResponse response =
                connectionService.sendFollowRequest("user1", "user2");

        assertNotNull(response);

        verify(connectionRepo).save(any());
        verify(userRepo).incrementFollowingCount("user1");
        verify(userRepo).incrementFollowersCount("user2");

        verify(notificationProducer)
                .newFollower("user1", "Akshay", "user2");
    }

    @Test
    void sendFollowRequest_PrivateAccount_Success() {

        when(userRepo.findById("user1")).thenReturn(Optional.of(publicUser));
        when(userRepo.findById("user2")).thenReturn(Optional.of(privateUser));
        when(connectionRepo.findByFollowerIdAndFollowingId(any(), any()))
                .thenReturn(Optional.empty());

        when(connectionRepo.save(any())).thenReturn(connection);

        when(connectionMapper.toResponse(any()))
                .thenReturn(ConnectionResponse.builder().build());

        ConnectionResponse response =
                connectionService.sendFollowRequest("user1", "user2");

        assertNotNull(response);

        verify(notificationProducer)
                .friendRequestSend("user1", "Akshay", "user2");

        verify(userRepo, never())
                .incrementFollowersCount(any());
    }

    @Test
    void sendFollowRequest_SelfFollow_ShouldThrowException() {

        assertThrows(ConnectionRequestException.class,
                () -> connectionService.sendFollowRequest("user1", "user1"));
    }

    @Test
    void sendFollowRequest_FollowerNotFound_ShouldThrowException() {

        when(userRepo.findById("user1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> connectionService.sendFollowRequest("user1", "user2"));
    }

    @Test
    void sendFollowRequest_AlreadyFollowing_ShouldThrowException() {

        connection.setStatus(ConnectionStatus.FOLLOWING);

        when(userRepo.findById(any())).thenReturn(Optional.of(publicUser));
        when(connectionRepo.findByFollowerIdAndFollowingId(any(), any()))
                .thenReturn(Optional.of(connection));

        assertThrows(ConnectionRequestException.class,
                () -> connectionService.sendFollowRequest("user1", "user2"));
    }

    // =====================================================
    // UNFOLLOW USER
    // =====================================================

    @Test
    void unfollowUser_Success() {

        connection.setStatus(ConnectionStatus.FOLLOWING);

        when(connectionRepo.findByFollowerIdAndFollowingId(any(), any()))
                .thenReturn(Optional.of(connection));

        connectionService.unfollowUser("user1", "user2");

        verify(connectionRepo).delete(connection);

        verify(userRepo).decrementFollowingCount("user1");
        verify(userRepo).decrementFollowersCount("user2");
    }

    @Test
    void unfollowUser_NotFound_ShouldThrowException() {

        when(connectionRepo.findByFollowerIdAndFollowingId(any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(ConnectionRequestException.class,
                () -> connectionService.unfollowUser("user1", "user2"));
    }

    // =====================================================
    // ACCEPT REQUEST
    // =====================================================

    @Test
    void acceptFollowRequest_Success() {

        connection.setStatus(ConnectionStatus.REQUESTED);

        when(connectionRepo.findByFollowerIdAndFollowingId("user1", "user2"))
                .thenReturn(Optional.of(connection));

        when(connectionRepo.save(any()))
                .thenReturn(connection);

        when(userRepo.findById("user2"))
                .thenReturn(Optional.of(privateUser));

        when(connectionMapper.toResponse(any()))
                .thenReturn(ConnectionResponse.builder().build());

        ConnectionResponse response =
                connectionService.acceptFollowRequest("user2", "user1");

        assertNotNull(response);

        verify(notificationProducer)
                .friendRequestAccept("user2","John","user1");

        verify(userRepo).incrementFollowersCount("user2");
        verify(userRepo).incrementFollowingCount("user1");
    }

    @Test
    void acceptFollowRequest_NotFound_ShouldThrowException() {

        when(connectionRepo.findByFollowerIdAndFollowingId(any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(ConnectionRequestException.class,
                () -> connectionService.acceptFollowRequest("user2", "user1"));
    }

    // =====================================================
    // REJECT REQUEST
    // =====================================================

    @Test
    void rejectFollowRequest_Success() {

        when(connectionRepo.findByFollowerIdAndFollowingId(any(), any()))
                .thenReturn(Optional.of(connection));

        connectionService.rejectFollowRequest("user2", "user1");

        verify(connectionRepo).delete(connection);
    }

    // =====================================================
    // GET FOLLOWERS
    // =====================================================

    @Test
    void getFollowers_Success() {

        Page<Connections> page =
                new PageImpl<>(List.of(connection), pageable, 1);

        when(connectionRepo.findByFollowingIdAndStatus(any(), any(), any()))
                .thenReturn(page);

        when(userRepo.findByUserIdIn(any()))
                .thenReturn(List.of(publicUser));

        PageResponse<UserSummary> response =
                connectionService.getFollowers("user2", pageable);

        assertEquals(1, response.getContent().size());
    }

    // =====================================================
    // CHECK FOLLOW STATUS
    // =====================================================

    @Test
    void checkFollowStatus_Mutual() {

        when(connectionRepo.existsByFollowerIdAndFollowingIdAndStatus("user1","user2",ConnectionStatus.FOLLOWING))
                .thenReturn(true);

        when(connectionRepo.existsByFollowerIdAndFollowingIdAndStatus("user2","user1",ConnectionStatus.FOLLOWING))
                .thenReturn(true);

        when(connectionRepo.existsByFollowerIdAndFollowingIdAndStatus("user1","user2",ConnectionStatus.REQUESTED))
                .thenReturn(false);

        ConnectionStatusResponse response =
                connectionService.checkFollowStatus("user1", "user2");

        assertEquals(FollowStatus.MUTUAL, response.getStatus());
    }

    // =====================================================
    // ADD CLOSE FRIEND
    // =====================================================

    @Test
    void addCloseFriend_Success() {

        CloseFriends closeFriend =
                CloseFriends.builder()
                        .id("cf1")
                        .userId("user1")
                        .closeFriendId("user2")
                        .createdAt(Instant.now())
                        .build();

        when(userRepo.findById("user1")).thenReturn(Optional.of(publicUser));
        when(userRepo.findById("user2")).thenReturn(Optional.of(privateUser));

        when(closeFriendsRepo.findByUserIdAndCloseFriendId(any(), any()))
                .thenReturn(Optional.empty());

        when(closeFriendsRepo.save(any()))
                .thenReturn(closeFriend);

        CloseFriendResponse response =
                connectionService.addCloseFriend("user1", "user2");

        assertNotNull(response);

        verify(userRepo).incrementCloseFriendsCount("user1");
    }

    @Test
    void addCloseFriend_AlreadyExists_ShouldThrowException() {

        when(userRepo.findById(any()))
                .thenReturn(Optional.of(publicUser));

        when(closeFriendsRepo.findByUserIdAndCloseFriendId(any(), any()))
                .thenReturn(Optional.of(new CloseFriends()));

        assertThrows(RuntimeException.class,
                () -> connectionService.addCloseFriend("user1", "user2"));
    }

    // =====================================================
    // REMOVE CLOSE FRIEND
    // =====================================================

    @Test
    void removeCloseFriend_Success() {

        CloseFriends relation = CloseFriends.builder()
                .userId("user1")
                .closeFriendId("user2")
                .build();

        when(closeFriendsRepo.findByUserIdAndCloseFriendId(any(), any()))
                .thenReturn(Optional.of(relation));

        connectionService.removeCloseFriend("user1", "user2");

        verify(closeFriendsRepo).delete(relation);

        verify(userRepo).decrementCloseFriendsCount("user1");
    }

    // =====================================================
    // GET CLOSE FRIENDS
    // =====================================================

    @Test
    void getCloseFriends_Success() {

        CloseFriends relation = CloseFriends.builder()
                .id("cf1")
                .userId("user1")
                .closeFriendId("user2")
                .createdAt(Instant.now())
                .build();

        when(closeFriendsRepo.findByUserId("user1"))
                .thenReturn(List.of(relation));

        when(userRepo.findByUserIdIn(any()))
                .thenReturn(List.of(privateUser));

        List<CloseFriendResponse> response =
                connectionService.getCloseFriends("user1");

        assertEquals(1, response.size());
    }

}
