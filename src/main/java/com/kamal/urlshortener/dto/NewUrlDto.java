package com.kamal.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
public class NewUrlDto {

    @Schema(example = "https://google.com")
    @NotBlank(message = "Original URL is required")
    @URL(message = "Invalid URL format")
    private String originalUrl;

    @Schema(example = "2026-12-31T23:59:59")
    private LocalDateTime expiresAt;

    @Schema(example = "kamal1", description = "Optional custom alias (exactly 6 alphanumeric characters)")
    private String customAlias;
}