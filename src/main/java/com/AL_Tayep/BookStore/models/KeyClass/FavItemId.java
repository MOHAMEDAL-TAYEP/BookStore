package com.AL_Tayep.BookStore.models.KeyClass;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;



@Embeddable
public record FavItemId(@Column(name = "user_id") Long userId,
    @Column(name = "item_id") Long itemId
) implements Serializable {}
