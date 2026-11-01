package com.AL_Tayep.BookStore.Dto.OrderDtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.AL_Tayep.BookStore.models.Enums.OrderStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record OrderSummaryDto(Long id, @Enumerated(EnumType.STRING) OrderStatus status,
                              BigDecimal totalPrice, LocalDateTime createdAt) {
                              }
