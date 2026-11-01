package com.AL_Tayep.BookStore.models;



import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "carts")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"user", "cartItems"})
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "cartItems"})
@SuperBuilder
public class Cart extends GrandClass {
    

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="User_id")
    private User user;


    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CartItem> cartItems;


   public BigDecimal calcTotalPrice() {
    return cartItems.stream()
        .map(ci -> ci.getItem().getFinalPrice()
                     .multiply(BigDecimal.valueOf(ci.getAmount())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
}
    
}
