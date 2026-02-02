package com.trip.Services;


public interface TripNotificationProducer {
    
    
    public void sendTripRequestNotification(String senderId, String receivers, String tripId,String tripName);
    public void acceptTripRequestNotification(String senderId, String receiverId, String tripId,String tripName);
}
