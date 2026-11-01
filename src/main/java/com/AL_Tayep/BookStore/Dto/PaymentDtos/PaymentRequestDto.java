package com.AL_Tayep.BookStore.Dto.PaymentDtos;

import com.AL_Tayep.BookStore.models.Enums.PaymentMethod;

public record PaymentRequestDto(Long orderId ,PaymentMethod paymentMethod) {
    
}
