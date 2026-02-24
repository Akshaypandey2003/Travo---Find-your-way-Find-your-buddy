package com.feedback.Entity;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

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
@Document(collection = "companion_reviews")
@CompoundIndexes({
        @CompoundIndex(
                name = "unique_review_per_trip_reviewer_target_idx",
                def = "{'tripId': 1, 'reviewerUserId': 1, 'targetUserId': 1}",
                unique = true),
        @CompoundIndex(name = "trip_target_created_idx", def = "{'tripId': 1, 'targetUserId': 1, 'createdAt': -1}")
})
public class CompanionReview {
    @Id
    private String reviewId;

    @NotBlank
    @Indexed
    private String tripId;

    @NotBlank
    @Indexed
    private String reviewerUserId;

    @NotBlank
    @Indexed
    private String targetUserId;

    @Min(1)
    @Max(5)
    private int rating;

    @Size(max = 1500)
    private String review;

    @Builder.Default
    private Set<String> tags = new LinkedHashSet<>();

    @Builder.Default
    @PastOrPresent
    private Instant createdAt = Instant.now();
}
