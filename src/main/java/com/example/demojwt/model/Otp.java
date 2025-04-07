package com.example.demojwt.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Otp {
    private Long otpId;
    private String otpValue;
    private LocalDateTime expiration;
    private Long userId;

}