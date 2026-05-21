package com.kamal.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewUrlDto {

    @NotBlank(message = "Original url is required")
    private String originalUrl;

    @NotBlank(message = "Short code is required")
    @Size(min = 6, max = 6, message = "Short code must be 6 characters long")
    private String shortCode;
}
