package com.AL_Tayep.BookStore.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.AL_Tayep.BookStore.models.KeyClass.OrderItemId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;



@Entity
@Table(name = "Orders_Items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class OrderItem{

    @EmbeddedId 
    OrderItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JoinColumn(name="order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
     @MapsId("itemId")
    @JoinColumn(name="item_id")
    private Item item; 

    @Column(nullable=false)
    @Positive 
    private Integer amount;
    
     @Column(nullable=false)
    private BigDecimal	priceAtPurchase;

    @CreationTimestamp
    private  LocalDateTime createdAt;
}
