package com.chat.Service;

import java.util.Map;

import com.chat.Entity.Chat;
import com.chat.Entity.Message;

public interface ChatDomainEventProducer {
    void publishChatCreated(Chat chat);

    void publishChatUpdated(Chat chat, Map<String, Object> updatedFields);

    void publishChatDeleted(Chat chat);

    void publishMessageSent(Message message, Chat chat);

    void publishMessageUpdated(Message message);

    void publishMessageRead(Message message);

    void publishMessageDeleted(Message message);
}
