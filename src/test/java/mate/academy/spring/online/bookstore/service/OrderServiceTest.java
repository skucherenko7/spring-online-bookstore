package mate.academy.spring.online.bookstore.service;

import mate.academy.spring.online.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.spring.online.bookstore.dto.order.OrderResponseDto;
import mate.academy.spring.online.bookstore.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.spring.online.bookstore.dto.orderitem.OrderItemResponseDto;
import mate.academy.spring.online.bookstore.exception.EntityNotFoundException;
import mate.academy.spring.online.bookstore.exception.OrderProcessingException;
import mate.academy.spring.online.bookstore.mapper.OrderItemMapper;
import mate.academy.spring.online.bookstore.mapper.OrderMapper;
import mate.academy.spring.online.bookstore.model.*;
import mate.academy.spring.online.bookstore.repository.order.OrderItemRepository;
import mate.academy.spring.online.bookstore.repository.order.OrderRepository;
import mate.academy.spring.online.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.spring.online.bookstore.service.order.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    private User user;
    private ShoppingCart shoppingCart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);

        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());
    }

    @Test
    void save_shouldCreateOrderSuccessfully() {
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setShippingAddress("145 Main St, City, Country");

        User user = new User();
        user.setId(1L);

        ShoppingCart shoppingCart = new ShoppingCart();
        CartItem item = new CartItem();
        Book book = new Book();
        book.setId(1L);
        book.setPrice(BigDecimal.valueOf(20));
        item.setBook(book);
        item.setQuantity(1);
        shoppingCart.addItemToCart(item);

        when(shoppingCartRepository.findByUser_Id(user.getId())).thenReturn(shoppingCart);
        when(orderMapper.toDto(any())).thenReturn(new OrderResponseDto(1L, 1L, Set.of(), LocalDateTime.now(),
                BigDecimal.valueOf(100), "PENDING"));
        when(orderRepository.save(any())).thenReturn(new Order());

        OrderResponseDto result = orderService.save(user, requestDto);

        assertNotNull(result);
        verify(shoppingCartRepository).findByUser_Id(user.getId());
        verify(orderRepository).save(any());
        verify(shoppingCartRepository).save(shoppingCart);

    }

    @Test
    void save_shouldThrowOrderProcessingExceptionWhenCartIsEmpty() {
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setShippingAddress("123 Адреса");

        shoppingCart.setCartItems(new HashSet<>());

        OrderProcessingException exception = assertThrows(OrderProcessingException.class, () -> {
            orderService.save(user, requestDto);
        });

        assertEquals("Shoppingcart is empty! You can't place an order.", exception.getMessage());
    }

    @Test
    void getOrderItems_shouldReturnOrderItems() {
        Long orderId = 1L;
        Long userId = 1L;

        Order order = new Order();

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);

        order.setOrderItems(new HashSet<>());
        order.getOrderItems().add(orderItem);

        when(orderRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.of(order));
        when(orderItemMapper.toDto(any())).thenReturn(new OrderItemResponseDto(1L, 1L, 2));

        List<OrderItemResponseDto> result = orderService.getOrderItems(orderId, userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findByIdAndUserId(orderId, userId);
    }

    @Test
    void updateOrderStatus_shouldUpdateOrderStatus() {
        Long orderId = 1L;
        UpdateOrderStatusRequestDto updateDto = new UpdateOrderStatusRequestDto(Order.Status.SHIPPED);

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(Order.Status.PENDING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        UpdateOrderStatusRequestDto result = orderService.updateOrderStatus(orderId, updateDto);

        assertEquals(Order.Status.SHIPPED, result.status());

        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_shouldThrowExceptionIfOrderNotFound() {
        Long orderId = 999L;

        UpdateOrderStatusRequestDto updateDto = new UpdateOrderStatusRequestDto(Order.Status.SHIPPED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderService.updateOrderStatus(orderId, updateDto);
        });

        assertEquals("Order not found with ID: " + orderId, exception.getMessage());
    }

    @Test
    void findById_shouldReturnOrderDto() {
        Long orderId = 1L;
        Long userId = 1L;
        Order order = new Order();
        order.setId(orderId);

        Set<OrderItemResponseDto> orderItems = new HashSet<>();
        BigDecimal total = BigDecimal.valueOf(100.0);
        String status = "PENDING";
        LocalDateTime orderDate = LocalDateTime.now();

        OrderResponseDto orderResponseDto = new OrderResponseDto(
                orderId, userId, orderItems, orderDate, total, status
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(any())).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.findById(orderId);

        assertNotNull(result);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void findAll_shouldReturnListOfOrders() {

        Long userId = 1L;
        Pageable pageable = Pageable.unpaged();
        User user = new User();
        user.setId(userId);

        Order order = new Order();
        Page<Order> orders = new PageImpl<>(List.of(order));

        when(orderRepository.findByUser(eq(user), eq(pageable))).thenReturn(orders);

        OrderResponseDto orderResponseDto = new OrderResponseDto(
                1L,
                1L,
                Set.of(),
                LocalDateTime.now(),
                BigDecimal.valueOf(100),
                "PENDING"
        );

        when(orderMapper.toDto(any(Order.class))).thenReturn(orderResponseDto);

        List<OrderResponseDto> result = orderService.findAll(user, pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findByUser(eq(user), eq(pageable));
    }

    @Test
    void getOrderItem_shouldReturnOrderItem() {
        Long orderId = 1L;
        Long itemId = 1L;
        Long userId = 1L;
        OrderItem orderItem = new OrderItem();
        orderItem.setId(itemId);

        OrderItemResponseDto orderItemResponseDto = new OrderItemResponseDto(itemId, 1L, 2);

        when(orderItemRepository.findByIdAndOrder_IdAndOrder_User_Id(itemId, orderId, userId))
                .thenReturn(Optional.of(orderItem));
        when(orderItemMapper.toDto(any())).thenReturn(orderItemResponseDto);

        OrderItemResponseDto result = orderService.getOrderItem(orderId, itemId, userId);

        assertNotNull(result);
        verify(orderItemRepository).findByIdAndOrder_IdAndOrder_User_Id(itemId, orderId, userId);
    }

    @Test
    void getOrderItem_shouldThrowExceptionIfOrderItemNotFound() {

        Long orderId = 1L;
        Long itemId = 999L;
        Long userId = 1L;

        when(orderItemRepository.findByIdAndOrder_IdAndOrder_User_Id(itemId, orderId, userId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderService.getOrderItem(orderId, itemId, userId);
        });

        assertTrue(exception.getMessage().contains("OrderItem not found with ID"));
    }
}
