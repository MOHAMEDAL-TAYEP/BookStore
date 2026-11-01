package com.AL_Tayep.BookStore.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AL_Tayep.BookStore.Dto.OrderDtos.CreateOrderRequestDto;
import com.AL_Tayep.BookStore.Dto.OrderDtos.OrderResponseDto;
import com.AL_Tayep.BookStore.Dto.OrderDtos.OrderSummaryDto;
import com.AL_Tayep.BookStore.Dto.OrderDtos.UpdateOrderDto;
import com.AL_Tayep.BookStore.Services.OrderService;
import com.AL_Tayep.BookStore.Services.PaymentService;
import com.AL_Tayep.BookStore.models.Payment;
import com.AL_Tayep.BookStore.models.User;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController 
@RequestMapping("/api/orders")
public class OrderController {

   private final  OrderService orderService;
   private final PaymentService paymentService;
    public OrderController(OrderService orderService,PaymentService paymentService)
    {
        this.orderService = orderService;
        this.paymentService=paymentService;
    } 



    // get all ordrd for admin
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<OrderSummaryDto>> getAllOrders() {
       return ResponseEntity.ok(orderService.getAllOrderSummaries());
    }


    //get all order by user id  admin
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<OrderSummaryDto>> getOrderByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getAllOrderSummariesByUserId(userId));
    }


    //get order by id for  user
    @GetMapping("/users/{orderId}")
    public ResponseEntity<OrderResponseDto> getMyOrderById(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long orderId) {
         User user=(User) userDetails;
        return ResponseEntity.ok(orderService.getMyOrderbyId(orderId,user));
    }
    


    //get order by id for  admin
      @GetMapping("/{orderId}")
      @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<OrderResponseDto> getOrderById(
        @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderbyId(orderId));
    }


    // add new order for user
    @PostMapping("/users")
    @PreAuthorize("hasAnyRole('USER')")
    public  ResponseEntity<OrderResponseDto> createOrder(@AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody CreateOrderRequestDto dto)
    {
         User user=(User) userDetails;
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addNewOrder(user,dto));
    }

    // user cancel order 
    @PreAuthorize("hasAnyRole('USER')")
    @PatchMapping("/users/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> UserCancelOrder(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long orderId) {
         User user=(User) userDetails;
         return ResponseEntity.ok(orderService.cancelOrder(user, orderId));
    }

    // admin update user order status
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public  ResponseEntity<OrderResponseDto>  updateOrderStatus(@PathVariable Long orderId,@Valid  @RequestBody UpdateOrderDto dto) {
       
          return ResponseEntity.ok(orderService.updateOrderStatus(orderId,dto.orderStatus()));
    }
    
    
}
