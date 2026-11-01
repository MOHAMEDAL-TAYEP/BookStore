package com.AL_Tayep.BookStore.Dto.UserDtos;

import jakarta.validation.constraints.Size;

public record ChangePasswordDto(@Size(min=8) String oldPassword,@Size(min=8)String newPassword) {
    
}
