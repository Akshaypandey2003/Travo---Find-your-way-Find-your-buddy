package com.chat.Service;

import java.util.List;

import com.chat.Entity.Message;

public interface MessageService {
    
    // Define methods for message operations, e.g., sendMessage, getMessage, deleteMessage
    public Message sendMessage(Message message);
    
    public List<Message> getMessage(String chatId);
    
    public void deleteMessage(String messageId);

    public Message updateMessage(String messageId, Message message);
    public Message updateReadStatus(String messageId);
    
    // Additional methods as needed
}
