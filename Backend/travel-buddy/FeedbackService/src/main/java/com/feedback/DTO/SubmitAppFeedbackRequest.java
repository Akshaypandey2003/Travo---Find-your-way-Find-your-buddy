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
public class SubmitAppFeedbackRequest {
    @NotBlank
    private String userId;

    private String tripId;

    @Min(1)
    @Max(5)
    private int rating;

    @Size(max = 2000)
    private String comment;

    @Builder.Default
    private Set<String> featuresLiked = new LinkedHashSet<>();
}
