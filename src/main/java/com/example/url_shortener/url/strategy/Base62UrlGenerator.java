package com.example.url_shortener.url.strategy;

import org.springframework.stereotype.Component;

@Component("base62UrlGenerator")
public class Base62UrlGenerator implements UrlGeneratorStrategy {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public String generate(String originalUrl) {
        long number = System.nanoTime(); // 간단한 고유 값 (DB ID를 사용해도 좋음)
        return encodeBase62(number);
    }

    private String encodeBase62(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(BASE62.charAt((int) (value % 62)));
            value /= 62;
        }
        return sb.reverse().toString();
    }
}