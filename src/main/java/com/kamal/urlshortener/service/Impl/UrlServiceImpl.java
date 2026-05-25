package com.kamal.urlshortener.service.Impl;

import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;
import com.kamal.urlshortener.entity.UrlEntity;
import com.kamal.urlshortener.exception.ResourceNotFoundException;
import com.kamal.urlshortener.repository.UrlRepository;
import com.kamal.urlshortener.service.UrlService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
@Transactional
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final ModelMapper modelMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public UrlServiceImpl(UrlRepository urlRepository, ModelMapper modelMapper, StringRedisTemplate stringRedisTemplate) {
        this.urlRepository = urlRepository;
        this.modelMapper = modelMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public UrlDto createUrl(NewUrlDto dto) {

        UrlEntity urlEntity = modelMapper.map(dto, UrlEntity.class);

        urlEntity.setShortCode(generateCode());
        urlEntity.setCreatedAt(LocalDateTime.now());
        urlEntity.setClickCount(0L);

        urlEntity = urlRepository.save(urlEntity);  // check duplicate short code is pending, collision check

        UrlDto response = modelMapper.map(urlEntity, UrlDto.class);  // repetitive code
        response.setShortUrl(
                "http://localhost:8080/api/urls/r/" + urlEntity.getShortCode()
        );
        return response;
    }


    @Override
    public UrlDto updateUrl(Long id, NewUrlDto dto) {

        UrlEntity urlEntity = urlRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("URL not found for id: " + id)
                );

        modelMapper.map(dto, urlEntity);
        urlEntity = urlRepository.save(urlEntity);

        UrlDto response = modelMapper.map(urlEntity, UrlDto.class);  // repetitive code
        response.setShortUrl(
                "http://localhost:8080/api/urls/r/" + urlEntity.getShortCode()
        );
        return response;
    }

    @Override
    public UrlDto patchUrl(Long id, Map<String, Object> updates) {

        UrlEntity urlEntity = urlRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("URL not found for id: " + id)
                );

        updates.forEach((field, value) -> {
                    switch (field) {
                        case "originalUrl":
                            urlEntity.setOriginalUrl((String) value);  // No validation check as of now
                            break;
                        case "shortCode":
                            urlEntity.setShortCode((String) value);
                            break;
                        default:
                            throw new IllegalArgumentException("Field not supported: " + field); // Need to handle globally
                    }
                }
        );

        UrlEntity savedUrlEntity = urlRepository.save(urlEntity);

        UrlDto response = modelMapper.map(savedUrlEntity, UrlDto.class);  // repetitive code
        response.setShortUrl(
                "http://localhost:8080/api/urls/r/" + savedUrlEntity.getShortCode()
        );
        return response;
    }

    @Override
    public void deleteUrl(Long id) {

        UrlEntity urlEntity = urlRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("URL not found for id: " + id)
                );

        urlRepository.delete(urlEntity);
    }

    @Override
    public UrlDto getUrlById(Long id) {

        UrlEntity urlEntity = urlRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "URL not found for id: " + id
                        )
                );

        UrlDto response = modelMapper.map(urlEntity, UrlDto.class);  // repetitive code
        response.setShortUrl(
                "http://localhost:8080/api/urls/r/" + urlEntity.getShortCode()
        );
        return response;
    }

    @Override
    public String redirectUrl(String shortCode) {

        String cachedUrl = stringRedisTemplate.opsForValue().get(shortCode);

        if (cachedUrl != null) {
            incrementClick(shortCode); // Make it async
            return cachedUrl;
        }

        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Short code not found: " + shortCode)
                );

        stringRedisTemplate.opsForValue().set(shortCode, urlEntity.getOriginalUrl());

        incrementClick(shortCode);

        return urlEntity.getOriginalUrl();
    }

    private void incrementClick(String shortCode) {

        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode)
                .orElseThrow();

        urlEntity.setClickCount(urlEntity.getClickCount() + 1);

        urlRepository.save(urlEntity);
    }

    private String generateCode() {

        String base62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();

        String code;
        int attempts = 0;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(
                        base62.charAt(
                                random.nextInt(base62.length())
                        )
                );
            }
            code = sb.toString();
            attempts++;
        } while (urlRepository.findByShortCode(code).isPresent() && attempts < 5); // use constant for 5

        if (attempts == 5) {
            throw new RuntimeException("Couldn't generate unique short code");
        }
        return code;
    }
}
