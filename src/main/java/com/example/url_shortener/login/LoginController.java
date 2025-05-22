package com.example.url_shortener.login;

import com.example.url_shortener.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/api/user/me")
    @ResponseBody
    public String getCurrentUser(@AuthenticationPrincipal User user) {
        return "현재 로그인한 사용자: " + user.getEmail();
    }
}
