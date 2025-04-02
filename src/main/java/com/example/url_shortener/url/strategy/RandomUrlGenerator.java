package com.example.url_shortener.url.strategy;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component("randomUrlGenerator") // @Qualifier로 구분 가능
public class RandomUrlGenerator implements UrlGeneratorStrategy {

    @Override
    public String generate(String originalUrl) {
        return RandomStringUtils.randomAlphanumeric(8); // 랜덤 8자리
    }
}