package com.AL_Tayep.BookStore.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "items")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Item extends GrandClass 
{

   

@Column(nullable = false)
private String title;

@Column(nullable = false)
private String 	description;


@Column(nullable = false, precision = 10, scale = 2)
private BigDecimal price;

@Column(nullable = false)
@PositiveOrZero 
private Integer amount;

@Min(0)
@Max(100)
private Integer discount;

@Column(nullable = false, precision = 10, scale = 2)
private BigDecimal finalPrice;

@OneToMany(mappedBy = "item")
private List<CartItem> cartItem;

@OneToMany(mappedBy = "item")
private List<FavItem> favItem;

     @PrePersist
    @PreUpdate
    public void calculateFinalPrice() {
        BigDecimal currentPrice = (this.price != null) ? this.price : BigDecimal.ZERO;
        BigDecimal currentDiscount = (this.discount != null) ? BigDecimal.valueOf(this.discount) : BigDecimal.ZERO;
        
        this.finalPrice = currentPrice
        .multiply(BigDecimal.valueOf(100).subtract(currentDiscount))
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

} 