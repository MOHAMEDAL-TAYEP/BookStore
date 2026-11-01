package com.AL_Tayep.BookStore.models.KeyClass;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;



@Embeddable
public record CartItemId(@Column(name = "cart_id") Long cartId,
    @Column(name = "item_id") Long itemId
) implements Serializable {}
