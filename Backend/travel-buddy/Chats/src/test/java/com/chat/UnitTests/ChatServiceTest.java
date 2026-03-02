package com.chat.UnitTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chat.Entity.Chat;
import com.chat.Exceptions.ChatNotFoundException;
import com.chat.Repository.ChatRepo;
import com.chat.Repository.MessageRepository;
import com.chat.Service.ChatDomainEventProducer;
import com.chat.Service.NotificationProducer;
import com.chat.ServiceImpl.ChatServiceImpl;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepo chatRepo;

    @Mock
    private MessageRepository messageRepo;

    @Mock
    private NotificationProducer notificationProducer;

    @Mock
    private ChatDomainEventProducer chatDomainEventProducer;

    @InjectMocks
    private ChatServiceImpl chatService;

    private Chat chat;

    @BeforeEach
    void setup() {
        chat = new Chat();
        chat.setChatId("chat1");
        chat.setGroupChat(true);
        chat.setGroupName("Test Group");
        chat.setParticipants(new java.util.HashSet<>(Set.of("user1", "user2")));
        chat.setGroupAdmin(new TreeSet<>(Set.of("admin1")));
    }

    // ---------- CREATE CHAT ----------

    @Test
    void shouldCreateChatSuccessfully() {
        when(chatRepo.save(chat)).thenReturn(chat);

        Chat result = chatService.createChat(chat);

        assertThat(result).isNotNull();
        verify(chatRepo).save(chat);
    }

    // ---------- GET CHAT ----------

    @Test
    void shouldGetChatsForUser() {
        when(chatRepo.findByParticipantsContaining("user1"))
                .thenReturn(List.of(chat));

        List<Chat> chats = chatService.getChatsByUserId("user1");

        assertThat(chats).hasSize(1);
    }

    @Test
    void shouldFailGetChats_whenNoChatsFound() {
        when(chatRepo.findByParticipantsContaining("user1"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> chatService.getChatsByUserId("user1"))
                .isInstanceOf(ChatNotFoundException.class)
                .hasMessageContaining("user1");
    }

    // ---------- GET CHAT BY ID ----------

    @Test
    void shouldGetChatByChatId() {
        when(chatRepo.findById("chat1")).thenReturn(java.util.Optional.of(chat));

        Chat result = chatService.getChatById("chat1");

        assertThat(result.getChatId()).isEqualTo("chat1");
    }

    @Test
    void shouldFailGetChatByChatId_whenNotFound() {
        when(chatRepo.findById("chat1")).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> chatService.getChatById("chat1"))
                .isInstanceOf(ChatNotFoundException.class);
    }

    // ---------- UPDATE CHAT ----------

    @Test
    void shouldUpdateChatSuccessfully() {
        Chat update = new Chat();
        update.setGroupName("Updated Group");

        when(chatRepo.findById("chat1")).thenReturn(java.util.Optional.of(chat));
        when(chatRepo.save(any(Chat.class))).thenReturn(chat);

        Chat result = chatService.updateChat("admin1", "chat1", update);

        assertThat(result.getGroupName()).isEqualTo("Updated Group");
    }

    @Test
    void shouldFailUpdateChat_whenChatNotFound() {
        when(chatRepo.findById("chat1")).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> chatService.updateChat("admin1", "chat1", new Chat()))
                .isInstanceOf(ChatNotFoundException.class);
    }

    // ---------- UPDATE FAVORITE ----------

    @Test
    void shouldAddAndRemoveFavorite() {
        when(chatRepo.findById("chat1")).thenReturn(Optional.of(chat));
        when(chatRepo.save(chat)).thenReturn(chat);

        Chat added = chatService.updateFavorite("chat1", "user1");
        assertThat(added.getFavoriteBy()).contains("user1");

        Chat removed = chatService.updateFavorite("chat1", "user1");
        assertThat(removed.getFavoriteBy()).doesNotContain("user1");
    }

    // ---------- UPDATE GROUP MEMBERS ----------

    @Test
    void shouldUpdateGroupMembers() {
        when(chatRepo.findById("chat1")).thenReturn(Optional.of(chat));
        when(chatRepo.save(chat)).thenReturn(chat);

        Chat result = chatService.updateGroupMembers(
                "admin1",
                "chat1",
                Set.of("user3"));

        assertThat(result.getParticipants()).contains("user3");
    }

    // ---------- DELETE CHAT ----------

    @Test
    void shouldDeleteChatSuccessfully() {
        when(chatRepo.findById("chat1")).thenReturn(java.util.Optional.of(chat));
        doNothing().when(messageRepo).deleteByChatId("chat1");
        doNothing().when(chatRepo).deleteById("chat1");

        chatService.deleteChat("admin1", "chat1");

        verify(messageRepo).deleteByChatId("chat1");
        verify(chatRepo).deleteById("chat1");
    }

    @Test
    void shouldFailDeleteChat_whenChatNotFound() {
        when(chatRepo.findById("chat1")).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> chatService.deleteChat("admin1", "chat1"))
                .isInstanceOf(ChatNotFoundException.class);
    }
}
