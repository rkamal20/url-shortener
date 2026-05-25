package com.kamal.urlshortener.service;

import com.kamal.urlshortener.dto.NewUrlDto;
import com.kamal.urlshortener.dto.UrlDto;
import com.kamal.urlshortener.dto.AnalyticsDto;

public interface UrlService {

    UrlDto shortenUrl(NewUrlDto dto);

    String redirectUrl(String shortCode);

    AnalyticsDto getAnalytics(String shortCode);
}
