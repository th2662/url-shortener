package com.example.url_shortener.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Integer id;
    private String email;
    private String role;
}