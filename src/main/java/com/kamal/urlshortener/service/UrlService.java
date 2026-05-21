package com.kamal.urlshortener.service;

import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;

import java.util.List;
import java.util.Map;

public interface UrlService {

    List<UrlDto> getUrls();

    UrlDto getUrlById(Long id);

    UrlDto addUrl(NewUrlDto newUrlDto);

    void deleteUrlById(Long id);

    UrlDto updateUrl(Long id, NewUrlDto newUrlDto);

    UrlDto updatePartialUrl(Long id, Map<String, Object> updates);
}
