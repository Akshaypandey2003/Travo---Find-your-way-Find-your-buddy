package com.feedback.Entity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "feedbacks") // Assuming MongoDB
public class FeedBack {
    @Id
    private String id;
    private String tripId;           // ID of the trip
    private String authorId;       // The user giving feedback
    private String comment;          // Actual feedback message
    private int rating;              // Optional: 1–5 stars
    private Set<String> tags = new TreeSet<>(); // optional
    private LocalDateTime createdAt = LocalDateTime.now();;
}