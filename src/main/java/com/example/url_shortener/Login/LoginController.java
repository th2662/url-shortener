package com.example.url_shortener.Login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // login.html 또는 login.jsp와 같은 뷰
    }
}
