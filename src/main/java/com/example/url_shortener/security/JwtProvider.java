package com.example.url_shortener.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    // 고정된 키 (256비트 이상) - 환경 변수나 설정 파일에서 로딩하는 게 이상적
    private final Key secretKey = Keys.hmacShaKeyFor(
            "thisIsAStaticSecretKeyMustBeAtLeast256BitsLong!".getBytes(StandardCharsets.UTF_8)
    );

    // 토큰 만료 시간 (2시간)
    private final long expiration = 1000 * 60 * 60 * 2;

    // 토큰 생성
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey) // 자동 알고리즘 결정됨
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 토큰: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("잘못된 형식의 토큰: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("서명이 일치하지 않음: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("빈 토큰: " + e.getMessage());
        }
        return false;
    }

    // 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}