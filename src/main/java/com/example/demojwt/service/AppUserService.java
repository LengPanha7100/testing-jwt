package com.example.demojwt.service;

import com.example.demojwt.model.AppUser;
import com.example.demojwt.dto.request.AppUserRequest;
import com.example.demojwt.dto.response.AppUserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserService extends UserDetailsService {
    AppUserResponse register(AppUserRequest appUserRequest);

    AppUser getUserByEmail(String email);

    void verify(String email,String otp);

    void sentOtp(String email);

    void resend(String email);
}
