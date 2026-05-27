package com.kamal.urlshortener.service;

import com.kamal.urlshortener.dto.AnalyticsDto;
import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;
import com.kamal.urlshortener.entity.UrlEntity;
import com.kamal.urlshortener.repository.UrlRepository;
import com.kamal.urlshortener.service.impl.UrlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @InjectMocks
    private UrlServiceImpl urlService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "https://short.ly/");
    }

    @Test
    void shortenUrl_NoCustomAlias_ShouldGenerateCode() {
        // Arrange
        NewUrlDto dto = new NewUrlDto();
        dto.setOriginalUrl("https://example.com");
        UrlEntity mappedEntity = new UrlEntity();
        UrlEntity savedEntity = new UrlEntity();
        savedEntity.setShortCode("abc123");

        UrlDto expectedDto = new UrlDto();

        when(modelMapper.map(dto, UrlEntity.class)).thenReturn(mappedEntity);
        when(urlRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, UrlDto.class)).thenReturn(expectedDto);

        // Act
        UrlDto result = urlService.shortenUrl(dto);

        // Assert
        assertNotNull(result);
        assertEquals("https://short.ly/abc123", result.getShortUrl());
        verify(urlRepository).save(mappedEntity);
    }

    @Test
    void redirectUrl_CacheHit_ShouldReturnCachedUrl() {
        // Arrange
        String shortCode = "abc123";
        String cachedUrl = "https://example.com";

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setShortCode(shortCode);
        urlEntity.setClickCount(5L);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(shortCode)).thenReturn(cachedUrl);

        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(urlEntity));
        when(urlRepository.save(any())).thenReturn(urlEntity);

        // Act
        String result = urlService.redirectUrl(shortCode);

        // Assert
        assertEquals(cachedUrl, result);
    }

    @Test
    void getAnalytics_ValidShortCode_ShouldReturnAnalyticsDto() {
        // Arrange
        String shortCode = "abc123";

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setShortCode(shortCode);
        urlEntity.setOriginalUrl("https://example.com");
        urlEntity.setClickCount(5L);

        AnalyticsDto analyticsDto = new AnalyticsDto();

        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(urlEntity));
        when(modelMapper.map(urlEntity, AnalyticsDto.class)).thenReturn(analyticsDto);

        // Act
        AnalyticsDto result = urlService.getAnalytics(shortCode);

        // Assert
        assertNotNull(result);
        verify(urlRepository).findByShortCode(shortCode);
    }
}