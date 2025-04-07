package com.example.demojwt.controller;

import com.example.demojwt.exception.BadRequestException;
import com.example.demojwt.jwt.JwtService;
import com.example.demojwt.model.AppUser;
import com.example.demojwt.dto.request.AppUserRequest;
import com.example.demojwt.dto.request.AuthRequest;
import com.example.demojwt.dto.response.AuthResponse;
import com.example.demojwt.repository.OtpRepository;
import com.example.demojwt.service.AppUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auths")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserService appUserService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, AppUserService appUserService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.appUserService = appUserService;
        this.jwtService = jwtService;
    }

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest authRequest) throws Exception {
        authenticate(authRequest.getEmail() , authRequest.getPassword());
        AppUser appUser = appUserService.getUserByEmail(authRequest.getEmail());
        if(!appUser.getIsVerify()){
            throw new BadRequestException("Please verify account before login");
        }
        final UserDetails userDetails = appUserService.loadUserByUsername(authRequest.getEmail());
        System.out.println("data" + userDetails);
        final String token = jwtService.generateToken(userDetails);
        AuthResponse authResponse = new AuthResponse(token);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AppUserRequest appUserRequest) throws Exception {
        return ResponseEntity.ok(appUserService.register(appUserRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String email , @RequestParam String otp) throws Exception {
        appUserService.verify(email,otp);
        return ResponseEntity.ok("Verify account success");
    }

    @PostMapping("/send OTP")
    public ResponseEntity<?> sendOTP(@RequestParam String email) throws Exception {
        appUserService.sentOtp(email);
        return ResponseEntity.ok("Send OTP successfully, Please check on the email and verify account");
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resend(@RequestParam String email) throws Exception {
        appUserService.resend(email);
        return ResponseEntity.ok("Resend OTP successfully");
    }
}
