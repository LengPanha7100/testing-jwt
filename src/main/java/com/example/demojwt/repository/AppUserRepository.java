package com.example.demojwt.repository;

import com.example.demojwt.model.AppUser;
import com.example.demojwt.dto.request.AppUserRequest;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface AppUserRepository {

    @Results(id = "appUSerId" , value = {
            @Result(property = "userId" , column = "user_id"),
            @Result(property = "fullName" , column = "full_name"),
            @Result(property = "isVerify" , column = "is_verify")
    })

    @Select("""
    SELECT * FROM app_users WHERE email = #{email}
    """)
    AppUser findByEmail(String email);

    @Select("""
        INSERT INTO app_users
        VALUES (default,#{appUser.fullName} , #{appUser.email}, #{appUser.username}, #{appUser.password})
        returning *;
    """)
    @ResultMap("appUSerId")
    AppUser register(@Param("appUser") AppUserRequest appUserRequest);

    @Select("""
    UPDATE app_users SET is_verify = true WHERE email = #{email}
    """)
    void verifyCode(String email , String otp);

    void resend(String s, String email);


    @Select("""
       UPDATE otp SET otp= #{sendOtp}, expiration = #{expiration} WHERE  user_id = #{userId}
    """)
    void resend(String sendOtp , LocalDateTime expiration , Long userId);
}
