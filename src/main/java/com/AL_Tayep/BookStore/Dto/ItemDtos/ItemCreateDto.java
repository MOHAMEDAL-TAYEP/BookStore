package com.AL_Tayep.BookStore.Dto.ItemDtos;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

public record ItemCreateDto(@Column(nullable = false)String title,
@Column(nullable = false)String description,
@Column(nullable = false, precision = 10, scale = 2)BigDecimal price,
@Column(nullable = false) @PositiveOrZero Integer amount,
@Column(nullable = false) @Min(0) @Max(100) Integer discount) {
    
}
