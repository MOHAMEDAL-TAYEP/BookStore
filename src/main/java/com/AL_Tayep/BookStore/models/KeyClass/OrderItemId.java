package com.AL_Tayep.BookStore.models.KeyClass;

import java.io.Serializable;

import jakarta.persistence.Column;

public record OrderItemId(@Column(name = "order_id") Long orderId,
    @Column(name = "item_id") Long itemId
) implements Serializable {}