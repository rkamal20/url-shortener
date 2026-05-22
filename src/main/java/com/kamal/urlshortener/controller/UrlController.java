package com.kamal.urlshortener.controller;

import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;
import com.kamal.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<UrlDto> createUrl(@RequestBody @Valid NewUrlDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.urlService.createUrl(dto));
    }


}
