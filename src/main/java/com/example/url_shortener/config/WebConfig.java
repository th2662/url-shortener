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
        registry.addViewController("/{spring:^(?!api$).*$}")
                .setViewName("forward:/index.html");

        registry.addViewController("/**/{spring:^(?!api$).*$}")
                .setViewName("forward:/index.html");

        registry.addViewController("/{spring:^(?!api$).*$}/**{spring:?!(\\.js|\\.css|\\.png|\\.jpg|\\.jpeg|\\.svg|\\.ico|\\.json)$}")
                .setViewName("forward:/index.html");
    }
}

