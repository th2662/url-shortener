package com.example.url_shortener.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.util.Map;

public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter implements OrderedFilter {

    private final JwtProvider jwtProvider;

    public JwtLoginFilter(String url,
                          AuthenticationManager authMgr,
                          JwtProvider jwtProvider) {
        super(url);
        this.jwtProvider = jwtProvider;
        setFilterProcessesUrl(url);
        setAuthenticationManager(authMgr);
    }

    @Override
    public int getOrder() {
        // UsernamePasswordAuthenticationFilter 보통 0 이므로,
        // 그 앞이나 뒤로 충분히 밀리도록 HIGHEST_PRECEDENCE + n
        return OrderedFilter.HIGHEST_PRECEDENCE + 10;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException, IOException, ServletException {

        System.out.println("JwtLoginFilter: attemptAuthentication called!");

        var creds = new ObjectMapper().readValue(req.getInputStream(), Map.class);

        String username = creds.get("username").toString();
        String password = creds.get("password").toString();

        System.out.println("입력한 이메일(username): " + username);
        System.out.println("입력한 비밀번호(password): " + password);

        var token = new UsernamePasswordAuthenticationToken(username, password);

        // DEBUG: 직접 패스워드 비교 (UserDetailsService에서 로드되었을 때를 전제로 함)
        // 실제 AuthenticationManager가 matches 해주지만, 일단 여기서 눈으로 확인
        var userDetails = getAuthenticationManager()
                .authenticate(token)
                .getPrincipal(); // 성공했을 경우만 내려옴

        System.out.println("→ userDetails.getClass(): " + userDetails.getClass().getName());
        // 성공 시 token은 바로 return하면 되고, 실패하면 catch에서 처리됨
        return token;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
        String jwt = jwtProvider.generateToken(auth.getName());
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter()
                .write(new ObjectMapper().writeValueAsString(Map.of("token", jwt)));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter()
                .write(new ObjectMapper().writeValueAsString(
                        Map.of("error", "로그인 실패: " + failed.getMessage())
                ));
    }
}