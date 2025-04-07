package com.example.demojwt.repository;

import com.example.demojwt.dto.request.OtpRequest;
import com.example.demojwt.model.Otp;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OtpRepository {
    @Insert("""
    INSERT INTO otp(otp, expiration, user_id)
    VALUES (#{req.otp}, #{req.expiration}, #{req.userId})
    """)
    void insetOtp(@Param("req") OtpRequest request);

    @Results(id = "id" , value = {
            @Result(property = "otpId" , column = "otp_id"),
            @Result(property = "otpValue" , column = "otp"),
            @Result(property = "userId" , column = "user_id")
    })
    @Select("""
    SELECT * FROM otp WHERE otp = #{otpValue}
    """)
    Otp getOtpValue(String otpValue);
}
