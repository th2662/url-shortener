package com.example.url_shortener.Login;

import com.example.url_shortener.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/api/user/me")
    @ResponseBody
    public String getCurrentUser(@AuthenticationPrincipal User user) {
        return "현재 로그인한 사용자: " + user.getEmail();
    }
}
