package com.example.url_shortener.url;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    // ✅ 반드시 @RequestParam 안에 이름 명시!
    @PostMapping
    public Url createUrl(
            @RequestParam(value = "originalUrl", required = true) String originalUrl,
            @RequestParam(value = "userId", required = true) Long userId
    ) {
        // userId 검증 예시
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid userId");
        }
        return urlService.createShortUrl(originalUrl, userId);
    }
}