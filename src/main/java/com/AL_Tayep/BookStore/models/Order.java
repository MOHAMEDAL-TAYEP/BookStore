package com.AL_Tayep.BookStore.models;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.AL_Tayep.BookStore.models.Enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "Orders")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Order extends GrandClass {


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

     @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private String phoneNumber1;

    private String phoneNumber2;

    @Column(nullable = false, precision = 10, scale = 2)
    private  BigDecimal totalPrice;

    @ManyToOne()
    @JoinColumn(name="User_id")
    User user;

   @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<OrderItem> orderItems ;
    
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Payment> payment;
}
