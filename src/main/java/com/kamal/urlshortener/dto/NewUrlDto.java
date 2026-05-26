package com.kamal.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
public class NewUrlDto {

    @NotBlank(message = "Original URL is required")
    @URL(message = "Invalid URL format")
    private String originalUrl;

    private LocalDateTime expiresAt;
}