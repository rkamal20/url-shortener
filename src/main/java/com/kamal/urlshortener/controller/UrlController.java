package com.kamal.urlshortener.controller;

import com.kamal.urlshortener.dto.AnalyticsDto;
import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;
import com.kamal.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @Operation(
            summary = "Create short URL",
            description = "Generates a shortened URL for a given original URL"
    )
    @ApiResponse(responseCode = "201", description = "Short URL created successfully")
    @PostMapping("/shorten")
    public ResponseEntity<UrlDto> shortenUrl(@RequestBody @Valid NewUrlDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(urlService.shortenUrl(dto));
    }

    @Operation(
            summary = "Redirect to original URL",
            description = "Redirects client to original URL using short code"
    )
    @ApiResponse(responseCode = "302", description = "Redirect successful")
    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirectUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.redirectUrl(shortCode);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

    @Operation(
            summary = "Get URL analytics",
            description = "Returns click count and metadata for a short URL"
    )
    @ApiResponse(responseCode = "200", description = "Analytics fetched successfully")
    @GetMapping("/analytics/{shortCode}")
    public ResponseEntity<AnalyticsDto> getAnalytics(@PathVariable String shortCode) {
        return ResponseEntity.ok(urlService.getAnalytics(shortCode));
    }
}
