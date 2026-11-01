package com.AL_Tayep.BookStore.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AL_Tayep.BookStore.Dto.AuthDtos.AuthResponseDto;
import com.AL_Tayep.BookStore.Dto.UserDtos.LoginRequestDto;
import com.AL_Tayep.BookStore.Dto.UserDtos.UserCreateDto;

import com.AL_Tayep.BookStore.Services.AuthService;


import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
        private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

     @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody UserCreateDto dto) {

        AuthResponseDto created = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public  ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto)
    {
               return  ResponseEntity.ok(authService.login(dto));
    }
    
}
