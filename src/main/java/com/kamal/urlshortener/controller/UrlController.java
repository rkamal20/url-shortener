package com.kamal.urlshortener.controller;

import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;
import com.kamal.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/urls")
public class UrlController {

    private final UrlService urlService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<UrlDto>> getUrls() {
        return ResponseEntity.ok(this.urlService.getUrls());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UrlDto> getUrlById(@PathVariable Long id) {
        return ResponseEntity.ok(this.urlService.getUrlById(id));
    }

    @PostMapping
    public ResponseEntity<UrlDto> addUrl(@RequestBody @Valid NewUrlDto newUrlDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.urlService.addUrl(newUrlDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUrlById(@PathVariable Long id) {
        this.urlService.deleteUrlById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UrlDto> updateUrl(@PathVariable Long id, @RequestBody @Valid NewUrlDto newUrlDto) {
        return ResponseEntity.ok(this.urlService.updateUrl(id, newUrlDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UrlDto> updatePartialUrl(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(this.urlService.updatePartialUrl(id, updates));
    }
}
