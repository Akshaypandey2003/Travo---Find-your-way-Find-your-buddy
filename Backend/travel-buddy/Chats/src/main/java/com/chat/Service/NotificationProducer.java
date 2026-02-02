package com.chat.Service;


public interface NotificationProducer {
   public void messageSent(String senderId, String receiverId, String chatId, String chatName) ;

    public void groupCreated(String senderId, String receiverId, String chatId, String chatName) ;

    public void groupUpdated(String senderId, String receiverId, String chatId, String chatName);

    public void addGroupMember(String senderId, String receiverId, String chatId, String chatName);
    public void removeGroupMember(String senderId, String receiverId, String chatId,String chatName);

    public void deleteGroup(String senderId,String receiverId, String chatId, String chatName);
}
