package com.AL_Tayep.BookStore.Dto.CartDtos;

import java.math.BigDecimal;

public record CartItemResponseDto(Long cartId,Long itemId,String title,Integer amount,BigDecimal unitPrice,BigDecimal totelPrice) {
    
}
