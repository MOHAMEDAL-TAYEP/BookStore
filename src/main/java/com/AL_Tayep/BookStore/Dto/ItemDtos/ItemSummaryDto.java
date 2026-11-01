package com.AL_Tayep.BookStore.Dto.ItemDtos;

import java.math.BigDecimal;

public record ItemSummaryDto(Long id,String title,BigDecimal price,Integer dicount,BigDecimal finalPrice,boolean instock) {
    
}
