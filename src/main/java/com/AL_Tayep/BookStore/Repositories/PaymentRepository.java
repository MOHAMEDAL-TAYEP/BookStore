package com.AL_Tayep.BookStore.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AL_Tayep.BookStore.models.Payment;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
   Optional<List<Payment>> findByOrderId(Long orderId);
}
