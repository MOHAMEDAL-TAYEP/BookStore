package com.AL_Tayep.BookStore.Services;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AL_Tayep.BookStore.Dto.PaymentDtos.PaymentRequestDto;
import com.AL_Tayep.BookStore.Dto.PaymentDtos.PaymentResponseDto;
import com.AL_Tayep.BookStore.Exceptions.ConflictException;
import com.AL_Tayep.BookStore.Exceptions.ForbiddenException;
import com.AL_Tayep.BookStore.Exceptions.ResourceNotFoundException;
import com.AL_Tayep.BookStore.Repositories.OrderRepository;
import com.AL_Tayep.BookStore.Repositories.PaymentRepository;
import com.AL_Tayep.BookStore.models.Payment;
import com.AL_Tayep.BookStore.models.User;
import com.AL_Tayep.BookStore.models.Enums.OrderStatus;
import com.AL_Tayep.BookStore.models.Enums.PaymentStatus;
import com.AL_Tayep.BookStore.models.Order;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    private PaymentResponseDto toPaymentResponseDto(Payment p) {
        return new PaymentResponseDto(p.getId(), p.getOrder().getId(),
                p.getPaymentMethod(), p.getPaymentStatus(),
                p.getAmount(), p.getTransactionId(), p.getCreatedAt());
    }

    // User pays an order (Supports retries if previous failed)
    @Transactional
    public PaymentResponseDto pay(User user, Long orderId, PaymentRequestDto dto) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (!order.getUser().getId().equals(user.getId()))
            throw new ForbiddenException("This order is not yours");
        if (order.getStatus() != OrderStatus.PENDING)
            throw new ConflictException("Only pending orders can be paid");

        // 1. Fetch existing attempts safely (Default to empty list if null)
        List<Payment> payments = paymentRepository.findByOrderId(orderId)
            .orElse(Collections.emptyList());

        // 2. Check if there's already a successful or active payment
        for (Payment p : payments) {
            if (p.getPaymentStatus() != PaymentStatus.FAILED)
                throw new ConflictException("This order is already paid, cancelled, or processing payment.");
        }

        // 3. Create a new payment attempt record
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(dto.paymentMethod());
        payment.setAmount(order.getTotalPrice());            
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setPaymentStatus(PaymentStatus.COMPLETED); 

        // 4. Crucial: Update Order status to PAID so it matches the successful payment
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return toPaymentResponseDto(paymentRepository.save(payment));
    }

    public List<PaymentResponseDto> getMyPayments(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (!order.getUser().getId().equals(user.getId()))
            throw new ForbiddenException("This order is not yours");

        List<Payment> payments = paymentRepository.findByOrderId(orderId)
            .orElse(Collections.emptyList());

        return payments.stream().map(this::toPaymentResponseDto).toList();
    }

    public PaymentResponseDto getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("No payment with this id"));
        return toPaymentResponseDto(payment);
    }


    public List<PaymentResponseDto> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream().map(this::toPaymentResponseDto).toList();
    }

}