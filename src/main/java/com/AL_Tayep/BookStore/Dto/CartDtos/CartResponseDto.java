package com.AL_Tayep.BookStore.Dto.CartDtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CartResponseDto(Long id,List<CartItemResponseDto> items,BigDecimal totalPrice,LocalDateTime lastModify) {
    
}
