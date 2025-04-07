package com.example.demojwt.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/books")
@RestController
@SecurityRequirement(name = "bearerAuth")
public class BookController{
    @GetMapping
    public String books(){
        return "Get all books success";
    }
}
