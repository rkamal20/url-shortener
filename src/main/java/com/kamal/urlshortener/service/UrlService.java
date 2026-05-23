package com.kamal.urlshortener.service;

import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;

import java.util.Map;

public interface UrlService {

    UrlDto createUrl(NewUrlDto dto);

    UrlDto updateUrl(Long id, NewUrlDto dto);

    UrlDto patchUrl(Long id, Map<String, Object> updates);

    void deleteUrl(Long id);

    UrlDto getUrlById(Long id);
}
