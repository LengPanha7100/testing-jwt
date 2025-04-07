package com.example.demojwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserResponse {
    private Long userId;
    private String fullName;
    private String username;
    private String email;
}