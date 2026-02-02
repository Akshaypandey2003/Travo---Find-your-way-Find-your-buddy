package com.feedback.Clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.feedback.DTO.TripSummary;

@FeignClient(
    name = "TRIP-SERVICE",   // Eureka service name (IMPORTANT)
    fallback = TripClientFallback.class
)
public interface TripClient {
    @GetMapping("/trip/internal/summary/{tripId}")
    TripSummary getTripSummaryById(@PathVariable String tripId);
}
