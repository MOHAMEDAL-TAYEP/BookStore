package com.AL_Tayep.BookStore.Dto.UserDtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(@Column(nullable = false,unique = true)@Email  String email,
@Size(min=8)String password) {

    
}
