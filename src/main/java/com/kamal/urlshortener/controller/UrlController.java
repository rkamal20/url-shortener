package com.kamal.urlshortener.controller;

import com.kamal.urlshortener.dto.AnalyticsDto;
import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;
import com.kamal.urlshortener.service.UrlService;
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

    @PostMapping("/shorten")
    public ResponseEntity<UrlDto> shortenUrl(@RequestBody @Valid NewUrlDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(urlService.shortenUrl(dto));
    }

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirectUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.redirectUrl(shortCode);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

    @GetMapping("/analytics/{shortCode}")
    public ResponseEntity<AnalyticsDto> getAnalytics(@PathVariable String shortCode) {
        return ResponseEntity.ok(urlService.getAnalytics(shortCode));
    }
}
