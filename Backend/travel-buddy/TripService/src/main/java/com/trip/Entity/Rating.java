package com.trip.Entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
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
@Document(collection = "ratings")
@CompoundIndexes({
        @CompoundIndex(
                name = "unique_rating_by_reviewer_target_trip_idx",
                def = "{'reviewerUserId': 1, 'targetUserId': 1, 'tripId': 1}",
                unique = true)
})
public class Rating {

    @Id
    private String ratingId;

    @NotBlank(message = "Trip id is required")
    @Indexed
    private String tripId;

    @NotBlank(message = "Reviewer user id is required")
    @Indexed
    private String reviewerUserId;

    @NotBlank(message = "Target user id is required")
    @Indexed
    private String targetUserId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating; // 1-5

    @Size(max = 1500, message = "Review cannot exceed 1500 characters")
    private String review;

    @Builder.Default
    @PastOrPresent(message = "createdAt cannot be in the future")
    private Instant createdAt = Instant.now();
}
