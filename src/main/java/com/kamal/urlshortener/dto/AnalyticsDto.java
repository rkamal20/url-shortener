package com.kamal.urlshortener.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnalyticsDto {

    private String originalUrl;
    private String shortUrl;
    private Long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}