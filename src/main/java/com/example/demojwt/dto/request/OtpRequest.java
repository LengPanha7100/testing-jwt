package com.example.demojwt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpRequest {
    private String otp;
    private LocalDateTime expiration;
    private Long userId;
}
