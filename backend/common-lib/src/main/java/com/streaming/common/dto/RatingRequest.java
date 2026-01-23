package com.streaming.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RatingRequest {
    @NotBlank
    private String songId;

    @Min(1)
    @Max(5)
    private int value;
}