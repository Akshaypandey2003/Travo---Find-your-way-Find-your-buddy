package com.chat.ServiceImpl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chat.Entity.Chat;
import com.chat.Exceptions.ChatNotFoundException;
import com.chat.Repository.ChatRepo;
import com.chat.Repository.MessageRepository;
import com.chat.Service.ChatService;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepo chatRepo;

    @Autowired
    private MessageRepository messageRepo;

    @Override
    public Chat createChat(Chat chat) {
        try {
            return chatRepo.save(chat);
        } catch (Exception e) {
            throw new RuntimeException("Error creating chat: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Chat> getChat(String userId) {
        try {
            List<Chat> chats = chatRepo.findByParticipantsContaining(userId);
            if (chats == null || chats.isEmpty()) {
                throw new ChatNotFoundException("Chats not found for userId: " + userId);
            }
            return chats;

        } catch (Exception e) {
            throw new RuntimeException("Error retrieving chat: " + e.getMessage(), e);
        }
    }

    @Override
    public Chat getChatByChatId(String chatId) {
        try {
            Chat chat = chatRepo.findById(chatId)
                    .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));

            return chat;

        } catch (Exception e) {
            throw new RuntimeException("Error retrieving chat: " + e.getMessage(), e);
        }
    }

    @Override
    public Chat updateChat(String chatId, Chat chat) {
        try {
            Chat existingChat = chatRepo.findById(chatId)
                    .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));

            // Update the existing chat with new values
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
                Set<String> existingAdmins = existingChat.getGroupAdmin();
                Set<String> newAdmins = chat.getGroupAdmin();
                existingAdmins.addAll(newAdmins);
            }
            if (chat.getParticipants() != null) {

                existingChat.setParticipants(chat.getParticipants());
            }

            return chatRepo.save(existingChat);
        } catch (Exception e) {
            throw new RuntimeException("Error updating chat: " + e.getMessage(), e);
        }
    }
   public Chat updateFavorite(String chatId, String userId) {
        try {
            Chat chat = chatRepo.findById(chatId)
                    .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));

            Set<String> favoriteBy = chat.getFavoriteBy();
            if (favoriteBy.contains(userId)) {
                favoriteBy.remove(userId); // Unmark as favorite
            } else {
                favoriteBy.add(userId); // Mark as favorite
            }
            chat.setFavoriteBy(favoriteBy);

            return chatRepo.save(chat);
        } catch (Exception e) {
            throw new RuntimeException("Error updating favorite status: " + e.getMessage(), e);
        }
    }
   public Chat updateGroupMembers(String chatId, Set<String> members) {
        try {
            Chat chat = chatRepo.findById(chatId)
                    .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));

            Set<String> existingMembers = chat.getParticipants();

            for (String member : members) {
                if (!existingMembers.contains(member)) {
                    existingMembers.add(member); // Add new member
                } else {
                    existingMembers.remove(member); // Remove existing member
                }
            }
            chat.setParticipants(existingMembers);
            
            return chatRepo.save(chat);

        } catch (Exception e) {
            throw new RuntimeException("Error updating group members: " + e.getMessage(), e);
        }
    }
    @Override
    public void deleteChat(String chatId) {
        try {
            messageRepo.deleteByChatId(chatId);
            chatRepo.deleteById(chatId);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting chat: " + e.getMessage(), e);
        }
    }

}
