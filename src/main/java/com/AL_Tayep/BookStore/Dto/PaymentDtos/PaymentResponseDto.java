package com.AL_Tayep.BookStore.Dto.PaymentDtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.AL_Tayep.BookStore.models.Enums.PaymentMethod;
import com.AL_Tayep.BookStore.models.Enums.PaymentStatus;

public record PaymentResponseDto(Long id, Long orderId,PaymentMethod paymentMethod, PaymentStatus status,BigDecimal amount,String transactionId, LocalDateTime createdAt) {
    
}
