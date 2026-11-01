package com.AL_Tayep.BookStore.Services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AL_Tayep.BookStore.Dto.CartDtos.CartItemDto;
import com.AL_Tayep.BookStore.Dto.CartDtos.CartItemResponseDto;
import com.AL_Tayep.BookStore.Dto.CartDtos.CartResponseDto;
import com.AL_Tayep.BookStore.Dto.CartDtos.UpdateCartItemDto;
import com.AL_Tayep.BookStore.Exceptions.BadRequestException;
import com.AL_Tayep.BookStore.Exceptions.ResourceNotFoundException;
import com.AL_Tayep.BookStore.Repositories.CartItemRepository;
import com.AL_Tayep.BookStore.Repositories.CartRepository;
import com.AL_Tayep.BookStore.Repositories.ItemRepository;
import com.AL_Tayep.BookStore.models.Cart;
import com.AL_Tayep.BookStore.models.CartItem;
import com.AL_Tayep.BookStore.models.Item;
import com.AL_Tayep.BookStore.models.User;
import com.AL_Tayep.BookStore.models.KeyClass.CartItemId;

@Service
@Transactional(readOnly = true)
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    public CartService(CartItemRepository cartItemRepository,
            CartRepository cartRepository,
            ItemRepository itemRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    public CartItemResponseDto toCartItemResponseDto(CartItem cartItem) {
        BigDecimal totalPrice = cartItem.getItem().getFinalPrice().multiply(BigDecimal.valueOf(cartItem.getAmount()));

        CartItemResponseDto dto = new CartItemResponseDto(
                cartItem.getId().cartId(),
                cartItem.getId().itemId(),
                cartItem.getItem().getTitle(),
                cartItem.getAmount(),
                cartItem.getItem().getFinalPrice(),
                totalPrice);

        return dto;
    }

    public CartResponseDto toCartResponseDto(Cart cart) {
        CartResponseDto dto = new CartResponseDto(cart.getId(),
                cart.getCartItems().stream().map(this::toCartItemResponseDto).toList(),
                cart.calcTotalPrice(), cart.getLastModify());
        return dto;
    }
    
    @Transactional
    public  CartResponseDto addCartItem(User user,CartItemDto cartItemDto)
    {
        Cart cart=cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user " + user.getId()));
        Item item=itemRepository.findById(cartItemDto.itemId())
         .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + cartItemDto.itemId()));

        CartItem cartItem= cart.getCartItems().stream()
        .filter(ci -> ci.getItem().getId().equals(item.getId())).findFirst().orElse(null);
        int newAmount= cartItemDto.amount()+(cartItem ==null? 0:cartItem.getAmount());
        if (newAmount > item.getAmount())
        throw new BadRequestException("Only " + item.getAmount() + " in stock");
        if(cartItem==null)
        {
            cartItem =new CartItem();
            CartItemId id=new CartItemId(cart.getId(),item.getId());
            cartItem.setId(id); 
            cartItem.setAmount(newAmount);
            cartItem.setItem(item);
            cartItem.setCart(cart);
            cart.getCartItems().add(cartItem);
        }
        else {
        cartItem.setAmount(newAmount);
    }
    cart.setLastModify(LocalDateTime.now());
   
    return toCartResponseDto(cart);
    }
     @Transactional
    public CartResponseDto updateCartItem(User user,Long itemId,UpdateCartItemDto dto)
    {
         Cart cart = cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user " + user.getId()));

           CartItem cartItem = cart.getCartItems().stream()
        .filter(ci -> ci.getItem().getId().equals(itemId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("This item is not in the cart"));

        if (dto.amount() > cartItem.getItem().getAmount())
        throw new BadRequestException("Only " + cartItem.getItem().getAmount() + " in stock");
        cartItem.setAmount(dto.amount());
        cart.setLastModify(LocalDateTime.now());

         return toCartResponseDto(cart);
}


@Transactional
public CartResponseDto removeCartItem(User user, Long itemId) {
    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user " + user.getId()));

    CartItem cartItem = cart.getCartItems().stream()
        .filter(ci -> ci.getItem().getId().equals(itemId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("This item is not in the cart"));

    cart.getCartItems().remove(cartItem);   
   cart.setLastModify(LocalDateTime.now());
    return toCartResponseDto(cart);
}

@Transactional
public CartResponseDto clearCart(User user) {
    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user " + user.getId()));
    cart.getCartItems().clear();          
    cart.setLastModify(LocalDateTime.now());
    return toCartResponseDto(cart);
}


public CartResponseDto getCart(User user) {
    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user " + user.getId()));
    return toCartResponseDto(cart);
}
public List<CartResponseDto> getAllCarts()
{
    return cartRepository.findAll().stream().map(this::toCartResponseDto).toList();
}
}
