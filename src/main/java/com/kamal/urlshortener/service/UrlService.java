package com.kamal.urlshortener.service;

import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;

public interface UrlService {

    UrlDto createUrl(NewUrlDto dto);

    UrlDto getUrl(Long id);

    UrlDto updateUrl(Long id, NewUrlDto dto);

    UrlDto patchUrl(Long id, NewUrlDto dto);

    void deleteUrl(Long id);

    UrlDto getUrlById(Long id);
}
