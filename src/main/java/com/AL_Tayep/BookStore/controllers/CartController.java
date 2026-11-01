package com.AL_Tayep.BookStore.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.AL_Tayep.BookStore.Dto.CartDtos.CartItemDto;
import com.AL_Tayep.BookStore.Dto.CartDtos.CartResponseDto;
import com.AL_Tayep.BookStore.Dto.CartDtos.UpdateCartItemDto;
import com.AL_Tayep.BookStore.Services.CartService;
import com.AL_Tayep.BookStore.Services.ItemService;
import com.AL_Tayep.BookStore.models.User;

import jakarta.validation.Valid;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    CartService cartService;
    ItemService itemService;

    public CartController(CartService cartService, ItemService itemService) {
        this.cartService = cartService;
        this.itemService = itemService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CartResponseDto>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    @GetMapping("/users")
    public ResponseEntity<CartResponseDto> getCartByUserId(@AuthenticationPrincipal UserDetails userDetails) {
          User user=(User) userDetails;
        return ResponseEntity.ok(cartService.getCart(user));
    }


    @PutMapping("/users/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
 public ResponseEntity<CartResponseDto> updateCartItem(@AuthenticationPrincipal UserDetails userDetails,
                                                      @PathVariable Long itemId,
                                                      @Valid @RequestBody UpdateCartItemDto dto) {
                                                         User user=(User) userDetails;
    return ResponseEntity.ok(cartService.updateCartItem(user, itemId, dto));
                                                      }

   @PostMapping("/users")
   @PreAuthorize("hasRole('USER')")
   public ResponseEntity<CartResponseDto>  saveCartItem(@AuthenticationPrincipal UserDetails userDetails,@RequestBody CartItemDto dto) {
    User user=(User) userDetails;
    return  ResponseEntity.ok(cartService.addCartItem(user, dto));
   }

   @DeleteMapping("/users/items/{itemId}")
   @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponseDto> removeCartItem(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long itemId)
    {
         User user=(User) userDetails;
        return ResponseEntity.ok(cartService.removeCartItem(user, itemId));
    }

    @DeleteMapping("users")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponseDto> ClearCArt(@AuthenticationPrincipal UserDetails userDetails)
    {
         User user=(User) userDetails;
        return ResponseEntity.ok(cartService.clearCart(user));
    }

}
