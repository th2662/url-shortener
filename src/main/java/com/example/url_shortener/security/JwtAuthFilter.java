package com.example.url_shortener.security;

import com.example.url_shortener.user.service.CustomUserDetailsService;
import com.example.url_shortener.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);


    // 토큰 발급 및 파싱 클래스
    private final JwtProvider jwtProvider;

    // 사용자 정보 조회용 서비스 (UserDetailsService 구현체)
    private final CustomUserDetailsService userDetailsService;

    // 생성자: 필터가 토큰 검사와 사용자 조회를 할 수 있도록 의존성 주입
    public JwtAuthFilter(JwtProvider jwtProvider, CustomUserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    // 모든 요청마다 실행되는 필터 메서드
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println(">> JwtAuthFilter 실행 시작");
        System.out.println(">> DispatcherType: " + request.getDispatcherType());
        System.out.println(">> RequestURI: " + request.getRequestURI());

        String token = resolveToken(request);
        System.out.println(">> 추출된 토큰: " + token);


        if (token != null && jwtProvider.validateToken(token)) {
            String username = jwtProvider.getUsernameFromToken(token);
            System.out.println(">> username: " + username);

            UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
            User user = userPrincipal.getUser(); // getter 통해 실제 User 엔티티 획득

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug(">> 인증 설정 완료: " + authentication);
            System.out.println(">> 인증 설정 완료: " + authentication);
        } else {
            log.debug(">> 토큰이 없거나 유효하지 않음");
            System.out.println(">> 토큰이 없거나 유효하지 않음");

        }

        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 JWT 추출하는 헬퍼 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); // "Bearer " 제거하고 토큰만 리턴
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        log.debug(">> JwtAuthFilter shouldNotFilter 호출됨");
        log.debug(">> DispatcherType: " + request.getDispatcherType());
        log.debug(">> URI: " + request.getRequestURI());

        // filter 제외할 항목
        String uri = request.getRequestURI();
        return uri.equals("/") ||
                uri.equals("/index.html") ||
                uri.startsWith("/assets/") ||
                uri.startsWith("/static/") ||
                uri.startsWith("/favicon.ico") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/api/login") ||
                uri.startsWith("/api/public/");
    }

}