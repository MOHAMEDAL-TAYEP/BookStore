package com.AL_Tayep.BookStore.Dto.OrderDtos;

import com.AL_Tayep.BookStore.models.Enums.PaymentMethod;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record CreateOrderRequestDto(String deliveryAddress,String  phoneNumber1,String phoneNumber2,@Enumerated(EnumType.STRING) PaymentMethod paymentMethod) {
    
}
