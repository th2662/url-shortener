package com.example.url_shortener.user;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("입력한 이메일: " + username);

        Optional<User> userOpt = userRepository.findByEmail(username);
        if (userOpt.isEmpty()) {
            System.out.println("DB에서 사용자를 찾지 못했어요");
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        User user = userOpt.get();
        System.out.println("사용자 찾음: " + user.getEmail());

        System.out.println("✅ 이메일: " + user.getEmail());
        System.out.println("✅ 비밀번호(암호화됨): " + user.getPassword());
        System.out.println("✅ 권한: " + user.getRole());

        // 추가적인 확인 작업위해 아래 코드 추가
        System.out.println("✅ DB 저장 비밀번호: " + user.getPassword());
        System.out.println("✅ 입력한 비밀번호: pass@word1");
        System.out.println("✅ matches? " + new BCryptPasswordEncoder().matches("pass@word1", user.getPassword()));


        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

}
