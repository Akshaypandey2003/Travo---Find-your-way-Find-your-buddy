package com.chat.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chat.Entity.Message;
import com.chat.Exceptions.MessageNotFoundException;
import com.chat.Repository.MessageRepository;
import com.chat.Service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepo;

    @Override
    public Message sendMessage(Message message) {
        try {
            return messageRepo.save(message);
        } catch (Exception e) {
            throw new RuntimeException("Error sending message: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Message> getMessage(String chatId) {
        try {
            List<Message> messages = messageRepo.findByChatIdOrderByCreatedAtDesc(chatId);

            if (messages == null || messages.isEmpty()) {
                throw new MessageNotFoundException("Messages not found for chatId: " + chatId);
            }
            return messages;
        } catch (Exception e) {
            throw new RuntimeException("Error sending message: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMessage(String messageId) {
       try {
        messageRepo.deleteById(messageId);
       } catch (Exception e) {
          throw new RuntimeException("Error sending message: " + e.getMessage(), e);
       }
    }

    @Override
    public Message updateMessage(String messageId, Message message) 
    {
        Message existingMessage = messageRepo.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + messageId));
        
        if(message.getMessageContent()!=null)
        existingMessage.setMessageContent(message.getMessageContent());

        if(message.getMediaUrl()!=null)
        existingMessage.setMediaUrl(message.getMediaUrl());
        if(message.getMessageType()!=null)
        existingMessage.setMediaType(message.getMediaType());
        existingMessage.setRead(message.isRead());

        return messageRepo.save(existingMessage);
    }
    @Override
    public Message updateReadStatus(String messageId) 
    {
        Message existingMessage = messageRepo.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + messageId));
        
        
        existingMessage.setRead(true);

        return messageRepo.save(existingMessage);
    }

}
