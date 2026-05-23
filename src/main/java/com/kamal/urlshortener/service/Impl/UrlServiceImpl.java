package com.kamal.urlshortener.service.Impl;

import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;
import com.kamal.urlshortener.entity.UrlEntity;
import com.kamal.urlshortener.exception.ResourceNotFoundException;
import com.kamal.urlshortener.repository.UrlRepository;
import com.kamal.urlshortener.service.UrlService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
@Transactional
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final ModelMapper modelMapper;

    public UrlServiceImpl(UrlRepository urlRepository, ModelMapper modelMapper) {
        this.urlRepository = urlRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UrlDto createUrl(NewUrlDto dto) {

        UrlEntity urlEntity = modelMapper.map(dto, UrlEntity.class);

        urlEntity.setShortCode(this.generateCode());
        urlEntity.setCreatedAt(LocalDateTime.now());
        urlEntity.setClickCount(0L);

        urlEntity = this.urlRepository.save(urlEntity);  // check duplicate short code is pending, collision check

        UrlDto response = modelMapper.map(urlEntity, UrlDto.class);  // repetitive code
        response.setShortUrl(
                "http://localhost:8080/api/urls/r/" + urlEntity.getShortCode()
        );
        return response;
    }


    @Override
    public UrlDto updateUrl(Long id, NewUrlDto dto) {

        UrlEntity urlEntity = this.urlRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("URL not found for id: " + id)
                );

        modelMapper.map(dto, urlEntity);
        urlEntity = this.urlRepository.save(urlEntity);

        UrlDto response = modelMapper.map(urlEntity, UrlDto.class);  // repetitive code
        response.setShortUrl(
                "http://localhost:8080/api/urls/r/" + urlEntity.getShortCode()
        );
        return response;
    }

    @Override
    public UrlDto patchUrl(Long id, Map<String, Object> updates) {

        UrlEntity urlEntity = this.urlRepository.findById(id)
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

        UrlEntity savedUrlEntity = this.urlRepository.save(urlEntity);

        UrlDto response = modelMapper.map(savedUrlEntity, UrlDto.class);  // repetitive code
        response.setShortUrl(
                "http://localhost:8080/api/urls/r/" + savedUrlEntity.getShortCode()
        );
        return response;
    }

    @Override
    public void deleteUrl(Long id) {

        UrlEntity urlEntity = this.urlRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("URL not found for id: " + id)
                );

        this.urlRepository.delete(urlEntity);
    }

    @Override
    public UrlDto getUrlById(Long id) {

        UrlEntity urlEntity = this.urlRepository.findById(id)
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

        UrlEntity urlEntity = this.urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Short code not found: " + shortCode)
                );

        urlEntity.setClickCount(urlEntity.getClickCount() + 1);
        this.urlRepository.save(urlEntity);

        return urlEntity.getOriginalUrl();
    }

    private String generateCode() {

        String base62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        Random random = new Random();

        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            code.append(
                    base62.charAt(
                            random.nextInt(base62.length())
                    )
            );
        }
        return code.toString();
    }
}
