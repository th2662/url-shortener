package com.example.url_shortener.url.dto;

import com.example.url_shortener.url.entity.Url;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Jackson 역직렬화용
@AllArgsConstructor
public class UrlResponseDto {
    private String shortUrl;
    private String originalUrl;
    private int accessCount;

    public static UrlResponseDto from(Url url) {
        return new UrlResponseDto(
                url.getShortUrl(),
                url.getOriginalUrl(),
                url.getVisitCount() // or url.getAccessCount() depending on your field name
        );
    }
}