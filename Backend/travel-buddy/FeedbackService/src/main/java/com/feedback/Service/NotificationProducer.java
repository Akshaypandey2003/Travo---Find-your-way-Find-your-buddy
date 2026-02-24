package com.feedback.Service;


public interface NotificationProducer {
    
    void sendCompanionReviewNotification(String senderId, String receiverId, String tripId, String tripName);
}
