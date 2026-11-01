package com.AL_Tayep.BookStore.Dto.OrderDtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.AL_Tayep.BookStore.models.Enums.OrderStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record OrderResponseDto(Long id,@Enumerated(EnumType.STRING) OrderStatus status,String deliveryAddress,String phoneNumber1,
    String phoneNumber2,BigDecimal totalPrice,List<OrderItemResponseDto> items,String PaymentResponse,LocalDateTime createdAt	) {
    
}
