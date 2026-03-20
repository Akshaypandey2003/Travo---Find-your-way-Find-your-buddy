package com.chat.UnitTests;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chat.Entity.Chat;
import com.chat.Entity.ChatParticipant;
import com.chat.Entity.Message;
import com.chat.Exceptions.MessageNotFoundException;
import com.chat.Repository.ChatRepo;
import com.chat.Repository.MessageRepository;
import com.chat.Service.ChatDomainEventProducer;
import com.chat.Service.NotificationProducer;
import com.chat.ServiceImpl.MessageServiceImpl;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    private MessageServiceImpl messageService;

    @Mock
    private MessageRepository messageRepo;

    @Mock
    private ChatRepo chatRepo;

    @Mock
    private NotificationProducer chatNotificationProducer;

    @Mock
    private ChatDomainEventProducer chatDomainEventProducer;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private Chat chat;
    private Message message;

    @BeforeEach
    void setup() {
        lenient().when(cacheManager.getCache(anyString())).thenReturn(cache);

        chat = new Chat();
        chat.setChatId("chat123");
        chat.setGroupChat(true);
        chat.setGroupName("Dev Group");
        chat.setParticipants(Set.of(
                new ChatParticipant("user1", "User One", "pic1"),
                new ChatParticipant("user2", "User Two", "pic2"),
                new ChatParticipant("user3", "User Three", "pic3")));

        message = new Message();
        message.setMessageId("msg1");
        message.setChatId("chat123");
        message.setSenderId("user1");
        message.setMessageContent("Hello");
        message.setCreatedAt(LocalDateTime.now());
    }

    // ---------- SEND MESSAGE ----------

    @Test
    void shouldSendMessage_andNotifyOtherParticipants() {

        when(chatRepo.findById("chat123")).thenReturn(java.util.Optional.of(chat));
        when(chatRepo.save(any(Chat.class))).thenReturn(chat);
        when(messageRepo.save(any(Message.class))).thenReturn(message);

        Message result = messageService.sendMessage("chat123", message);

        assertThat(result).isNotNull();

        verify(chatRepo).save(chat);
        verify(messageRepo).save(message);

        // user2 & user3 should be notified, NOT sender (user1)
        verify(chatNotificationProducer)
                .messageSent("user1", "user2", "chat123", "Dev Group");

        verify(chatNotificationProducer)
                .messageSent("user1", "user3", "chat123", "Dev Group");

        verify(chatNotificationProducer, never())
                .messageSent("user1", "user1", "chat123", "Dev Group");
    }

    @Test
    void shouldFailSendMessage_whenChatNotFound() {

        when(chatRepo.findById("chat123")).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> messageService.sendMessage("chat123", message))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Chat not found");
    }

    // ---------- GET MESSAGES ----------

    @Test
    void shouldGetMessagesByChatId() {

        when(messageRepo.findByChatIdOrderByCreatedAtDesc("chat123"))
                .thenReturn(List.of(message));

        List<Message> messages = messageService.getMessagesByChatId("chat123");

        assertThat(messages).hasSize(1);
    }

    @Test
    void shouldFailGetMessages_whenNoMessagesFound() {

        when(messageRepo.findByChatIdOrderByCreatedAtDesc("chat123"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> messageService.getMessagesByChatId("chat123"))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessageContaining("chat123");

    }

    // ---------- DELETE MESSAGE ----------

    @Test
    void shouldDeleteMessage() {

        when(messageRepo.findById("msg1")).thenReturn(java.util.Optional.of(message));
        doNothing().when(messageRepo).deleteById("msg1");

        messageService.deleteMessage("msg1");

        verify(messageRepo).findById("msg1");
        verify(messageRepo).deleteById("msg1");
    }

    // ---------- UPDATE MESSAGE ----------

    @Test
    void shouldUpdateMessageContentAndMedia() {

        Message updated = new Message();
        updated.setMessageContent("Updated text");
        updated.setMediaUrl("image.png");
        updated.setRead(true);

        when(messageRepo.findById("msg1")).thenReturn(java.util.Optional.of(message));
        when(messageRepo.save(any(Message.class))).thenAnswer(i -> i.getArgument(0));

        Message result = messageService.updateMessage("msg1", updated);

        assertThat(result.getMessageContent()).isEqualTo("Updated text");
        assertThat(result.getMediaUrl()).isEqualTo("image.png");
        assertThat(result.isRead()).isTrue();
    }

    @Test
    void shouldFailUpdateMessage_whenMessageNotFound() {

        when(messageRepo.findById("msg1")).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> messageService.updateMessage("msg1", message))
                .isInstanceOf(MessageNotFoundException.class);
    }

    // ---------- UPDATE READ STATUS ----------

    @Test
    void shouldUpdateReadStatus() {

        when(messageRepo.findById("msg1")).thenReturn(java.util.Optional.of(message));
        when(messageRepo.save(any(Message.class))).thenAnswer(i -> i.getArgument(0));

        Message result = messageService.updateReadStatus("msg1");

        assertThat(result.isRead()).isTrue();
    }
}
