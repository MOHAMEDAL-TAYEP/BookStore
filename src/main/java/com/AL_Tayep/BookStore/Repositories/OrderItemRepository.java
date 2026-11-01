package com.AL_Tayep.BookStore.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AL_Tayep.BookStore.Dto.OrderDtos.OrderResponseDto;
import com.AL_Tayep.BookStore.models.OrderItem;
import com.AL_Tayep.BookStore.models.KeyClass.OrderItemId;

public interface OrderItemRepository extends JpaRepository<OrderItem,OrderItemId> {

    
}
