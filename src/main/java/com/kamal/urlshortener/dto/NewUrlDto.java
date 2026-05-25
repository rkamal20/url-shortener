package com.kamal.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NewUrlDto {

    @NotBlank(message = "Original URL is required")
    private String originalUrl;

    private LocalDateTime expiresAt;
}