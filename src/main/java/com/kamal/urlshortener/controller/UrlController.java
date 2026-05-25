package com.kamal.urlshortener.controller;

import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;
import com.kamal.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirectUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.redirectUrl(shortCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));

        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

    @PostMapping
    public ResponseEntity<UrlDto> createUrl(@RequestBody @Valid NewUrlDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(urlService.createUrl(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UrlDto> getUrlById(@PathVariable Long id) {
        return ResponseEntity.ok(urlService.getUrlById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id) {
        urlService.deleteUrl(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UrlDto> updateUrl(@PathVariable Long id, @RequestBody @Valid NewUrlDto dto) {
        return ResponseEntity.ok(urlService.updateUrl(id, dto));
    }

//    @PatchMapping("/{id}")
//    public ResponseEntity<UrlDto> patchUrl(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
//        return ResponseEntity.ok(urlService.patchUrl(id, updates));
//    }
}
