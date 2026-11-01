package com.AL_Tayep.BookStore.Dto.CartDtos;

import jakarta.persistence.Column;

public record CartItemDto(@Column(nullable = false) Long itemId,
    @Column(nullable = false)Integer amount) {
    
}