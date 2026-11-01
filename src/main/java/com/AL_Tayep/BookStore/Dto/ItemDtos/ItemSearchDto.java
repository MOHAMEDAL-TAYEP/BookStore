package com.AL_Tayep.BookStore.Dto.ItemDtos;

import java.math.BigDecimal;

public record ItemSearchDto(String keyWord,BigDecimal minPrice,BigDecimal MaxPrice,String sort) {
    
}
