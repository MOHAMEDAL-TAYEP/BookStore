package com.AL_Tayep.BookStore.Dto.ItemDtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ItemUpdateDto(String title,String description,BigDecimal price, @Min(0) @Max(100) Integer Discount,Double amount) {

}
