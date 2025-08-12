package com.gateway.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import com.gateway.DTO.Author;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class FeedBack {

    private String id;
    private String tripId;           // ID of the trip
    private String authorId;       // The user giving feedback
    private Author author;
    private String comment;          // Actual feedback message
    private int rating;              // Optional: 1–5 stars

    @Builder.Default
    private Set<String> tags = new TreeSet<>(); // optional
    private LocalDateTime createdAt=LocalDateTime.now();;
    private MessageResponse messageResponse = new MessageResponse();
}
