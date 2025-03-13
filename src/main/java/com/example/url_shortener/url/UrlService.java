package com.example.url_shortener.url;

import com.example.url_shortener.url.dto.UrlResponseDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public Optional<UrlResponseDto> getUrlByShortCode(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl).map(url -> {
            UrlResponseDto dto = new UrlResponseDto();
            dto.setShortUrl(url.getShortUrl());
            dto.setOriginalUrl(url.getOriginalUrl());
            dto.setAccessCount(url.getVisitCount());
            return dto;
        });
    }
}
