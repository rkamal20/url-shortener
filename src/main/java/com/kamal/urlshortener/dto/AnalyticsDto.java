package com.kamal.urlshortener.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnalyticsDto {

    private String originalUrl;

    private String shortUrl;

    private Long clickCount;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private String status;

}