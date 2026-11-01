package com.AL_Tayep.BookStore.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AL_Tayep.BookStore.models.CartItem;
import com.AL_Tayep.BookStore.models.KeyClass.CartItemId;

public interface CartItemRepository extends JpaRepository<CartItem,CartItemId> {
     Optional<CartItem> findByCart_IdAndItem_Id(Long cartId, Long itemId);
      List<CartItem> findByCart_Id(Long cartId);
      void deleteByCart_IdAndItem_Id(Long cartId, Long itemId);
}
