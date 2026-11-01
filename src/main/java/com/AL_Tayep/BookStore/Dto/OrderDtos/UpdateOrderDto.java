package com.AL_Tayep.BookStore.Dto.OrderDtos;

import com.AL_Tayep.BookStore.models.Enums.OrderStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record UpdateOrderDto(@Enumerated(EnumType.STRING) OrderStatus orderStatus) {
    
}
