package com.AL_Tayep.BookStore.Dto.CartDtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Positive;

public record UpdateCartItemDto(@Column(nullable = false) @Positive Integer amount) {

}
