package com.feedback.Clients;

import com.feedback.DTO.TripSummary;

public class TripClientFallback implements TripClient {

     @Override
    public TripSummary getTripSummaryById(String tripId) {
        return new TripSummary(
            tripId,
            "Unknown Trip",
            null
        );
    }
    
}
