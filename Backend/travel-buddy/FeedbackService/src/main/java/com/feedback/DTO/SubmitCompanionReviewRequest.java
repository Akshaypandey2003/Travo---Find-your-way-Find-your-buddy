package com.feedback.DTO;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
public class SubmitCompanionReviewRequest {
    @NotBlank
    private String tripId;

    @NotBlank
    private String reviewerUserId;

    @NotBlank
    private String targetUserId;

    @Min(1)
    @Max(5)
    private int rating;

    @Size(max = 1500)
    private String review;

    @Builder.Default
    private Set<String> tags = new LinkedHashSet<>();
}
