package com.trip.Entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Rating {

    @Id
    private String ratingId;

    private String tripId;

    private String reviewerUserId;

    private String targetUserId;

    private int rating; // 1–5

    private String review;

    private Instant createdAt;
}