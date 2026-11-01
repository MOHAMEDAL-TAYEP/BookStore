package com.AL_Tayep.BookStore.Dto.ItemDtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ItemReponseDto(Long id,String title,String description,BigDecimal price,Integer disount,BigDecimal finalPrice, @Min(0) @Max(100) Integer amount) {
    
}
