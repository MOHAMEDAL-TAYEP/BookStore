package com.AL_Tayep.BookStore.Services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AL_Tayep.BookStore.Dto.OrderDtos.CreateOrderRequestDto;
import com.AL_Tayep.BookStore.Dto.OrderDtos.OrderItemResponseDto;
import com.AL_Tayep.BookStore.Dto.OrderDtos.OrderResponseDto;
import com.AL_Tayep.BookStore.Dto.OrderDtos.OrderSummaryDto;
import com.AL_Tayep.BookStore.Exceptions.BadRequestException;
import com.AL_Tayep.BookStore.Exceptions.ConflictException;
import com.AL_Tayep.BookStore.Exceptions.ForbiddenException;
import com.AL_Tayep.BookStore.Exceptions.ResourceNotFoundException;
import com.AL_Tayep.BookStore.Repositories.CartRepository;
import com.AL_Tayep.BookStore.Repositories.ItemRepository;
import com.AL_Tayep.BookStore.Repositories.OrderItemRepository;
import com.AL_Tayep.BookStore.Repositories.OrderRepository;
import com.AL_Tayep.BookStore.Repositories.PaymentRepository;
import com.AL_Tayep.BookStore.models.Cart;
import com.AL_Tayep.BookStore.models.CartItem;
import com.AL_Tayep.BookStore.models.Item;
import com.AL_Tayep.BookStore.models.Order;
import com.AL_Tayep.BookStore.models.OrderItem;
import com.AL_Tayep.BookStore.models.Payment;
import com.AL_Tayep.BookStore.models.User;
import com.AL_Tayep.BookStore.models.Enums.OrderStatus;
import com.AL_Tayep.BookStore.models.Enums.PaymentStatus;
import com.AL_Tayep.BookStore.models.KeyClass.OrderItemId;




@Service 
@Transactional(readOnly=true)
public class OrderService {

    final private OrderRepository orderRepository;
    final private  OrderItemRepository orderItemRepository;
    final private ItemRepository itemRepository;
    final private CartRepository cartRepository;
    final private  PaymentRepository paymentRepository;

    public OrderService(OrderRepository orderRepository,OrderItemRepository orderItemRepository,ItemRepository itemRepository,CartRepository cartRepository,
        PaymentRepository paymentRepository
    )
    {
        this.orderRepository=orderRepository;
        this.orderItemRepository=orderItemRepository;
        this.itemRepository=itemRepository;
        this.cartRepository=cartRepository;
        this.paymentRepository=paymentRepository;
    }

   public OrderItemResponseDto toOrderItemResponseDto(OrderItem orderItem)
   {

             return  new OrderItemResponseDto(
                orderItem.getItem().getId(),
                orderItem.getItem().getTitle(),
                orderItem.getAmount(),
                orderItem.getPriceAtPurchase(),
                orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getAmount()))
             );
   } 
 private OrderResponseDto toOrderResponseDto(Order order) {
    return new OrderResponseDto(
        order.getId(),
        order.getStatus(),
        order.getDeliveryAddress(),
        order.getPhoneNumber1(),
        order.getPhoneNumber2(),
        order.getTotalPrice(),
        order.getOrderItems().stream().map(this::toOrderItemResponseDto).toList(),
       null,
        order.getCreatedAt()
    );
}

    public OrderItem addOrderItem(Order order,CartItem cartitem,Item item)
    {
        OrderItemId orderItemId=new OrderItemId(order.getId(), item.getId());
        OrderItem orderItem=new OrderItem();
        orderItem.setAmount(cartitem.getAmount());
        orderItem.setId(orderItemId);
        orderItem.setPriceAtPurchase(item.getFinalPrice());
        orderItem.setOrder(order);
        orderItem.setItem(item);
        return  orderItem;
    }


    private void validateCart(Cart cart) {
    if (cart.getCartItems().isEmpty())
        throw new BadRequestException("Cart is empty");

    for (CartItem ci : cart.getCartItems()) {
        Item item = ci.getItem();
        if (ci.getAmount() > item.getAmount())
            throw new BadRequestException("Not enough stock for '" + item.getTitle()
                + "': only " + item.getAmount() + " left");
    }
}
    @Transactional 
    public OrderResponseDto addNewOrder(User user,CreateOrderRequestDto createOrderRequestDto)
    {
        Cart cart=cartRepository.findByUserId(user.getId()).orElseThrow(()-> new ResourceNotFoundException("there is not cart for this user"));
        validateCart(cart);
        Order order=new Order();
         BigDecimal total = BigDecimal.ZERO;
          order.setPhoneNumber1(createOrderRequestDto.phoneNumber1());
        order.setPhoneNumber2(createOrderRequestDto.phoneNumber2());
        order.setDeliveryAddress(createOrderRequestDto.deliveryAddress());
        order.setStatus(OrderStatus.PENDING);
        order.setUser(cart.getUser());
        order.setTotalPrice(total);
        order.setOrderItems(new ArrayList<>());
        order = orderRepository.save(order); 
        for(CartItem cartItem:cart.getCartItems())
        {
            Item item=itemRepository.findByIdForUpdate(cartItem.getItem().getId()).orElseThrow(()-> new ResourceNotFoundException("there is not item for this id: "+cartItem.getItem().getId()));
             if (cartItem.getAmount() > item.getAmount())
            throw new BadRequestException("Not enough stock for '" + item.getTitle() + "'");
            item.setAmount(item.getAmount()-cartItem.getAmount());
            order.getOrderItems().add(addOrderItem(order, cartItem,item));

            total= total.add(item.getFinalPrice()
                .multiply(BigDecimal.valueOf(cartItem.getAmount())));
        }
        cart.getCartItems().clear();
        cart.setLastModify(LocalDateTime.now()); 
        order.setTotalPrice(total);
        return toOrderResponseDto(order);
    }

     @Transactional 
     public  void deleteOrderById( Long orderId)
     {
         orderRepository.deleteById(orderId);
     }

     public OrderResponseDto getOrderbyId(Long id)
     {
        Order order=orderRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("there no order with this id: "+id));
        return  toOrderResponseDto(order);
    }

     public OrderResponseDto getMyOrderbyId(Long orderId,User user)
     {
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("there no order with this id: "+orderId));
        if(!order.getUser().getId().equals(user.getId()))
             throw new ForbiddenException("you do not have this order");
        return  toOrderResponseDto(order);
    }

    public List<OrderResponseDto>getAllUserOrderByUserId(User user)
    {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream().map(this::toOrderResponseDto).toList();
    }

   @Transactional
public OrderResponseDto cancelOrder(User user, Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

    if (!order.getUser().getId().equals(user.getId()))
        throw new ForbiddenException("This order is not yours");

    if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PAID)
        throw new ConflictException("Only pending or paid orders can be cancelled");

    // refund / fail payments
    for (Payment p : paymentRepository.findByOrderId(orderId).orElse(Collections.emptyList())) {
        if (p.getPaymentStatus() == PaymentStatus.COMPLETED) {
            p.setPaymentStatus(PaymentStatus.REFUNDED);
        } else if (p.getPaymentStatus() == PaymentStatus.PENDING) {
            p.setPaymentStatus(PaymentStatus.FAILED);
        }
    }

    // restore stock
    for (OrderItem oi : order.getOrderItems()) {
        Item item = itemRepository.findByIdForUpdate(oi.getItem().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + oi.getItem().getId()));
        item.setAmount(item.getAmount() + oi.getAmount());
    }

    order.setStatus(OrderStatus.CANCELLED);
    return toOrderResponseDto(order);
}

    @Transactional 
    public  OrderResponseDto updateOrderStatus(Long orderId,OrderStatus status)
    {
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("there is no order with this id: "+orderId));
        order.setStatus(status);
        return  toOrderResponseDto(order);
    }

    public List<OrderSummaryDto> getAllOrderSummariesByUserId(Long userId) {
    return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(o -> new OrderSummaryDto(o.getId(), o.getStatus(),
                                      o.getTotalPrice(), o.getCreatedAt()))
        .toList();
}

    public List<OrderSummaryDto> getAllOrderSummaries() {
    return orderRepository.findAll().stream()
        .map(o -> new OrderSummaryDto(o.getId(), o.getStatus(),
                                      o.getTotalPrice(), o.getCreatedAt()))
        .toList();
}

}
