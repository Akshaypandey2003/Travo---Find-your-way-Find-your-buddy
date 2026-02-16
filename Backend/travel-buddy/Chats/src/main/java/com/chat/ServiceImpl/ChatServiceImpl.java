package com.chat.ServiceImpl;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chat.Entity.Chat;
import com.chat.Exceptions.ChatNotFoundException;
import com.chat.Repository.ChatRepo;
import com.chat.Repository.MessageRepository;
import com.chat.Service.ChatService;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ChatRepo chatRepo;
    private final MessageRepository messageRepo;
    private final ChatNotificationProducer notificationProducer;

    public ChatServiceImpl(
            ChatRepo chatRepo,
            MessageRepository messageRepo,
            ChatNotificationProducer notificationProducer) {
        this.chatRepo = chatRepo;
        this.messageRepo = messageRepo;
        this.notificationProducer = notificationProducer;
    }

    @Override
    public Chat createChat(Chat chat) {

        Chat savedChat = chatRepo.save(chat);

        if (savedChat.isGroupChat()) {
            notifyGroupCreated(savedChat);
        }

        log.info("Chat created successfully. chatId={}", savedChat.getChatId());
        return savedChat;
    }

    private void notifyGroupCreated(Chat chat) {
        String adminId = ((TreeSet<String>) chat.getGroupAdmin()).first();

        for (String participant : chat.getParticipants()) {
            try {
                notificationProducer.groupCreated(
                        adminId,
                        participant,
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

    @Override
    @Transactional(readOnly = true)
    public List<Chat> getChat(String userId) {

        List<Chat> chats = chatRepo.findByParticipantsContaining(userId);

        if (chats == null || chats.isEmpty()) {
            throw new ChatNotFoundException("Chats not found for userId: " + userId);
        }

        return chats;
    }

    @Override
    @Transactional(readOnly = true)
    public Chat getChatByChatId(String chatId) {

        return chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatNotFoundException("Chat not found with id: " + chatId)
                );
    }

    @Override
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

        if (existingChat.isGroupChat()) {
            notifyGroupUpdated(adminId, existingChat);
        }

        return chatRepo.save(existingChat);
    }

    private void notifyGroupUpdated(String adminId, Chat chat) {
        for (String participant : chat.getParticipants()) {
            try {
                notificationProducer.groupUpdated(
                        adminId,
                        participant,
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

    public Chat updateFavorite(String chatId, String userId) {

        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatNotFoundException("Chat not found with id: " + chatId)
                );

        Set<String> favoriteBy = chat.getFavoriteBy();

        if (favoriteBy.contains(userId)) {
            favoriteBy.remove(userId);
        } else {
            favoriteBy.add(userId);
        }

        chat.setFavoriteBy(favoriteBy);
        return chatRepo.save(chat);
    }

    public Chat updateGroupMembers(String adminId, String chatId, Set<String> members) {

        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatNotFoundException("Chat not found with id: " + chatId)
                );

        Set<String> existingMembers = chat.getParticipants();

        for (String member : members) {
            try {
                if (!existingMembers.contains(member)) {
                    notificationProducer.addGroupMember(
                            adminId,
                            member,
                            chat.getChatId(),
                            chat.getGroupName()
                    );
                    existingMembers.add(member);
                } else {
                    notificationProducer.removeGroupMember(
                            adminId,
                            member,
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
        return chatRepo.save(chat);
    }

    @Override
    public void deleteChat(String adminId, String chatId) {

        Chat existingChat = chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatNotFoundException("Chat not found with id: " + chatId)
                );

        for (String participant : existingChat.getParticipants()) {
            try {
                notificationProducer.deleteGroup(
                        adminId,
                        participant,
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

        log.info("Chat deleted successfully. chatId={}", chatId);
    }
}
