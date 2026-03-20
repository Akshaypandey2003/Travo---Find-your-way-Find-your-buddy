package com.chat.ServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chat.Entity.Chat;
import com.chat.Entity.ChatParticipant;
import com.chat.Exceptions.ChatNotFoundException;
import com.chat.Repository.ChatRepo;
import com.chat.Repository.MessageRepository;
import com.chat.Service.ChatDomainEventProducer;
import com.chat.Service.ChatService;
import com.chat.Service.NotificationProducer;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);
    private static final String CACHE_CHAT_BY_ID = "chatById";
    private static final String CACHE_CHATS_BY_USER_ID = "chatsByUserId";
    private static final String CACHE_MESSAGES_BY_CHAT_ID = "messagesByChatId";

    private final ChatRepo chatRepo;
    private final MessageRepository messageRepo;
    private final NotificationProducer notificationProducer;
    private final ChatDomainEventProducer chatDomainEventProducer;

    public ChatServiceImpl(
            ChatRepo chatRepo,
            MessageRepository messageRepo,
            NotificationProducer notificationProducer,
            ChatDomainEventProducer chatDomainEventProducer) {
        this.chatRepo = chatRepo;
        this.messageRepo = messageRepo;
        this.notificationProducer = notificationProducer;
        this.chatDomainEventProducer = chatDomainEventProducer;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_CHATS_BY_USER_ID, allEntries = true)
    })
    public Chat createChat(Chat chat) {

        Chat savedChat = chatRepo.save(chat);
        chatDomainEventProducer.publishChatCreated(savedChat);

        if (savedChat.isGroupChat()) {
            notifyGroupCreated(savedChat);
        }

        log.info("Chat created successfully. chatId={}", savedChat.getChatId());
        return savedChat;
    }

    private void notifyGroupCreated(Chat chat) {
        String adminId = resolveActor(chat.getGroupAdmin());

        for (ChatParticipant participant : chat.getParticipants()) {
            try {
                notificationProducer.groupCreated(
                        adminId,
                        participant.getUserId(),
                        chat.getChatId(),
                        chat.getGroupName()
                );
            } catch (Exception ex) {
                log.error(
                        "Failed to send group created notification. chatId={}, participant={}",
                        chat.getChatId(),
                        participant,
                        ex
                );
            }
        }
    }

    private String resolveActor(Set<String> admins) {
        if (admins == null || admins.isEmpty()) {
            return "SYSTEM";
        }
        return admins.iterator().next();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_CHATS_BY_USER_ID, key = "#userId")
    public List<Chat> getChatsByUserId(String userId) {

        List<Chat> chats = chatRepo.findByParticipantsContaining(userId);

        if (chats == null || chats.isEmpty()) {
            throw new ChatNotFoundException("Chats not found for userId: " + userId);
        }

        return chats;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_CHAT_BY_ID, key = "#chatId")
    public Chat getChatById(String chatId) {

        return chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatNotFoundException("Chat not found with id: " + chatId)
                );
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_CHAT_BY_ID, key = "#chatId"),
            @CacheEvict(value = CACHE_CHATS_BY_USER_ID, allEntries = true)
    })
    public Chat updateChat(String adminId, String chatId, Chat chat) {

        Chat existingChat = chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatNotFoundException("Chat not found with id: " + chatId)
                );

        if (chat.getGroupName() != null) {
            existingChat.setGroupName(chat.getGroupName());
        }
        if (chat.getGroupDescription() != null) {
            existingChat.setGroupDescription(chat.getGroupDescription());
        }
        if (chat.getGroupImageUrl() != null) {
            existingChat.setGroupImageUrl(chat.getGroupImageUrl());
        }
        if (chat.getGroupAdmin() != null) {
            existingChat.getGroupAdmin().addAll(chat.getGroupAdmin());
        }
        if (chat.getParticipants() != null) {
            existingChat.setParticipants(chat.getParticipants());
        }

        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("groupName", existingChat.getGroupName());
        updatedFields.put("groupDescription", existingChat.getGroupDescription());
        updatedFields.put("groupImageUrl", existingChat.getGroupImageUrl());
        updatedFields.put("participants", existingChat.getParticipants());

        if (existingChat.isGroupChat()) {
            notifyGroupUpdated(adminId, existingChat);
        }

        Chat savedChat = chatRepo.save(existingChat);
        chatDomainEventProducer.publishChatUpdated(savedChat, updatedFields);
        return savedChat;
    }

    private void notifyGroupUpdated(String adminId, Chat chat) {
        for (ChatParticipant participant : chat.getParticipants()) {
            try {
                notificationProducer.groupUpdated(
                        adminId,
                        participant.getUserId(),
                        chat.getChatId(),
                        chat.getGroupName()
                );
            } catch (Exception ex) {
                log.error(
                        "Failed to send group updated notification. chatId={}, participant={}",
                        chat.getChatId(),
                        participant,
                        ex
                );
            }
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_CHAT_BY_ID, key = "#chatId"),
            @CacheEvict(value = CACHE_CHATS_BY_USER_ID, allEntries = true)
    })
    public Chat updateFavorite(String chatId, String userId) {

        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatNotFoundException("Chat not found with id: " + chatId)
                );

        Set<String> favoriteBy = chat.getFavoriteBy() == null ? new HashSet<>() : chat.getFavoriteBy();

        if (favoriteBy.contains(userId)) {
            favoriteBy.remove(userId);
        } else {
            favoriteBy.add(userId);
        }

        chat.setFavoriteBy(favoriteBy);
        Chat updatedChat = chatRepo.save(chat);
        chatDomainEventProducer.publishChatUpdated(updatedChat, Map.of("favoriteBy", updatedChat.getFavoriteBy()));
        return updatedChat;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_CHAT_BY_ID, key = "#chatId"),
            @CacheEvict(value = CACHE_CHATS_BY_USER_ID, allEntries = true)
    })
    public Chat updateGroupMembers(String adminId, String chatId, Set<ChatParticipant> members) {

        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatNotFoundException("Chat not found with id: " + chatId)
                );

        Set<ChatParticipant> existingMembers = chat.getParticipants() == null ? new HashSet<>() : chat.getParticipants();

        for (ChatParticipant member : members) {
            try {
                if (!existingMembers.contains(member)) {
                    notificationProducer.addGroupMember(
                            adminId,
                            member.getUserId(),
                            chat.getChatId(),
                            chat.getGroupName()
                    );
                    existingMembers.add(member);
                } else {
                    notificationProducer.removeGroupMember(
                            adminId,
                            member.getUserId(),
                            chat.getChatId(),
                            chat.getGroupName()
                    );
                    existingMembers.remove(member);
                }
            } catch (Exception ex) {
                log.error(
                        "Failed to update group member. chatId={}, member={}",
                        chat.getChatId(),
                        member,
                        ex
                );
            }
        }

        chat.setParticipants(existingMembers);
        Chat updatedChat = chatRepo.save(chat);
        chatDomainEventProducer.publishChatUpdated(updatedChat, Map.of("participants", updatedChat.getParticipants()));
        return updatedChat;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_CHAT_BY_ID, key = "#chatId"),
            @CacheEvict(value = CACHE_CHATS_BY_USER_ID, allEntries = true),
            @CacheEvict(value = CACHE_MESSAGES_BY_CHAT_ID, key = "#chatId")
    })
    public void deleteChat(String adminId, String chatId) {

        Chat existingChat = chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatNotFoundException("Chat not found with id: " + chatId)
                );

        for (ChatParticipant participant : existingChat.getParticipants()) {
            try {
                notificationProducer.deleteGroup(
                        adminId,
                        participant.getUserId(),
                        chatId,
                        existingChat.getGroupName()
                );
            } catch (Exception ex) {
                log.error(
                        "Failed to send delete group notification. chatId={}, participant={}",
                        chatId,
                        participant,
                        ex
                );
            }
        }

        messageRepo.deleteByChatId(chatId);
        chatRepo.deleteById(chatId);
        chatDomainEventProducer.publishChatDeleted(existingChat);

        log.info("Chat deleted successfully. chatId={}", chatId);
    }
}
