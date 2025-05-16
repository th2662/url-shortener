package com.example.url_shortener.url.controller;

import com.example.url_shortener.url.dto.UrlResponseDto;
import com.example.url_shortener.url.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    // @RequestParam 안에 이름 명시!
    @PostMapping
    public UrlResponseDto createUrl(@RequestParam(value = "originalUrl", required = true) String originalUrl, @RequestParam(value = "userId", required = true) Long userId) {
        // userId 검증 예시
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid userId");
        }
        return urlService.createShortUrl(originalUrl, userId);
    }

    @GetMapping("/{shortUrl}")
    public UrlResponseDto getOriginalUrl(@PathVariable String shortUrl) {
        return urlService.findByShortUrl(shortUrl);
    }


    @GetMapping("/r/{shortUrl}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortUrl) {
        String originalUrl = urlService.findByShortUrl(shortUrl).getOriginalUrl();
        return ResponseEntity.status(HttpStatus.FOUND) // 302
                .location(URI.create(originalUrl))
                .build();
    }
}