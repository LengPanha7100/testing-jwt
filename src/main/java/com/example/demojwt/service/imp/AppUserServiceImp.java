package com.example.demojwt.service.imp;

import com.example.demojwt.exception.BadRequestException;
import com.example.demojwt.model.AppUser;
import com.example.demojwt.dto.request.AppUserRequest;
import com.example.demojwt.dto.request.OtpRequest;
import com.example.demojwt.dto.response.AppUserResponse;
import com.example.demojwt.model.Otp;
import com.example.demojwt.repository.AppUserRepository;
import com.example.demojwt.repository.OtpRepository;
import com.example.demojwt.service.AppUserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.modelmapper.ModelMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AppUserServiceImp implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private final OtpRepository otpRepository;
    // In-memory OTP store (email → otp)
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public AppUserServiceImp(AppUserRepository appUserRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, TemplateEngine templateEngine, JavaMailSender javaMailSender, OtpRepository otpRepository) {
        this.appUserRepository = appUserRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
        this.otpRepository = otpRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByEmail(email);
        if (appUser == null) {
            System.out.println("Not found email");
        }
        return appUser;
    }
    @Override
    public AppUserResponse register(AppUserRequest appUserRequest) {
        String sendOtp  = generateOtp();
        otpStore.put(appUserRequest.getEmail() , sendOtp);
        appUserRequest.setPassword(passwordEncoder.encode(appUserRequest.getPassword()));
        AppUser appUser = appUserRepository.register(appUserRequest);
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(2);
        OtpRequest otpRequest = new OtpRequest(sendOtp , expirationDate , appUser.getUserId() );
        otpRepository.insetOtp(otpRequest);
//        verify(sendOtp , appUserRequest.getEmail());
        return modelMapper.map(appUser, AppUserResponse.class);
    }

    @Override
    public AppUser getUserByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    @Override
    public void verify(String email, String otp) {
        Otp otpObject = otpRepository.getOtpValue(otp);

        if (otpObject == null) {
            throw new BadRequestException("Invalid otp");
        }

        if (!otpObject.getExpiration().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("OTP expired");
        }

        System.out.println(otpObject);
        appUserRepository.verifyCode(email , otp);
    }



    @Override
    public void sentOtp(String email) {
        AppUser appUser = appUserRepository.findByEmail(email);
        if(appUser == null) {
            System.out.println("Not found user");
            return;
        }
        String sendOtp  = generateOtp();
        otpStore.put(email, sendOtp);
        try {
            Context context = new Context();
            context.setVariable("otp", sendOtp);
            String process = templateEngine.process("index", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setSubject("Verify your email with OTP");
            mimeMessageHelper.setText(process, true);
            mimeMessageHelper.setTo(email);
            javaMailSender.send(mimeMessage);
            LocalDateTime expiration = LocalDateTime.now().plusMinutes(2);
            OtpRequest otpRequest = new OtpRequest(sendOtp , expiration , appUser.getUserId() );
            otpRepository.insetOtp(otpRequest);
        }catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void resend(String email) {
        AppUser appUser = appUserRepository.findByEmail(email);
        String sendOtp  = generateOtp();
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(2);
        appUserRepository.resend(sendOtp, expirationDate , appUser.getUserId());
//        verify(sendOtp , email);
    }

    private String generateOtp() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }
}
