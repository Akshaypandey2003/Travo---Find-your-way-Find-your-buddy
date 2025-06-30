package com.chat.Service;

import java.util.List;
import java.util.Set;

import com.chat.Entity.Chat;

public interface ChatService {
    
    // Define methods for chat operations, e.g., createChat, getChat, updateChat, deleteChat
    public Chat createChat(Chat chat);
    
    public List<Chat> getChat(String userId);
    public Chat getChatByChatId(String chatId);
    
    public Chat updateChat(String chatId, Chat chat);
    public Chat updateFavorite(String chatId, String userId);
    public Chat updateGroupMembers(String chatId, Set<String> members);
    
    public void deleteChat(String chatId);
    
    // Additional methods as needed
}
