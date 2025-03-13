package com.example.url_shortener.url.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlResponseDto {
    private String shortUrl;
    private String originalUrl;
    private int accessCount;
}