package com.AL_Tayep.BookStore.Dto.AuthDtos;

public record AuthResponseDto(String token,String tokenType) {
    public AuthResponseDto(String token) {
        this(token, "Bearer");
    }
}