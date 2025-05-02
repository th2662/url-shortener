package com.example.url_shortener.security;

import com.example.url_shortener.user.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // 사용자 인증 정보를 조회할 커스텀 UserDetailsService
    private final CustomUserDetailsService userDetailsService;
    // JWT 토큰을 생성·검증할 프로바이더
    private final JwtProvider jwtProvider;

    /**
     * 생성자 주입(Constructor Injection)
     * @param uds CustomUserDetailsService 빈
     * @param jp JwtProvider 빈
     */
    public SecurityConfig(CustomUserDetailsService uds, JwtProvider jp) {
        this.userDetailsService = uds;
        this.jwtProvider        = jp;
    }

    /**
     * AuthenticationManager 빈 생성
     * UserDetailsService와 PasswordEncoder를 설정하여
     * 실제 인증 처리를 담당할 객체를 빈으로 등록한다.
     */
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(userDetailsService)   // 사용자 조회 서비스 연결
                .passwordEncoder(passwordEncoder());      // 비밀번호 암호화 방식 지정
        return authBuilder.build();
    }

    /**
     * SecurityFilterChain 빈 생성
     * HTTP 요청에 대한 보안 설정을 정의한다.
     *
     * 1) 로그인 전용 필터를 UsernamePasswordAuthenticationFilter 자리에 삽입
     * 2) 로그인 이후 모든 요청에 대해 JWT 검증 필터를 UsernamePasswordAuthenticationFilter 이전에 실행
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            AuthenticationManager authMgr) throws Exception {

        // 1) POST "/api/login" 요청을 처리할 커스텀 로그인 필터 생성
        JwtLoginFilter loginFilter =
                new JwtLoginFilter("/api/login", authMgr, jwtProvider);

        http
                // 인증 관리자 설정
                .authenticationManager(authMgr)
                // CSRF 보호 비활성화 (API 전용 서비스이므로 필요 없다고 판단)
                .csrf(AbstractHttpConfigurer::disable)
                // CORS 전체 비활성화 (필요시 세부 설정 추가 가능)
                .cors(AbstractHttpConfigurer::disable)
                // URL별 접근 권한 설정
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/api/login", "/api/public/**").permitAll()  // 로그인 및 공개 API는 모두 허용
                        .requestMatchers("/api/user/me").authenticated()
                        .anyRequest().authenticated()                                  // 그 외 요청은 인증 필요
                )
                // 2) 로그인 전용 필터를 스프링 기본 UsernamePasswordAuthenticationFilter 자리에 삽입
                .addFilterAt(
                        loginFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                // 3) 로그인 이후 JWT를 검증할 필터를 UsernamePasswordAuthenticationFilter 이전에 실행
                .addFilterBefore(
//                        new JwtAuthFilter(jwtProvider, userDetailsService),
                        jwtAuthFilter(), // Bean으로 등록한 필터 호출
                        UsernamePasswordAuthenticationFilter.class
                );

        // 설정 적용 후 HttpSecurity 빌드
        return http.build();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtProvider, userDetailsService);
    }

    /**
     * 비밀번호 암호화 방식(PasswordEncoder) 빈 등록
     * BCrypt 알고리즘 사용 (Spring Security 권장)
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}