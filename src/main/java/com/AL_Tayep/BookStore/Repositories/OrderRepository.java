package com.AL_Tayep.BookStore.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AL_Tayep.BookStore.models.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
