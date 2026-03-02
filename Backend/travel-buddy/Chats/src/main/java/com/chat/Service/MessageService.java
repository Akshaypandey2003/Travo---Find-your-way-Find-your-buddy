package com.chat.Service;

import java.util.List;

import com.chat.Entity.Message;

public interface MessageService {
    
    Message sendMessage(String chatId, Message message);
    
    List<Message> getMessagesByChatId(String chatId);
    
    void deleteMessage(String messageId);

    Message updateMessage(String messageId, Message message);
    Message updateReadStatus(String messageId);
}
