package com.AL_Tayep.BookStore.Dto.OrderDtos;

import java.math.BigDecimal;

public record OrderItemResponseDto(Long itemId,String title,Integer amount,BigDecimal priceAtPurchase,BigDecimal lineTotal) {
    
}
