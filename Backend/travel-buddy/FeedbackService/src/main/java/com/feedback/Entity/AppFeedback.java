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
@Document(collection = "app_feedbacks")
public class AppFeedback {
    @Id
    private String feedbackId;

    @NotBlank
    @Indexed
    private String userId;

    @Indexed
    private String tripId;

    @Min(1)
    @Max(5)
    private int rating;

    @Size(max = 2000)
    private String comment;

    @Builder.Default
    private Set<String> featuresLiked = new LinkedHashSet<>();

    @Builder.Default
    @PastOrPresent
    private Instant createdAt = Instant.now();
}
