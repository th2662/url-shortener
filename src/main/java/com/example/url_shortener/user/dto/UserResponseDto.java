package com.example.url_shortener.user.dto;

import com.example.url_shortener.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDto {
    private Integer id;
    private String email;
    private String role;

    public UserResponseDto(User user) {
        this.id = Math.toIntExact(user.getId());
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}