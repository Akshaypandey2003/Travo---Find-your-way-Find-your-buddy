package com.chat.Service;

import java.util.List;
import java.util.Set;

import com.chat.Entity.Chat;
import com.chat.Entity.ChatParticipant;

public interface ChatService {
    
    Chat createChat(Chat chat);
    
    List<Chat> getChatsByUserId(String userId);
    Chat getChatById(String chatId);
    
    Chat updateChat(String adminId, String chatId, Chat chat);
    Chat updateFavorite(String chatId, String userId);
    Chat updateGroupMembers(String adminId, String chatId, Set<ChatParticipant> members);
    
    void deleteChat(String adminId, String chatId);
}
