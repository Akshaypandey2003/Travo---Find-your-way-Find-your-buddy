package com.blog.DTO;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BlogEngagementBatchRequest {
    @NotEmpty
    @Size(max = 100)
    private List<@NotBlank String> blogIds;
}