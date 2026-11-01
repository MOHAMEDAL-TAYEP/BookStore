package com.AL_Tayep.BookStore.Dto.FavDtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record FavItemResponseDto (Long userId,String title,BigDecimal price,BigDecimal  finalPrice,Boolean instock,LocalDateTime createdAt){
    
}
