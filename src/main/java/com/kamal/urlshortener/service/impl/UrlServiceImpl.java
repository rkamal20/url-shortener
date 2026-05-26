package com.kamal.urlshortener.service.impl;

import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;
import com.kamal.urlshortener.dto.AnalyticsDto;
import com.kamal.urlshortener.entity.UrlEntity;
import com.kamal.urlshortener.exception.UrlExpiredException;
import com.kamal.urlshortener.exception.ResourceNotFoundException;
import com.kamal.urlshortener.repository.UrlRepository;
import com.kamal.urlshortener.service.UrlService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
public class UrlServiceImpl implements UrlService {

    private static final int MAX_GENERATION_ATTEMPTS = 5;
    private static final Random RANDOM = new Random();

    @Value("${app.base-url}")
    private String baseUrl;

    private final UrlRepository urlRepository;
    private final ModelMapper modelMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public UrlServiceImpl(UrlRepository urlRepository, ModelMapper modelMapper, StringRedisTemplate stringRedisTemplate) {
        this.urlRepository = urlRepository;
        this.modelMapper = modelMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public UrlDto shortenUrl(NewUrlDto dto) {

        UrlEntity urlEntity = modelMapper.map(dto, UrlEntity.class);
        urlEntity.setShortCode(generateCode());

        urlEntity = urlRepository.save(urlEntity);

        UrlDto urlDto = modelMapper.map(urlEntity, UrlDto.class);
        urlDto.setShortUrl(baseUrl + urlEntity.getShortCode());

        return urlDto;
    }

    @Override
    public String redirectUrl(String shortCode) {

        String cachedUrl = stringRedisTemplate.opsForValue().get(shortCode);

        if (cachedUrl != null) {
            incrementClick(shortCode); // move click tracking to async processing for better redirect latency
            return cachedUrl;
        }

        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Short code not found: " + shortCode)
                );

        if (urlEntity.getExpiresAt() != null && LocalDateTime.now().isAfter(urlEntity.getExpiresAt())) {
            throw new UrlExpiredException("Short URL has expired"); // invalidate cached entry when expired URL is detected
        }

        stringRedisTemplate.opsForValue().set(shortCode, urlEntity.getOriginalUrl());

        incrementClick(shortCode);

        return urlEntity.getOriginalUrl();
    }

    @Override
    public AnalyticsDto getAnalytics(String shortCode) {

        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Short code not found: " + shortCode)
                );

        return mapToDto(urlEntity);
    }

    private AnalyticsDto mapToDto(UrlEntity urlEntity) {

        AnalyticsDto dto = modelMapper.map(urlEntity, AnalyticsDto.class);
        dto.setShortUrl(
                baseUrl + urlEntity.getShortCode()
        );
        return dto;
    }

    private void incrementClick(String shortCode) {

        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode)
                .orElseThrow();

        urlEntity.setClickCount(urlEntity.getClickCount() + 1);

        urlRepository.save(urlEntity);
    }

    private String generateCode() {

        String base62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        String code;
        int attempts = 0;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(
                        base62.charAt(
                                RANDOM.nextInt(base62.length())
                        )
                );
            }
            code = sb.toString();
            attempts++;
        } while (urlRepository.findByShortCode(code).isPresent() && attempts < MAX_GENERATION_ATTEMPTS);

        if (attempts == MAX_GENERATION_ATTEMPTS) {
            throw new RuntimeException("Couldn't generate unique short code");
        }
        return code;
    }
}
