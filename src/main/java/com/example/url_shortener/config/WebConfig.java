package com.example.url_shortener.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Vue의 history mode를 사용할 때,
     * 존재하지 않는 경로를 모두 index.html로 포워딩하여
     * Vue Router가 처리할 수 있도록 함
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 1) 확장자(.) 없는 최상위 경로
        registry.addViewController("/{path:[^\\.]+}")
                .setViewName("forward:/index.html");
        // 2) 확장자(.) 없는 하위 경로 전부
        registry.addViewController("/**/{path:[^\\.]+}")
                .setViewName("forward:/index.html");
    }
}

