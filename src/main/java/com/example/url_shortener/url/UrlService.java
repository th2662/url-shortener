package com.example.url_shortener.url;

import com.example.url_shortener.url.strategy.UrlGeneratorStrategy;
import com.example.url_shortener.user.User;
import com.example.url_shortener.user.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlGeneratorStrategy urlGeneratorStrategy;
    private final UserRepository userRepository;

    // 모든 의존성을 생성자로 주입해야 함
    public UrlService(
            @Qualifier("randomUrlGenerator") UrlGeneratorStrategy urlGeneratorStrategy,
            UrlRepository urlRepository,
            UserRepository userRepository
    ) {
        this.urlGeneratorStrategy = urlGeneratorStrategy;
        this.urlRepository = urlRepository;
        this.userRepository = userRepository;
    }

    public Url createShortUrl(String originalUrl, Long userId) {
        String shortUrl;
        do {
            shortUrl = urlGeneratorStrategy.generate(originalUrl);
        } while (urlRepository.findByShortUrl(shortUrl).isPresent());

        // userId로 User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. ID: " + userId));

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(shortUrl);
        url.setUser(user); // ✅ 연관관계 설정

        return urlRepository.save(url);
    }
}