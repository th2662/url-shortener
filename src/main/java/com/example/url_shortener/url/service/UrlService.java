package com.example.url_shortener.url.service;

import com.example.url_shortener.url.dto.UrlResponseDto;
import com.example.url_shortener.url.entity.Url;
import com.example.url_shortener.url.repository.UrlRepository;
import com.example.url_shortener.url.strategy.UrlGeneratorStrategy;
import com.example.url_shortener.user.entity.User;
import com.example.url_shortener.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlGeneratorStrategy urlGeneratorStrategy;
    private final UserRepository userRepository;

    // 모든 의존성을 생성자로 주입해야 함
    public UrlService(@Qualifier("randomUrlGenerator") UrlGeneratorStrategy urlGeneratorStrategy, UrlRepository urlRepository, UserRepository userRepository) {
        this.urlGeneratorStrategy = urlGeneratorStrategy;
        this.urlRepository = urlRepository;
        this.userRepository = userRepository;
    }

    public UrlResponseDto createShortUrl(String originalUrl, Long userId) {
        String shortUrl;
        do {
            shortUrl = urlGeneratorStrategy.generate(originalUrl);
        } while (urlRepository.findByShortUrl(shortUrl).isPresent());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. ID: " + userId));

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(shortUrl);
        url.setUser(user);

        Url savedUrl = urlRepository.save(url);

        return new UrlResponseDto(savedUrl.getShortUrl(), savedUrl.getOriginalUrl(), savedUrl.getVisitCount());
    }

    public UrlResponseDto findByShortUrl(String shortUrl) {
        Url url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new IllegalArgumentException("Short URL not found: " + shortUrl));
        System.out.println("=== [DEBUG] user: " + url.getUser());  // 또는 log.debug 사용


        return new UrlResponseDto(url.getShortUrl(), url.getOriginalUrl(), url.getVisitCount());
    }


}