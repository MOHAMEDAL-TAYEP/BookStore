package com.AL_Tayep.BookStore.Dto.UserDtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;

public record UserCreateDto(@Column(nullable = false)String name,
String password,
@Column(nullable=false) @Email String email) 
{

}
