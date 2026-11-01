package com.AL_Tayep.BookStore.models;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

import com.AL_Tayep.BookStore.models.Enums.PaymentMethod;
import com.AL_Tayep.BookStore.models.Enums.PaymentStatus;

@Entity
@Table(name = "payment")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Payment extends GrandClass {
    
    @ManyToOne 
    @JoinColumn(name="order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private  PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
     private PaymentStatus paymentStatus;
     
     BigDecimal amount;
     
     private  String transactionId; 
}
