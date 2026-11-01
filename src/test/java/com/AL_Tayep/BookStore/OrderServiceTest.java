package com.AL_Tayep.BookStore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AL_Tayep.BookStore.Dto.OrderDtos.CreateOrderRequestDto;
import com.AL_Tayep.BookStore.Dto.OrderDtos.OrderResponseDto;
import com.AL_Tayep.BookStore.Exceptions.BadRequestException;
import com.AL_Tayep.BookStore.Exceptions.ConflictException;
import com.AL_Tayep.BookStore.Exceptions.ForbiddenException;
import com.AL_Tayep.BookStore.Exceptions.ResourceNotFoundException;
import com.AL_Tayep.BookStore.Repositories.CartRepository;
import com.AL_Tayep.BookStore.Repositories.ItemRepository;
import com.AL_Tayep.BookStore.Repositories.OrderItemRepository;
import com.AL_Tayep.BookStore.Repositories.OrderRepository;
import com.AL_Tayep.BookStore.Repositories.PaymentRepository;
import com.AL_Tayep.BookStore.Services.OrderService;
import com.AL_Tayep.BookStore.models.Cart;
import com.AL_Tayep.BookStore.models.CartItem;
import com.AL_Tayep.BookStore.models.Item;
import com.AL_Tayep.BookStore.models.Order;
import com.AL_Tayep.BookStore.models.OrderItem;
import com.AL_Tayep.BookStore.models.Payment;
import com.AL_Tayep.BookStore.models.User;
import com.AL_Tayep.BookStore.models.Enums.OrderStatus;
import com.AL_Tayep.BookStore.models.Enums.PaymentStatus;
import com.AL_Tayep.BookStore.models.KeyClass.CartItemId;
import com.AL_Tayep.BookStore.models.KeyClass.OrderItemId;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock OrderItemRepository orderItemRepository;
    @Mock ItemRepository itemRepository;
    @Mock CartRepository cartRepository;
    @Mock PaymentRepository paymentRepository; // used by cancelOrder once the payment logic is merged into it

    @InjectMocks OrderService orderService;

    private User user;
    private Item book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        book = new Item();
        book.setId(5L);
        book.setTitle("Clean Code");
        book.setAmount(10);
        book.setFinalPrice(new BigDecimal("20.00"));
    }

    // ---------- helpers ----------

    private Cart cartWith(int amount) {
        Cart cart = new Cart();
        cart.setId(100L);
        cart.setUser(user);
        CartItem ci = CartItem.builder()
                .id(new CartItemId(100L, book.getId()))
                .cart(cart)
                .item(book)
                .amount(amount)
                .build();
        cart.setCartItems(new ArrayList<>(List.of(ci)));
        return cart;
    }

    private CreateOrderRequestDto checkoutRequest() {
        return new CreateOrderRequestDto("12 Nile St, Cairo", "01000000000", null, null);
    }

    private Order orderOf(User owner, OrderStatus status, int amount) {
        Order order = new Order();
        order.setId(50L);
        order.setUser(owner);
        order.setStatus(status);
        order.setTotalPrice(new BigDecimal("40.00"));
        OrderItem oi = OrderItem.builder()
                .id(new OrderItemId(50L, book.getId()))
                .order(order)
                .item(book)
                .amount(amount)
                .priceAtPurchase(book.getFinalPrice())
                .build();
        order.setOrderItems(new ArrayList<>(List.of(oi)));
        return order;
    }

    // ---------- addNewOrder ----------

    @Test
    void addNewOrder_success_reducesStock_computesTotal_clearsCart() {
        Cart cart = cartWith(2);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(itemRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(book));

        OrderResponseDto result = orderService.addNewOrder(user, checkoutRequest());

        assertEquals(OrderStatus.PENDING, result.status());
        assertEquals(0, new BigDecimal("40.00").compareTo(result.totalPrice()));
        assertEquals(1, result.items().size());
        assertEquals(8, book.getAmount(), "stock must drop by the ordered amount");
        assertTrue(cart.getCartItems().isEmpty(), "cart must be emptied after checkout");
    }

    @Test
    void addNewOrder_emptyCart_throwsBadRequest_andSavesNothing() {
        Cart cart = cartWith(1);
        cart.getCartItems().clear();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        assertThrows(BadRequestException.class,
                () -> orderService.addNewOrder(user, checkoutRequest()));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void addNewOrder_notEnoughStock_throwsBadRequest_andKeepsStock() {
        Cart cart = cartWith(11); // only 10 in stock
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        assertThrows(BadRequestException.class,
                () -> orderService.addNewOrder(user, checkoutRequest()));

        assertEquals(10, book.getAmount());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void addNewOrder_noCart_throwsNotFound() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.addNewOrder(user, checkoutRequest()));
    }

    // ---------- cancelOrder ----------

    @Test
    void cancelOrder_pending_restoresStock_andMarksCancelled() {
        Order order = orderOf(user, OrderStatus.PENDING, 2);
        book.setAmount(8);
        when(orderRepository.findById(50L)).thenReturn(Optional.of(order));
        when(itemRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(book));

        OrderResponseDto result = orderService.cancelOrder(user, 50L);

        assertEquals(OrderStatus.CANCELLED, result.status());
        assertEquals(10, book.getAmount(), "ordered quantity must go back to stock");
    }

    @Test
    void cancelOrder_failsOpenPayment() {
        Order order = orderOf(user, OrderStatus.PENDING, 1);
        Payment pending = new Payment();
        pending.setPaymentStatus(PaymentStatus.PENDING);
        when(orderRepository.findById(50L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(50L)).thenReturn(Optional.of(List.of(pending)));
        when(itemRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(book));

        orderService.cancelOrder(user, 50L);

        assertEquals(PaymentStatus.FAILED, pending.getPaymentStatus());
    }

    @Test
    void cancelOrder_otherUsersOrder_throwsForbidden_andChangesNothing() {
        User stranger = new User();
        stranger.setId(2L);
        Order order = orderOf(user, OrderStatus.PENDING, 2);
        book.setAmount(8);
        when(orderRepository.findById(50L)).thenReturn(Optional.of(order));

        assertThrows(ForbiddenException.class, () -> orderService.cancelOrder(stranger, 50L));

        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(8, book.getAmount());
    }

    @Test
    void cancelOrder_shipped_throwsConflict() {
        Order order = orderOf(user, OrderStatus.SHIPPED, 1);
        when(orderRepository.findById(50L)).thenReturn(Optional.of(order));

        assertThrows(ConflictException.class, () -> orderService.cancelOrder(user, 50L));

        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    void cancelOrder_unknownOrder_throwsNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.cancelOrder(user, 99L));
    }
}
