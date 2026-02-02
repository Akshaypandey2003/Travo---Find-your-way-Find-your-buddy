package com.feedback.Service;


public interface NotificationProducer {
    
    void sendFeedbackNotification(String senderId, String receiverId, String feedbackId, String tripName);
}
