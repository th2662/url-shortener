package com.example.url_shortener.url.strategy;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component("hashUrlGenerator")
public class HashUrlGenerator implements UrlGeneratorStrategy {

    @Override
    public String generate(String originalUrl) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(originalUrl.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, 8); // 앞 8자리 사용
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash generation error", e);
        }
    }
}