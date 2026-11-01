package com.AL_Tayep.BookStore.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AL_Tayep.BookStore.Dto.PaymentDtos.PaymentRequestDto;
import com.AL_Tayep.BookStore.Dto.PaymentDtos.PaymentResponseDto;
import com.AL_Tayep.BookStore.Services.PaymentService;
import com.AL_Tayep.BookStore.models.User;

import jakarta.validation.Valid;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/orders/users/{orderId}/payment")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<PaymentResponseDto> pay(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId, @Valid @RequestBody PaymentRequestDto dto) {
                 User user=(User) userDetails;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.pay(user, orderId, dto));
    }

    @GetMapping("/api/orders/users/{orderId}/payment")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<PaymentResponseDto>> getMyPaymentForOrder(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
         User user=(User) userDetails;
        return ResponseEntity.ok(paymentService.getMyPayments(user, orderId));
    }

    @GetMapping("/api/payments/{paymentId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
     public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @GetMapping("/api/payments")
    @PreAuthorize("hasAnyRole('ADMIN')")
     public ResponseEntity<List<PaymentResponseDto>> getAllPayment() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }



}